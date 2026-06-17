<template>
  <el-dialog
      v-model="dialogVisible"
      title="添加好友"
      width="400px"
      :before-close="handleClose"
  >
    <div class="friend-request-dialog">
      <div class="user-info">
        <el-avatar :size="60" :src="userInfo.avatar || defaultAvatar" />
        <div class="user-detail">
          <div class="nickname">{{ userInfo.nickname }}</div>
          <div class="tip">来自游戏评论</div>
        </div>
      </div>

      <el-form label-position="top">
        <el-form-item label="验证消息（可选）">
          <el-input
              v-model="message"
              type="textarea"
              :rows="3"
              placeholder="请输入验证消息"
              maxlength="200"
              show-word-limit
          />
        </el-form-item>
      </el-form>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="loading">
          发送申请
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { sendFriendRequest } from '@/api/friend'
import type { SendFriendRequestDTO } from '@/Types/Friend'

interface UserInfo {
  userId: number
  nickname: string
  avatar: string
}

const props = defineProps<{
  modelValue: boolean
  userInfo: UserInfo
  sourceCommentId?: number
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void
  (e: 'success'): void
}>()

const message = ref('')
const loading = ref(false)
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 使用 computed 双向绑定
const dialogVisible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const handleClose = () => {
  message.value = ''
  emit('update:modelValue', false)
}

const handleSubmit = async () => {
  loading.value = true
  try {
    const data: SendFriendRequestDTO = {
      toUserId: props.userInfo.userId,
      message: message.value,
      sourceCommentId: props.sourceCommentId
    }
    await sendFriendRequest(data)
    ElMessage.success('好友申请已发送')
    emit('success')
    handleClose()
  } catch (error: any) {
    ElMessage.error(error.message || '发送失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.friend-request-dialog {
  .user-info {
    display: flex;
    align-items: center;
    gap: 16px;
    margin-bottom: 24px;
    padding: 16px;
    background: #2a475e;
    border-radius: 8px;

    .user-detail {
      flex: 1;

      .nickname {
        font-size: 16px;
        color: #c7d5e0;
        font-weight: 600;
        margin-bottom: 4px;
      }

      .tip {
        font-size: 12px;
        color: #8f98a0;
      }
    }
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>
