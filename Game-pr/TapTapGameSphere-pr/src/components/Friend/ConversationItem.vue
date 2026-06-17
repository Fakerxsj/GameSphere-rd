<template>
  <div class="conversation-item" @click="handleClick">
    <el-badge :value="conversation.unreadCount" :hidden="conversation.unreadCount === 0" :max="99">
      <el-avatar :size="48" :src="conversation.avatar || defaultAvatar" />
    </el-badge>
    <div class="conversation-info">
      <div class="conversation-header">
        <span class="nickname">{{ conversation.nickname }}</span>
        <span class="time">{{ formatTime(conversation.lastMessageTime) }}</span>
      </div>
      <div class="last-message">{{ conversation.lastMessage }}</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { ConversationVO } from '@/Types/Friend'

const props = defineProps<{
  conversation: ConversationVO
}>()

const emit = defineEmits<{
  (e: 'click', userId: number): void
}>()

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const formatTime = (time: string) => {
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))

  if (days === 0) {
    return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  } else if (days === 1) {
    return '昨天'
  } else if (days < 7) {
    return `${days}天前`
  } else {
    return date.toLocaleDateString('zh-CN', { month: '2-digit', day: '2-digit' })
  }
}

const handleClick = () => {
  emit('click', props.conversation.userId)
}
</script>

<style scoped lang="scss">
.conversation-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  cursor: pointer;
  transition: background 0.2s;

  &:hover {
    background: #2a475e;
  }

  .conversation-info {
    flex: 1;
    min-width: 0;

    .conversation-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 4px;

      .nickname {
        font-size: 15px;
        color: #c7d5e0;
        font-weight: 500;
      }

      .time {
        font-size: 12px;
        color: #8f98a0;
      }
    }

    .last-message {
      font-size: 13px;
      color: #8f98a0;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
  }
}
</style>
