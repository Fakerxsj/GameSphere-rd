<template>
  <header class="header">
    <div class="search-bar">
      <el-input
          v-model="searchQuery"
          placeholder="搜索游戏..."
          prefix-icon="Search"
          class="steam-search"
          @keyup.enter="handleSearch"
      />
    </div>
    <div class="user-info">
      <el-dropdown v-if="userStore.userInfo && userStore.userInfo.id" trigger="click">
        <span class="el-dropdown-link">
          <el-avatar :size="32" :src="userStore.userInfo.avatar || defaultAvatar" />
          <span class="nickname">{{ userStore.userInfo.nickname }}</span>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item v-if="userStore.isAdmin" @click="$router.push('/admin')">
              <el-icon><Setting /></el-icon>
              <span>后台管理</span>
            </el-dropdown-item>
            <el-dropdown-item @click="$router.push('/friend-center')">
              <el-icon><User /></el-icon>
              <span>好友中心</span>
            </el-dropdown-item>
            <el-dropdown-item @click="$router.push('/profile')">
              <el-icon><UserFilled /></el-icon>
              <span>个人中心</span>
            </el-dropdown-item>
            <el-dropdown-item divided @click="handleLogout">
              <el-icon><SwitchButton /></el-icon>
              <span>退出登录</span>
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
      <div v-else class="login-btns">
        <el-button type="primary" link @click="$router.push('/login')">登录</el-button>
        <el-button type="primary" @click="$router.push('/register')">注册</el-button>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/modules/user'
import { House, Compass, Setting, User, UserFilled, SwitchButton } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const searchQuery = ref('')
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const handleSearch = () => {
  if (searchQuery.value) {
    router.push({ path: '/home', query: { keyword: searchQuery.value } })
  }
}

const handleLogout = async () => {
  await userStore.logout()
  router.push('/login')
}
</script>

<style scoped lang="scss">
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  height: 100%;

  .nav-links {
    display: flex;
    gap: 24px;

    .nav-item {
      display: flex;
      align-items: center;
      gap: 6px;
      padding: 8px 16px;
      color: #c7d5e0;
      text-decoration: none;
      font-size: 14px;
      border-radius: 3px;
      transition: all 0.2s;

      .el-icon {
        font-size: 18px;
      }

      &:hover {
        background: #2a475e;
        color: #66c0f4;
      }

      &.active {
        background: #2a475e;
        color: #66c0f4;
        border-bottom: 2px solid #66c0f4;
      }
    }
  }

  .search-bar {
    .steam-search {
      width: 300px;

      :deep(.el-input__wrapper) {
        background-color: #2a475e;
        box-shadow: none;
        border: 1px solid #1b2838;
        border-radius: 3px;

        &.is-focus {
          box-shadow: 0 0 0 1px #66c0f4 inset;
        }

        .el-input__inner {
          color: #c7d5e0;

          &::placeholder {
            color: #8f98a0;
          }
        }

        .el-input__prefix {
          color: #66c0f4;
        }
      }
    }
  }

  .user-info {
    display: flex;
    align-items: center;
    gap: 10px;

    .el-dropdown-link {
      display: flex;
      align-items: center;
      cursor: pointer;
      color: #c7d5e0;

      .nickname {
        margin-left: 8px;
        font-size: 14px;
      }
    }

    .login-btns {
      display: flex;
      gap: 8px;

      .el-button {
        color: #c7d5e0;

        &:hover {
          color: #66c0f4;
        }
      }
    }
  }
}
</style>
