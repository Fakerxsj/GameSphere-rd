<template>
  <div class="admin-layout">
    <el-container class="admin-container">
      <el-aside width="220px" class="admin-aside">
        <div class="logo">
          <h2>后台管理系统</h2>
        </div>
        <el-menu
            :default-active="activeMenu"
            class="admin-menu"
            background-color="#16202d"
            text-color="#c7d5e0"
            active-text-color="#66c0f4"
            router
        >
          <el-menu-item index="/admin/dashboard">
            <el-icon><DataAnalysis /></el-icon>
            <span>数据统计</span>
          </el-menu-item>

          <el-sub-menu index="user">
            <template #title>
              <el-icon><User /></el-icon>
              <span>用户管理</span>
            </template>
            <el-menu-item index="/admin/users">用户列表</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="game">
            <template #title>
              <el-icon><Monitor /></el-icon>
              <span>游戏管理</span>
            </template>
            <el-menu-item index="/admin/games">游戏列表</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="community">
            <template #title>
              <el-icon><ChatDotSquare /></el-icon>
              <span>社区管理</span>
            </template>
            <el-menu-item index="/admin/posts">帖子管理</el-menu-item>
            <el-menu-item index="/admin/forums">论坛热度</el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-aside>

      <el-main class="admin-main">
        <div class="admin-header">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/admin' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
          <div class="user-info">
            <span>{{ userStore.userInfo?.nickname }}</span>
            <el-button link @click="handleLogout">退出</el-button>
          </div>
        </div>
        <div class="admin-content">
          <router-view />
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/modules/user'
import { DataAnalysis, User, Monitor, ChatDotSquare } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

const currentTitle = computed(() => {
  return route.meta.title || '后台管理'
})

const handleLogout = async () => {
  await userStore.logout()
  router.push('/login')
}
</script>

<style scoped lang="scss">
.admin-layout {
  height: 100vh;
  background: #1b2838;
}

.admin-container {
  height: 100%;
}

.admin-aside {
  background: #16202d;
  border-right: 1px solid #2a475e;
  overflow-y: auto;

  .logo {
    padding: 20px;
    text-align: center;
    border-bottom: 1px solid #2a475e;

    h2 {
      color: #66c0f4;
      font-size: 18px;
      margin: 0;
    }
  }

  .admin-menu {
    border-right: none;
  }
}

.admin-main {
  padding: 0;
  background: #1b2838;

  .admin-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 15px 20px;
    background: #16202d;
    border-bottom: 1px solid #2a475e;

    :deep(.el-breadcrumb) {
      .el-breadcrumb__item {
        .el-breadcrumb__inner {
          color: #c7d5e0;
        }
      }
    }

    .user-info {
      display: flex;
      align-items: center;
      gap: 15px;
      color: #c7d5e0;
      font-size: 14px;
    }
  }

  .admin-content {
    padding: 20px;
    height: calc(100vh - 60px);
    overflow-y: auto;
  }
}
</style>
