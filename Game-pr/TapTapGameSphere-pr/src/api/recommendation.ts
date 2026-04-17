import request from '@/utils/request';

export function getHomeRecommendation() {
  return request({
    url: '/recommendation/home',
    method: 'get'
  });
}

export function getSimilarGames(gameId: number) {
  return request({
    url: `/recommendation/similar/${gameId}`,
    method: 'get'
  });
}