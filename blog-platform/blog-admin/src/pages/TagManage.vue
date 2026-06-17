<template>
  <div class="tag-page">
    <div class="page-toolbar">
      <h3>标签管理</h3>
      <el-button type="primary" @click="handleCreate">
        <el-icon><Plus /></el-icon> 新增标签
      </el-button>
    </div>

    <el-table :data="tags" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="name" label="标签名称" min-width="150" />
      <el-table-column prop="slug" label="URL别名" width="150" />
      <el-table-column prop="articleCount" label="文章数" width="80" />
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button text type="primary" size="small" @click="handleEdit(row as Tag)">编辑</el-button>
          <el-popconfirm title="确定删除？" @confirm="handleDelete((row as Tag).id)">
            <template #reference>
              <el-button text type="danger" size="small">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog
      v-model="dialogVisible"
      :title="editingTag ? '编辑标签' : '新增标签'"
      width="400px"
      @closed="resetForm"
    >
      <el-form ref="dialogFormRef" :model="dialogForm" :rules="dialogRules" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="dialogForm.name" />
        </el-form-item>
        <el-form-item label="URL别名" prop="slug">
          <el-input v-model="dialogForm.slug" placeholder="留空自动生成" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitDialog">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { getTags, createTag, updateTag, deleteTag } from '@/api/category'
import type { Tag } from '@/types'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const tags = ref<Tag[]>([])
const dialogVisible = ref(false)
const editingTag = ref<Tag | null>(null)
const submitting = ref(false)
const dialogFormRef = ref<FormInstance>()

const dialogForm = reactive({
  name: '',
  slug: ''
})

const dialogRules: FormRules = {
  name: [{ required: true, message: '请输入标签名称', trigger: 'blur' }]
}

async function fetchList() {
  loading.value = true
  try { tags.value = await getTags() }
  catch { /* handled */ }
  finally { loading.value = false }
}

function handleCreate() {
  editingTag.value = null
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row: Tag) {
  editingTag.value = row
  dialogForm.name = row.name
  // 防御性处理：slug 可能是值对象 { value: "xxx" } 或纯字符串
  dialogForm.slug = typeof row.slug === 'string' ? row.slug : (row.slug as any)?.value ?? ''
  dialogVisible.value = true
}

function resetForm() {
  dialogForm.name = ''
  dialogForm.slug = ''
}

async function submitDialog() {
  if (!dialogFormRef.value) return
  try { await dialogFormRef.value.validate() } catch { return }

  submitting.value = true
  try {
    if (editingTag.value) {
      await updateTag(editingTag.value.id, dialogForm)
      ElMessage.success({ message: '更新成功', duration: 1000 })
    } else {
      await createTag(dialogForm)
      ElMessage.success({ message: '创建成功', duration: 1000 })
    }
    dialogVisible.value = false
    fetchList()
  } catch { /* handled */ }
  finally { submitting.value = false }
}

async function handleDelete(id: number) {
  try {
    await deleteTag(id)
    ElMessage.success({ message: '删除成功', duration: 1000 })
    fetchList()
  } catch { /* handled */ }
}

onMounted(() => fetchList())
</script>

<style scoped>
.page-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.page-toolbar h3 {
  font-size: 18px;
}
</style>
