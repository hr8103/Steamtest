<script setup>
import { ref, onMounted, watch } from 'vue'
import request from '@/utils/request'
import { useRouter } from 'vue-router'

const props = defineProps(['mode', 'title'])
const router = useRouter()
const games = ref([])
const loading = ref(true)

const fetchAndSort = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/game/list')
    let all = Array.isArray(res) ? res : (res.data || [])

    // 逻辑分支
    if (props.mode === 'random') {
      // 随机打乱
      all = all.sort(() => 0.5 - Math.random())
    } else if (props.mode === 'price_asc') {
      // 价格从低到高
      all = all.sort((a, b) => Number(a.price) - Number(b.price))
    }

    games.value = all
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

onMounted(fetchAndSort)
watch(() => props.mode, fetchAndSort)
</script>

<template>
  <div class="browse-page-steam">
    <div class="content-width">
      <div class="page-header">
        <h2 class="title">{{ title }}</h2>
        <div class="desc" v-if="mode === 'random'">
          为您展示商店中的随机惊喜，探索未知的乐趣。
        </div>
        <div class="desc" v-else>
          为您筛选出的超值特惠商品，不错过任何一次机会。
        </div>
      </div>

      <div v-if="loading" class="loading-state">
        <div class="spinner"></div> 正在加载游戏库...
      </div>

      <div v-else class="game-list-vertical">
        <div class="browse-item" v-for="game in games" :key="game.id" @click="router.push(`/game/${game.id}`)">
          <div class="img-box">
            <img :src="game.coverUrl" />
          </div>

          <div class="info-box">
            <div class="g-title">{{ game.title }}</div>
            <div class="g-meta">
              <span class="platform-icon">WIN</span>
              <span class="pub-date">{{ game.releaseDate || '2025' }}</span>
            </div>
            <div class="g-tags">
              <span class="tag" v-for="t in (game.tags?game.tags.split(',').slice(0,4):[])" :key="t">{{t}}</span>
            </div>
          </div>

          <div class="price-action-box">
            <div class="discount-label" v-if="mode === 'price_asc'">-20%</div>
            <div class="price-container">
              <div class="original-price" v-if="mode === 'price_asc'">$ {{ (game.price * 1.25).toFixed(2) }}</div>
              <div class="final-price">$ {{ game.price }}</div>
            </div>
          </div>
        </div>
      </div>

      <div v-if="!loading && games.length === 0" class="empty-state">
        暂无相关游戏数据。
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 全局背景与字体 */
.browse-page-steam {
  background-color: #1b2838;
  min-height: 100vh;
  padding: 40px 0;
  color: #c7d5e0;
  font-family: "Motiva Sans", Arial, sans-serif;
}

/* ★★★ 1200px 宽屏 ★★★ */
.content-width {
  width: 1200px;
  margin: 0 auto;
}

/* 标题区 */
.page-header {
  margin-bottom: 30px;
  border-bottom: 1px solid #3c4450;
  padding-bottom: 15px;
}
.title {
  font-size: 32px;
  font-weight: 300;
  color: #fff;
  text-transform: uppercase;
  letter-spacing: 1px;
  margin: 0 0 10px 0;
}
.desc {
  font-size: 14px;
  color: #8f98a0;
}

/* 列表容器 */
.game-list-vertical {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

/* 单个长条卡片 */
.browse-item {
  display: flex;
  background-color: rgba(0, 0, 0, 0.2);
  height: 90px; /* 加高一点，更大气 */
  align-items: center;
  padding-right: 25px;
  cursor: pointer;
  transition: all 0.2s;
  border: 1px solid transparent;
}
.browse-item:hover {
  background-color: #202d3f;
  transform: translateX(5px); /* 微微右移的动效 */
  border-left: 3px solid #66c0f4; /* 左侧亮条 */
}

/* 图片 */
.img-box {
  width: 196px; /* 固定 Steam 列表图宽度 */
  height: 100%;
  margin-right: 20px;
  flex-shrink: 0;
}
.img-box img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 中间信息 */
.info-box {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  height: 100%;
}
.g-title {
  font-size: 18px;
  color: #fff;
  margin-bottom: 5px;
  font-weight: 500;
}
.g-meta {
  font-size: 12px;
  color: #556772;
  margin-bottom: 8px;
  display: flex;
  gap: 10px;
}
.platform-icon {
  color: #556772;
}
.g-tags {
  display: flex;
  gap: 5px;
}
.tag {
  font-size: 11px;
  color: #c7d5e0;
  background: rgba(255, 255, 255, 0.1);
  padding: 2px 6px;
  border-radius: 2px;
}

/* 价格区域 */
.price-action-box {
  display: flex;
  align-items: center;
  gap: 15px;
  min-width: 120px;
  justify-content: flex-end;
}
.discount-label {
  background: #4c6b22;
  color: #beee11;
  padding: 4px 6px;
  font-size: 14px;
  font-weight: bold;
}
.price-container {
  text-align: right;
}
.original-price {
  color: #738895;
  font-size: 11px;
  text-decoration: line-through;
}
.final-price {
  color: #fff;
  font-size: 16px;
  font-weight: bold;
}

/* 加载与空状态 */
.loading-state, .empty-state {
  text-align: center;
  padding: 60px;
  font-size: 18px;
  color: #8f98a0;
}
.spinner {
  display: inline-block;
  width: 20px;
  height: 20px;
  border: 2px solid #66c0f4;
  border-top-color: transparent;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-right: 10px;
  vertical-align: middle;
}
@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }
</style>