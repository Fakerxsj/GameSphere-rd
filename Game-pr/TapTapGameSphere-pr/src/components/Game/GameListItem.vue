<template>
  <div class="game-list-item" @click="$emit('click')">
    <img :src="game.coverImage || defaultCover" class="item-cover" loading="lazy" />
    <div class="item-info">
      <h4 class="name">{{ game.name }}</h4>
      <div class="meta">
        <span class="score" v-if="game.ratingScore">★ {{ game.ratingScore }}</span>
        <span class="type" v-if="game.gameType">{{ game.gameType }}</span>
        <span class="dev" v-if="game.developer">{{ game.developer }}</span>
      </div>
    </div>
    <el-button class="view-btn" type="primary" size="small" plain>查看</el-button>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  game: {
    id: number;
    name: string;
    coverImage?: string;
    ratingScore?: number | string;
    gameType?: string;
    developer?: string;
  }
}>();

defineEmits(['click']);

const defaultCover = 'https://via.placeholder.com/80x80?text=No+Image';
</script>

<style scoped lang="scss">
.game-list-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #2a475e;
  cursor: pointer;

  &:hover {
    background: #1b2838;
  }

  .item-cover {
    width: 60px;
    height: 60px;
    border-radius: 8px;
    object-fit: cover;
    margin-right: 12px;
  }

  .item-info {
    flex: 1;
    min-width: 0;

    .name {
      font-size: 15px;
      color: #c7d5e0;
      margin: 0 0 6px;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    .meta {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 12px;
      color: #8f98a0;

      .score {
        color: #a4d007;
        font-weight: bold;
      }
    }
  }

  .view-btn {
    margin-left: 12px;
    background: transparent;
    border-color: #66c0f4;
    color: #66c0f4;

    &:hover {
      background: #66c0f4;
      color: #1b2838;
    }
  }
}
</style>
