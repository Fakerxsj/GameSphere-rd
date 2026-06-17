<template>
  <div class="game-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>游戏列表</span>
          <div class="header-actions">
            <el-input
                v-model="searchQuery"
                placeholder="搜索游戏名称"
                style="width: 250px; margin-right: 10px;"
                @keyup.enter="handleSearch"
            />
            <el-button type="primary" @click="handleSearch">搜索</el-button>
          </div>
        </div>
      </template>

      <el-table :data="games" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="封面" width="100">
          <template #default="{ row }">
            <el-image
                :src="row.coverImage || defaultCover"
                style="width: 60px; height: 80px;"
                fit="cover"
            />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="游戏名称" width="200" />
        <el-table-column prop="gameType" label="分类" width="120" />
        <el-table-column prop="developer" label="开发商" width="150" />
        <el-table-column prop="downloadCount" label="下载量" width="100" />
        <el-table-column prop="ratingCount" label="评分人数" width="100" />
        <el-table-column prop="followCount" label="关注数" width="100" />
        <el-table-column prop="commentCount" label="评论数" width="100" />
        <el-table-column prop="ratingScore" label="评分" width="100" />
        <el-table-column label="操作" fixed="right" width="120">
          <template #default="{ row }">
            <el-button link type="danger" size="small" @click="deleteGame(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          style="margin-top: 20px; justify-content: center;"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getGameList } from '@/api/game'

const loading = ref(false)
const searchQuery = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const defaultCover = 'https://via.placeholder.com/60x80?text=Game'

interface GameItem {
  id: number
  name: string
  gameType: string
  developer: string
  coverImage: string
  downloadCount: number
  ratingCount: number
  followCount: number
  commentCount: number
  ratingScore: number
}

const games = ref<GameItem[]>([])

const handleSearch = () => {
  currentPage.value = 1
  loadGames()
}

const loadGames = async () => {
  loading.value = true
  try {
    const data = await getGameList({
      keyword: searchQuery.value || undefined,
      pageNum: currentPage.value,
      pageSize: pageSize.value
    })
    games.value = data.records || []
    total.value = data.total || 0
  } catch (error) {
    ElMessage.error('加载游戏列表失败')
  } finally {
    loading.value = false
  }
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  loadGames()
}

const handleCurrentChange = (page: number) => {
  currentPage.value = page
  loadGames()
}

const deleteGame = async (game: GameItem) => {
  try {
    await ElMessageBox.confirm(
        `确定要删除游戏 "${game.name}" 吗？此操作不可恢复！`,
        '确认删除',
        { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
    )
    ElMessage.success('游戏已删除')
    loadGames()
  } catch {
    // 用户取消
  }
}

onMounted(() => {
  loadGames()
})
</script>

<style scoped lang="scss">
.game-list {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    span {
      color: #c7d5e0;
      font-size: 16px;
      font-weight: 600;
    }

    .header-actions {
      display: flex;
      align-items: center;
    }
  }

  :deep(.el-card) {
    background: #16202d;
    border: 1px solid #2a475e;

    .el-card__header {
      background: #1b2838;
      border-bottom: 1px solid #2a475e;
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

  :deep(.el-pagination) {
    .btn-prev, .btn-next, .el-pager li {
      background: #1b2838;
      color: #c7d5e0;
      border: 1px solid #2a475e;

      &:hover {
        color: #66c0f4;
      }

      &.is-active {
        background: #66c0f4;
        color: #fff;
      }
    }
  }
}
</style>
