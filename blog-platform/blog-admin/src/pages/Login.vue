<template>
  <div class="login-page">
    <div class="login-card">
      <h1>博客管理后台</h1>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @submit.prevent="handleLogin">
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名"
            size="large"
            :prefix-icon="User"
          />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            show-password
            :prefix-icon="Lock"
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" :loading="loading" class="submit-btn" @click="handleLogin">
            登录管理后台
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import type { FormInstance, FormRules } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useAdminStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const adminStore = useAdminStore()
const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  if (!formRef.value) return
  try { await formRef.value.validate() } catch { return }
  loading.value = true
  try {
    await adminStore.login(form)
    ElMessage.success({ message: '登录成功', duration: 1000 })
    router.push('/')
  } catch { /* 已处理 */ }
  finally { loading.value = false }
}
</script>

<style scoped>
.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: #f0f2f5;
}

.login-card {
  width: 400px;
  padding: 48px 40px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
}

.login-card h1 {
  text-align: center;
  font-size: 24px;
  margin-bottom: 36px;
  color: #333;
}

.submit-btn {
  width: 100%;
}
</style>
