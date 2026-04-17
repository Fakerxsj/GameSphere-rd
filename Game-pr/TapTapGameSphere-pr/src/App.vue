<script setup lang="ts">
import { RouterView } from 'vue-router'
// 如果需要全局样式，可以在这里引入
// import './assets/styles/main.scss'
</script>

<template>
  <!-- RouterView 会根据当前 URL 渲染对应的页面组件 (如 Home.vue, Login.vue) -->
  <RouterView />
</template>

<style>
/* 全局重置样式，可选 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
  background-color: #f5f7fa; /* TapTap 风格的浅灰背景 */
  color: #333;
}

#app {
  width: 100%;
  height: 100vh;
}
</style>