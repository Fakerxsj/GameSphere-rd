import request from '@/utils/request'
import type {
    SendFriendRequestDTO,
    HandleFriendRequestDTO,
    FriendVO,
    FriendRequestVO,
    UpdateRemarkDTO
} from '@/Types/Friend'

export const sendFriendRequest = (data: SendFriendRequestDTO) => {
    return request.post('/friend/request', data)
}

export const handleFriendRequest = (data: HandleFriendRequestDTO) => {
    return request.post('/friend/request/handle', data)
}

export const getFriendRequests = () => {
    return request.get<FriendRequestVO[]>('/friend/requests')
}

export const getFriendList = () => {
    return request.get<FriendVO[]>('/friend/list')
}

export const removeFriend = (friendId: number) => {
    return request.delete(`/friend/${friendId}`)
}

export const updateRemark = (friendId: number, data: UpdateRemarkDTO) => {
    return request.put(`/friend/${friendId}/remark`, data)
}
