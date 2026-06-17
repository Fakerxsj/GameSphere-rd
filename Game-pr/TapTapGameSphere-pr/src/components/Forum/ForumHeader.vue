
<template>
  <div class="forum-header">
    <div class="header-banner">
      <img
          :src="gameInfo.backgroundImage || gameInfo.coverImage || defaultBanner"
          class="banner-img"
          loading="lazy"
      />
      <div class="banner-mask"></div>
    </div>

    <div class="header-content">
      <div class="game-info-section">
        <img :src="gameInfo.coverImage || defaultCover" class="game-cover" />
        <div class="game-details">
          <h1 class="game-name">{{ gameInfo.gameName }}</h1>
          <div class="game-stats">
            <span class="stat-item">{{ formatCount(gameInfo.followCount) }} 关注</span>
            <span class="stat-divider">·</span>
            <span class="stat-item">{{ formatCount(gameInfo.postCount) }} 帖子</span>
          </div>
        </div>
      </div>

      <el-button
          class="follow-btn"
          :type="isFollowed ? 'primary' : 'default'"
          @click="handleFollowToggle"
      >
        {{ isFollowed ? '已关注' : '关注' }}
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { GameForumVO } from '@/Types/Forum'

const props = defineProps<{
  gameInfo: GameForumVO
  isFollowed: boolean
}>()

const emit = defineEmits<{
  (e: 'follow'): void
  (e: 'unfollow'): void
}>()

const defaultCover = 'https://via.placeholder.com/120x120?text=No+Image'
const defaultBanner = 'https://via.placeholder.com/1200x300?text=No+Image'

const formatCount = (num: number) => {
  if (!num) return '0'
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + '万'
  }
  return num.toString()
}

const handleFollowToggle = () => {
  if (props.isFollowed) {
    emit('unfollow')
  } else {
    emit('follow')
  }
}
</script>

<style scoped lang="scss">
.forum-header {
  position: relative;

  .header-banner {
    position: relative;
    width: 100%;
    height: 240px;
    overflow: hidden;

    .banner-img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .banner-mask {
      position: absolute;
      bottom: 0;
      left: 0;
      right: 0;
      height: 120px;
      background: linear-gradient(to top, #1b2838, transparent);
    }
  }

  .header-content {
    display: flex;
    align-items: center;
    justify-content: space-between;
    max-width: 1200px;
    margin: -60px auto 0;
    padding: 0 20px 20px;
    position: relative;
    z-index: 10;

    .game-info-section {
      display: flex;
      align-items: center;
      gap: 16px;

      .game-cover {
        width: 100px;
        height: 100px;
        border-radius: 12px;
        border: 3px solid #1b2838;
        object-fit: cover;
      }

      .game-details {
        .game-name {
          font-size: 24px;
          color: #c7d5e0;
          margin: 0 0 8px;
          font-weight: bold;
        }

        .game-stats {
          display: flex;
          align-items: center;
          gap: 8px;
          font-size: 14px;
          color: #8f98a0;

          .stat-divider {
            color: #2a475e;
          }
        }
      }
    }

    .follow-btn {
      padding: 10px 32px;
      border-radius: 20px;
      font-size: 15px;
      font-weight: 600;
    }
  }
}
</style>
