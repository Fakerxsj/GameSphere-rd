<template>
  <div class="user-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户列表</span>
          <div class="header-actions">
            <el-input
                v-model="searchQuery"
                placeholder="搜索用户昵称/账号"
                style="width: 250px; margin-right: 10px;"
                @keyup.enter="handleSearch"
            />
            <el-button type="primary" @click="handleSearch">搜索</el-button>
          </div>
        </div>
      </template>

      <el-table :data="users" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="头像" width="80">
          <template #default="{ row }">
            <el-avatar :size="40" :src="row.avatar || defaultAvatar" />
          </template>
        </el-table-column>
        <el-table-column prop="nickname" label="昵称" width="150" />
        <el-table-column prop="username" label="账号" width="150" />
        <el-table-column prop="email" label="邮箱" width="200" />
        <el-table-column label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="row.roleId === 1 ? 'danger' : 'success'" size="small">
              {{ row.roleId === 1 ? '管理员' : '普通用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="180" />
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="viewBehavior(row)">
              查看行为
            </el-button>
            <el-button link type="warning" size="small" @click="resetPassword(row)">
              重置密码
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
import request from '@/utils/request'

const loading = ref(false)
const searchQuery = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const defaultAvatar = 'https://via.placeholder.com/40x40?text=U'

interface UserItem {
  id: number
  nickname: string
  username: string
  email: string
  avatar: string
  roleId: number
  status: number
  createTime: string
}

const users = ref<UserItem[]>([])

const handleSearch = () => {
  currentPage.value = 1
  loadUsers()
}

const loadUsers = async () => {
  loading.value = true
  try {
    const data = await request({
      url: '/user/list',
      method: 'get',
      params: {
        pageNum: currentPage.value,
        pageSize: pageSize.value,
        keyword: searchQuery.value || undefined
      }
    })
    users.value = data.records || []
    total.value = data.total || 0
  } catch (error) {
    ElMessage.error('加载用户列表失败')
  } finally {
    loading.value = false
  }
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  loadUsers()
}

const handleCurrentChange = (page: number) => {
  currentPage.value = page
  loadUsers()
}

const viewBehavior = (user: UserItem) => {
  ElMessage.info(`查看用户 ${user.nickname} 的行为记录 - 功能开发中`)
}

const resetPassword = async (user: UserItem) => {
  try {
    await ElMessageBox.confirm(
        `确定要重置用户 "${user.nickname}" 的密码吗？`,
        '确认操作',
        { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
    )
    ElMessage.success('密码已重置为默认密码')
  } catch {
    // 用户取消
  }
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped lang="scss">
.user-list {
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
