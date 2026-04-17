import request from '@/utils/request';

export function submitRating(gameId: number, score: number, content?: string) {
  return request({
    url: '/rating/submit',
    method: 'post',
    params: { gameId, score, content }
  });
}

export function getGameRatings(gameId: number) {
  return request({
    url: `/rating/game/${gameId}`,
    method: 'get'
  });
}