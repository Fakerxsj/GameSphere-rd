<template>
  <div class="ranking-card" @click="$emit('click')">
    <div class="rank-number" :class="getRankClass(index)">
      {{ index + 1 }}
    </div>

    <div class="game-info">
      <img :src="game.coverImage || defaultCover" class="game-cover" loading="lazy" />

      <div class="game-detail">
        <h4 class="game-name">{{ game.name }}</h4>

        <div class="game-meta">
          <span class="rating" v-if="game.ratingScore">
            <el-icon><Star /></el-icon>
            {{ game.ratingScore }}
          </span>
          <span class="tags" v-if="game.gameType">{{ game.gameType }}</span>
          <span class="developer" v-if="game.developer">{{ game.developer }}</span>
        </div>

        <div class="game-platform" v-if="game.platform">
          {{ game.platform }}
        </div>

        <div class="game-tags" v-if="game.gameType">
          <span class="tag-item" v-for="(tag, idx) in game.gameType.split(',').slice(0, 3)" :key="idx">
            {{ tag.trim() }}
          </span>
        </div>
      </div>
    </div>

    <div class="game-preview">
      <img
          :src="game.coverImage || defaultCover"
          class="preview-img"
          loading="lazy"
      />
      <div class="preview-mask" v-if="game.videoUrl">
        <el-icon class="play-icon"><VideoPlay /></el-icon>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Star, VideoPlay } from '@element-plus/icons-vue';

const props = defineProps<{
  game: {
    id: number;
    name: string;
    coverImage?: string;
    videoUrl?: string;
    ratingScore?: number | string;
    developer?: string;
    platform?: string;
    gameType?: string;
  };
  index: number;
}>();

defineEmits(['click']);

const defaultCover = 'https://via.placeholder.com/120x120?text=No+Image';

const getRankClass = (index: number) => {
  if (index === 0) return 'rank-first';
  if (index === 1) return 'rank-second';
  if (index === 2) return 'rank-third';
  return '';
};
</script>

<style scoped lang="scss">
.ranking-card {
  display: flex;
  align-items: center;
  padding: 16px;
  background: #16202d;
  border-bottom: 1px solid #2a475e;
  cursor: pointer;
  transition: background 0.2s;

  &:hover {
    background: #1b2838;
  }

  .rank-number {
    width: 32px;
    height: 32px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 18px;
    font-weight: bold;
    color: #8f98a0;
    margin-right: 16px;
    flex-shrink: 0;

    &.rank-first {
      color: #ffd700;
      font-size: 24px;
    }

    &.rank-second {
      color: #c0c0c0;
      font-size: 22px;
    }

    &.rank-third {
      color: #cd7f32;
      font-size: 20px;
    }
  }

  .game-info {
    display: flex;
    flex: 1;
    min-width: 0;

    .game-cover {
      width: 80px;
      height: 80px;
      border-radius: 8px;
      object-fit: cover;
      margin-right: 16px;
      flex-shrink: 0;
    }

    .game-detail {
      flex: 1;
      min-width: 0;
      display: flex;
      flex-direction: column;
      justify-content: center;

      .game-name {
        font-size: 16px;
        color: #c7d5e0;
        margin: 0 0 8px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }

      .game-meta {
        display: flex;
        align-items: center;
        gap: 12px;
        margin-bottom: 8px;
        flex-wrap: wrap;

        .rating {
          display: flex;
          align-items: center;
          gap: 4px;
          color: #a4d007;
          font-weight: bold;
          font-size: 14px;

          .el-icon {
            font-size: 16px;
          }
        }

        .tags,
        .developer {
          color: #8f98a0;
          font-size: 12px;
        }
      }

      .game-platform {
        color: #66c0f4;
        font-size: 12px;
        margin-bottom: 8px;
      }

      .game-tags {
        display: flex;
        gap: 8px;
        flex-wrap: wrap;

        .tag-item {
          padding: 2px 8px;
          background: #2a475e;
          color: #c7d5e0;
          font-size: 11px;
          border-radius: 3px;
        }
      }
    }
  }

  .game-preview {
    width: 160px;
    height: 90px;
    border-radius: 8px;
    overflow: hidden;
    position: relative;
    flex-shrink: 0;
    margin-left: 16px;

    .preview-img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .preview-mask {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background: rgba(0, 0, 0, 0.4);
      display: flex;
      align-items: center;
      justify-content: center;
      opacity: 0;
      transition: opacity 0.2s;

      .play-icon {
        font-size: 32px;
        color: #fff;
      }
    }

    &:hover .preview-mask {
      opacity: 1;
    }
  }
}
</style>
