<template>
  <div class="profile-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>个人中心</span>
        </div>
      </template>
      <div v-if="userStore.userInfo" class="user-info-display">
        <el-avatar :size="100" :src="userStore.userInfo.avatar" />
        <h2>{{ userStore.userInfo.nickname }}</h2>
        <p>ID: {{ userStore.userInfo.id }}</p>
        <p>Email: {{ userStore.userInfo.email || '未绑定' }}</p>
      </div>
      <div v-else>
        <p>请先登录</p>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { useUserStore } from '@/stores/modules/user';

const userStore = useUserStore();
</script>

<style scoped>
.profile-page {
  max-width: 800px;
  margin: 0 auto;
}
.user-info-display {
  text-align: center;
  padding: 20px;
}
</style>