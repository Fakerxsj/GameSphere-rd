<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <el-icon class="stat-icon" color="#66c0f4"><Monitor /></el-icon>
            <div class="stat-info">
              <p class="stat-title">游戏总数</p>
              <p class="stat-value">{{ stats.totalGames }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <el-icon class="stat-icon" color="#409eff"><ChatDotSquare /></el-icon>
            <div class="stat-info">
              <p class="stat-title">帖子总数</p>
              <p class="stat-value">{{ stats.totalPosts }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <el-icon class="stat-icon" color="#67c23a"><Message /></el-icon>
            <div class="stat-info">
              <p class="stat-title">评论总数</p>
              <p class="stat-value">{{ stats.totalComments }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <el-icon class="stat-icon" color="#e6a23c"><User /></el-icon>
            <div class="stat-info">
              <p class="stat-title">用户总数</p>
              <p class="stat-value">{{ stats.totalUsers }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>热门游戏 TOP 5</span>
          </template>
          <el-table :data="stats.hotGames" style="width: 100%" size="small" v-loading="loading">
            <el-table-column prop="rank" label="排名" width="80" />
            <el-table-column prop="name" label="游戏名称" />
            <el-table-column prop="views" label="关注数" width="120" />
            <el-table-column prop="rating" label="评分" width="100" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>高赞帖子 TOP 5</span>
          </template>
          <el-table :data="stats.hotPosts" style="width: 100%" size="small" v-loading="loading">
            <el-table-column prop="rank" label="排名" width="80" />
            <el-table-column prop="title" label="帖子内容" show-overflow-tooltip />
            <el-table-column prop="likes" label="点赞数" width="120" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Monitor, ChatDotSquare, Message, User } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getAdminStats } from '@/api/admin'

const loading = ref(false)

interface HotGame { rank: number; name: string; views: number; rating: number }
interface HotPost { rank: number; title: string; likes: number }

const stats = ref({
  totalGames: 0,
  totalPosts: 0,
  totalComments: 0,
  totalUsers: 0,
  hotGames: [] as HotGame[],
  hotPosts: [] as HotPost[]
})

const loadStats = async () => {
  loading.value = true
  try {
    const data = await getAdminStats()
    stats.value = data
  } catch (error) {
    ElMessage.error('加载统计数据失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped lang="scss">
.dashboard {
  .stat-card {
    background: #16202d;
    border: 1px solid #2a475e;

    :deep(.el-card__body) {
      padding: 20px;
    }

    .stat-content {
      display: flex;
      align-items: center;
      gap: 15px;

      .stat-icon {
        font-size: 48px;
      }

      .stat-info {
        .stat-title {
          color: #8f98a0;
          font-size: 14px;
          margin: 0 0 8px 0;
        }

        .stat-value {
          color: #c7d5e0;
          font-size: 28px;
          font-weight: 600;
          margin: 0;
        }
      }
    }
  }

  :deep(.el-card) {
    background: #16202d;
    border: 1px solid #2a475e;

    .el-card__header {
      background: #1b2838;
      border-bottom: 1px solid #2a475e;
      color: #c7d5e0;
    }

    .el-card__body {
      padding: 0;
    }
  }

  :deep(.el-table) {
    background: transparent;
    color: #c7d5e0;

    &::before {
      display: none;
    }

    th.el-table__cell {
      background: #1b2838 !important;
      color: #c7d5e0 !important;
      border-bottom: 1px solid #2a475e !important;
    }

    td.el-table__cell {
      background: #16202d !important;
      color: #c7d5e0 !important;
      border-bottom: 1px solid #2a475e !important;
    }

    .el-table__row {
      background-color: #16202d !important;

      &:hover > td.el-table__cell {
        background-color: #2a475e !important;
      }
    }
  }
}
</style>
