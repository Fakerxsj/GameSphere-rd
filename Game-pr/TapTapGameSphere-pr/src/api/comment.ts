import request from '@/utils/request';
import type { CommentRequest, CommentVO } from '@/Types/Forum';

export function getGameComments(gameId: number) {
  return request<CommentVO[]>({
    url: `/comment/game/${gameId}`,
    method: 'get'
  });
}

export function submitComment(data: CommentRequest) {
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

export function likeComment(id: number) {
  return request({
    url: `/comment/like/${id}`,
    method: 'post'
  });
}

export function unlikeComment(id: number) {
  return request({
    url: `/comment/like/${id}`,
    method: 'delete'
  });
}

export function getCommentReplies(parentId: number) {
  return request<CommentVO[]>({
    url: `/comment/${parentId}/replies`,
    method: 'get'
  });
}
