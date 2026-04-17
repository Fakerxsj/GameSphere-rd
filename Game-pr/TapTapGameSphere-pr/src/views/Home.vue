<template>
  <div class="home-page">
    <div class="section-title">
      <h3>为你推荐</h3>
      <p>基于你的游玩习惯生成的个性化推荐</p>
    </div>

    <div v-if="loading" class="loading-state">加载中...</div>
    
    <div v-else-if="games.length > 0" class="game-grid">
      <GameCard 
        v-for="game in games" 
        :key="game.id" 
        :game="game"
        @click="handleGameClick(game)"
      />
    </div>
    
    <EmptyState v-else description="暂无推荐游戏" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { getHomeRecommendation } from '@/api/recommendation';
import { clickGame } from '@/api/game';
import GameCard from '@/components/Game/GameCard.vue';
import EmptyState from '@/components/Common/EmptyState.vue';

interface Game {
  id: number;
  name: string;
  coverImage: string;
  ratingScore: number;
  developer: string;
}

const router = useRouter();
const games = ref<Game[]>([]);
const loading = ref(false);

onMounted(async () => {
  loading.value = true;
  try {
    const data = await getHomeRecommendation();
    // 假设返回的是数组，如果后端返回结构不同请在此调整
    games.value = Array.isArray(data) ? data : (data.list || []);
  } catch (error) {
    console.error('Load recommendation failed', error);
  } finally {
    loading.value = false;
  }
});

const handleGameClick = async (game: Game) => {
  try {
    await clickGame(game.id);
  } catch (e) {
    console.warn('Click report failed', e);
  }
  router.push(`/game/${game.id}`);
};
</script>

<style scoped lang="scss">
.home-page {
  .section-title {
    margin: 10px 0 20px;
    h3 { font-size: 20px; color: #333; margin-bottom: 5px; }
    p { font-size: 12px; color: #999; }
  }

  .game-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
    gap: 20px;
  }
  
  .loading-state {
    text-align: center;
    padding: 40px;
    color: #999;
  }
}
</style>