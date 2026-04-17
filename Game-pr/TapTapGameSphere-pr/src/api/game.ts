import request from '@/utils/request';

export interface GameSearchParams {
  keyword?: string;
  category?: string;
  pageNum?: number;
  pageSize?: number;
  sortBy?: string;
  sortOrder?: string;
}

export function getGameList(params: GameSearchParams) {
  return request({
    url: '/game/list',
    method: 'get',
    params
  });
}

export function getGameDetail(id: number) {
  return request({
    url: `/game/detail/${id}`,
    method: 'get'
  });
}

export function clickGame(id: number) {
  return request({
    url: `/game/click/${id}`,
    method: 'post'
  });
}

export function getRecommendGames(limit: number = 10, category?: string) {
  return request({
    url: '/game/recommend',
    method: 'get',
    params: { limit, category }
  });
}