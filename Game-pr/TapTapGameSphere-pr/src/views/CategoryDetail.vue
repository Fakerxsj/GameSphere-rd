<template>
  <div class="category-detail">
    <div class="header">
      <h2>{{ categoryName }}游戏</h2>
    </div>

    <div v-if="loading" class="loading-state">
      <el-icon class="is-loading"><Loading /></el-icon>
    </div>

    <div v-else-if="games.length > 0" class="game-list">
      <GameListItem
          v-for="game in games"
          :key="game.id"
          :game="game"
          @click="$router.push(`/game/${game.id}`)"
      />
    </div>

    <div v-else class="empty-state">
      <el-empty description="该分类下暂无游戏" />
    </div>

    <div class="load-more" v-if="hasMore && !loading">
      <el-button @click="loadMore" :loading="loadingMore">加载更多</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { Loading } from '@element-plus/icons-vue';
import { getCategoryGames } from '@/api/category';
import GameListItem from '@/components/Game/GameListItem.vue';

const route = useRoute();
const categoryId = ref(Number(route.params.id));
const categoryName = ref('');
const games = ref<any[]>([]);
const loading = ref(false);
const loadingMore = ref(false);
const hasMore = ref(true);
const offset = ref(0);
const limit = 20;

const loadGames = async (reset = false) => {
  if (reset) {
    offset.value = 0;
    games.value = [];
    hasMore.value = true;
  }

  loading.value = true;
  try {
    const data = await getCategoryGames(categoryId.value, { limit, offset: offset.value });
    console.log('📦 分类详情数据:', data);

    const newGames = data?.games || [];

    if (reset) games.value = newGames;
    else games.value = [...games.value, ...newGames];

    hasMore.value = newGames.length >= limit;
    offset.value += newGames.length;
  } finally {
    loading.value = false;
  }
};

const loadMore = async () => {
  loadingMore.value = true;
  await loadGames(false);
  loadingMore.value = false;
};

onMounted(() => {
  loadGames(true);
});
</script>

<style scoped lang="scss">
.category-detail {
  background: #1b2838;
  min-height: 100%;

  .header {
    padding: 20px;
    background: #16202d;
    border-bottom: 1px solid #2a475e;

    h2 {
      font-size: 22px;
      color: #c7d5e0;
      margin: 0;
    }
  }

  .game-list {
    padding: 0 20px;
  }

  .loading-state {
    padding: 50px;
    text-align: center;
    color: #66c0f4;
  }

  .empty-state {
    padding: 50px;
  }

  .load-more {
    padding: 20px;
    text-align: center;

    .el-button {
      background: #2a475e;
      border-color: #2a475e;
      color: #c7d5e0;
    }
  }
}
</style>
