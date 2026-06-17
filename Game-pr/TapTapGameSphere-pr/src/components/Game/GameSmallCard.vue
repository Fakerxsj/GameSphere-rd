<template>
  <div class="game-small-card" @click="$emit('click')">
    <img :src="game.coverImage || defaultCover" class="card-cover" loading="lazy" />
    <div class="card-info">
      <h4 class="name">{{ game.name }}</h4>
      <div class="tags" v-if="game.gameType">
        <span class="tag">{{ game.gameType.split(',')[0] }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  game: {
    id: number;
    name: string;
    coverImage?: string;
    gameType?: string;
  }
}>();

defineEmits(['click']);

const defaultCover = 'https://via.placeholder.com/120x120?text=No+Image';
</script>

<style scoped lang="scss">
.game-small-card {
  width: 100px;
  cursor: pointer;
  flex-shrink: 0;

  .card-cover {
    width: 100px;
    height: 100px;
    border-radius: 12px;
    object-fit: cover;
    margin-bottom: 8px;
    transition: transform 0.2s;
  }

  &:hover .card-cover {
    transform: scale(1.05);
  }

  .card-info {
    .name {
      font-size: 13px;
      color: #c7d5e0;
      margin: 0;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    .tags {
      margin-top: 4px;
      .tag {
        font-size: 10px;
        color: #8f98a0;
        background: #2a475e;
        padding: 2px 4px;
        border-radius: 3px;
      }
    }
  }
}
</style>
