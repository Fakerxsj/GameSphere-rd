<template>
  <div class="friend-list-item" @click="handleClick">
    <el-avatar :size="48" :src="friend.avatar || defaultAvatar" />
    <div class="friend-info">
      <div class="nickname">
        {{ friend.remark || friend.nickname }}
        <span v-if="friend.remark" class="original-name">({{ friend.nickname }})</span>
      </div>
      <div class="status">
        <span class="dot online"></span>
        <span class="text">在线</span>
      </div>
    </div>
    <el-dropdown trigger="click" @command="handleCommand">
      <el-icon class="more-icon"><MoreFilled /></el-icon>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item command="remark">修改备注</el-dropdown-item>
          <el-dropdown-item command="delete" divided>删除好友</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script setup lang="ts">
import { MoreFilled } from '@element-plus/icons-vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import type { FriendVO } from '@/Types/Friend'
import { useFriendStore } from '@/stores/modules/friend'

const props = defineProps<{
  friend: FriendVO
}>()

const emit = defineEmits<{
  (e: 'click', userId: number): void
  (e: 'delete', userId: number): void
  (e: 'updateRemark', userId: number): void
}>()

const friendStore = useFriendStore()
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const handleClick = () => {
  emit('click', props.friend.userId)
}

const handleCommand = async (command: string) => {
  if (command === 'delete') {
    try {
      await ElMessageBox.confirm('确定要删除该好友吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      await friendStore.deleteFriend(props.friend.userId)
      ElMessage.success('删除成功')
      emit('delete', props.friend.userId)
    } catch (error) {
    }
  } else if (command === 'remark') {
    emit('updateRemark', props.friend.userId)
  }
}
</script>

<style scoped lang="scss">
.friend-list-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  cursor: pointer;
  transition: background 0.2s;

  &:hover {
    background: #2a475e;

    .more-icon {
      opacity: 1;
    }
  }

  .friend-info {
    flex: 1;
    min-width: 0;

    .nickname {
      font-size: 15px;
      color: #c7d5e0;
      font-weight: 500;
      margin-bottom: 4px;

      .original-name {
        font-size: 12px;
        color: #8f98a0;
        font-weight: 400;
      }
    }

    .status {
      display: flex;
      align-items: center;
      gap: 6px;

      .dot {
        width: 8px;
        height: 8px;
        border-radius: 50%;

        &.online {
          background: #4ade80;
        }
      }

      .text {
        font-size: 12px;
        color: #8f98a0;
      }
    }
  }

  .more-icon {
    opacity: 0;
    color: #8f98a0;
    font-size: 18px;
    transition: opacity 0.2s;
  }
}
</style>
