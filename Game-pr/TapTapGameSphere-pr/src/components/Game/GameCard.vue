<template>
  <div
      class="game-card"
      @click="$emit('click')"
      @mouseenter="handleMouseEnter"
      @mouseleave="handleMouseLeave"
  >
    <div class="cover">
      <img
          :src="game.coverImage || defaultCover"
          alt="cover"
          loading="lazy"
          v-show="!isPlaying || !hasVideo"
      />
      <!-- YouTube 视频用 iframe 嵌入 -->
      <iframe
          v-if="isPlaying && isYouTubeVideo"
          :src="trailerUrl"
          class="preview-video"
          frameborder="0"
          allow="autoplay; encrypted-media"
          allowfullscreen
      ></iframe>
      <!-- 普通视频用 video 标签 -->
      <video
          v-else-if="isPlaying && hasVideo"
          ref="videoRef"
          :src="game.videoUrl"
          muted
          loop
          playsinline
          class="preview-video"
      ></video>
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
import { ref, computed } from 'vue';

const props = defineProps<{
  game: {
    id: number;
    name: string;
    coverImage?: string;
    videoUrl?: string;
    trailerUrl?: string;
    ratingScore?: number | string;
    developer?: string;
  }
}>();

defineEmits(['click']);

const defaultCover = 'https://via.placeholder.com/200x280?text=No+Image';
const isPlaying = ref(false);
const videoRef = ref<HTMLVideoElement | null>(null);

const hasVideo = computed(() => {
  return props.game.videoUrl && props.game.videoUrl.trim() !== '';
});

const isYouTubeVideo = computed(() => {
  const url = props.game.videoUrl || '';
  return url.includes('youtube.com') || url.includes('youtu.be');
});

const trailerUrl = computed(() => {
  if (isYouTubeVideo.value && props.game.trailerUrl) {
    return props.game.trailerUrl + '?autoplay=1&mute=1&loop=1&controls=0&showinfo=0&rel=0&modestbranding=1';
  }
  return '';
});

const handleMouseEnter = async () => {
  if (!hasVideo.value) return;

  isPlaying.value = true;

  if (isYouTubeVideo.value) {
    console.log('🎬 开始播放 YouTube 视频');
    return;
  }

  try {
    if (videoRef.value) {
      videoRef.value.currentTime = 0;
      await videoRef.value.play();
    }
  } catch (error: any) {
    console.warn('视频播放失败:', error.name, error.message);
    isPlaying.value = false;
  }
};

const handleMouseLeave = () => {
  if (videoRef.value) {
    videoRef.value.pause();
    videoRef.value.currentTime = 0;
  }
  isPlaying.value = false;
};
</script>


<style scoped lang="scss">
.game-card {
  background: #16202d;
  border-radius: 4px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  width: 100%;
  border: 1px solid #2a475e;

  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.4);
  }

  .cover {
    position: relative;
    padding-top: 140%;
    background: #1b2838;

    img {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .preview-video {
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
      background: rgba(23, 26, 33, 0.85);
      color: #a4d007;
      padding: 2px 6px;
      border-radius: 2px;
      font-weight: bold;
      font-size: 12px;
      border: 1px solid #4c6b22;
      z-index: 2;
    }
  }

  .info {
    padding: 10px;
    background: linear-gradient(to bottom, #16202d, #1b2838);

    .name {
      font-size: 14px;
      margin: 0 0 4px;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      color: #c7d5e0;
    }
    .developer {
      font-size: 12px;
      color: #8f98a0;
      margin: 0;
    }
  }
}
</style>
