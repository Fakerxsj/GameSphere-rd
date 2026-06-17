<template>
  <div class="home-page">
    <div class="section-title">
      <h3>{{ title }}</h3>
      <p>{{ subtitle }}</p>
    </div>

    <div v-if="loading" class="loading-state">
      <el-icon :size="40" class="is-loading"><Loading /></el-icon>
      <span>{{ loadingText }}</span>
    </div>

    <div v-else-if="games.length > 0" class="game-grid">
      <GameCard
          v-for="game in games"
          :key="game.id"
          :game="game"
          @click="handleGameClick(game)"
      />
    </div>

    <div v-else-if="searchKeyword" class="loading-state">
      <el-icon :size="40"><Search /></el-icon>
      <p>未找到与 "{{ searchKeyword }}" 相关的游戏</p>
      <el-button @click="clearSearch">清除搜索</el-button>
    </div>

    <div v-else class="loading-state">
      <p>暂无游戏数据</p>
      <el-button @click="handleCrawlMore" :loading="crawlLoading">
        爬取更多游戏
      </el-button>
    </div>

    <button class="refresh-fab" @click="handleCrawlMore" :disabled="crawlLoading">
      <el-icon v-if="crawlLoading" class="is-loading"><Loading /></el-icon>
      <el-icon v-else><Refresh /></el-icon>
    </button>
  </div>
</template>



<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { getHomeRecommendation, triggerCrawl } from '@/api/recommendation';
import { getGameList, clickGame } from '@/api/game';
import GameCard from '@/components/Game/GameCard.vue';
import EmptyState from '@/components/Common/EmptyState.vue';
import { ElMessage } from 'element-plus';
import { Loading, Refresh, Search } from '@element-plus/icons-vue';
import { useUserStore } from '@/stores/modules/user';

interface Game {
  id: number;
  name: string;
  coverImage: string;
  videoUrl?: string;
  ratingScore: number;
  developer: string;
}

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();
const games = ref<Game[]>([]);
const loading = ref(false);
const crawlLoading = ref(false);
const loadingText = ref('加载中...');
const searchKeyword = ref('');

const title = computed(() => {
  if (searchKeyword.value) {
    return `搜索结果：${searchKeyword.value}`;
  }
  if (!userStore.token) return '热门推荐';
  return userStore.userInfo ? '为你推荐' : '热门推荐';
});

const subtitle = computed(() => {
  if (searchKeyword.value) {
    return `找到 ${games.value.length} 个相关游戏`;
  }
  if (!userStore.token) return '当前最热门的游戏推荐';
  return userStore.userInfo ? '基于你的游玩习惯生成的个性化推荐' : '当前最热门的游戏推荐';
});

onMounted(async () => {
  await loadGames();
});

watch(() => route.query.keyword, (newKeyword) => {
  searchKeyword.value = (newKeyword as string) || '';
  loadGames();
});

const loadGames = async () => {
  loading.value = true;
  loadingText.value = searchKeyword.value ? '正在搜索游戏...' : '加载游戏数据...';

  try {
    if (searchKeyword.value) {
      await loadSearchResults();
    } else {
      await loadRecommendations();
    }
  } catch (error) {
    console.error('❌ 加载游戏失败:', error);
    ElMessage.error('加载游戏失败，请查看控制台');
  } finally {
    loading.value = false;
  }
};

const loadRecommendations = async () => {
  try {
    const response = await getHomeRecommendation();
    console.log('🔍 API 原始返回:', response);

    const apiData = response.data || response;
    console.log('📦 解析后的数据:', apiData);

    if (apiData.needCrawl) {
      ElMessage.info('数据库暂无数据，正在启动爬取任务...');
      await doCrawl();
      return;
    }

    const recommendationData = apiData.response;
    console.log('🎮 推荐游戏列表:', recommendationData);

    if (recommendationData && recommendationData.games && recommendationData.games.length > 0) {
      games.value = recommendationData.games;
      const reason = recommendationData.reason || '热门推荐';
      console.log(`✅ 成功加载 ${games.value.length} 个游戏（${reason}）`);
    } else {
      games.value = [];
      console.warn('️ 没有获取到游戏数据');
    }
  } catch (error) {
    console.error('❌ 加载推荐失败:', error);
    throw error;
  }
};

const loadSearchResults = async () => {
  try {
    const data = await getGameList({
      keyword: searchKeyword.value,
      pageNum: 1,
      pageSize: 50
    });

    console.log('🔍 搜索结果:', data);

    if (data && data.records && data.records.length > 0) {
      games.value = data.records.map((item: any) => ({
        id: item.id,
        name: item.name,
        coverImage: item.coverImage,
        videoUrl: item.videoUrl,
        ratingScore: item.ratingScore,
        developer: item.developer
      }));
      console.log(`✅ 找到 ${games.value.length} 个游戏`);
    } else {
      games.value = [];
      console.warn('⚠️ 未找到匹配的游戏');
    }
  } catch (error) {
    console.error('❌ 搜索失败:', error);
    throw error;
  }
};

const doCrawl = async () => {
  loading.value = true;
  crawlLoading.value = true;
  loadingText.value = userStore.token
      ? '正在根据你的喜好爬取游戏...'
      : '正在爬取热门游戏...';

  try {
    const crawlResult = await triggerCrawl();
    console.log('📥 爬取结果:', crawlResult);

    const data = crawlResult;

    if (data) {
      const savedCount = data.saved || 0;
      const skippedCount = data.skipped || 0;
      const totalCount = data.total || 0;

      console.log(`📊 爬取统计 - 总数: ${totalCount}, 新增: ${savedCount}, 跳过: ${skippedCount}`);

      if (savedCount > 0) {
        ElMessage.success(`为你新增了 ${savedCount} 个游戏`);
      } else if (totalCount === 0) {
        ElMessage.warning('未能从 IGDB 获取到新游戏，请稍后重试');
      } else {
        ElMessage.info('暂时没有更多新游戏了（所有游戏已存在）');
      }

      await new Promise(resolve => setTimeout(resolve, 1000));
      await loadGames();
    } else {
      ElMessage.error('爬取失败：未收到响应数据');
    }
  } catch (error: any) {
    console.error('❌ Crawl failed', error);
    ElMessage.error(error.message || '爬取失败');
  } finally {
    loading.value = false;
    crawlLoading.value = false;
  }
};

const handleCrawlMore = async () => {
  crawlLoading.value = true;
  try {
    const crawlResult = await triggerCrawl();
    console.log('📥 刷新结果:', crawlResult);

    const data = crawlResult;

    if (data) {
      const savedCount = data.saved || 0;
      const skippedCount = data.skipped || 0;
      const totalCount = data.total || 0;

      console.log(`📊 爬取统计 - 总数: ${totalCount}, 新增: ${savedCount}, 跳过: ${skippedCount}`);

      if (savedCount > 0) {
        ElMessage.success(`为你新增了 ${savedCount} 个游戏`);
      } else if (totalCount === 0) {
        ElMessage.warning('未能从 IGDB 获取到新游戏，请稍后重试');
      } else {
        ElMessage.info('暂时没有更多新游戏了（所有游戏已存在）');
      }

      await loadGames();
    } else {
      ElMessage.error('获取失败：未收到响应数据');
    }
  } catch (error: any) {
    console.error('❌ 刷新失败:', error);
    ElMessage.error(error.message || '获取失败');
  } finally {
    crawlLoading.value = false;
  }
};

const handleGameClick = async (game: Game) => {
  try {
    await clickGame(game.id);
  } catch (e) {
    console.warn('Click report failed', e);
  }
  router.push(`/game/${game.id}`);
};

const clearSearch = () => {
  searchKeyword.value = '';
  router.push({ path: '/home' });
};
</script>

<style scoped lang="scss">
.home-page {
  position: relative;
  min-height: 100%;

  .section-title {
    margin: 10px 0 20px;
    h3 { font-size: 20px; color: #c7d5e0; margin-bottom: 5px; }
    p { font-size: 12px; color: #8f98a0; }
  }

  .game-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
    gap: 20px;
  }

  .loading-state {
    text-align: center;
    padding: 60px 20px;
    color: #8f98a0;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 15px;
    .el-icon { font-size: 40px; color: #66c0f4; }
    span { font-size: 14px; }
    p { font-size: 14px; margin: 0; }
  }

  .refresh-fab {
    position: fixed;
    bottom: 40px;
    right: 40px;
    width: 60px;
    height: 60px;
    background: linear-gradient(135deg, #66c0f4 0%, #1a9fff 100%);
    border: none;
    border-radius: 50%;
    color: #fff;
    font-size: 24px;
    box-shadow: 0 4px 16px rgba(26, 159, 255, 0.4);
    cursor: pointer;
    z-index: 999;
    transition: all 0.3s ease;
    &:hover { transform: scale(1.1); box-shadow: 0 6px 20px rgba(26, 159, 255, 0.6); }
    &:active { transform: scale(0.95); }
  }
}
</style>
