<template>
  <div class="empty-state">
    <el-empty :description="description" />
  </div>
</template>

<script setup lang="ts">
defineProps({
  description: {
    type: String,
    default: '暂无数据'
  }
});
</script>

<style scoped>
.empty-state {
  padding: 40px 0;
  display: flex;
  justify-content: center;
}
</style>