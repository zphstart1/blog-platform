<template>
  <el-container class="admin-layout">
    <el-aside width="220px" class="admin-aside">
      <div class="logo">
        <router-link to="/">📝 管理后台</router-link>
      </div>
      <el-menu
        :default-active="currentRoute"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
      >
        <el-menu-item index="/">
          <el-icon><DataAnalysis /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        <el-menu-item index="/articles">
          <el-icon><Document /></el-icon>
          <span>文章管理</span>
        </el-menu-item>
        <el-menu-item index="/articles/new">
          <el-icon><Edit /></el-icon>
          <span>写文章</span>
        </el-menu-item>
        <el-menu-item index="/categories">
          <el-icon><Folder /></el-icon>
          <span>分类管理</span>
        </el-menu-item>
        <el-menu-item index="/tags">
          <el-icon><Collection /></el-icon>
          <span>标签管理</span>
        </el-menu-item>
        <el-menu-item index="/comments">
          <el-icon><ChatLineSquare /></el-icon>
          <span>评论审核</span>
        </el-menu-item>
        <el-menu-item v-if="adminStore.isOwnerOrAdmin" index="/users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="admin-header">
        <div class="header-left">
          <h2>{{ pageTitle }}</h2>
        </div>
        <div class="header-right">
          <span class="user-name">{{ adminStore.user?.nickname || '管理员' }}</span>
          <el-button text @click="handleLogout">退出</el-button>
        </div>
      </el-header>
      <el-main class="admin-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAdminStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const adminStore = useAdminStore()

const currentRoute = computed(() => route.path)
const pageTitle = computed(() => route.meta.title || '管理后台')

function handleLogout() {
  adminStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.admin-layout {
  height: 100vh;
}

.admin-aside {
  background: #304156;
  overflow-y: auto;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo a {
  color: #fff;
  font-size: 18px;
  font-weight: 600;
  text-decoration: none;
}

.admin-header {
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #e6e6e6;
  padding: 0 24px;
  height: 60px;
}

.header-left h2 {
  font-size: 18px;
  font-weight: 600;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-name {
  font-size: 14px;
  color: #666;
}

.admin-main {
  background: #f0f2f5;
  padding: 24px;
}
</style>
