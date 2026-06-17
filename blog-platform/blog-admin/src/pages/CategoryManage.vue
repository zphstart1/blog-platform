<template>
  <div class="category-page">
    <div class="page-toolbar">
      <h3>分类管理</h3>
      <el-button type="primary" @click="handleCreate">
        <el-icon><Plus /></el-icon> 新增分类
      </el-button>
    </div>

    <el-table :data="categories" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="name" label="分类名称" min-width="150" />
      <el-table-column prop="slug" label="URL别名" width="150" />
      <el-table-column prop="description" label="描述" min-width="200" />
      <el-table-column prop="articleCount" label="文章数" width="80" />
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button text type="primary" size="small" @click="handleEdit(row as Category)">编辑</el-button>
          <el-popconfirm title="确定删除？" @confirm="handleDelete((row as Category).id)">
            <template #reference>
              <el-button text type="danger" size="small">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="editingCategory ? '编辑分类' : '新增分类'"
      width="500px"
      @closed="resetForm"
    >
      <el-form ref="dialogFormRef" :model="dialogForm" :rules="dialogRules" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="dialogForm.name" />
        </el-form-item>
        <el-form-item label="URL别名" prop="slug">
          <el-input v-model="dialogForm.slug" placeholder="留空自动生成" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="dialogForm.description" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="dialogForm.sortOrder" :min="0" />
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
import { getCategories, createCategory, updateCategory, deleteCategory } from '@/api/category'
import type { Category } from '@/types'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const categories = ref<Category[]>([])
const dialogVisible = ref(false)
const editingCategory = ref<Category | null>(null)
const submitting = ref(false)
const dialogFormRef = ref<FormInstance>()

const dialogForm = reactive({
  name: '',
  slug: '',
  description: '',
  sortOrder: 0
})

const dialogRules: FormRules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

async function fetchList() {
  loading.value = true
  try { categories.value = await getCategories() }
  catch { /* handled */ }
  finally { loading.value = false }
}

function handleCreate() {
  editingCategory.value = null
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row: Category) {
  editingCategory.value = row
  dialogForm.name = row.name
  // 防御性处理：slug 可能是值对象 { value: "xxx" } 或纯字符串
  dialogForm.slug = typeof row.slug === 'string' ? row.slug : (row.slug as any)?.value ?? ''
  dialogForm.description = row.description || ''
  dialogForm.sortOrder = row.sortOrder ?? 0
  dialogVisible.value = true
}

function resetForm() {
  dialogForm.name = ''
  dialogForm.slug = ''
  dialogForm.description = ''
  dialogForm.sortOrder = 0
}

async function submitDialog() {
  if (!dialogFormRef.value) return
  try { await dialogFormRef.value.validate() } catch { return }

  submitting.value = true
  try {
    if (editingCategory.value) {
      await updateCategory(editingCategory.value.id, dialogForm)
      ElMessage.success({ message: '更新成功', duration: 1000 })
    } else {
      await createCategory(dialogForm)
      ElMessage.success({ message: '创建成功', duration: 1000 })
    }
    dialogVisible.value = false
    fetchList()
  } catch { /* handled */ }
  finally { submitting.value = false }
}

async function handleDelete(id: number) {
  try {
    await deleteCategory(id)
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
