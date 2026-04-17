import request from '@/utils/request';

// 获取当前用户信息 (GET /user/info)
export function getUserInfo() {
  return request({
    url: '/user/info',
    method: 'get'
  });
}

// 更新用户信息 (PUT /user/info)
export function updateUserInfo(data) {
  return request({
    url: '/user/info',
    method: 'put',
    data
  });
}