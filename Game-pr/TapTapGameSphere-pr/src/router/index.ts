import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import MainLayout from '@/components/Layout/MainLayout.vue'
import AdminLayout from '@/views/Admin.vue'
import Home from '@/views/Home.vue'
import Login from '@/views/Login.vue'
import Register from '@/views/Register.vue'
import GameDetail from '@/views/GameDetail.vue'
import Profile from '@/views/Profile.vue'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: Register,
    meta: { requiresAuth: false }
  },
  {
    path: '/admin',
    component: AdminLayout,
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      {
        path: '',
        redirect: '/admin/dashboard'
      },
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { title: '数据统计' }
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/UserList.vue'),
        meta: { title: '用户列表' }
      },
      {
        path: 'games',
        name: 'AdminGames',
        component: () => import('@/views/admin/GameList.vue'),
        meta: { title: '游戏列表' }
      },
      {
        path: 'posts',
        name: 'AdminPosts',
        component: () => import('@/views/admin/PostList.vue'),
        meta: { title: '帖子管理' }
      },
      {
        path: 'forums',
        name: 'AdminForums',
        component: () => import('@/views/admin/ForumStats.vue'),
        meta: { title: '论坛热度' }
      }
    ]
  },
  {
    path: '/discover',
    name: 'Discover',
    component: MainLayout,
    meta: { title: '发现游戏' },
    children: [
      {
        path: '',
        component: () => import('@/views/Discover.vue'),
      }
    ]
  },
  {
    path: '/category',
    name: 'CategoryHome',
    component: MainLayout,
    meta: { title: '游戏分类' },
    children: [
      {
        path: '',
        component: () => import('@/views/CategoryHome.vue'),
      }
    ]
  },
  {
    path: '/category/:id',
    name: 'CategoryDetail',
    component: MainLayout,
    meta: { title: '分类详情' },
    children: [
      {
        path: '',
        component: () => import('@/views/CategoryDetail.vue'),
      }
    ]
  },
  {
    path: '/forum',
    name: 'Forum',
    component: MainLayout,
    meta: { title: '游戏论坛' },
    children: [
      {
        path: '',
        name: 'ForumHome',
        component: () => import('@/views/ForumHome.vue')
      },
      {
        path: 'game/:id',
        name: 'GameForum',
        component: () => import('@/views/GameForum.vue')
      }
    ]
  },
  {
    path: '/',
    component: MainLayout,
    redirect: '/home',
    children: [
      {
        path: 'home',
        name: 'Home',
        component: Home,
        meta: { title: '首页' }
      },
      {
        path: 'game/:id',
        name: 'GameDetail',
        component: GameDetail,
        meta: { title: '游戏详情' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: Profile,
        meta: { title: '个人中心', requiresAuth: true }
      },
      {
        path: 'friend-center',
        name: 'FriendCenter',
        component: () => import('@/views/FriendCenter.vue'),
        meta: { title: '好友中心', requiresAuth: true }
      },
      {
        path: 'chat/:friendId',
        name: 'ChatDetail',
        component: () => import('@/views/ChatDetail.vue'),
        meta: { title: '聊天', requiresAuth: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const userInfoStr = localStorage.getItem('userInfo')
  let userInfo = null
  if (userInfoStr) {
    try {
      userInfo = JSON.parse(userInfoStr)
    } catch (e) {
      console.error('解析用户信息失败', e)
    }
  }

  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else if (to.meta.requiresAdmin) {
    if (!userInfo || userInfo.roleId !== 1) {
      next('/home')
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router
