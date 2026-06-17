NEW_FILE_CODE
<template>
  <div class="forum-game-card" @click="handleClick">
    <div class="card-cover">
      <img :src="game.coverImage || defaultCover" :alt="game.name" loading="lazy" />
    </div>
    <div class="card-info">
      <h3 class="game-name">{{ game.name }}</h3>
      <div class="game-stats">
        <span class="stat-item">
          <el-icon><ChatLineRound /></el-icon>
          {{ formatCount(game.commentCount) }} 新帖
        </span>
        <span class="stat-item" v-if="game.followCount">
          <el-icon><Star /></el-icon>
          {{ formatCount(game.followCount) }} 关注
        </span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ChatLineRound, Star } from '@element-plus/icons-vue'
import type { GameBriefVO } from '@/Types/Forum'

const props = defineProps<{
  game: GameBriefVO
}>()

const emit = defineEmits<{
  (e: 'click'): void
}>()

const defaultCover = 'https://via.placeholder.com/200x280?text=No+Image'

const formatCount = (num: number) => {
  if (!num) return '0'
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + '万'
  }
  return num.toString()
}

const handleClick = () => {
  emit('click')
}
</script>

<style scoped lang="scss">
.forum-game-card {
  display: flex;
  gap: 12px;
  padding: 12px;
  background: #16202d;
  border: 1px solid #2a475e;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background: #1b2838;
    border-color: #66c0f4;
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
  }

  .card-cover {
    flex-shrink: 0;
    width: 60px;
    height: 60px;
    border-radius: 8px;
    overflow: hidden;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }

  .card-info {
    flex: 1;
    min-width: 0;

    .game-name {
      font-size: 15px;
      color: #c7d5e0;
      margin: 0 0 8px;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      font-weight: 600;
    }

    .game-stats {
      display: flex;
      flex-direction: column;
      gap: 4px;

      .stat-item {
        display: flex;
        align-items: center;
        gap: 4px;
        font-size: 12px;
        color: #8f98a0;

        .el-icon {
          font-size: 14px;
        }
      }
    }
  }
}
</style>
