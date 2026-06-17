<template>
  <div class="category-home">
    <div v-if="loading" class="loading-state">
      <el-icon class="is-loading"><Loading /></el-icon>
      <span>加载中...</span>
    </div>

    <div v-else-if="categories.length === 0" class="empty-state">
      <el-empty description="暂无分类数据">
        <el-button type="primary" @click="fetchData">刷新</el-button>
      </el-empty>
    </div>

    <div v-else class="category-sections">
      <div v-for="cat in categories" :key="cat.categoryId" class="section">
        <div class="section-header" @click="$router.push(`/category/${cat.categoryId}`)">
          <h3>{{ cat.categoryName }} ></h3>
        </div>

        <div v-if="cat.games && cat.games.length > 0" class="games-scroll">
          <GameSmallCard
              v-for="game in cat.games"
              :key="game.id"
              :game="game"
              @click="$router.push(`/game/${game.id}`)"
          />
        </div>
        <div v-else class="no-games">该分类下暂无游戏</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { Loading } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import { getCategoryHome } from '@/api/category';
import GameSmallCard from '@/components/Game/GameSmallCard.vue';

interface CategoryData {
  categoryId: number;
  categoryName: string;
  games: any[];
}

const categories = ref<CategoryData[]>([]);
const loading = ref(false);

const fetchData = async () => {
  loading.value = true;
  try {
    const data = await getCategoryHome();
    console.log('📦 分类首页数据:', data);

    // 兼容拦截器解包后的数据结构
    const list = Array.isArray(data) ? data : (data?.list || data?.data || []);
    categories.value = list;

    console.log('📂 解析后的分类数量:', categories.value.length);
  } catch (e: any) {
    console.error('❌ 获取分类数据失败:', e);
    ElMessage.error(e.message || '获取分类数据失败');
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  fetchData();
});
</script>

<style scoped lang="scss">
.category-home {
  padding: 20px;

  .loading-state,
  .empty-state {
    text-align: center;
    padding: 50px;
    color: #66c0f4;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 15px;
  }

  .section {
    margin-bottom: 30px;

    .section-header {
      display: flex;
      align-items: center;
      margin-bottom: 15px;
      cursor: pointer;

      h3 {
        font-size: 18px;
        color: #c7d5e0;
        margin: 0;

        &:hover {
          color: #66c0f4;
        }
      }
    }

    .games-scroll {
      display: flex;
      gap: 16px;
      overflow-x: auto;
      padding-bottom: 10px;
      scrollbar-width: thin;
      scrollbar-color: #2a475e #1b2838;
    }

    .no-games {
      color: #8f98a0;
      font-size: 14px;
      padding: 10px 0;
    }
  }
}
</style>
