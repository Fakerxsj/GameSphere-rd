<template>
  <div class="discover-page">
    <div class="ranking-header">
      <h2>发现游戏</h2>
      <p class="update-tip">按最近下载热度计算 · 每 20 分钟更新</p>
    </div>

    <div class="ranking-tabs">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="热门榜" name="hot"></el-tab-pane>
        <el-tab-pane label="评分榜" name="rating"></el-tab-pane>
        <el-tab-pane label="下载榜" name="download"></el-tab-pane>
        <el-tab-pane label="关注榜" name="follow"></el-tab-pane>
      </el-tabs>
    </div>

    <div v-if="loading" class="loading-state">
      <el-icon :size="40" class="is-loading"><Loading /></el-icon>
      <span>加载中...</span>
    </div>

    <div v-else-if="games.length > 0" class="ranking-list">
      <RankingCard
          v-for="(game, index) in games"
          :key="game.id"
          :game="game"
          :index="index"
          @click="handleGameClick(game)"
      />
    </div>

    <div v-else class="empty-state">
      <el-empty description="暂无游戏数据" />
    </div>

    <div class="load-more" v-if="hasMore && !loading">
      <el-button @click="loadMore" :loading="loadingMore">
        加载更多
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { Loading } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import { getHotRanking, getRatingRanking, getDownloadRanking, getFollowRanking } from '@/api/ranking';
import { clickGame } from '@/api/game';
import RankingCard from '@/components/Game/RankingCard.vue';

interface Game {
  id: number;
  name: string;
  coverImage?: string;
  videoUrl?: string;
  ratingScore?: number | string;
  developer?: string;
  platform?: string;
  gameType?: string;
  downloadCount?: number;
  followCount?: number;
}

const router = useRouter();
const activeTab = ref('hot');
const games = ref<Game[]>([]);
const loading = ref(false);
const loadingMore = ref(false);
const offset = ref(0);
const hasMore = ref(true);
const limit = 20;

const rankingApiMap = {
  hot: getHotRanking,
  rating: getRatingRanking,
  download: getDownloadRanking,
  follow: getFollowRanking
};

onMounted(() => {
  loadRanking();
});

const loadRanking = async (reset = false) => {
  if (reset) {
    offset.value = 0;
    games.value = [];
    hasMore.value = true;
  }

  loading.value = true;

  try {
    const apiFn = rankingApiMap[activeTab.value as keyof typeof rankingApiMap];
    if (!apiFn) throw new Error('未知的榜单类型');

    // 1. 调用接口
    const response = await apiFn({ limit, offset: offset.value });
    console.log(' API 原始响应:', response);

    // 2. 万能兼容逻辑：无论拦截器是否解包，或者是否漏写 return，都能安全获取
    // 优先级：response.data (未解包) > response (已解包) > {} (兜底)
    const resultData = response?.data || response || {};

    // 3. 安全读取 games
    const newGames = resultData.games || [];
    console.log(' 解析后的游戏列表:', newGames);

    if (reset) {
      games.value = newGames;
    } else {
      games.value = [...games.value, ...newGames];
    }

    hasMore.value = newGames.length >= limit;
    offset.value += newGames.length;
  } catch (error: any) {
    console.error('❌ 加载排行榜失败:', error);
    ElMessage.error(error.message || '加载失败');
  } finally {
    loading.value = false;
  }
};

// ... existing code ...

const handleTabChange = () => {
  loadRanking(true);
};

const loadMore = async () => {
  loadingMore.value = true;
  await loadRanking(false);
  loadingMore.value = false;
};

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
.discover-page {
  background: #1b2838;
  min-height: 100%;

  .ranking-header {
    padding: 20px;
    background: #16202d;
    border-bottom: 1px solid #2a475e;

    h2 {
      font-size: 24px;
      color: #c7d5e0;
      margin: 0 0 8px;
    }

    .update-tip {
      font-size: 12px;
      color: #8f98a0;
      margin: 0;
    }
  }

  .ranking-tabs {
    background: #16202d;
    border-bottom: 1px solid #2a475e;
    padding: 0 20px;

    :deep(.el-tabs__header) {
      margin: 0;
    }

    :deep(.el-tabs__item) {
      color: #8f98a0;

      &.is-active {
        color: #66c0f4;
      }
    }

    :deep(.el-tabs__active-bar) {
      background-color: #66c0f4;
    }
  }

  .ranking-list {
    background: #16202d;
  }

  .loading-state,
  .empty-state {
    padding: 60px 20px;
    text-align: center;
    color: #8f98a0;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 15px;

    .el-icon {
      font-size: 40px;
      color: #66c0f4;
    }
  }

  .load-more {
    padding: 20px;
    text-align: center;
    background: #16202d;

    .el-button {
      background: #2a475e;
      border-color: #2a475e;
      color: #c7d5e0;

      &:hover {
        background: #66c0f4;
        border-color: #66c0f4;
      }
    }
  }
}
</style>
