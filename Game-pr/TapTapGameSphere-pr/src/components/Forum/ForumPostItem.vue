<template>
  <div v-if="post" class="forum-post-item" :class="{ 'is-top': post.isTop }">
    <div v-if="post.isTop" class="top-badge">
      <el-icon><Top /></el-icon>
      置顶
    </div>

    <div class="post-header">
      <img
          :src="post.userAvatar || defaultAvatar"
          :alt="post.userNickname"
          class="user-avatar"
          @click="handleAvatarClick"
      />
      <div class="user-info">
        <span class="user-name">{{ post.userNickname }}</span>
        <span class="post-time">{{ formatTime(post.createTime) }}</span>
      </div>
    </div>

    <div class="post-content">
      <p class="post-text">{{ post.content }}</p>
      <div v-if="post.images && post.images.length > 0" class="post-images">
        <img
            v-for="(img, index) in post.images"
            :key="index"
            :src="img"
            :alt="`图片${index + 1}`"
            class="post-image"
        />
      </div>
    </div>

    <div class="post-footer">
      <div class="action-buttons">
        <button class="action-btn" :class="{ active: post.isLiked }" @click="handleLike">
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
      <div class="replies-header" @click="handleToggleReplies">
        <div class="replies-title">
          <el-icon class="toggle-icon" :class="{ 'is-expanded': showReplies }">
            <ArrowDown v-if="!showReplies" />
            <ArrowUp v-else />
          </el-icon>
          <span class="replies-count">{{ post.replies.length }} 条回复</span>
        </div>
        <span class="toggle-text">{{ showReplies ? '收起回复' : '查看回复' }}</span>
      </div>

      <transition name="replies-slide">
        <div v-show="showReplies" class="replies-content">
          <ReplyItem
              v-for="reply in post.replies"
              :key="reply.id"
              :reply="reply"
              :replying-to="replyingTo"
              @like="$emit('like', $event)"
              @reply="handleReplyFromChild"
          >
            <template #reply-editor>
              <slot name="reply-editor"></slot>
            </template>
          </ReplyItem>
        </div>
      </transition>
    </div>
  </div>

  <FriendRequestDialog
      v-model="showRequestDialog"
      :userInfo="targetUser"
      @success="handleRequestSuccess"
  />
</template>

<script setup lang="ts">import { computed, ref } from 'vue'
import { Top, Star, ChatLineRound, Loading, ArrowDown, ArrowUp } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import type { ForumPostVO } from '@/Types/Forum'
import ReplyItem from './ReplyItem.vue'
import FriendRequestDialog from '@/components/Friend/FriendRequestDialog.vue'
import { useUserStore } from '@/stores/modules/user'

interface Props {
  post?: ForumPostVO | null
  replyingTo?: number | null
}

const props = withDefaults(defineProps<Props>(), {
  post: null,
  replyingTo: null
})

interface Emits {
  (e: 'like', commentId: number): void
  (e: 'reply', commentId: number): void
}

const emit = defineEmits<Emits>()

const userStore = useUserStore()
const defaultAvatar = 'https://via.placeholder.com/40x40?text=U'
const showRequestDialog = ref(false)
const targetUser = ref({
  userId: 0,
  nickname: '',
  avatar: ''
})
const showReplies = ref(false)

const isReplying = computed(() => props.post ? props.replyingTo === props.post.id : false)

const handleToggleReplies = () => {
  showReplies.value = !showReplies.value
}
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

const handleReplyFromChild = (commentId: number) => {
  emit('reply', commentId)
}

const handleAvatarClick = () => {
  if (!userStore.userInfo) {
    ElMessage.warning('请先登录')
    return
  }
  if (!props.post) return

  const postUserId = Number(props.post.userId)
  const currentUserId = Number(userStore.userInfo.id)

  if (postUserId === currentUserId) {
    ElMessage.warning('不能添加自己为好友')
    return
  }

  targetUser.value = {
    userId: postUserId,
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

<style scoped lang="scss">
.forum-post-item {
  position: relative;
  background: #1b2838;
  border: 1px solid #2a475e;
  border-radius: 8px;
  padding: 16px;
  transition: all 0.2s;

  &:hover {
    border-color: #66c0f4;
  }

  &.is-top {
    border-color: #66c0f4;
    background: linear-gradient(135deg, #1b2838 0%, #2a475e 100%);
  }

  .top-badge {
    position: absolute;
    top: -10px;
    right: 16px;
    display: flex;
    align-items: center;
    gap: 4px;
    padding: 2px 8px;
    background: #66c0f4;
    color: #fff;
    font-size: 12px;
    font-weight: 600;
    border-radius: 4px;

    .el-icon {
      font-size: 14px;
    }
  }

  .post-header {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 12px;

    .user-avatar {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      object-fit: cover;
      cursor: pointer;
      transition: transform 0.2s;
      flex-shrink: 0;

      &:hover {
        transform: scale(1.1);
      }
    }

    .user-info {
      display: flex;
      flex-direction: column;
      gap: 4px;

      .user-name {
        font-size: 14px;
        color: #c7d5e0;
        font-weight: 600;
      }

      .post-time {
        font-size: 12px;
        color: #8f98a0;
      }
    }
  }

  .post-content {
    margin-bottom: 12px;

    .post-text {
      font-size: 14px;
      color: #c7d5e0;
      line-height: 1.6;
      margin: 0 0 12px 0;
      word-break: break-word;
    }

    .post-images {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;

      .post-image {
        width: 120px;
        height: 120px;
        object-fit: cover;
        border-radius: 6px;
        cursor: pointer;
        transition: transform 0.2s;

        &:hover {
          transform: scale(1.05);
        }
      }
    }
  }

  .post-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-top: 12px;
    border-top: 1px solid #2a475e;

    .action-buttons {
      display: flex;
      gap: 12px;

      .action-btn {
        display: flex;
        align-items: center;
        gap: 6px;
        padding: 6px 12px;
        background: transparent;
        border: 1px solid #2a475e;
        border-radius: 6px;
        color: #8f98a0;
        font-size: 13px;
        cursor: pointer;
        transition: all 0.2s;

        .el-icon {
          font-size: 16px;
        }

        &:hover {
          background: #2a475e;
          color: #c7d5e0;
          border-color: #66c0f4;
        }

        &.active {
          background: #2a475e;
          color: #66c0f4;
          border-color: #66c0f4;
        }
      }
    }
  }

  .reply-editor-wrapper {
    margin-top: 16px;
    padding: 12px;
    background: #16202d;
    border-radius: 6px;
    border: 1px solid #2a475e;
  }

  .replies-section {
    margin-top: 16px;
    padding-top: 12px;
    border-top: 1px solid #2a475e;
    background: #16202d;
    border-radius: 6px;
    padding: 16px;
    margin: 16px 0 0 0;

    .replies-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 12px;
      padding-bottom: 8px;
      border-bottom: 1px solid #2a475e;
      cursor: pointer;
      transition: all 0.2s;

      &:hover {
        .replies-title {
          color: #66c0f4;
        }
        .toggle-text {
          color: #c7d5e0;
        }
      }

      .replies-title {
        display: flex;
        align-items: center;
        gap: 8px;
        color: #66c0f4;
        font-weight: 600;
        transition: color 0.2s;

        .toggle-icon {
          font-size: 14px;
          transition: transform 0.3s;

          &.is-expanded {
            transform: rotate(180deg);
          }
        }

        .replies-count {
          font-size: 13px;
        }
      }

      .toggle-text {
        font-size: 12px;
        color: #8f98a0;
        transition: color 0.2s;
      }
    }

    .replies-content {
      overflow: hidden;
    }
  }
}

.replies-slide-enter-active,
.replies-slide-leave-active {
  transition: all 0.3s ease;
  max-height: 2000px;
  opacity: 1;
}

.replies-slide-enter-from,
.replies-slide-leave-to {
  max-height: 0;
  opacity: 0;
}
</style>
