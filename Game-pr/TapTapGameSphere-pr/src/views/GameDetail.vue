<template>
  <div class="game-detail" v-if="game">
    <div class="media-banner">
      <div v-if="game.videoUrl || game.trailerUrl" class="video-container">
        <iframe
            v-if="isYouTubeUrl(game.videoUrl || game.trailerUrl)"
            :src="getYouTubeEmbedUrl(game.videoUrl || game.trailerUrl)"
            class="media-content"
            frameborder="0"
            allowfullscreen
            allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
        ></iframe>
        <video
            v-else
            :src="game.videoUrl || game.trailerUrl"
            class="media-content"
            controls
            autoplay
            muted
            loop
        ></video>
        <div class="media-mask" @click="playMedia = !playMedia">
          <el-icon v-if="!playMedia" class="play-icon"><VideoPlay /></el-icon>
        </div>
      </div>
      <img
          v-else
          :src="game.backgroundImage || game.coverImage || defaultBanner"
          class="banner-img"
          loading="lazy"
      />
      <div class="banner-mask"></div>
    </div>

    <div class="game-info-section">
      <div class="game-header">
        <div class="game-info-left">
          <img :src="game.coverImage || defaultCover" class="game-icon" loading="lazy" />
          <div class="game-basic">
            <h1 class="game-name">{{ game.name }}</h1>
            <div class="game-stats">
              <span class="stat-item">{{ formatCount(displayData.follows) }} 关注</span>
            </div>
          </div>
        </div>
        <el-button
            class="follow-btn"
            :type="isFollowed ? 'primary' : 'default'"
            @click="toggleFollow"
        >
          {{ isFollowed ? '已关注' : '关注' }}
        </el-button>
      </div>

      <div class="tags-section" v-if="game.gameType">
        <span
            class="game-tag"
            v-for="(tag, idx) in game.gameType.split(',').slice(0, 5)"
            :key="idx"
        >
          {{ tag.trim() }}
        </span>
      </div>

      <div class="stats-grid">
        <div class="stat-item">
          <div class="stat-value">{{ formatCount(displayData.popularity) }}</div>
          <div class="stat-label">热度</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ displayData.size }}</div>
          <div class="stat-label">游戏大小</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ formatCount(displayData.follows) }}</div>
          <div class="stat-label">关注</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ formatDate(game.releaseDate) }}</div>
          <div class="stat-label">上线日期</div>
        </div>
      </div>

      <div class="description-section">
        <p class="desc-text">{{ game.description || '暂无介绍' }}</p>
      </div>

      <div class="developer-note" v-if="game.developer">
        <div class="note-title">开发商 {{ game.developer }}</div>
        <div class="note-links">
          <span class="note-link">隐私政策</span>
          <span class="note-link">所需权限</span>
        </div>
      </div>

      <div class="action-bar">
        <div class="forum-link" @click="goToForum">
          <el-icon><ChatDotRound /></el-icon>
          <span>论坛</span>
        </div>
        <el-button class="download-btn" type="primary" size="large">
          <el-icon><Download /></el-icon>
          立即下载
        </el-button>
        <el-button class="qr-btn" circle>
          <el-icon><FullScreen /></el-icon>
        </el-button>
      </div>
    </div>
  </div>

  <div v-else class="loading-container">
    <el-icon :size="40" class="is-loading"><Loading /></el-icon>
    <span>加载中...</span>
  </div>
</template>


<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import {
  Star, StarFilled, Check, Monitor, Iphone,
  VideoPlay, ChatDotRound, Download, FullScreen, Loading
} from '@element-plus/icons-vue';
import { getGameDetail } from '@/api/game';

const route = useRoute();
const router = useRouter();
const game = ref<any>(null);
const isFollowed = ref(false);
const playMedia = ref(false);

// 随机生成的展示数据
const displayData = ref({
  popularity: 0,
  size: '',
  follows: 0
});

const defaultCover = 'https://via.placeholder.com/120x120?text=No+Image';
const defaultBanner = 'https://via.placeholder.com/800x450?text=No+Image';

// 生成随机整数
const randomInt = (min: number, max: number) => {
  return Math.floor(Math.random() * (max - min + 1)) + min;
};

// 生成随机展示数据
const generateDisplayData = () => {
  // 热度：10w-100w
  displayData.value.popularity = randomInt(100000, 1000000);
  // 游戏大小：10G-100G
  displayData.value.size = randomInt(10, 100) + 'G';
  // 关注：10w-100w
  displayData.value.follows = randomInt(100000, 1000000);
};

// 格式化数字
const formatCount = (num: number) => {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + '万';
  }
  return num.toString();
};

// 格式化日期
const formatDate = (date: any) => {
  if (!date) return '--';
  const d = new Date(date);
  return d.toLocaleDateString('zh-CN');
};

// 判断是否为 YouTube 链接
const isYouTubeUrl = (url: string) => {
  return url && (url.includes('youtube.com') || url.includes('youtu.be'));
};

// 获取 YouTube 嵌入链接
const getYouTubeEmbedUrl = (url: string) => {
  if (!url) return '';
  const match = url.match(/(?:youtu\.be\/|youtube\.com\/(?:embed\/|v\/|watch\?v=|watch\?.+&v=))([^&?]+)/);
  if (match && match[1]) {
    return `https://www.youtube.com/embed/${match[1]}`;
  }
  return url;
};

// 切换关注状态
const toggleFollow = () => {
  isFollowed.value = !isFollowed.value;
  ElMessage.success(isFollowed.value ? '关注成功' : '已取消关注');
};

// 跳转到游戏论坛
const goToForum = () => {
  if (game.value && game.value.id) {
    router.push(`/forum/game/${game.value.id}`);
  } else {
    ElMessage.warning('游戏信息未加载完成');
  }
};

onMounted(async () => {
  // 生成随机展示数据
  generateDisplayData();

  const id = Number(route.params.id);
  if (id) {
    try {
      game.value = await getGameDetail(id);
      console.log('游戏详情数据:', game.value);
    } catch (e) {
      console.error('获取游戏详情失败:', e);
      ElMessage.error('加载失败');
    }
  }
});
</script>

<style scoped lang="scss">.game-detail {
  background: #1b2838;
  min-height: 100vh;

  .media-banner {
    position: relative;
    width: 100%;
    height: 240px;
    overflow: hidden;

    .video-container {
      position: relative;
      width: 100%;
      height: 100%;

      .media-content {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }

      .media-mask {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: rgba(0, 0, 0, 0.3);
        display: flex;
        align-items: center;
        justify-content: center;
        cursor: pointer;
        transition: opacity 0.3s;

        .play-icon {
          font-size: 64px;
          color: #fff;
          filter: drop-shadow(0 2px 8px rgba(0, 0, 0, 0.5));
        }

        &:hover {
          background: rgba(0, 0, 0, 0.2);
        }
      }
    }

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

  .game-info-section {
    max-width: 1200px;
    margin: -60px auto 0;
    padding: 0 20px 20px;
    position: relative;
    z-index: 10;

    .game-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 20px;

      .game-info-left {
        display: flex;
        align-items: center;
        gap: 16px;

        .game-icon {
          width: 100px;
          height: 100px;
          border-radius: 12px;
          border: 3px solid #1b2838;
          object-fit: cover;
          flex-shrink: 0;
        }

        .game-basic {
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

    .stats-grid {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 12px;
      padding: 12px 0;
      border-top: 1px solid #2a475e;
      border-bottom: 1px solid #2a475e;
      margin-bottom: 12px;

      .stat-item {
        text-align: center;

        .stat-value {
          font-size: 14px;
          font-weight: bold;
          color: #c7d5e0;
          margin-bottom: 2px;
        }

        .stat-label {
          font-size: 11px;
          color: #8f98a0;
        }
      }
    }

    .tags-section {
      display: flex;
      flex-wrap: wrap;
      gap: 6px;
      margin-bottom: 12px;

      .game-tag {
        padding: 3px 10px;
        background: #2a475e;
        color: #c7d5e0;
        font-size: 12px;
        border-radius: 4px;
        cursor: pointer;

        &:hover {
          background: #66c0f4;
          color: #1b2838;
        }
      }
    }

    .description-section {
      margin-bottom: 12px;

      .desc-text {
        font-size: 13px;
        line-height: 1.6;
        color: #8f98a0;
        margin: 0;
      }
    }

    .developer-note {
      padding: 12px;
      background: #16202d;
      border-radius: 8px;
      margin-bottom: 12px;

      .note-title {
        font-size: 13px;
        color: #c7d5e0;
        margin-bottom: 6px;
      }

      .note-links {
        display: flex;
        gap: 16px;

        .note-link {
          font-size: 11px;
          color: #66c0f4;
          cursor: pointer;

          &:hover {
            text-decoration: underline;
          }
        }
      }
    }

    .action-bar {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 12px 0;
      border-top: 1px solid #2a475e;

      .forum-link {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 4px;
        color: #8f98a0;
        font-size: 11px;
        cursor: pointer;
        padding: 6px 12px;

        .el-icon {
          font-size: 18px;
        }

        &:hover {
          color: #66c0f4;
        }
      }

      .download-btn {
        flex: 1;
        background: linear-gradient(135deg, #66c0f4, #4a9fd4);
        border: none;
        border-radius: 20px;
        font-size: 14px;
        font-weight: bold;
        padding: 10px 20px;

        &:hover {
          background: linear-gradient(135deg, #7dd0ff, #66c0f4);
        }
      }

      .qr-btn {
        width: 40px;
        height: 40px;
        padding: 0;
        background: transparent;
        border: 1px solid #2a475e;
        color: #8f98a0;

        &:hover {
          border-color: #66c0f4;
          color: #66c0f4;
        }
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
}
</style>
