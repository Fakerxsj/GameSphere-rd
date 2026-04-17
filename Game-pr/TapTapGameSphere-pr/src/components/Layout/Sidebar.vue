<template>
  <div class="sidebar-menu">
    <div class="logo">
      <h2>GameSphere</h2>
    </div>
    <el-menu
      default-active="1"
      class="el-menu-vertical"
      router
      background-color="#ffffff"
      text-color="#303133"
      active-text-color="#409EFF"
    >
      <el-menu-item index="/home">
        <el-icon><House /></el-icon>
        <span>首页推荐</span>
      </el-menu-item>
      <el-menu-item index="/discover">
        <el-icon><Compass /></el-icon>
        <span>发现游戏</span>
      </el-menu-item>
      
      <el-sub-menu index="categories">
        <template #title>
          <el-icon><Menu /></el-icon>
          <span>游戏分类</span>
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
import { House, Compass, Menu } from '@element-plus/icons-vue';

interface Category {
  id: number;
  name: string;
}

const categories = ref<Category[]>([]);

onMounted(async () => {
  try {
    categories.value = await getCategoryList();
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
    border-bottom: 1px solid #eee;
    h2 {
      color: #409EFF;
      font-size: 20px;
      margin: 0;
    }
  }
  .el-menu-vertical {
    border-right: none;
  }
}
</style>