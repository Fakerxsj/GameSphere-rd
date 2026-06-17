
import request from '@/utils/request';

export function crawlPopularGames() {
    return request({
        url: '/crawler/taptap/popular',
        method: 'post'
    });
}

export function crawlFromTapTap(url: string) {
    return request({
        url: '/crawler/taptap',
        method: 'post',
        params: { url }
    });
}

export function getCrawlerStatus() {
    return request({
        url: '/crawler/status',
        method: 'get'
    });
}
