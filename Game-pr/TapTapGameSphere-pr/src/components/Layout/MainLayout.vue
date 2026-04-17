<template>
  <div class="app-wrapper">
    <Sidebar class="sidebar-container" />
    <div class="main-container">
      <Header class="header-container" />
      <main class="app-main">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import Sidebar from './Sidebar.vue';
import Header from './Header.vue';
</script>

<style scoped lang="scss">
.app-wrapper {
  display: flex;
  height: 100vh;
  width: 100%;
  
  .sidebar-container {
    width: 220px;
    background-color: #fff;
    border-right: 1px solid #eee;
    flex-shrink: 0;
    transition: width 0.3s;
  }
  
  .main-container {
    flex: 1;
    display: flex;
    flex-direction: column;
    background-color: #f5f7fa;
    overflow: hidden;
    
    .header-container {
      height: 60px;
      background-color: #fff;
      box-shadow: 0 1px 4px rgba(0,21,41,.08);
      z-index: 10;
    }
    
    .app-main {
      flex: 1;
      padding: 20px;
      overflow-y: auto;
    }
  }
}
</style>