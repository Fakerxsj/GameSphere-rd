<template>
  <div class="profile-page">
    <div class="profile-card">
      <div class="profile-header">
        <span class="profile-title">个人中心</span>
      </div>

      <div v-if="userStore.userInfo" class="user-info-display">
        <div class="avatar-section">
          <div class="avatar-wrapper">
            <img
                :src="userStore.userInfo.avatar || defaultAvatar"
                :alt="userStore.userInfo.nickname"
                class="user-avatar"
            />
          </div>
          <el-upload
              class="avatar-uploader"
              action=""
              :show-file-list="false"
              :before-upload="beforeAvatarUpload"
              :http-request="handleAvatarUpload"
          >
            <el-button class="steam-btn" :loading="uploading">
              {{ uploading ? '上传中...' : '更换头像' }}
            </el-button>
          </el-upload>
        </div>
        <h2 class="user-nickname">{{ userStore.userInfo.nickname }}</h2>
        <p class="meta">邮箱: {{ userStore.userInfo.email || '未绑定' }}</p>

        <el-divider class="profile-divider" />

        <el-form :model="editForm" label-width="80px" class="edit-form">
          <el-form-item label="昵称">
            <el-input v-model="editForm.nickname" placeholder="请输入昵称" />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="editForm.email" placeholder="请输入邮箱地址" />
          </el-form-item>
          <el-form-item label="签名">
            <el-input v-model="editForm.signature" type="textarea" placeholder="写一句话介绍自己吧" :rows="3" />
          </el-form-item>
          <el-form-item>
            <el-button class="steam-btn save-btn" :loading="saving" @click="handleSave">保存修改</el-button>
          </el-form-item>
        </el-form>
      </div>
      <div v-else class="not-logged-in">
        <div class="empty-state">
          <el-icon class="empty-icon"><UserFilled /></el-icon>
          <p class="empty-text">请先登录</p>
        </div>
        <el-button class="steam-btn" @click="$router.push('/login')">去登录</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, watch } from 'vue'
import { useUserStore } from '@/stores/modules/user'
import { uploadAvatar, updateUserInfo } from '@/api/user'
import { ElMessage } from 'element-plus'
import { UserFilled } from '@element-plus/icons-vue'

const userStore = useUserStore()
const uploading = ref(false)
const saving = ref(false)
const defaultAvatar = 'https://via.placeholder.com/120x120?text=U'

const editForm = reactive({
  nickname: '',
  email: '',
  signature: ''
})

watch(() => userStore.userInfo, (info) => {
  if (info) {
    editForm.nickname = info.nickname || ''
    editForm.email = info.email || ''
    editForm.signature = ''
  }
}, { immediate: true })

const beforeAvatarUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB!')
    return false
  }
  return true
}

const handleAvatarUpload = async (options: any) => {
  const file = options.file
  uploading.value = true
  try {
    const avatarUrl = await uploadAvatar(file)
    userStore.updateAvatar(avatarUrl)
    ElMessage.success('头像上传成功')
  } catch (error: any) {
    ElMessage.error(error.message || '头像上传失败')
  } finally {
    uploading.value = false
  }
}

const handleSave = async () => {
  if (!editForm.nickname.trim()) {
    ElMessage.warning('昵称不能为空')
    return
  }

  if (editForm.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(editForm.email)) {
    ElMessage.warning('邮箱格式不正确')
    return
  }

  saving.value = true
  try {
    await updateUserInfo({
      nickname: editForm.nickname,
      email: editForm.email || null,
      signature: editForm.signature
    })
    if (userStore.userInfo) {
      userStore.userInfo.nickname = editForm.nickname
      userStore.userInfo.email = editForm.email || undefined
      userStore.setUserInfo(userStore.userInfo)
    }
    ElMessage.success('保存成功')
  } catch (error: any) {
    ElMessage.error(error.message || '保存失败')
  } finally {
    saving.value = false
  }
}
</script>

<style scoped lang="scss">
.profile-page {
  min-height: 100vh;
  background-color: #1b2838;
  background-image: radial-gradient(circle at 50% 50%, rgba(102, 192, 244, 0.05) 0%, transparent 50%);
  padding: 40px 20px;
  display: flex;
  justify-content: center;
  align-items: flex-start;
}

.profile-card {
  width: 100%;
  max-width: 800px;
  background-color: #16202d;
  border: 1px solid #2a475e;
  border-radius: 8px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.4);
  overflow: hidden;
}

.profile-header {
  padding: 20px 30px;
  background: linear-gradient(135deg, #1b2838 0%, #2a475e 100%);
  border-bottom: 1px solid #2a475e;

  .profile-title {
    font-size: 20px;
    font-weight: 600;
    color: #c7d5e0;
  }
}

.user-info-display {
  padding: 40px 30px;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  margin-bottom: 30px;

  .avatar-wrapper {
    width: 120px;
    height: 120px;
    border-radius: 50%;
    overflow: hidden;
    border: 3px solid #2a475e;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.3);
    transition: all 0.3s;

    &:hover {
      border-color: #66c0f4;
      box-shadow: 0 4px 20px rgba(102, 192, 244, 0.3);
    }

    .user-avatar {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }
}

.user-nickname {
  font-size: 24px;
  color: #c7d5e0;
  font-weight: 600;
  margin: 0 0 10px 0;
}

.meta {
  color: #8f98a0;
  font-size: 14px;
  margin: 0 0 30px 0;
}

.profile-divider {
  border-color: #2a475e;
  margin: 30px 0;
}

.edit-form {
  max-width: 600px;
  margin: 0 auto;

  :deep(.el-form-item__label) {
    color: #c7d5e0;
    font-weight: 500;
  }

  :deep(.el-input__wrapper) {
    background-color: #1b2838;
    box-shadow: 0 0 0 1px #2a475e inset;
    transition: all 0.2s;

    &:hover {
      box-shadow: 0 0 0 1px #66c0f4 inset;
    }

    &.is-focus {
      box-shadow: 0 0 0 1px #66c0f4 inset;
    }
  }

  :deep(.el-input__inner) {
    color: #c7d5e0;
  }

  :deep(.el-textarea__inner) {
    background-color: #1b2838;
    color: #c7d5e0;
    box-shadow: 0 0 0 1px #2a475e inset;
    transition: all 0.2s;

    &:hover {
      box-shadow: 0 0 0 1px #66c0f4 inset;
    }

    &:focus {
      box-shadow: 0 0 0 1px #66c0f4 inset;
    }
  }

  :deep(.el-form-item) {
    margin-bottom: 24px;
  }
}

.not-logged-in {
  text-align: center;
  padding: 60px 20px;

  .empty-state {
    margin-bottom: 30px;

    .empty-icon {
      font-size: 64px;
      color: #2a475e;
      margin-bottom: 16px;
    }

    .empty-text {
      font-size: 16px;
      color: #8f98a0;
      margin: 0;
    }
  }
}

.steam-btn {
  background: linear-gradient(to right, #66c0f4 5%, #2a475e 60%);
  border: none;
  color: #fff;
  border-radius: 4px;
  padding: 10px 24px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);

  &:hover {
    background: linear-gradient(to right, #66c0f4 5%, #2a475e 80%);
    box-shadow: 0 4px 12px rgba(102, 192, 244, 0.3);
    transform: translateY(-1px);
  }

  &:active {
    transform: translateY(0);
  }

  &.save-btn {
    width: 100%;
    max-width: 200px;
    padding: 12px 24px;
  }
}
</style>
