NEW_FILE_CODE
<template>
  <div class="comment-editor">
    <div class="editor-header">
      <img :src="userAvatar || defaultAvatar" class="user-avatar" />
      <span class="editor-title">发表评论</span>
    </div>

    <div class="editor-body">
      <textarea
          v-model="content"
          class="editor-textarea"
          :placeholder="placeholder"
          @input="handleInput"
      ></textarea>

      <div class="editor-footer">
        <div class="char-count">{{ content.length }}/500</div>
        <el-button
            type="primary"
            :disabled="!content.trim() || content.length > 500"
            @click="handleSubmit"
        >
          发布
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/modules/user'

const props = defineProps<{
  gameId: number
  parentId?: number
  placeholder?: string
}>()

const emit = defineEmits<{
  (e: 'submit', content: string): void
}>()

const userStore = useUserStore()
const content = ref('')
const defaultAvatar = 'https://via.placeholder.com/40x40?text=U'

const userAvatar = userStore.userInfo?.avatar || ''

const handleInput = () => {
  if (content.value.length > 500) {
    content.value = content.value.slice(0, 500)
  }
}

const handleSubmit = () => {
  if (!content.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }

  if (content.value.length > 500) {
    ElMessage.warning('评论内容不能超过500字')
    return
  }

  emit('submit', content.value.trim())
  content.value = ''
}
</script>

<style scoped lang="scss">
.comment-editor {
  background: #16202d;
  border: 1px solid #2a475e;
  border-radius: 8px;
  padding: 16px;

  .editor-header {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 12px;

    .user-avatar {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      object-fit: cover;
    }

    .editor-title {
      font-size: 14px;
      color: #8f98a0;
    }
  }

  .editor-body {
    .editor-textarea {
      width: 100%;
      min-height: 100px;
      padding: 12px;
      background: #1b2838;
      border: 1px solid #2a475e;
      border-radius: 6px;
      color: #c7d5e0;
      font-size: 14px;
      line-height: 1.6;
      resize: vertical;
      transition: border-color 0.2s;

      &:focus {
        outline: none;
        border-color: #66c0f4;
      }

      &::placeholder {
        color: #6b7d8e;
      }
    }

    .editor-footer {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-top: 12px;

      .char-count {
        font-size: 12px;
        color: #8f98a0;
      }
    }
  }
}
</style>
