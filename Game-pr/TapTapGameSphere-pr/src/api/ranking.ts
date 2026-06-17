import request from '@/utils/request';

export function getHotRanking(params?: { limit?: number; offset?: number }) {
    return request({
        url: '/ranking/hot',
        method: 'get',
        params
    });
}

export function getRatingRanking(params?: { limit?: number; offset?: number }) {
    return request({
        url: '/ranking/rating',
        method: 'get',
        params
    });
}

export function getDownloadRanking(params?: { limit?: number; offset?: number }) {
    return request({
        url: '/ranking/download',
        method: 'get',
        params
    });
}

export function getFollowRanking(params?: { limit?: number; offset?: number }) {
    return request({
        url: '/ranking/follow',
        method: 'get',
        params
    });
}
