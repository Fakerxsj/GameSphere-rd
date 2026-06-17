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

export function uploadAvatar(file: File) {
  const formData = new FormData();
  formData.append('file', file);
  return request({
    url: '/file/avatar',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
}
