<template>
  <el-config-provider :locale="zhCn">
    <div id="blog-app">
      <AppHeader />
      <main class="blog-main">
        <div class="blog-container">
          <router-view v-slot="{ Component }">
            <transition name="fade" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </div>
      </main>
      <AppFooter />
    </div>
  </el-config-provider>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import AppHeader from '@/components/AppHeader.vue'
import AppFooter from '@/components/AppFooter.vue'
import { useAppStore } from '@/stores/app'

const appStore = useAppStore()

onMounted(() => {
  appStore.fetchSiteMeta()
})
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto,
    'Helvetica Neue', Arial, 'Noto Sans SC', sans-serif;
  color: #333;
  background: #f5f7fa;
}

.blog-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
