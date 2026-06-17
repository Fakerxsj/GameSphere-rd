<template>
  <div class="reply-item" :class="{ 'is-deep': level > 1 }">
    <div class="avatar-wrapper" @click="handleAvatarClick">
      <img :src="reply.userAvatar || defaultAvatar" class="reply-avatar" />
    </div>
    <div class="reply-content">
      <div class="reply-header">
        <span class="reply-user">{{ reply.userNickname }}</span>
        <span class="reply-time">{{ formatTime(reply.createTime) }}</span>
      </div>

      <div class="reply-text">{{ reply.content }}</div>

      <div v-if="reply.images && reply.images.length > 0" class="reply-images">
        <img
            v-for="(img, index) in reply.images"
            :key="index"
            :src="img"
            class="reply-image"
            loading="lazy"
        />
      </div>

      <div class="reply-footer">
        <button class="reply-action" :class="{ liked: reply.isLiked }" @click="handleLike">
          <el-icon><Star /></el-icon>
          <span>{{ reply.likeCount || 0 }}</span>
        </button>
        <button class="reply-action" @click="handleReply">
          <el-icon><ChatLineRound /></el-icon>
          <span>回复</span>
        </button>
        <button class="reply-action" @click="handleAddFriend">
          <el-icon><UserFilled /></el-icon>
          <span>加好友</span>
        </button>
      </div>

      <div v-if="isReplying" class="reply-editor-inline">
        <slot name="reply-editor"></slot>
      </div>

      <div v-if="reply.replies && reply.replies.length > 0" class="reply-children">
        <ReplyItem
            v-for="child in reply.replies"
            :key="child.id"
            :reply="child"
            :level="level + 1"
            :replying-to="replyingTo"
            @like="$emit('like', $event)"
            @reply="$emit('reply', $event)"
        >
          <template #reply-editor>
            <slot name="reply-editor"></slot>
          </template>
        </ReplyItem>
      </div>
    </div>

    <FriendRequestDialog
        v-model="showRequestDialog"
        :userInfo="targetUser"
        :sourceCommentId="reply.id"
        @success="handleRequestSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Star, ChatLineRound, UserFilled } from '@element-plus/icons-vue'
import type { CommentVO } from '@/Types/Forum'
import FriendRequestDialog from '@/components/Friend/FriendRequestDialog.vue'
import { useUserStore } from '@/stores/modules/user'

const props = withDefaults(defineProps<{
  reply: CommentVO
  level?: number
  replyingTo?: number | null
}>(), {
  level: 1,
  replyingTo: null
})

const emit = defineEmits<{
  (e: 'like', commentId: number): void
  (e: 'reply', commentId: number): void
}>()

const userStore = useUserStore()
const defaultAvatar = 'https://via.placeholder.com/32x32?text=U'
const showRequestDialog = ref(false)
const targetUser = ref({
  userId: 0,
  nickname: '',
  avatar: ''
})

const isReplying = computed(() => props.replyingTo === props.reply.id)

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

const handleLike = () => {
  emit('like', props.reply.id)
}

const handleReply = () => {
  console.log('🔔 ReplyItem 回复按钮被点击，ID:', props.reply.id)
  emit('reply', props.reply.id)
}

// ... existing code ...

const handleAvatarClick = () => {
  console.log('💬 回复头像点击调试信息:')
  console.log('  userStore.userInfo:', userStore.userInfo)
  console.log('  props.reply:', props.reply)
  console.log('  props.reply?.userId:', props.reply?.userId, typeof props.reply?.userId)
  console.log('  userStore.userInfo?.id:', userStore.userInfo?.id, typeof userStore.userInfo?.id)

  if (!userStore.userInfo) {
    console.log('⚠️ 用户未登录')
    ElMessage.warning('请先登录')
    return
  }
  if (!props.reply) {
    console.log('⚠️ reply 为空')
    return
  }

  const replyUserId = Number(props.reply.userId)
  const currentUserId = Number(userStore.userInfo.id)

  console.log('  比较结果:', replyUserId === currentUserId)

  if (replyUserId === currentUserId) {
    console.log('⚠️ 点击了自己的头像')
    ElMessage.warning('不能添加自己为好友')
    return
  }

  console.log('✅ 准备打开加好友对话框')
  targetUser.value = {
    userId: replyUserId,
    nickname: props.reply.userNickname,
    avatar: props.reply.userAvatar || defaultAvatar
  }
  showRequestDialog.value = true
  console.log('  showRequestDialog:', showRequestDialog.value)
}

// ... existing code ...


const handleAddFriend = () => {
  handleAvatarClick()
}

const handleRequestSuccess = () => {
  ElMessage.success('好友申请已发送')
}
</script>

<style scoped lang="scss">
.reply-item {
  display: flex;
  gap: 10px;
  padding: 12px 0;
  border-bottom: 1px solid #2a475e;

  &.is-deep {
    margin-left: 42px;
    padding-left: 12px;
    border-left: 2px solid #2a475e;
  }

  &:last-child {
    border-bottom: none;
  }

  .avatar-wrapper {
    cursor: pointer;
    transition: transform 0.2s;

    &:hover {
      transform: scale(1.1);
    }

    .reply-avatar {
      width: 32px;
      height: 32px;
      border-radius: 50%;
      object-fit: cover;
      flex-shrink: 0;
    }
  }

  .reply-content {
    flex: 1;
    min-width: 0;

    .reply-header {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 6px;

      .reply-user {
        font-size: 13px;
        color: #c7d5e0;
        font-weight: 600;
      }

      .reply-time {
        font-size: 11px;
        color: #8f98a0;
      }
    }

    .reply-text {
      font-size: 13px;
      color: #8f98a0;
      line-height: 1.6;
      margin-bottom: 8px;
    }

    .reply-images {
      display: flex;
      gap: 6px;
      margin-bottom: 8px;

      .reply-image {
        width: 80px;
        height: 80px;
        object-fit: cover;
        border-radius: 4px;
        cursor: pointer;
        transition: transform 0.2s;

        &:hover {
          transform: scale(1.05);
        }
      }
    }

    .reply-footer {
      display: flex;
      gap: 16px;

      .reply-action {
        display: flex;
        align-items: center;
        gap: 4px;
        padding: 4px 8px;
        background: transparent;
        border: none;
        color: #8f98a0;
        font-size: 12px;
        cursor: pointer;
        border-radius: 4px;
        transition: all 0.2s;

        .el-icon {
          font-size: 14px;
        }

        &:hover {
          background: #2a475e;
          color: #c7d5e0;
        }

        &.liked {
          color: #66c0f4;

          .el-icon {
            color: #66c0f4;
          }
        }
      }
    }

    .reply-editor-inline {
      margin-top: 12px;
      padding: 12px;
      background: #16202d;
      border-radius: 6px;
      border: 1px solid #2a475e;
    }

    .reply-children {
      margin-top: 12px;
      padding-left: 12px;
      border-left: 2px solid #2a475e;
    }
  }
}
</style>
