<template>
  <div class="forum-stats">
    <el-card>
      <template #header>
        <span>论坛热度排行</span>
      </template>

      <el-table :data="forumStats" v-loading="loading" style="width: 100%">
        <el-table-column prop="rank" label="排名" width="80" />
        <el-table-column label="游戏封面" width="100">
          <template #default="{ row }">
            <el-image
                :src="row.coverImage || defaultCover"
                style="width: 60px; height: 80px;"
                fit="cover"
            />
          </template>
        </el-table-column>
        <el-table-column prop="gameName" label="游戏名称" />
        <el-table-column prop="heatScore" label="热度指数" width="120" />
        <el-table-column prop="postCount" label="帖子数" width="100" />
        <el-table-column prop="commentCount" label="评论数" width="100" />
        <el-table-column prop="favoriteCount" label="关注数" width="100" />
        <el-table-column prop="viewCount" label="下载量" width="120" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getForumStats } from '@/api/admin'

const loading = ref(false)
const defaultCover = 'https://via.placeholder.com/60x80?text=Game'

interface ForumStatItem {
  rank: number
  gameName: string
  coverImage: string
  heatScore: number
  postCount: number
  commentCount: number
  favoriteCount: number
  viewCount: number
}

const forumStats = ref<ForumStatItem[]>([])

const loadStats = async () => {
  loading.value = true
  try {
    const data = await getForumStats()
    forumStats.value = data || []
  } catch (error) {
    ElMessage.error('加载论坛热度数据失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped lang="scss">
.forum-stats {
  :deep(.el-card) {
    background: #16202d;
    border: 1px solid #2a475e;

    .el-card__header {
      background: #1b2838;
      border-bottom: 1px solid #2a475e;
      color: #c7d5e0;
      font-size: 16px;
      font-weight: 600;
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
