import { createApp } from 'vue'
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import 'prismjs/themes/prism-tomorrow.css'

import App from './App.vue'
import router from './router'
import { registerTokenGetter } from './api/request'
import { useUserStore } from './stores/user'

const app = createApp(App)

// Pinia 状态管理
const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)
app.use(pinia)

// Vue Router
app.use(router)

// Element Plus UI
app.use(ElementPlus, { size: 'default' })

// 注册 token 获取函数（Axios 拦截器使用）
registerTokenGetter(() => {
  try {
    return useUserStore().token
  } catch {
    return localStorage.getItem('blog_token')
  }
})

// 暴露 userStore 到 window，供 Axios 401 拦截器使用
app.mount('#app')

// 挂载后注册全局引用
;(window as any).__userStore = useUserStore()
