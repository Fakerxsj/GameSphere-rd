<template>
  <div class="chat-detail">
    <div class="chat-header">
      <el-button text @click="handleBack">
        <el-icon><ArrowLeft /></el-icon>
        返回
      </el-button>
      <div class="user-info">
        <el-avatar :size="40" :src="userInfo?.avatar || defaultAvatar" />
        <div class="user-detail">
          <div class="nickname">{{ userInfo?.nickname || '未知用户' }}</div>
          <div class="status">在线</div>
        </div>
      </div>
    </div>

    <div class="message-list" ref="messageListRef">
      <div
          v-for="message in messageStore.currentChatMessages"
          :key="message.id"
          :class="['message-item', { 'is-self': message.fromUserId === userStore.userInfo?.id }]"
      >
        <el-avatar
            v-if="!isSelf(message)"
            :size="36"
            :src="message.fromUserAvatar || userInfo?.avatar || defaultAvatar"
        />
        <div class="message-content">
          <!-- 图片消息 -->
          <div v-if="message.messageType === 2 && message.content" class="image-message">
            <img :src="message.content" alt="聊天图片" @click="previewImage(message.content)" />
          </div>
          <!-- 文字消息 -->
          <div v-else class="bubble">{{ message.content }}</div>
          <div class="time">{{ formatTime(message.createTime) }}</div>
        </div>
        <el-avatar
            v-if="isSelf(message)"
            :size="36"
            :src="userStore.userInfo?.avatar || defaultAvatar"
        />
      </div>
      <EmptyState v-if="messageStore.currentChatMessages.length === 0" description="暂无聊天记录" />
    </div>

    <MessageInput @send="handleSendMessage" @sendImage="handleSendImage" :loading="sending" />

    <!-- 图片预览对话框 -->
    <el-dialog v-model="imagePreviewVisible" width="600px" :show-close="true" class="image-preview-dialog">
      <img :src="previewImageUrl" style="width: 100%; border-radius: 8px" />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/modules/user'
import { useFriendStore } from '@/stores/modules/friend'
import { useMessageStore } from '@/stores/modules/message'
import MessageInput from '@/components/Friend/MessageInput.vue'
import EmptyState from '@/components/Common/EmptyState.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const friendStore = useFriendStore()
const messageStore = useMessageStore()

const friendId = Number(route.params.friendId)
const messageListRef = ref<HTMLElement | null>(null)
const sending = ref(false)
const userInfo = ref<any>(null)
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
const imagePreviewVisible = ref(false)
const previewImageUrl = ref('')

const isSelf = (message: any) => {
  return message.fromUserId === userStore.userInfo?.id
}

const formatTime = (time: string) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

const handleBack = () => {
  router.push('/friend-center')
}

const loadUserInfo = () => {
  const friend = friendStore.friendList.find(f => f.userId === friendId)
  if (friend) {
    userInfo.value = { nickname: friend.remark || friend.nickname, avatar: friend.avatar }
    return
  }
  const conversation = messageStore.conversations.find(c => c.userId === friendId)
  if (conversation) {
    userInfo.value = { nickname: conversation.nickname, avatar: conversation.avatar }
    return
  }
  userInfo.value = { nickname: '用户' + friendId, avatar: defaultAvatar }
}

const handleSendMessage = async (content: string) => {
  if (!content.trim()) return
  sending.value = true
  try {
    const tempMsg = {
      id: Date.now(),
      fromUserId: userStore.userInfo?.id,
      toUserId: friendId,
      content: content,
      messageType: 1,
      createTime: new Date().toISOString(),
      isRead: 1
    } as any
    messageStore.currentChatMessages.push(tempMsg)
    await scrollToBottom()

    messageStore.sendMessage(friendId, content)
  } catch (error) {
    console.error('发送消息失败', error)
  } finally {
    sending.value = false
  }
}

const handleSendImage = async (imageUrl: string) => {
  sending.value = true
  try {
    const tempMsg = {
      id: Date.now(),
      fromUserId: userStore.userInfo?.id,
      toUserId: friendId,
      content: imageUrl,
      messageType: 2,
      createTime: new Date().toISOString(),
      isRead: 1
    } as any
    messageStore.currentChatMessages.push(tempMsg)
    await scrollToBottom()

    // 通过 WebSocket 发送图片消息
    messageStore.sendMessage(friendId, imageUrl, 2)
  } catch (error) {
    console.error('发送图片失败', error)
  } finally {
    sending.value = false
  }
}

const previewImage = (url: string) => {
  previewImageUrl.value = url
  imagePreviewVisible.value = true
}

const scrollToBottom = async () => {
  await nextTick()
  if (messageListRef.value) {
    messageListRef.value.scrollTop = messageListRef.value.scrollHeight
  }
}

watch(() => messageStore.currentChatMessages.length, () => {
  scrollToBottom()
})

onMounted(async () => {
  console.log('💬 聊天页面加载, friendId:', friendId)

  if (!messageStore.isConnected && userStore.userInfo?.id) {
    console.log('🔌 重新连接 WebSocket...')
    messageStore.connectWebSocket(userStore.userInfo.id)
  }

  await friendStore.fetchFriendList()
  await messageStore.fetchMessageHistory(friendId, 1, 20)
  await messageStore.markMessagesRead(friendId)
  loadUserInfo()
  await scrollToBottom()
})

onUnmounted(() => {
  // 离开聊天页面时不要断开 WebSocket
})
</script>

<style scoped lang="scss">
.chat-detail {
  max-width: 900px;
  margin: 0 auto;
  height: calc(100vh - 120px);
  display: flex;
  flex-direction: column;
  background: #1b2838;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);

  .chat-header {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 16px 20px;
    background: #171a21;
    border-bottom: 1px solid #2a475e;

    .user-info {
      display: flex;
      align-items: center;
      gap: 12px;
      flex: 1;

      .user-detail {
        .nickname {
          font-size: 16px;
          color: #c7d5e0;
          font-weight: 600;
        }

        .status {
          font-size: 12px;
          color: #4ade80;
        }
      }
    }
  }

  .message-list {
    flex: 1;
    overflow-y: auto;
    padding: 20px;

    .message-item {
      display: flex;
      gap: 12px;
      margin-bottom: 16px;

      &.is-self {
        flex-direction: row-reverse;

        .message-content {
          align-items: flex-end;

          .bubble {
            background: #66c0f4;
            color: #1b2838;
          }
        }
      }

      .message-content {
        display: flex;
        flex-direction: column;
        max-width: 60%;

        .image-message {
          img {
            max-width: 280px;
            max-height: 200px;
            border-radius: 8px;
            object-fit: cover;
            cursor: pointer;
            transition: transform 0.2s;

            &:hover {
              transform: scale(1.02);
            }
          }
        }

        .bubble {
          padding: 10px 14px;
          background: #2a475e;
          color: #c7d5e0;
          border-radius: 8px;
          font-size: 14px;
          line-height: 1.6;
          word-break: break-word;
        }

        .time {
          font-size: 11px;
          color: #8f98a0;
          margin-top: 4px;
        }
      }
    }
  }
}
</style>
