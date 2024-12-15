import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores'
import { showToast } from 'vant'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'layout',
      redirect: '/home',
      component: () => import('../views/layout/LayoutPage.vue'),
      children: [
        {
          path: '/public',
          name: 'public',
          component: () => import('../views/PublicPage.vue'),
          meta: { title: '首页' }
        },
        {
          path: '/home',
          name: 'home',
          component: () => import('../views/HomePage.vue'),
          meta: { title: '匹配' }
        },
        {
          path: '/team',
          name: 'team',
          component: () => import('../views/team/TeamPage.vue'),
          meta: { title: '队伍' }
        },
        {
          path: '/user',
          name: 'user',
          component: () => import('../views/user/UserPage.vue'),
          meta: { title: '我的' }
        }
      ]
    },
    {
      path: '/search',
      name: 'search',
      component: () => import('../views/search/SearchPage.vue')
    },
    {
      path: '/searchResults',
      name: 'searchResults',
      component: () => import('../views/search/SearchResultsPage.vue')
    },
    {
      path: '/edit',
      name: 'edit',
      component: () => import('../views/user/EditUserPage.vue')
    },
    {
      path: '/editDetail',
      name: 'editDetail',
      component: () => import('../views/layout/LayoutEditNavBar.vue'),
      children: [
        {
          path: '/editDetail/username',
          name: 'username',
          component: () => import('../views/user/editDetail/EditUsername.vue'),
          meta: { title: '昵称' }
        },
        {
          path: '/editDetail/phone',
          name: 'phone',
          component: () => import('../views/user/editDetail/EditPhone.vue'),
          meta: { title: '电话' }
        },
        {
          path: '/editDetail/email',
          name: 'email',
          component: () => import('../views/user/editDetail/EditEmail.vue'),
          meta: { title: '邮箱' }
        },
        {
          path: '/editDetail/profile',
          name: 'profile',
          component: () => import('../views/user/editDetail/EditProfile.vue'),
          meta: { title: '简介' }
        },
        {
          path: '/editDetail/tags',
          name: 'tags',
          component: () => import('../views/user/editDetail/EditTags.vue'),
          meta: { title: '标签' }
        }
      ]
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/user/LoginPage.vue')
    },
    {
      path: '/teamOperation',
      name: 'teamOperation',
      component: () => import('../views/team/TeamOperationPage.vue')
    },
    {
      path: '/teamAdd',
      name: 'teamAdd',
      component: () => import('../views/team/TeamAddPage.vue')
    },
    {
      path: '/teamEdit/:id',
      name: 'teamEdit',
      component: () => import('../views/team/TeamEditPage.vue')
    }
  ]
})

router.beforeEach(async (to, from) => {
  const userStore = useUserStore()
  // 需要拦截的页面数组
  const interceptPages = [
    '/edit',
    '/editDetail',
    '/editDetail/username',
    '/editDetail/phone'
  ]
  // 如果用户未登录且访问的是拦截页面数组中的页面，则重定向到登录页面
  if (!userStore.isLogin && interceptPages.includes(to.path)) {
    showToast('请先登录')
    return '/login'
  }
})

export default router
