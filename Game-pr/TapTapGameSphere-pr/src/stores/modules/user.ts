import { defineStore } from 'pinia';
import { ref } from 'vue';
import { login as apiLogin, logout as apiLogout, getUserInfo } from '@/api/auth';

export interface UserInfo {
  id: number;
  username: string;
  nickname: string;
  avatar: string;
  email?: string;
  role?: number;
}

// 假设登录接口返回的数据结构
interface LoginResponse {
  token: string;
}

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('token') || '');
  const userInfo = ref<UserInfo | null>(null);

  const setToken = (newToken: string) => {
    token.value = newToken;
    localStorage.setItem('token', newToken);
  };

  const login = async (username: string, password: string) => {
    try {
      // 修复：Axios 返回的是 AxiosResponse，实际数据在 .data 中
      const response = await apiLogin({ username, password });
      // 假设 apiLogin 返回的是 AxiosResponse<LoginResponse>
      // 如果 apiLogin 已经经过拦截器处理直接返回 data，则无需 .data
      // 这里按照标准 Axios 行为处理，即 response.data 才是后端返回的业务数据
      const loginData = response.data as LoginResponse; 
      
      if (loginData && loginData.token) {
        setToken(loginData.token);
        await getInfo();
      } else {
        throw new Error('登录失败：未获取到 token');
      }
    } catch (error) {
      return Promise.reject(error);
    }
  };

  const getInfo = async () => {
    try {
      const response = await getUserInfo();
      // 同样处理 getUserInfo 的返回值
      userInfo.value = response.data as UserInfo;
    } catch (error) {
      return Promise.reject(error);
    }
  };

  const logout = async () => {
    try {
      await apiLogout();
    } catch (e) {
      console.error(e);
    } finally {
      token.value = '';
      userInfo.value = null;
      localStorage.removeItem('token');
    }
  };

  return {
    token,
    userInfo,
    login,
    getInfo,
    logout,
    setToken
  };
});