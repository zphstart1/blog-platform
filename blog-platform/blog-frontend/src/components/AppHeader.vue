<template>
  <header class="app-header">
    <div class="header-inner">
      <router-link to="/" class="logo">📝 个人博客</router-link>
      <nav class="header-nav">
        <router-link to="/" class="nav-link">首页</router-link>
        <router-link to="/archive" class="nav-link">归档</router-link>
        <router-link to="/search" class="nav-link">搜索</router-link>
        <router-link to="/links" class="nav-link">友链</router-link>
        <router-link to="/about" class="nav-link">关于</router-link>
      </nav>
      <div class="header-actions">
        <template v-if="userStore.isLoggedIn && userStore.user">
          <el-dropdown>
            <span class="user-info">
              <el-avatar :size="32" :src="userStore.user.avatar || undefined">
                {{ userStore.user.nickname?.charAt(0) }}
              </el-avatar>
              <span class="user-name">{{ userStore.user.nickname }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item disabled>
                  {{ userStore.user.username }}
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <router-link to="/login">
            <el-button size="small">登录</el-button>
          </router-link>
          <router-link to="/register" style="margin-left: 8px">
            <el-button size="small" type="primary">注册</el-button>
          </router-link>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'

const userStore = useUserStore()
const router = useRouter()

function handleLogout() {
  userStore.logout()
  router.push('/')
}
</script>

<style scoped>
.app-header {
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  font-size: 20px;
  font-weight: 700;
  color: #333;
  text-decoration: none;
}

.header-nav {
  display: flex;
  gap: 24px;
}

.nav-link {
  color: #666;
  text-decoration: none;
  font-size: 15px;
  transition: color 0.2s;
}

.nav-link:hover,
.nav-link.router-link-active {
  color: #409eff;
}

.header-actions {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.user-name {
  font-size: 14px;
  color: #333;
}
</style>
