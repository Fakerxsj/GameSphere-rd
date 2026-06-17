<template>
  <div class="friend-center">
    <div class="header">
      <h2>好友中心</h2>
      <el-input
          v-model="searchQuery"
          placeholder="搜索好友..."
          prefix-icon="Search"
          class="search-input"
      />
    </div>

    <div class="content">
      <div class="tab-bar">
        <div
            :class="['tab-item', { active: activeTab === 'messages' }]"
            @click="activeTab = 'messages'"
        >
          <el-icon><ChatDotRound /></el-icon>
          <span>消息</span>
          <el-badge
              v-if="messageStore.totalUnreadCount > 0"
              :value="messageStore.totalUnreadCount"
              class="badge"
          />
        </div>
        <div
            :class="['tab-item', { active: activeTab === 'contacts' }]"
            @click="activeTab = 'contacts'"
        >
          <el-icon><User /></el-icon>
          <span>联系人</span>
          <el-badge
              v-if="friendStore.unreadRequestCount > 0"
              :value="friendStore.unreadRequestCount"
              class="badge"
          />
        </div>
      </div>

      <div class="tab-content">
        <div v-if="activeTab === 'messages'" class="message-list">
          <ConversationItem
              v-for="conversation in messageStore.conversations"
              :key="conversation.userId"
              :conversation="conversation"
              @click="handleChatClick"
          />
          <EmptyState v-if="messageStore.conversations.length === 0" description="暂无消息" />
        </div>

        <div v-if="activeTab === 'contacts'" class="contact-list">
          <div v-if="friendStore.friendRequests.length > 0" class="request-section">
            <div class="section-title">新的朋友</div>
            <FriendRequestItem
                v-for="request in friendStore.friendRequests"
                :key="request.id"
                :request="request"
                @handled="handleRequestHandled"
            />
          </div>

          <div class="friend-section">
            <div class="section-title">
              我的好友 ({{ friendStore.friendList.length }})
            </div>
            <FriendListItem
                v-for="friend in filteredFriends"
                :key="friend.userId"
                :friend="friend"
                @click="handleChatClick"
                @delete="handleFriendDeleted"
                @update-remark="handleUpdateRemark"
            />
            <EmptyState v-if="friendStore.friendList.length === 0" description="暂无好友" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ChatDotRound, User } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useFriendStore } from '@/stores/modules/friend'
import { useMessageStore } from '@/stores/modules/message'
import { useUserStore } from '@/stores/modules/user'
import ConversationItem from '@/components/Friend/ConversationItem.vue'
import FriendListItem from '@/components/Friend/FriendListItem.vue'
import FriendRequestItem from '@/components/Friend/FriendRequestItem.vue'
import EmptyState from '@/components/Common/EmptyState.vue'

const router = useRouter()
const userStore = useUserStore()
const friendStore = useFriendStore()
const messageStore = useMessageStore()

const activeTab = ref('messages')
const searchQuery = ref('')

const filteredFriends = computed(() => {
  if (!searchQuery.value) {
    return friendStore.friendList
  }
  const query = searchQuery.value.toLowerCase()
  return friendStore.friendList.filter(friend =>
      friend.nickname.toLowerCase().includes(query) ||
      (friend.remark && friend.remark.toLowerCase().includes(query))
  )
})

const handleChatClick = (friendId: number) => {
  console.log('🔗 点击好友，准备跳转到聊天页面:', friendId)
  router.push({ name: 'ChatDetail', params: { friendId: friendId.toString() } })
}

const handleRequestHandled = async () => {
  await friendStore.fetchFriendRequests()
  await friendStore.fetchFriendList()
}

const handleFriendDeleted = () => {
  ElMessage.success('删除成功')
}

const handleUpdateRemark = async (userId: number) => {
  try {
    const { value } = await ElMessageBox.prompt('请输入备注名', '修改备注', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPattern: /.+/,
      inputErrorMessage: '备注名不能为空'
    })
    await friendStore.updateFriendRemark(userId, value)
    ElMessage.success('修改成功')
  } catch (error) {
  }
}

onMounted(async () => {
  await friendStore.fetchFriendRequests()
  await friendStore.fetchFriendList()
  await messageStore.fetchConversations()
  await messageStore.fetchUnreadCount()

  if (userStore.userInfo?.id) {
    messageStore.connectWebSocket(userStore.userInfo.id)
  }
})

onUnmounted(() => {
  messageStore.disconnectWebSocket()
})
</script>


<style scoped lang="scss">
.friend-center {
  max-width: 1200px;
  margin: 0 auto;
  background: #1b2838;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);

  .header {
    padding: 20px 24px;
    background: #171a21;
    border-bottom: 1px solid #2a475e;

    h2 {
      color: #c7d5e0;
      font-size: 20px;
      margin: 0 0 16px 0;
    }

    .search-input {
      :deep(.el-input__wrapper) {
        background: #2a475e;
        box-shadow: none;
        border: 1px solid #1b2838;

        .el-input__inner {
          color: #c7d5e0;

          &::placeholder {
            color: #8f98a0;
          }
        }
      }
    }
  }

  .content {
    display: flex;
    flex-direction: column;
    min-height: 600px;

    .tab-bar {
      display: flex;
      background: #171a21;
      border-bottom: 1px solid #2a475e;

      .tab-item {
        flex: 1;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 8px;
        padding: 16px;
        color: #8f98a0;
        cursor: pointer;
        transition: all 0.2s;
        position: relative;

        &:hover {
          color: #c7d5e0;
          background: #2a475e;
        }

        &.active {
          color: #66c0f4;
          background: #2a475e;

          &::after {
            content: '';
            position: absolute;
            bottom: 0;
            left: 0;
            right: 0;
            height: 2px;
            background: #66c0f4;
          }
        }

        .el-icon {
          font-size: 20px;
        }

        .badge {
          position: absolute;
          top: 8px;
          right: 20%;
        }
      }
    }

    .tab-content {
      flex: 1;
      overflow-y: auto;
      max-height: calc(100vh - 300px);

      .message-list,
      .contact-list {
        padding-bottom: 16px;
      }

      .request-section {
        border-bottom: 1px solid #2a475e;

        .section-title {
          padding: 12px 16px;
          font-size: 14px;
          color: #8f98a0;
          background: #171a21;
        }
      }

      .friend-section {
        .section-title {
          padding: 12px 16px;
          font-size: 14px;
          color: #8f98a0;
          background: #171a21;
          border-bottom: 1px solid #2a475e;
        }
      }
    }
  }
}
</style>
