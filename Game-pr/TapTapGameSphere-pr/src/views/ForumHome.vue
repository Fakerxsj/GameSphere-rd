
<template>
  <div class="forum-home">
    <div class="page-header">
      <h1>游戏论坛</h1>
      <p class="subtitle">发现精彩讨论，分享游戏体验</p>
    </div>

    <div class="forum-sections">
      <section v-if="followedForums.length > 0" class="forum-block">
        <div class="section-header">
          <h2 class="section-title">
            <el-icon><Star /></el-icon>
            关注的游戏论坛
          </h2>
          <el-button text type="primary" @click="$router.push('/forum')">
            查看全部 ›
          </el-button>
        </div>
        <div class="game-grid">
          <ForumGameCard
              v-for="game in followedForums"
              :key="game.id"
              :game="game"
              @click="goToGameForum(game.id)"
          />
        </div>
      </section>

      <section class="forum-block">
        <div class="section-header">
          <h2 class="section-title">
            <el-icon><TrendCharts /></el-icon>
            热门游戏论坛
          </h2>
        </div>
        <div class="game-grid">
          <ForumGameCard
              v-for="game in hotForums"
              :key="game.id"
              :game="game"
              @click="goToGameForum(game.id)"
          />
        </div>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Star, TrendCharts } from '@element-plus/icons-vue'
import { getForumHomeData } from '@/api/forum'
import type { GameBriefVO } from '@/types/forum'
import ForumGameCard from '@/components/Forum/ForumGameCard.vue'

const router = useRouter()
const followedForums = ref<GameBriefVO[]>([])
const hotForums = ref<GameBriefVO[]>([])

const goToGameForum = (gameId: number) => {
  router.push(`/forum/game/${gameId}`)
}

onMounted(async () => {
  try {
    const data = await getForumHomeData()
    followedForums.value = data.followedForums || []
    hotForums.value = data.hotForums || []
  } catch (error) {
    console.error('获取论坛首页数据失败:', error)
    ElMessage.error('加载论坛数据失败')
  }
})
</script>

<style scoped lang="scss">
.forum-home {
  padding: 20px;
  background: #1b2838;
  min-height: 100vh;

  .page-header {
    margin-bottom: 32px;

    h1 {
      font-size: 28px;
      color: #c7d5e0;
      margin: 0 0 8px;
      font-weight: bold;
    }

    .subtitle {
      font-size: 14px;
      color: #8f98a0;
      margin: 0;
    }
  }

  .forum-sections {
    .forum-block {
      margin-bottom: 40px;

      .section-header {
        display: flex;
        align-items: center;
        justify-content: space-between;
        margin-bottom: 20px;

        .section-title {
          display: flex;
          align-items: center;
          gap: 8px;
          font-size: 20px;
          color: #c7d5e0;
          margin: 0;
          font-weight: 600;

          .el-icon {
            font-size: 24px;
            color: #66c0f4;
          }
        }
      }

      .game-grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
        gap: 16px;
      }
    }
  }
}
</style>
