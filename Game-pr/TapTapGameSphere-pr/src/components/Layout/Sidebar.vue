<template>
  <div class="sidebar-menu">
    <div class="logo">
      <h2>GameSphere</h2>
    </div>
    <el-menu
        default-active="/home"
        class="el-menu-vertical"
        router
        background-color="#171a21"
        text-color="#c7d5e0"
        active-text-color="#66c0f4"
    >
      <el-menu-item index="/home">
        <el-icon><House /></el-icon>
        <span>首页推荐</span>
      </el-menu-item>
      <el-menu-item index="/discover">
        <el-icon><Compass /></el-icon>
        <span>发现游戏</span>
      </el-menu-item>
      <el-menu-item index="/category">
        <el-icon><Grid /></el-icon>
        <span>游戏分类</span>
      </el-menu-item>
      <el-menu-item index="/forum">
        <el-icon><ChatDotRound /></el-icon>
        <span>论坛</span>
      </el-menu-item>

      <el-sub-menu index="categories">
        <template #title>
          <el-icon><Menu /></el-icon>
          <span>分类详情</span>
        </template>
        <el-menu-item v-for="cat in categories" :key="cat.id" :index="`/category/${cat.id}`">
          {{ cat.name }}
        </el-menu-item>
      </el-sub-menu>
    </el-menu>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { getCategoryList } from '@/api/category';
import { House, Compass, Menu, ChatDotRound } from '@element-plus/icons-vue';

interface Category {
  id: number;
  name: string;
}

const categories = ref<Category[]>([]);

onMounted(async () => {
  try {
    const response = await getCategoryList();
    categories.value = response.data;
  } catch (e) {
    console.error('Failed to load categories', e);
  }
});
</script>

<style scoped lang="scss">
.sidebar-menu {
  height: 100%;

  .logo {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-bottom: 1px solid #2a475e;
    background: linear-gradient(to right, #171a21, #1b2838);

    h2 {
      color: #66c0f4;
      font-size: 20px;
      margin: 0;
      font-weight: bold;
      letter-spacing: 1px;
    }
  }

  .el-menu-vertical {
    border-right: none;

    :deep(.el-menu-item) {
      &:hover {
        background-color: #2a475e !important;
      }

      &.is-active {
        background-color: #2a475e !important;
        border-left: 3px solid #66c0f4;
      }
    }
  }
}
</style>
