<template>
  <div class="game-card" @click="$emit('click')">
    <div class="cover">
      <img :src="game.coverImage || defaultCover" alt="cover" loading="lazy" />
      <div class="rating-badge" v-if="game.ratingScore">
        {{ game.ratingScore }}
      </div>
    </div>
    <div class="info">
      <h4 class="name">{{ game.name }}</h4>
      <p class="developer">{{ game.developer || '未知开发商' }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  game: {
    id: number;
    name: string;
    coverImage?: string;
    ratingScore?: number | string;
    developer?: string;
  }
}>();

defineEmits(['click']);

const defaultCover = 'https://via.placeholder.com/200x280?text=No+Image';
</script>

<style scoped lang="scss">
.game-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  width: 100%;
  
  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  }

  .cover {
    position: relative;
    padding-top: 140%; /* 200/280 aspect ratio */
    background: #f0f0f0;
    
    img {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
    
    .rating-badge {
      position: absolute;
      top: 8px;
      right: 8px;
      background: rgba(0,0,0,0.7);
      color: #ffd700;
      padding: 2px 6px;
      border-radius: 4px;
      font-weight: bold;
      font-size: 12px;
    }
  }

  .info {
    padding: 10px;
    .name {
      font-size: 14px;
      margin: 0 0 4px;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      color: #333;
    }
    .developer {
      font-size: 12px;
      color: #999;
      margin: 0;
    }
  }
}
</style>