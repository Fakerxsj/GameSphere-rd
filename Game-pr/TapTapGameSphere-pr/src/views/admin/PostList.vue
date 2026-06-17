<template>
  <div class="post-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>帖子管理</span>
          <div class="header-actions">
            <el-input
                v-model="searchQuery"
                placeholder="搜索帖子内容"
                style="width: 250px; margin-right: 10px;"
                @keyup.enter="handleSearch"
            />
            <el-button type="primary" @click="handleSearch">搜索</el-button>
          </div>
        </div>
      </template>

      <el-table :data="posts" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="gameName" label="游戏" width="180" />
        <el-table-column prop="userNickname" label="发布者" width="120" />
        <el-table-column prop="content" label="内容" show-overflow-tooltip />
        <el-table-column prop="likeCount" label="点赞数" width="100" />
        <el-table-column prop="replyCount" label="回复数" width="100" />
        <el-table-column prop="createTime" label="发布时间" width="180" />
        <el-table-column label="操作" fixed="right" width="120">
          <template #default="{ row }">
            <el-button link type="danger" size="small" @click="deletePost(row)">
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
import { getAdminPosts, deleteAdminPost } from '@/api/admin'

const loading = ref(false)
const searchQuery = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

interface PostItem {
  id: number
  gameName: string
  userNickname: string
  content: string
  likeCount: number
  replyCount: number
  createTime: string
}

const posts = ref<PostItem[]>([])

const handleSearch = () => {
  currentPage.value = 1
  loadPosts()
}

const loadPosts = async () => {
  loading.value = true
  try {
    const data = await getAdminPosts({
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchQuery.value || undefined
    })
    posts.value = data.records || []
    total.value = data.total || 0
  } catch (error) {
    ElMessage.error('加载帖子列表失败')
  } finally {
    loading.value = false
  }
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  loadPosts()
}

const handleCurrentChange = (page: number) => {
  currentPage.value = page
  loadPosts()
}

const deletePost = async (post: PostItem) => {
  try {
    await ElMessageBox.confirm(
        `确定要删除这个帖子吗？`,
        '确认删除',
        { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
    )
    await deleteAdminPost(post.id)
    ElMessage.success('帖子已删除')
    loadPosts()
  } catch {
    // 用户取消
  }
}

onMounted(() => {
  loadPosts()
})
</script>

<style scoped lang="scss">
.post-list {
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
