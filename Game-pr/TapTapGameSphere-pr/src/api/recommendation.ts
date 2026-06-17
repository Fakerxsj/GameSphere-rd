import request from '@/utils/request';

export function getHomeRecommendation() {
  return request({
    url: '/recommendation/home',
    method: 'get'
  });
}

export function triggerCrawl() {
  return request({
    url: '/recommendation/crawl',
    method: 'post'
  });
}

export function getCrawlStatus() {
  return request({
    url: '/recommendation/crawl/status',
    method: 'get'
  });
}

export function getSimilarGames(gameId: number) {
  return request({
    url: `/recommendation/similar/${gameId}`,
    method: 'get'
  });
}
