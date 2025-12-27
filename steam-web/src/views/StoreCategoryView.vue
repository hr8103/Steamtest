<script setup>
import { ref, onMounted, computed } from 'vue'
import request from '@/utils/request'
import { useRouter } from 'vue-router'

const router = useRouter()
const allGames = ref([])
const allTags = ref([])
const selectedTags = ref([]) // 选中的标签

const fetchData = async () => {
  try {
    const res = await request.get('/api/game/list')
    const list = Array.isArray(res) ? res : (res.data || [])
    allGames.value = list

    // 提取所有唯一标签
    const tagSet = new Set()
    list.forEach(g => {
      if (g.tags) {
        g.tags.split(',').forEach(t => tagSet.add(t.trim()))
      }
    })
    allTags.value = Array.from(tagSet).sort()
  } catch (e) {
    console.error(e)
  }
}

// 切换标签选择
const toggleTag = (tag) => {
  if (selectedTags.value.includes(tag)) {
    selectedTags.value = selectedTags.value.filter(t => t !== tag)
  } else {
    selectedTags.value.push(tag)
  }
}

// 计算属性：过滤后的游戏
const filteredGames = computed(() => {
  if (selectedTags.value.length === 0) return allGames.value
  return allGames.value.filter(g => {
    if (!g.tags) return false
    const gameTags = g.tags.split(',')
    // 必须包含所有选中的标签 (AND 逻辑)
    return selectedTags.value.every(sel => gameTags.includes(sel))
  })
})

onMounted(fetchData)
</script>

<template>
  <div class="category-page-steam">
    <div class="content-width">
      <div class="page-header">
        <h2 class="title">按类别浏览</h2>
        <div class="desc">选择下方标签，精准定位您的下一款最爱。</div>
      </div>

      <div class="filter-panel">
        <div class="panel-head">
          <span class="head-icon">⊞</span> 热门标签筛选
        </div>

        <div class="tags-container">
          <div
              class="steam-tag-btn"
              v-for="tag in allTags"
              :key="tag"
              :class="{ active: selectedTags.includes(tag) }"
              @click="toggleTag(tag)"
          >
            {{ tag }}
            <span v-if="selectedTags.includes(tag)" class="check-mark">✔</span>
          </div>
        </div>

        <div class="panel-footer">
          <div class="result-count">
            <span v-if="selectedTags.length === 0">显示所有商品</span>
            <span v-else>
              已选: <span class="highlight-text">{{ selectedTags.join(' + ') }}</span>
            </span>
            <span class="count-badge">{{ filteredGames.length }} 款游戏</span>
          </div>

          <button v-if="selectedTags.length > 0" class="clear-btn" @click="selectedTags = []">
            重置筛选 ✕
          </button>
        </div>
      </div>

      <div class="game-grid-4">
        <div class="store-card" v-for="game in filteredGames" :key="game.id" @click="router.push(`/game/${game.id}`)">
          <div class="card-cover">
            <img :src="game.coverUrl" />
          </div>
          <div class="card-body">
            <div class="card-title" :title="game.title">{{ game.title }}</div>
            <div class="card-tags">
              {{ game.tags ? game.tags.split(',').slice(0,3).join(', ') : '' }}
            </div>
            <div class="card-price-row">
              <div class="card-price">$ {{ game.price }}</div>
            </div>
          </div>
        </div>
      </div>

      <div v-if="filteredGames.length === 0" class="no-result-box">
        <div class="no-result-icon">☹</div>
        没有找到同时包含这些标签的游戏。
      </div>

    </div>
  </div>
</template>

<style scoped>
/* 全局样式 */
.category-page-steam { background: #1b2838; min-height: 100vh; padding: 40px 0; color: #c7d5e0; font-family: "Motiva Sans", Arial, sans-serif; }

/* ★★★ 1200px 宽屏 ★★★ */
.content-width { width: 1200px; margin: 0 auto; }

/* 头部 */
.page-header { margin-bottom: 25px; border-bottom: 1px solid #2a475e; padding-bottom: 15px; }
.title { font-size: 32px; font-weight: 300; color: #fff; text-transform: uppercase; margin: 0; letter-spacing: 1px; }
.desc { color: #8f98a0; margin-top: 5px; font-size: 14px; }

/* 筛选面板 */
.filter-panel {
  background: linear-gradient(to bottom, #2a475e 0%, #1b2838 100%);
  border: 1px solid #415d75;
  border-radius: 4px;
  margin-bottom: 40px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.3);
}
.panel-head {
  background: #2a475e;
  padding: 10px 20px;
  color: #fff;
  font-size: 15px;
  font-weight: bold;
  border-bottom: 1px solid #415d75;
}
.head-icon { color: #66c0f4; margin-right: 5px; }

.tags-container {
  padding: 20px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

/* 标签按钮 */
.steam-tag-btn {
  background: rgba(103, 193, 245, 0.1);
  border: 1px solid rgba(103, 193, 245, 0.2);
  color: #67c1f5;
  padding: 6px 14px;
  border-radius: 2px;
  cursor: pointer;
  font-size: 13px;
  user-select: none;
  transition: all 0.2s;
  display: flex;
  align-items: center;
}
.steam-tag-btn:hover {
  background: #67c1f5;
  color: #fff;
  border-color: #fff;
  box-shadow: 0 0 5px rgba(103, 193, 245, 0.5);
}
.steam-tag-btn.active {
  background: #67c1f5;
  color: #1b2838;
  font-weight: bold;
  border-color: #fff;
  box-shadow: 0 0 8px rgba(103, 193, 245, 0.6);
}
.check-mark { margin-left: 6px; font-weight: bold; }

.panel-footer {
  background: rgba(0,0,0,0.2);
  padding: 12px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
  color: #8f98a0;
}
.highlight-text { color: #fff; }
.count-badge { background: #3d4450; padding: 2px 8px; border-radius: 10px; color: #fff; margin-left: 10px; font-size: 12px; }
.clear-btn { background: none; border: none; color: #ff4c4c; cursor: pointer; font-size: 13px; font-weight: bold; }
.clear-btn:hover { text-decoration: underline; }

/* 4列游戏网格 */
.game-grid-4 {
  display: grid;
  grid-template-columns: repeat(4, 1fr); /* 宽屏4列 */
  gap: 20px;
}

/* 游戏卡片 */
.store-card {
  background: #16202d;
  cursor: pointer;
  transition: transform 0.2s, background-color 0.2s;
  box-shadow: 0 4px 10px rgba(0,0,0,0.3);
  position: relative;
}
.store-card:hover {
  transform: translateY(-5px);
  background: #202d3f;
  z-index: 2;
  box-shadow: 0 10px 20px rgba(0,0,0,0.5);
}

.card-cover {
  height: 160px; /* 大图 */
  overflow: hidden;
  position: relative;
}
.card-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.card-body {
  padding: 12px 15px;
}
.card-title {
  color: #c7d5e0;
  font-size: 15px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 5px;
}
.store-card:hover .card-title { color: #fff; }

.card-tags {
  font-size: 11px;
  color: #556772;
  margin-bottom: 10px;
  height: 16px;
  overflow: hidden;
}

.card-price-row {
  display: flex;
  justify-content: flex-end;
}
.card-price {
  color: #c7d5e0;
  font-size: 15px;
}

/* 空状态 */
.no-result-box {
  text-align: center;
  padding: 80px 0;
  color: #556772;
  font-size: 18px;
}
.no-result-icon {
  font-size: 48px;
  margin-bottom: 10px;
  color: #3d4450;
}
</style>