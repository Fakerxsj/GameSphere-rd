import request from '@/utils/request'
import type { GameBriefVO, GameForumVO, FourmSectionVO, FollowedAndHotData } from '@/Types/Forum'

export function getForumHomeData() {
    return request<FollowedAndHotData>({
        url: '/forum/home',
        method: 'get'
    })
}

export function getFollowedForums() {
    return request<GameBriefVO[]>({
        url: '/forum/followed',
        method: 'get'
    })
}

export function getHotForums(limit: number = 10) {
    return request<GameBriefVO[]>({
        url: '/forum/hot',
        method: 'get',
        params: { limit }
    })
}

export function getGameForumInfo(gameId: number) {
    return request<GameForumVO>({
        url: `/forum/game/${gameId}`,
        method: 'get'
    })
}

export function getForumSections(gameId: number) {
    return request<FourmSectionVO[]>({
        url: `/forum/game/${gameId}/sections`,
        method: 'get'
    })
}
