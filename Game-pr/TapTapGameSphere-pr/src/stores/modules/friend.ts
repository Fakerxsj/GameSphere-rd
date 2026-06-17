import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getFriendRequests, getFriendList, handleFriendRequest, removeFriend, updateRemark } from '@/api/friend'
import type { FriendVO, FriendRequestVO } from '@/Types/Friend'

export const useFriendStore = defineStore('friend', () => {
    const friendList = ref<FriendVO[]>([])
    const friendRequests = ref<FriendRequestVO[]>([])
    const unreadRequestCount = ref(0)

    const fetchFriendRequests = async () => {
        try {
            const requests = await getFriendRequests()
            friendRequests.value = Array.isArray(requests) ? requests : []
            unreadRequestCount.value = friendRequests.value.length
        } catch (error) {
            console.error('获取好友申请失败', error)
            friendRequests.value = []
            unreadRequestCount.value = 0
        }
    }

    const fetchFriendList = async () => {
        try {
            const friends = await getFriendList()
            friendList.value = Array.isArray(friends) ? friends : []
        } catch (error) {
            console.error('获取好友列表失败', error)
            friendList.value = []
        }
    }

    const handleRequest = async (requestId: number, status: number) => {
        try {
            await handleFriendRequest({ requestId, status })
            await fetchFriendRequests()
            if (status === 1) {
                await fetchFriendList()
            }
        } catch (error) {
            throw error
        }
    }

    const deleteFriend = async (friendId: number) => {
        try {
            await removeFriend(friendId)
            await fetchFriendList()
        } catch (error) {
            throw error
        }
    }

    const updateFriendRemark = async (friendId: number, remark: string) => {
        try {
            await updateRemark(friendId, { remark })
            await fetchFriendList()
        } catch (error) {
            throw error
        }
    }

    return {
        friendList,
        friendRequests,
        unreadRequestCount,
        fetchFriendRequests,
        fetchFriendList,
        handleRequest,
        deleteFriend,
        updateFriendRemark
    }
})
