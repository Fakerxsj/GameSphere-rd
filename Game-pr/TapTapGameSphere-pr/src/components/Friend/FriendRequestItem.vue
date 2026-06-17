<template>
  <div class="friend-request-item">
    <el-avatar :size="48" :src="request.fromUserAvatar || defaultAvatar" />
    <div class="request-info">
      <div class="header">
        <span class="nickname">{{ request.fromUserNickname }}</span>
        <span class="time">{{ formatTime(request.createTime) }}</span>
      </div>
      <div v-if="request.message" class="message">{{ request.message }}</div>
      <div class="actions" v-if="request.status === 0">
        <el-button type="primary" size="small" @click="handleAccept">同意</el-button>
        <el-button size="small" @click="handleReject">拒绝</el-button>
      </div>
      <div v-else class="status-text">
        {{ request.status === 1 ? '已同意' : '已拒绝' }}
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus'
import type { FriendRequestVO } from '@/Types/Friend'
import { useFriendStore } from '@/stores/modules/friend'

const props = defineProps<{
  request: FriendRequestVO
}>()

const emit = defineEmits<{
  (e: 'handled'): void
}>()

const friendStore = useFriendStore()
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const formatTime = (time: string) => {
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const hours = Math.floor(diff / (1000 * 60 * 60))

  if (hours < 1) {
    const minutes = Math.floor(diff / (1000 * 60))
    return minutes < 1 ? '刚刚' : `${minutes}分钟前`
  } else if (hours < 24) {
    return `${hours}小时前`
  } else {
    const days = Math.floor(hours / 24)
    return `${days}天前`
  }
}

const handleAccept = async () => {
  try {
    await friendStore.handleRequest(props.request.id, 1)
    ElMessage.success('已同意好友申请')
    emit('handled')
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  }
}

const handleReject = async () => {
  try {
    await friendStore.handleRequest(props.request.id, 2)
    ElMessage.success('已拒绝好友申请')
    emit('handled')
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  }
}
</script>

<style scoped lang="scss">
.friend-request-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px;
  border-bottom: 1px solid #2a475e;

  .request-info {
    flex: 1;

    .header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 6px;

      .nickname {
        font-size: 15px;
        color: #c7d5e0;
        font-weight: 600;
      }

      .time {
        font-size: 12px;
        color: #8f98a0;
      }
    }

    .message {
      font-size: 13px;
      color: #8f98a0;
      margin-bottom: 12px;
      padding: 8px;
      background: #1b2838;
      border-radius: 4px;
    }

    .actions {
      display: flex;
      gap: 8px;
    }

    .status-text {
      font-size: 13px;
      color: #8f98a0;
    }
  }
}
</style>
