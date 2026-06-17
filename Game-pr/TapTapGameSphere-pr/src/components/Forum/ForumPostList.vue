<template>
  <div class="forum-post-list">
    <div v-if="loading" class="loading-state">
      <el-icon class="is-loading"><Loading /></el-icon>
      <span>加载中...</span>
    </div>

    <div v-else-if="posts.length === 0" class="empty-state">
      <el-empty description="暂无帖子" />
    </div>

    <div v-else class="post-items">
      <ForumPostItem
          v-for="post in posts"
          :key="post.id"
          :post="post"
          :replying-to="replyingTo"
          @like="$emit('like', $event)"
          @reply="$emit('reply', $event)"
      >
        <template v-if="replyingTo === post.id" #reply-editor>
          <slot name="reply-editor" :post-id="post.id"></slot>
        </template>
      </ForumPostItem>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Loading } from '@element-plus/icons-vue'
import type { ForumPostVO } from '@/Types/Forum'
import ForumPostItem from './ForumPostItem.vue'

const props = defineProps<{
  posts: ForumPostVO[]
  loading: boolean
  replyingTo?: number | null
}>()

const emit = defineEmits<{
  (e: 'like', postId: number): void
  (e: 'reply', postId: number): void
}>()
</script>

<style scoped lang="scss">
.forum-post-list {
  .empty-state {
    padding: 40px 0;
  }

  .post-items {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }

  .loading-state {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    padding: 24px;
    color: #8f98a0;
    font-size: 14px;

    .el-icon {
      font-size: 20px;
      color: #66c0f4;
    }
  }
}
</style>
