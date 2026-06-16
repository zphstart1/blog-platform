import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const AdminLayout = () => import('@/pages/AdminLayout.vue')
const Login = () => import('@/pages/Login.vue')
const Dashboard = () => import('@/pages/Dashboard.vue')
const ArticleList = () => import('@/pages/ArticleList.vue')
const ArticleEditor = () => import('@/pages/ArticleEditor.vue')
const CategoryManage = () => import('@/pages/CategoryManage.vue')
const TagManage = () => import('@/pages/TagManage.vue')
const CommentReview = () => import('@/pages/CommentReview.vue')
const UserManage = () => import('@/pages/UserManage.vue')
const NotFound = () => import('@/pages/NotFound.vue')

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { title: '管理后台登录' }
  },
  {
    path: '/',
    component: AdminLayout,
    meta: { requiresAuth: true },
    children: [
      { path: '', name: 'Dashboard', component: Dashboard, meta: { title: '仪表盘' } },
      { path: 'articles', name: 'ArticleList', component: ArticleList, meta: { title: '文章管理' } },
      { path: 'articles/new', name: 'ArticleNew', component: ArticleEditor, meta: { title: '写文章' } },
      { path: 'articles/:id/edit', name: 'ArticleEdit', component: ArticleEditor, meta: { title: '编辑文章' } },
      { path: 'categories', name: 'CategoryManage', component: CategoryManage, meta: { title: '分类管理' } },
      { path: 'tags', name: 'TagManage', component: TagManage, meta: { title: '标签管理' } },
      { path: 'comments', name: 'CommentReview', component: CommentReview, meta: { title: '评论审核' } },
      { path: 'users', name: 'UserManage', component: UserManage, meta: { title: '用户管理' } }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: NotFound,
    meta: { title: '404' }
  }
]

const router = createRouter({
  history: createWebHistory('/admin/'),
  routes
})

// 路由守卫：鉴权
router.beforeEach((to, _from, next) => {
  if (to.meta.requiresAuth) {
    const token = localStorage.getItem('admin_token')
    if (!token) {
      next('/login')
      return
    }
  }
  next()
})

router.afterEach((to) => {
  const title = (to.meta.title as string) || '管理后台'
  document.title = `${title} - 博客管理`
})

export default router
