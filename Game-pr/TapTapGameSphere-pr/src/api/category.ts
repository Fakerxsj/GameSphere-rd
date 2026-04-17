import request from '@/utils/request';

export function getCategoryList() {
  return request({
    url: '/category/list',
    method: 'get'
  });
}

export function getChildCategories(parentId: number) {
  return request({
    url: `/category/children/${parentId}`,
    method: 'get'
  });
}