<template>
  <div class="game-detail" v-if="game">
    <div class="header-info">
      <img :src="game.coverImage" class="cover-lg" />
      <div class="info-text">
        <h1>{{ game.name }}</h1>
        <p class="meta">{{ game.developer }} | {{ game.platform }}</p>
        <div class="rating">
          <el-rate v-model="averageRating" disabled show-score text-color="#ff9900" />
          <span class="score-num">{{ game.ratingScore }}</span>
        </div>
        <el-button type="primary" size="large">立即下载</el-button>
      </div>
    </div>
    
    <div class="content-tabs">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="介绍" name="intro">
          <div class="intro-text" v-html="game.description"></div>
        </el-tab-pane>
        <el-tab-pane label="评论" name="comments">
          <!-- 评论组件占位 -->
          <div class="comments-section">
            <p>评论功能开发中...</p>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
  <div v-else class="loading">加载中...</div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { getGameDetail } from '@/api/game';

const route = useRoute();
const game = ref<any>(null);
const activeTab = ref('intro');
const averageRating = ref(0);

onMounted(async () => {
  const id = Number(route.params.id);
  if (id) {
    try {
      game.value = await getGameDetail(id);
      averageRating.value = Number(game.value.ratingScore) / 2; // el-rate 是5分制，后端可能是10分制
    } catch (e) {
      console.error(e);
    }
  }
});
</script>

<style scoped lang="scss">
.game-detail {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  
  .header-info {
    display: flex;
    gap: 20px;
    margin-bottom: 30px;
    
    .cover-lg {
      width: 200px;
      height: 280px;
      object-fit: cover;
      border-radius: 8px;
    }
    
    .info-text {
      flex: 1;
      h1 { margin: 0 0 10px; font-size: 24px; }
      .meta { color: #666; margin-bottom: 15px; }
      .rating { display: flex; align-items: center; gap: 10px; margin-bottom: 20px; }
    }
  }
}
</style>