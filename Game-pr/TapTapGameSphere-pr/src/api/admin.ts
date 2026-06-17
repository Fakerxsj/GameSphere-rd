import request from '@/utils/request';

export function getAdminStats() {
    return request({
        url: '/admin/stats',
        method: 'get'
    });
}

export function getAdminPosts(params?: {
    pageNum?: number;
    pageSize?: number;
    gameId?: number | null;
    keyword?: string;
}) {
    return request({
        url: '/admin/posts',
        method: 'get',
        params
    });
}

export function deleteAdminPost(id: number) {
    return request({
        url: `/admin/posts/${id}`,
        method: 'delete'
    });
}

export function getForumStats() {
    return request({
        url: '/admin/forum-stats',
        method: 'get'
    });
}
