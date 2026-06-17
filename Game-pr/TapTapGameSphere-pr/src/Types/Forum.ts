
export interface GameBriefVO {
    id: number
    name: string
    coverImage: string
    bannerImage?: string
    commentCount: number
    followCount: number
    ratingScore?: number
    developer?: string
}

export interface FourmSectionVO {
    id: number
    gameId: number
    name: string
    icon?: string
    postCount: number
    orderNum: number
}

export interface ForumPostVO {
    id: number
    gameId: number
    userId: number
    userNickname: string
    userAvatar: string
    title?: string
    content: string
    images: string[]
    likeCount: number
    replyCount: number
    viewCount?: number
    isTop: boolean
    isEssence?: boolean
    isOfficial?: boolean
    isLiked: boolean
    sectionName?: string
    tags?: string[]
    createTime: string
    updateTime?: string
}

export interface CommentVO {
    id: number
    userId: number
    userNickname: string
    userAvatar: string
    gameId: number
    content: string
    images: string[]
    likeCount: number
    replyCount: number
    isLiked: boolean
    createTime: string
    replies?: CommentVO[]
}

export interface GameForumVO {
    gameId: number
    gameName: string
    coverImage: string
    backgroundImage?: string
    followCount: number
    postCount: number
    isFollowed: boolean
    sections: FourmSectionVO[]
}

export interface FollowedAndHotData {
    followedForums: GameBriefVO[]
    hotForums: GameBriefVO[]
}

export interface CommentRequest {
    gameId: number
    content: string
    parentId?: number
    images?: string
}
