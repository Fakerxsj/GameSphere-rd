NEW_FILE_CODE
<template>
  <div class="forum-tabs">
    <div class="section-icons">
      <div
          v-for="section in sections"
          :key="section.id"
          class="section-icon"
          :class="{ active: activeSection === section.id }"
          @click="handleSectionClick(section.id)"
      >
        <div class="icon-placeholder">{{ section.name.charAt(0) }}</div>
        <span class="icon-label">{{ section.name }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { FourmSectionVO } from '@/types/forum'

const props = defineProps<{
  sections: FourmSectionVO[]
  activeSection: number
}>()

const emit = defineEmits<{
  (e: 'change-section', sectionId: number): void
}>()

const handleSectionClick = (sectionId: number) => {
  emit('change-section', sectionId)
}
</script>

<style scoped lang="scss">
.forum-tabs {
  background: #16202d;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 16px;

  .section-icons {
    display: flex;
    gap: 24px;
    overflow-x: auto;

    &::-webkit-scrollbar {
      display: none;
    }

    .section-icon {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 8px;
      cursor: pointer;
      transition: all 0.2s;
      flex-shrink: 0;

      .icon-placeholder {
        width: 48px;
        height: 48px;
        background: #2a475e;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 20px;
        color: #8f98a0;
        font-weight: bold;
        transition: all 0.2s;
      }

      .icon-label {
        font-size: 12px;
        color: #8f98a0;
        white-space: nowrap;
      }

      &:hover {
        .icon-placeholder {
          background: #66c0f4;
          color: #1b2838;
        }

        .icon-label {
          color: #66c0f4;
        }
      }

      &.active {
        .icon-placeholder {
          background: #66c0f4;
          color: #1b2838;
          box-shadow: 0 4px 12px rgba(102, 192, 244, 0.3);
        }

        .icon-label {
          color: #66c0f4;
          font-weight: 600;
        }
      }
    }
  }
}
</style>
