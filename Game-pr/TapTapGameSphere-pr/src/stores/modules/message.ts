import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getConversations, getMessageHistory, markAsRead, getUnreadCount } from '@/api/message'
import type { ConversationVO, MessageVO } from '@/Types/Friend'
import { useUserStore } from '@/stores/modules/user'

export const useMessageStore = defineStore('message', () => {
    const conversations = ref<ConversationVO[]>([])
    const currentChatMessages = ref<MessageVO[]>([])
    const totalUnreadCount = ref(0)
    const ws = ref<WebSocket | null>(null)
    const isConnected = ref(false)
    const userStore = useUserStore()

    const fetchConversations = async () => {
        try {
            const list = await getConversations()
            conversations.value = Array.isArray(list) ? list : (list?.data || [])
        } catch (error) {
            console.error('获取会话列表失败', error)
            conversations.value = []
        }
    }

    const fetchMessageHistory = async (friendId: number, pageNum = 1, pageSize = 20) => {
        try {
            const res = await getMessageHistory(friendId, { pageNum, pageSize })
            const list = Array.isArray(res) ? res : (res?.data || [])
            currentChatMessages.value = list.sort((a: any, b: any) => {
                return new Date(a.createTime).getTime() - new Date(b.createTime).getTime()
            })
        } catch (error) {
            console.error('获取聊天记录失败', error)
            currentChatMessages.value = []
        }
    }

    const markMessagesRead = async (friendId: number) => {
        try {
            await markAsRead(friendId)
            await fetchConversations()
        } catch (error) {
            console.error('标记已读失败', error)
        }
    }

    const fetchUnreadCount = async () => {
        try {
            const count = await getUnreadCount()
            totalUnreadCount.value = typeof count === 'number' ? count : (count?.data || 0)
        } catch (error) {
            console.error('获取未读数失败', error)
            totalUnreadCount.value = 0
        }
    }

    const connectWebSocket = (userId: number) => {
        if (ws.value) ws.value.close()

        const wsUrl = `ws://localhost:8082/api/ws/chat?userId=${userId}`
        console.log('尝试连接 WebSocket:', wsUrl)
        ws.value = new WebSocket(wsUrl)

        ws.value.onopen = () => {
            isConnected.value = true
            console.log('WebSocket 连接成功')
        }

        ws.value.onmessage = (event) => {
            try {
                const data = JSON.parse(event.data)
                console.log('📥 WebSocket 收到数据:', data)

                // 1. 后端 ack（包含 status 和 messageId）→ 替换临时消息
                if (data.status === 'success' && data.messageId) {
                    const tempMsgIndex = currentChatMessages.value.findIndex(
                        m => m.content && m.id === data.messageId + 1000000000000
                    )
                    if (tempMsgIndex !== -1) {
                        // 用真实 ID 替换临时 ID
                        currentChatMessages.value[tempMsgIndex].id = data.messageId
                        console.log('✅ 临时消息已替换为真实消息，ID:', data.messageId)
                    }
                    return
                }

                // 2. 对方发的新消息（包含 content 和 fromUserId）
                if (data.content && data.fromUserId) {
                    const exists = currentChatMessages.value.some(m => m.id === data.id)
                    if (!exists) {
                        const newMsg: any = {
                            id: data.id,
                            fromUserId: data.fromUserId,
                            toUserId: data.toUserId,
                            content: data.content,
                            messageType: data.messageType || 1,
                            createTime: new Date(data.createTime).toISOString(),
                            isRead: 1
                        }
                        currentChatMessages.value.push(newMsg)
                        console.log('✅ 收到对方新消息')
                    }
                }
                fetchConversations()
            } catch (error) {
                console.error('解析消息失败', error)
            }
        }

        ws.value.onerror = (error) => {
            console.error('WebSocket 错误:', error)
            isConnected.value = false
        }

        ws.value.onclose = () => {
            console.log('WebSocket 连接关闭')
            isConnected.value = false
        }
    }

    const sendMessage = (toUserId: number, content: string, messageType = 1) => {
        if (ws.value && isConnected.value) {
            ws.value.send(JSON.stringify({ toUserId, content, messageType }))
        }
    }

    const disconnectWebSocket = () => {
        if (ws.value) {
            ws.value.close()
            ws.value = null
        }
    }

    return {
        conversations,
        currentChatMessages,
        totalUnreadCount,
        isConnected,
        fetchConversations,
        fetchMessageHistory,
        markMessagesRead,
        fetchUnreadCount,
        connectWebSocket,
        sendMessage,
        disconnectWebSocket
    }
})
