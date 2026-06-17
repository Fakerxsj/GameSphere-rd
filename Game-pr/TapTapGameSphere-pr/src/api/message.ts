import request from '@/utils/request'
import type { SendMessageDTO, ConversationVO, MessageVO } from '@/Types/Friend'

export const sendMessage = (data: SendMessageDTO) => {
    return request.post('/message/send', data)
}

export const getConversations = () => {
    return request.get<ConversationVO[]>('/message/conversations')
}

export const getMessageHistory = (friendId: number, params: { pageNum: number; pageSize: number }) => {
    return request.get<MessageVO[]>(`/message/history/${friendId}`, { params })
}

export const markAsRead = (friendId: number) => {
    return request.put(`/message/read/${friendId}`)
}

export const getUnreadCount = () => {
    return request.get<number>('/message/unread/count')
}
