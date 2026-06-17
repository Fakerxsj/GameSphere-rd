<template>
  <div class="message-input">
    <el-input
        v-model="content"
        type="textarea"
        :rows="2"
        placeholder="输入消息..."
        @keydown.enter.exact.prevent="handleSend"
    />
    <div class="input-footer">
      <div class="actions">
        <el-button type="text" :icon="Picture" @click="triggerImageUpload">
          图片
        </el-button>
        <input
            ref="fileInputRef"
            type="file"
            accept="image/*"
            style="display: none"
            @change="handleImageSelect"
        />
      </div>
      <el-button type="primary" @click="handleSend" :loading="loading">
        发送
      </el-button>
    </div>

    <!-- 图片预览区域 -->
    <div v-if="previewImage" class="image-preview">
      <img :src="previewImage" alt="预览" />
      <el-icon class="remove-icon" @click="removeImage"><Close /></el-icon>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Picture, Close } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { uploadChatImage } from '@/api/file'

const props = defineProps<{
  loading?: boolean
}>()

const emit = defineEmits<{
  (e: 'send', content: string): void
  (e: 'sendImage', imageUrl: string): void
}>()

const content = ref('')
const fileInputRef = ref<HTMLInputElement | null>(null)
const previewImage = ref('')
const selectedFile = ref<File | null>(null)

const triggerImageUpload = () => {
  fileInputRef.value?.click()
}

const handleImageSelect = (event: Event) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  if (!file) return

  // 校验文件类型
  if (!file.type.startsWith('image/')) {
    ElMessage.warning('请选择图片文件')
    return
  }

  // 校验文件大小 (10MB)
  if (file.size > 10 * 1024 * 1024) {
    ElMessage.warning('图片大小不能超过 10MB')
    return
  }

  selectedFile.value = file
  // 生成预览
  const reader = new FileReader()
  reader.onload = (e) => {
    previewImage.value = e.target?.result as string
  }
  reader.readAsDataURL(file)
}

const removeImage = () => {
  previewImage.value = ''
  selectedFile.value = null
  if (fileInputRef.value) {
    fileInputRef.value.value = ''
  }
}

const handleSend = async () => {
  // 如果有图片，先上传图片
  if (selectedFile.value) {
    try {
      ElMessage.info('图片上传中...')
      const imageUrl = await uploadChatImage(selectedFile.value)
      emit('sendImage', imageUrl)
      removeImage()
    } catch (error: any) {
      ElMessage.error(error.message || '图片上传失败')
    }
    return
  }

  // 发送文字消息
  if (!content.value.trim()) {
    ElMessage.warning('请输入消息内容')
    return
  }
  emit('send', content.value)
  content.value = ''
}
</script>

<style scoped lang="scss">
.message-input {
  padding: 16px;
  background: #1b2838;
  border-top: 1px solid #2a475e;

  :deep(.el-textarea__inner) {
    background: #2a475e;
    border: 1px solid #1b2838;
    color: #c7d5e0;
    border-radius: 8px;

    &::placeholder {
      color: #8f98a0;
    }
  }

  .input-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 12px;

    .actions {
      display: flex;
      gap: 8px;
    }
  }

  .image-preview {
    position: relative;
    margin-top: 12px;
    display: inline-block;

    img {
      max-width: 200px;
      max-height: 150px;
      border-radius: 8px;
      object-fit: cover;
    }

    .remove-icon {
      position: absolute;
      top: -8px;
      right: -8px;
      width: 24px;
      height: 24px;
      background: #e74c3c;
      color: white;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      font-size: 14px;
      transition: transform 0.2s;

      &:hover {
        transform: scale(1.1);
      }
    }
  }
}
</style>
