
<template>
  <div class="game-forum" v-if="gameInfo">
    <ForumHeader
        :game-info="gameInfo"
        :is-followed="isFollowed"
        @follow="handleFollow"
        @unfollow="handleUnfollow"
    />

    <div class="forum-content">
      <ForumTabs
          :sections="sections"
          :active-section="activeSection"
          @change-section="handleSectionChange"
      />

      <div class="post-list-wrapper">
        <div class="filter-bar">
          <div class="filter-tabs">
            <span
                v-for="tab in filterTabs"
                :key="tab.value"
                class="filter-tab"
                :class="{ active: activeFilter === tab.value }"
                @click="handleFilterChange(tab.value)"
            >
              {{ tab.label }}
            </span>
          </div>
        </div>

        <ForumPostList
            :posts="posts"
            :loading="loading"
            :replying-to="replyingTo"
            @like="handleLikePost"
            @reply="handleReplyPost"
        >
          <template #reply-editor="{ postId }">
            <CommentEditor
                :game-id="gameId"
                :parent-id="postId"
                placeholder="回复评论..."
                @submit="handleReplySubmit"
            />
          </template>
        </ForumPostList>

        <div class="comment-editor-wrapper">
          <CommentEditor
              :game-id="gameId"
              placeholder="发表你的看法..."
              @submit="handleCommentSubmit"
          />
        </div>
      </div>
    </div>
  </div>

  <div v-else class="loading-container">
    <el-icon :size="40" class="is-loading"><Loading /></el-icon>
    <span>加载中...</span>
  </div>
</template>

<script setup lang="ts">import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import { getGameForumInfo } from '@/api/forum'
import { getGameComments, submitComment, likeComment, unlikeComment } from '@/api/comment'
import type { GameForumVO, FourmSectionVO, ForumPostVO, CommentVO } from '@/Types/Forum'
import ForumHeader from '@/components/Forum/ForumHeader.vue'
import ForumTabs from '@/components/Forum/ForumTabs.vue'
import ForumPostList from '@/components/Forum/ForumPostList.vue'
import CommentEditor from '@/components/Forum/CommentEditor.vue'

const route = useRoute()
const gameId = Number(route.params.id)

const gameInfo = ref<GameForumVO | null>(null)
const isFollowed = ref(false)
const sections = ref<FourmSectionVO[]>([])
const posts = ref<ForumPostVO[]>([])
const loading = ref(false)

const activeSection = ref<number>(0)
const activeFilter = ref('all')

const replyingTo = ref<number | null>(null)


const filterTabs = [
  { label: '全部', value: 'all' },
  { label: '官方', value: 'official' },
  { label: '精华', value: 'essence' },
  { label: '视频', value: 'video' }
]

const handleFollow = async () => {
  isFollowed.value = true
  if (gameInfo.value) {
    gameInfo.value.followCount++
  }
  ElMessage.success('关注成功')
}

const handleUnfollow = async () => {
  isFollowed.value = false
  if (gameInfo.value) {
    gameInfo.value.followCount--
  }
  ElMessage.success('已取消关注')
}

const handleSectionChange = (sectionId: number) => {
  activeSection.value = sectionId
  loadPosts()
}

const handleFilterChange = (filter: string) => {
  activeFilter.value = filter
  loadPosts()
}

const handleLikePost = async (postId: number) => {
  const post = posts.value.find(p => p.id === postId)
  if (!post) return

  try {
    if (post.isLiked) {
      await unlikeComment(postId)
      post.likeCount--
      post.isLiked = false
    } else {
      await likeComment(postId)
      post.likeCount++
      post.isLiked = true
    }
  } catch (error) {
    console.error('点赞失败:', error)
    ElMessage.error('操作失败')
  }
}


const handleReplyPost = (postId: number) => {
  console.log('📝 ========== GameForum.handleReplyPost ==========')
  console.log('📝 传入的 postId:', postId, '类型:', typeof postId)
  console.log('📝 当前 replyingTo.value:', replyingTo.value, '类型:', typeof replyingTo.value)
  console.log('📝 比较结果 (replyingTo.value === postId):', replyingTo.value === postId)

  const newValue = replyingTo.value === postId ? null : postId
  console.log('📝 准备设置的新值:', newValue)

  replyingTo.value = newValue

  console.log('📝 设置后 replyingTo.value:', replyingTo.value)
  console.log('📝 ==============================================')
}


const handleReplySubmit = async (content: string) => {
  if (!replyingTo.value) return

  try {
    await submitComment({
      gameId: gameId,
      content: content,
      parentId: replyingTo.value
    })
    ElMessage.success('回复成功')
    replyingTo.value = null
    await loadPosts()
  } catch (error) {
    console.error('回复失败:', error)
    ElMessage.error('回复失败')
  }
}

const handleCommentSubmit = async (content: string) => {
  try {
    await submitComment({
      gameId: gameId,
      content: content,
      parentId: 0
    })
    ElMessage.success('评论成功')
    await loadPosts()
  } catch (error) {
    console.error('评论失败:', error)
    ElMessage.error('评论失败')
  }
}

const loadPosts = async () => {
  loading.value = true
  try {
    const comments = await getGameComments(gameId)
    console.log('📝 评论数据:', comments) // 调试用
    posts.value = (comments || []).map((comment: any) => ({
      id: comment.id,
      gameId: comment.gameId,
      userId: comment.userId,
      userNickname: comment.userNickname,
      userAvatar: comment.userAvatar,
      content: comment.content,
      images: comment.images ? (typeof comment.images === 'string' ? comment.images.split(',') : comment.images) : [],
      likeCount: comment.likeCount,
      replyCount: comment.replyCount,
      isTop: comment.isTop === 1,
      isLiked: comment.isLiked || false,
      createTime: comment.createTime
    }))
  } catch (error) {
    console.error('加载帖子失败:', error)
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  try {
    const forumInfo = await getGameForumInfo(gameId)
    console.log(' 游戏论坛信息:', forumInfo) // 调试用

    if (forumInfo) {
      gameInfo.value = forumInfo
      isFollowed.value = forumInfo.isFollowed || false
      sections.value = forumInfo.sections || []
      await loadPosts()
    }
  } catch (error) {
    console.error('加载论坛信息失败:', error)
    ElMessage.error('加载失败')
  }
})
</script>

<style scoped lang="scss">
.game-forum {
  background: #1b2838;
  min-height: 100vh;

  .forum-content {
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;

    .post-list-wrapper {
      margin-top: 20px;

      .filter-bar {
        display: flex;
        align-items: center;
        padding: 12px 0;
        border-bottom: 1px solid #2a475e;
        margin-bottom: 16px;

        .filter-tabs {
          display: flex;
          gap: 24px;

          .filter-tab {
            font-size: 14px;
            color: #8f98a0;
            cursor: pointer;
            transition: color 0.2s;

            &:hover {
              color: #c7d5e0;
            }

            &.active {
              color: #66c0f4;
              font-weight: 600;
            }
          }
        }
      }

      .comment-editor-wrapper {
        margin-top: 24px;
        padding-top: 24px;
        border-top: 1px solid #2a475e;
      }
    }
  }
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background: #1b2838;
  color: #66c0f4;
  gap: 16px;
  font-size: 16px;
}
</style>
