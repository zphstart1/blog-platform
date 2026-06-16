<template>
  <div class="tag-cloud">
    <el-tag
      v-for="tag in tags"
      :key="tag.id"
      :style="{ fontSize: getFontSize(tag.weight || tag.articleCount || 1) + 'px' }"
      class="cloud-tag"
      effect="plain"
      @click="$emit('tag-click', tag)"
    >
      {{ tag.name }}
    </el-tag>
    <el-empty v-if="!tags.length" description="暂无标签" />
  </div>
</template>

<script setup lang="ts">
import type { Tag } from '@/types'

defineProps<{
  tags: Tag[]
}>()

defineEmits<{
  'tag-click': [tag: Tag]
}>()

function getFontSize(weight: number): number {
  const minSize = 12
  const maxSize = 28
  const clamped = Math.max(1, Math.min(weight, 30))
  return minSize + ((clamped - 1) / 29) * (maxSize - minSize)
}
</script>

<style scoped>
.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  padding: 16px;
  justify-content: center;
}

.cloud-tag {
  cursor: pointer;
  transition: transform 0.2s;
}

.cloud-tag:hover {
  transform: scale(1.1);
}
</style>
