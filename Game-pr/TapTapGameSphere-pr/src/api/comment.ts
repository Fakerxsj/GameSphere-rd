import request from '@/utils/request';

export interface CommentParams {
  gameId: number;
  content: string;
  parentId?: number;
  images?: string;
}

export function getGameComments(gameId: number) {
  return request({
    url: `/comment/game/${gameId}`,
    method: 'get'
  });
}

export function submitComment(data: CommentParams) {
  return request({
    url: '/comment/submit',
    method: 'post',
    data
  });
}

export function deleteComment(id: number) {
  return request({
    url: `/comment/${id}`,
    method: 'delete'
  });
}