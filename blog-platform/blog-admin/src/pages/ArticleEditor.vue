<template>
  <div class="editor-page">
    <div class="page-toolbar">
      <el-button @click="$router.back()">
        <el-icon><ArrowLeft /></el-icon> 返回
      </el-button>
      <h3 class="page-title">{{ isEdit ? '编辑文章' : '写文章' }}</h3>
      <div class="toolbar-right">
        <el-button @click="handleSaveDraft" :loading="saving">保存草稿</el-button>
        <el-button type="primary" @click="handlePublish" :loading="saving">发布</el-button>
      </div>
    </div>

    <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
      <el-row :gutter="20">
        <el-col :span="16">
          <el-form-item label="文章标题" prop="title">
            <el-input v-model="form.title" placeholder="输入文章标题" size="large" />
          </el-form-item>
          <el-form-item label="URL 别名" prop="slug">
            <el-input v-model="form.slug" placeholder="留空则自动生成" />
          </el-form-item>
          <el-form-item label="文章内容" prop="content">
            <!-- ByteMD 编辑器 -->
            <div class="editor-wrapper">
              <Editor
                :value="form.content"
                :plugins="plugins"
                :locale="zhHans"
                @change="handleContentChange"
              />
            </div>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-card class="side-card">
            <template #header>发布设置</template>
            <el-form-item label="分类">
              <el-select v-model="form.categoryId" placeholder="选择分类" clearable style="width: 100%">
                <el-option
                  v-for="cat in categories"
                  :key="cat.id"
                  :label="cat.name"
                  :value="cat.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="标签">
              <el-select
                v-model="form.tagIds"
                multiple
                placeholder="选择标签"
                style="width: 100%"
              >
                <el-option
                  v-for="tag in tags"
                  :key="tag.id"
                  :label="tag.name"
                  :value="tag.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="封面图">
              <el-input v-model="form.coverImage" placeholder="封面图URL" />
            </el-form-item>
            <el-form-item label="摘要">
              <el-input v-model="form.summary" type="textarea" :rows="3" placeholder="留空则自动截取" />
            </el-form-item>
            <el-form-item label="置顶">
              <el-switch v-model="isTop" />
            </el-form-item>
          </el-card>

          <el-card class="side-card" style="margin-top: 16px">
            <template #header>图片上传</template>
            <el-upload
              :http-request="handleUpload"
              :show-file-list="false"
              accept=".jpg,.jpeg,.png,.gif,.webp"
            >
              <el-button type="primary" plain :loading="uploading">
                <el-icon><Upload /></el-icon> 上传图片
              </el-button>
            </el-upload>
            <div v-if="uploadedUrl" class="upload-result">
              <el-image :src="uploadedUrl" fit="contain" style="width:100%;max-height:200px;margin-top:8px" />
              <el-input v-model="uploadedUrl" size="small" readonly style="margin-top:8px" />
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import type { FormInstance, FormRules } from 'element-plus'
import { Editor } from '@bytemd/vue-next'
import gfm from '@bytemd/plugin-gfm'
import highlight from '@bytemd/plugin-highlight'
import frontmatter from '@bytemd/plugin-frontmatter'
import mediumZoom from '@bytemd/plugin-medium-zoom'
import zhHans from 'bytemd/locales/zh_Hans.json'
import 'bytemd/dist/index.css'
import 'highlight.js/styles/github.css'

import { createArticle, updateArticle, getArticleById } from '@/api/article'
import { getCategories, getTags } from '@/api/category'
import { uploadImage } from '@/api/upload'
import { useAdminStore } from '@/stores/user'
import type { Category, Tag, ArticleDetail } from '@/types'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const adminStore = useAdminStore()
const formRef = ref<FormInstance>()
const saving = ref(false)
const uploading = ref(false)
const uploadedUrl = ref('')

const isEdit = computed(() => !!route.params.id)

const plugins = [gfm(), highlight(), frontmatter(), mediumZoom()]

const form = reactive({
  title: '',
  slug: '',
  content: '',
  summary: '',
  coverImage: '',
  categoryId: null as number | null,
  tagIds: [] as number[],
})

const isTop = ref(false)

const rules: FormRules = {
  title: [{ required: true, message: '请输入文章标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入文章内容', trigger: 'blur' }]
}

const categories = ref<Category[]>([])
const tags = ref<Tag[]>([])

function handleContentChange(v: string) {
  form.content = v
}

async function handleUpload(options: { file: File }) {
  const token = adminStore.token
  if (!token) {
    ElMessage.error({ message: '请先登录', duration: 1000 })
    return
  }
  uploading.value = true
  try {
    const result = await uploadImage(options.file, token)
    uploadedUrl.value = result.url
    ElMessage.success({ message: '上传成功', duration: 1000 })
  } catch { /* handled */ }
  finally { uploading.value = false }
}

async function handleSaveDraft() {
  await submitArticle('DRAFT')
}

async function handlePublish() {
  await submitArticle('PUBLISHED')
}

async function submitArticle(status: 'DRAFT' | 'PUBLISHED') {
  if (!formRef.value) return
  try { await formRef.value.validate() } catch { return }

  // 发布时校验：内容不能为空、摘要不能为空
  if (status === 'PUBLISHED') {
    if (!form.content || !form.content.trim()) {
      ElMessage.warning({ message: '发布文章时，内容不能为空', duration: 1000 })
      return
    }
    if (!form.summary || !form.summary.trim()) {
      ElMessage.warning({ message: '发布文章时，摘要不能为空', duration: 1000 })
      return
    }
  }

  saving.value = true
  try {
    const data = {
      title: form.title,
      slug: form.slug || undefined,
      content: form.content,
      summary: form.summary || undefined,
      coverImage: form.coverImage || undefined,
      categoryId: form.categoryId ?? undefined,
      tagIds: form.tagIds.length ? form.tagIds : undefined,
      status,
      isTop: isTop.value ? 1 : 0
    }

    if (isEdit.value) {
      await updateArticle(Number(route.params.id), data)
      ElMessage.success({ message: '更新成功', duration: 1000 })
    } else {
      await createArticle(data)
      ElMessage.success({ message: status === 'PUBLISHED' ? '发布成功' : '草稿已保存', duration: 1000 })
    }
    router.push('/articles')
  } catch { /* handled */ }
  finally { saving.value = false }
}

onMounted(async () => {
  try {
    const [catResult, tagResult] = await Promise.all([getCategories(), getTags()])
    categories.value = catResult
    tags.value = tagResult
  } catch { /* 静默 */ }

  // 编辑模式：加载文章数据
  if (isEdit.value) {
    try {
      const article: ArticleDetail = await getArticleById(Number(route.params.id))
      form.title = article.title
      form.slug = article.slug
      form.content = article.content || ''
      form.summary = article.summary || ''
      form.coverImage = article.coverImage || ''
      form.categoryId = article.category?.id ?? null
      form.tagIds = article.tags?.map(t => t.id) ?? []
      isTop.value = article.isTop
    } catch { /* handled */ }
  }
})
</script>

<style scoped>
.page-toolbar {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
}

.page-title {
  flex: 1;
  font-size: 18px;
}

.toolbar-right {
  display: flex;
  gap: 8px;
}

.editor-wrapper {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  min-height: 500px;
}

.editor-wrapper :deep(.bytemd) {
  height: 500px;
}

.side-card {
  margin-bottom: 16px;
}
</style>
