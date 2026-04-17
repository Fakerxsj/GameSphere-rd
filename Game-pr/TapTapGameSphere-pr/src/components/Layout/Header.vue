<template>
  <header class="header">
    <div class="search-bar">
      <el-input
        v-model="searchQuery"
        placeholder="搜索游戏..."
        prefix-icon="Search"
        style="width: 300px"
        @keyup.enter="handleSearch"
      />
    </div>
    <div class="user-info">
      <el-dropdown v-if="userStore.userInfo" trigger="click">
        <span class="el-dropdown-link">
          <el-avatar :size="32" :src="userStore.userInfo.avatar" />
          <span class="nickname">{{ userStore.userInfo.nickname }}</span>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="$router.push('/profile')">个人中心</el-dropdown-item>
            <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
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
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/modules/user';
import { Search } from '@element-plus/icons-vue';

const router = useRouter();
const userStore = useUserStore();
const searchQuery = ref('');

const handleSearch = () => {
  if (searchQuery.value) {
    router.push({ path: '/home', query: { keyword: searchQuery.value } });
  }
};

const handleLogout = async () => {
  await userStore.logout();
  router.push('/login');
};
</script>

<style scoped lang="scss">
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  height: 100%;

  .user-info {
    display: flex;
    align-items: center;
    gap: 10px;
    
    .el-dropdown-link {
      display: flex;
      align-items: center;
      cursor: pointer;
      .nickname {
        margin-left: 8px;
        font-size: 14px;
      }
    }
  }
}
</style>