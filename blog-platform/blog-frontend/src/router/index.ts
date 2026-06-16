// ============================================
// Vue Router 路由配置
// ============================================
import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

// 懒加载页面组件
const Home = () => import('@/pages/Home.vue')
const ArticleDetail = () => import('@/pages/ArticleDetail.vue')
const Search = () => import('@/pages/Search.vue')
const Archive = () => import('@/pages/Archive.vue')
const Login = () => import('@/pages/Login.vue')
const Register = () => import('@/pages/Register.vue')
const Links = () => import('@/pages/Links.vue')
const About = () => import('@/pages/About.vue')
const NotFound = () => import('@/pages/NotFound.vue')

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: { title: '首页' }
  },
  {
    path: '/article/:slug',
    name: 'ArticleDetail',
    component: ArticleDetail,
    meta: { title: '文章详情' }
  },
  {
    path: '/category/:slug',
    name: 'CategoryArticles',
    component: Home,
    meta: { title: '分类文章' }
  },
  {
    path: '/tag/:slug',
    name: 'TagArticles',
    component: Home,
    meta: { title: '标签文章' }
  },
  {
    path: '/search',
    name: 'Search',
    component: Search,
    meta: { title: '搜索' }
  },
  {
    path: '/archive',
    name: 'Archive',
    component: Archive,
    meta: { title: '归档' }
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { title: '登录', guest: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: Register,
    meta: { title: '注册', guest: true }
  },
  {
    path: '/links',
    name: 'Links',
    component: Links,
    meta: { title: '友情链接' }
  },
  {
    path: '/about',
    name: 'About',
    component: About,
    meta: { title: '关于' }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: NotFound,
    meta: { title: '404' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(_to, _from, savedPosition) {
    return savedPosition || { top: 0 }
  }
})

// 全局路由守卫：动态设置 TDK
router.afterEach((to) => {
  const title = (to.meta.title as string) || '个人博客'
  document.title = `${title} - 个人博客`
})

export default router
