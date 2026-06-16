// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  ssr: true,

  // SSG 模式：构建时生成静态页面
  nitro: {
    preset: 'static',
    prerender: {
      crawlLinks: true,
      routes: ['/', '/sitemap.xml']
    }
  },

  // 运行时配置
  // 构建时设置：NUXT_PUBLIC_API_BASE=https://your-api.com/api npx nuxt generate
  runtimeConfig: {
    public: {
      apiBase: process.env.NUXT_PUBLIC_API_BASE || 'http://127.0.0.1:8080/api'
    }
  },

  // 应用元数据
  app: {
    head: {
      htmlAttrs: { lang: 'zh-CN' },
      title: '个人博客',
      meta: [
        { charset: 'utf-8' },
        { name: 'viewport', content: 'width=device-width, initial-scale=1' },
        { name: 'description', content: '个人博客 - 技术分享与学习笔记' },
        { name: 'keywords', content: '博客,技术,编程,Java,Spring,Vue' }
      ],
      link: [
        { rel: 'icon', type: 'image/svg+xml', href: '/favicon.svg' }
      ]
    }
  },

  // CSS
  css: ['~/assets/css/main.css'],

  // 模块
  modules: ['@nuxtjs/sitemap'],

  // Sitemap 配置
  sitemap: {
    hostname: 'https://example.com',
    gzip: true,
    exclude: ['/admin/**', '/login', '/register'],
    defaults: {
      changefreq: 'daily',
      priority: 0.5
    }
  },

  // 构建优化
  build: {
    transpile: []
  },

  compatibilityDate: '2024-11-01'
})
