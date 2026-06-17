export interface FriendVO {
    userId: number
    nickname: string
    avatar: string
    remark?: string
    lastMessageTime?: string
    lastMessage?: string
    unreadCount: number
    createTime: string
}

export interface FriendRequestVO {
    id: number
    fromUserId: number
    fromUserNickname: string
    fromUserAvatar: string
    message?: string
    status: number
    sourceCommentId?: number
    createTime: string
}

export interface MessageVO {
    id: number
    fromUserId: number
    fromUserNickname: string
    fromUserAvatar: string
    toUserId: number
    content: string
    messageType: number
    isRead: number
    createTime: string
}

export interface ConversationVO {
    userId: number
    nickname: string
    avatar: string
    lastMessage: string
    lastMessageTime: string
    unreadCount: number
}

export interface SendFriendRequestDTO {
    toUserId: number
    message?: string
    sourceCommentId?: number
}

export interface HandleFriendRequestDTO {
    requestId: number
    status: number
}

export interface SendMessageDTO {
    toUserId: number
    content: string
    messageType?: number
}

export interface UpdateRemarkDTO {
    remark: string
}
