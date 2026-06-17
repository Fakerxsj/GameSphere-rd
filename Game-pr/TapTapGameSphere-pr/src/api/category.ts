import request from '@/utils/request';

export function getCategoryHome() {
  return request({
    url: '/category/home',
    method: 'get'
  });
}

export function getCategoryGames(id: number, params?: { limit?: number; offset?: number }) {
  return request({
    url: `/category/${id}`,
    method: 'get',
    params
  });
}

// 恢复 Sidebar 所需的分类列表接口
export function getCategoryList() {
  return request({
    url: '/category/list',
    method: 'get'
  });
}
