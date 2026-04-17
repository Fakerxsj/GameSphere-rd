import request from '@/utils/request';

export interface LoginParams {
  username: string;
  password: string;
}

export interface RegisterParams extends LoginParams {
  nickname?: string;
  email?: string;
}

export function login(data: LoginParams) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  });
}

export function register(data: RegisterParams) {
  return request({
    url: '/auth/register',
    method: 'post',
    data
  });
}

export function logout() {
  return request({
    url: '/auth/logout',
    method: 'get'
  });
}

export function getUserInfo() {
  return request({
    url: '/user/info',
    method: 'get'
  });
}