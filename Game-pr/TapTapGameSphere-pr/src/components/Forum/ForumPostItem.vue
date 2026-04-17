<template>
  <div v-if="!post" class="post-loading">
    <el-icon class="is-loading"><Loading /></el-icon>
  </div>
  <div v-else class="forum-post-item" :class="{ 'is-top': post.isTop }">
    <div v-if="post.isTop" class="top-badge">
      <el-icon><Top /></el-icon>
      <span>置顶</span>
    </div>

    <div class="post-header">
      <img :src="post.userAvatar || defaultAvatar" class="user-avatar" @click="handleAvatarClick" />
      <div class="user-info">
        <span class="user-name">{{ post.userNickname }}</span>
        <span class="post-time">{{ formatTime(post.createTime) }}</span>
      </div>
    </div>

    <div class="post-content">
      <h3 class="post-title" v-if="post.title">{{ post.title }}</h3>
      <p class="post-text">{{ post.content }}</p>
      <div v-if="post.images && post.images.length > 0" class="post-images">
        <img
            v-for="(img, index) in post.images.slice(0, 4)"
            :key="index"
            :src="img"
            class="post-image"
            loading="lazy"
        />
      </div>
    </div>

    <div class="post-footer">
      <div class="action-buttons">
        <button class="action-btn" :class="{ liked: post.isLiked }" @click="handleLike">
          <el-icon><Star /></el-icon>
          <span>{{ post.likeCount || 0 }}</span>
        </button>
        <button class="action-btn" :class="{ active: isReplying }" @click="handleReply">
          <el-icon><ChatLineRound /></el-icon>
          <span>{{ post.replyCount || 0 }}</span>
        </button>
      </div>
    </div>

    <div v-if="isReplying" class="reply-editor-wrapper">
      <slot name="reply-editor"></slot>
    </div>

    <div v-if="post.replies && post.replies.length > 0" class="replies-section">
      <ReplyItem
          v-for="reply in post.replies"
          :key="reply.id"
          :reply="reply"
          @like="$emit('like', $event)"
          @reply="$emit('reply', $event)"
      />
    </div>
  </div>

  <FriendRequestDialog
      v-model:visible="showRequestDialog"
      :user="targetUser"
      @success="handleRequestSuccess"
  />
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { Top, Star, ChatLineRound, Loading } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import type { ForumPostVO } from '@/Types/Forum'
import ReplyItem from './ReplyItem.vue'
import FriendRequestDialog from '@/components/Friend/FriendRequestDialog.vue'
import { useUserStore } from '@/stores/modules/user'

const props = defineProps<{
  post?: ForumPostVO | null
  replyingTo?: number | null
}>()

const emit = defineEmits<{
  (e: 'like', commentId: number): void
  (e: 'reply', commentId: number): void
}>()

const userStore = useUserStore()
const defaultAvatar = 'https://via.placeholder.com/40x40?text=U'
const showRequestDialog = ref(false)
const targetUser = ref({
  userId: 0,
  nickname: '',
  avatar: ''
})

const isReplying = computed(() => props.post && props.replyingTo === props.post.id)

const formatTime = (time?: string) => {
  if (!time) return '未知时间'
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

const handleLike = () => {
  if (props.post) {
    emit('like', props.post.id)
  }
}

const handleReply = () => {
  if (props.post) {
    emit('reply', props.post.id)
  }
}

const handleAvatarClick = () => {
  if (!userStore.userInfo) {
    ElMessage.warning('请先登录')
    return
  }
  if (!props.post) return
  if (props.post.userId === userStore.userInfo.id) {
    ElMessage.warning('不能添加自己为好友')
    return
  }
  targetUser.value = {
    userId: props.post.userId,
    nickname: props.post.userNickname,
    avatar: props.post.userAvatar || defaultAvatar
  }
  showRequestDialog.value = true
}

const handleRequestSuccess = () => {
  showRequestDialog.value = false
  ElMessage.success('好友请求已发送')
}
</script>
