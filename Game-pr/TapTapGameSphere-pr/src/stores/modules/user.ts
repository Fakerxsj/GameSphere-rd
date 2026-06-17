import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as apiLogin, logout as apiLogout, getUserInfo } from '@/api/auth'

export interface UserInfo {
  id: number
  username: string
  nickname: string
  avatar: string
  email?: string
  roleId?: number
}

interface LoginResponse {
  token: string
  userId: number
  username: string
  nickname: string
  avatar: string
  roleId: number
}

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('token') || '')
  const userInfo = ref<UserInfo | null>(null)

  const savedUserInfo = localStorage.getItem('userInfo')
  if (savedUserInfo) {
    try {
      userInfo.value = JSON.parse(savedUserInfo)
    } catch (e) {
      console.error('恢复用户信息失败', e)
    }
  }

  const isAdmin = computed(() => {
    return userInfo.value?.roleId === 1
  })

  const isNormalUser = computed(() => {
    return userInfo.value?.roleId === 2
  })

  const setToken = (newToken: string) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  const setUserInfo = (info: UserInfo | null) => {
    userInfo.value = info
    if (info) {
      localStorage.setItem('userInfo', JSON.stringify(info))
    } else {
      localStorage.removeItem('userInfo')
    }
  }

  const login = async (username: string, password: string) => {
    try {
      const loginData = await apiLogin({ username, password }) as unknown as LoginResponse

      if (loginData && loginData.token) {
        setToken(loginData.token)
        setUserInfo({
          id: loginData.userId,
          username: loginData.username,
          nickname: loginData.nickname || loginData.username,
          avatar: loginData.avatar || '',
          roleId: loginData.roleId
        })
      } else {
        throw new Error('登录失败：未获取到 token')
      }
    } catch (error) {
      return Promise.reject(error)
    }
  }

  const getInfo = async () => {
    try {
      const info = await getUserInfo() as unknown as UserInfo
      setUserInfo(info)
    } catch (error) {
      setToken('')
      setUserInfo(null)
      return Promise.reject(error)
    }
  }

  const logout = async () => {
    try {
      await apiLogout()
    } catch (e) {
      console.error(e)
    } finally {
      setToken('')
      setUserInfo(null)
    }
  }

  const updateAvatar = (avatarUrl: string) => {
    if (userInfo.value) {
      userInfo.value.avatar = avatarUrl
      setUserInfo(userInfo.value)
    }
  }

  return {
    token,
    userInfo,
    isAdmin,
    isNormalUser,
    login,
    getInfo,
    logout,
    setToken,
    setUserInfo,
    updateAvatar
  }
})
