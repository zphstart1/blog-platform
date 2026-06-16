<template>
  <div class="auth-page">
    <div class="auth-card">
      <h2 class="auth-title">注册</h2>
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-position="top"
        @submit.prevent="handleRegister"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="form.username"
            placeholder="4-20字符，字母开头"
            size="large"
          />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="6-30字符"
            show-password
            size="large"
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="再次输入密码"
            show-password
            size="large"
          />
        </el-form-item>
        <el-form-item label="邮箱（选填）" prop="email">
          <el-input
            v-model="form.email"
            placeholder="your@email.com"
            size="large"
          />
        </el-form-item>
        <el-form-item label="昵称（选填）" prop="nickname">
          <el-input
            v-model="form.nickname"
            placeholder="默认同用户名"
            size="large"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="submit-btn"
            @click="handleRegister"
          >
            注册
          </el-button>
        </el-form-item>
      </el-form>
      <div class="auth-footer">
        已有账号？<router-link to="/login">立即登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import type { FormInstance, FormRules } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { VALIDATION_RULES } from '@/constants'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  email: '',
  nickname: ''
})

const validateConfirmPassword = (_rule: any, value: string, callback: Function) => {
  if (value !== form.password) {
    callback(new Error('两次密码输入不一致'))
  } else {
    callback()
  }
}

const rules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    {
      pattern: VALIDATION_RULES.username.pattern,
      message: VALIDATION_RULES.username.message,
      trigger: 'blur'
    }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 30, message: VALIDATION_RULES.password.message, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ]
}

async function handleRegister() {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch {
    return
  }

  loading.value = true
  try {
    await userStore.register({
      username: form.username,
      password: form.password,
      email: form.email || undefined,
      nickname: form.nickname || undefined
    })
    ElMessage.success({ message: '注册成功，请登录', duration: 1000 })
    router.push('/login')
  } catch {
    // 错误已在拦截器中处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 60vh;
}

.auth-card {
  width: 420px;
  background: #fff;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.auth-title {
  text-align: center;
  font-size: 24px;
  margin-bottom: 32px;
  color: #333;
}

.submit-btn {
  width: 100%;
}

.auth-footer {
  text-align: center;
  font-size: 14px;
  color: #999;
}

.auth-footer a {
  color: #409eff;
  text-decoration: none;
}
</style>
