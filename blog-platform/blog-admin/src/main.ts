import { createApp } from 'vue'
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

import App from './App.vue'
import router from './router'
import { registerTokenGetter } from './api/request'

const app = createApp(App)

const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)
app.use(pinia)
app.use(router)
app.use(ElementPlus, { size: 'default' })

registerTokenGetter(() => {
  return localStorage.getItem('admin_token')
})

app.mount('#app')

// 全局引用
import { useAdminStore } from './stores/user'
;(window as any).__adminStore = useAdminStore()
