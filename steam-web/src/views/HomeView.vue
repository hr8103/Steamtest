<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { useRouter } from 'vue-router'

const router = useRouter()
const allGames = ref([]) // 存储所有游戏
const banners = ref([]) // 轮播图
const specialOffers = ref([]) // 特别优惠（随机）
const tabGames = ref([]) // 底部Tab展示的游戏（随机）
const activeTab = ref('new') // 当前Tab状态

// 洗牌算法 (Fisher-Yates)
const shuffle = (arr) => {
  let newArr = [...arr]
  for (let i = newArr.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [newArr[i], newArr[j]] = [newArr[j], newArr[i]];
  }
  return newArr
}

// 刷新底部Tab数据 (随机取8个)
const refreshTabGames = () => {
  if (allGames.value.length > 0) {
    tabGames.value = shuffle(allGames.value).slice(0, 8)
  }
}

// 切换Tab时触发随机刷新
const handleTabClick = (tab) => {
  activeTab.value = tab
  refreshTabGames()
}

const fetchData = async () => {
  try {
    const res = await request.get('/api/game/list')
    const data = Array.isArray(res) ? res : (res.data || [])
    allGames.value = data

    // 1. 随机轮播 (5个)
    banners.value = shuffle(data).slice(0, 5)

    // 2. 随机特别优惠 (4个)
    specialOffers.value = shuffle(data).slice(0, 4)

    // 3. 初始化底部Tab
    refreshTabGames()

  } catch (e) {
    console.error(e)
  }
}

const goFeatured = () => router.push('/store/featured')
const goCategory = () => router.push('/store/categories')
const goSpecial = () => router.push('/store/special')

onMounted(fetchData)
</script>

<template>
  <div class="home-container">
    <div class="content-width">

      <div class="store-nav-capsule">
        <div class="nav-item" @click="goFeatured">
          <span class="icon">★</span> 精选和推荐
        </div>
        <div class="nav-item" @click="goCategory">
          <span class="icon">⊞</span> 按类别浏览
        </div>
        <div class="nav-item" @click="goSpecial">
          <span class="icon">%</span> 特别优惠
        </div>
      </div>

      <div class="main-carousel">
        <el-carousel trigger="click" height="350px" :interval="4000" arrow="always">
          <el-carousel-item v-for="item in banners" :key="item.id">
            <div class="carousel-content" @click="router.push(`/game/${item.id}`)">
              <div class="main-img">
                <img :src="item.coverUrl" />
              </div>
              <div class="info-panel">
                <div class="game-title">{{ item.title }}</div>
                <div class="screenshot-grid">
                  <div class="ss-item" v-for="(s,i) in (item.screenshots?JSON.parse(item.screenshots).slice(0,4):[])" :key="i">
                    <img :src="s" />
                  </div>
                </div>
                <div class="carousel-footer">
                  <div class="tag-list">
                    <span v-for="t in (item.tags?item.tags.split(',').slice(0,2):[])" :key="t">{{t}}</span>
                  </div>
                  <div class="price-tag">$ {{ item.price }}</div>
                </div>
              </div>
            </div>
          </el-carousel-item>
        </el-carousel>
      </div>

      <div class="section-block">
        <div class="section-header">特别优惠</div>
        <div class="game-grid-4">
          <div class="game-card" v-for="g in specialOffers" :key="g.id" @click="router.push(`/game/${g.id}`)">
            <div class="card-img"><img :src="g.coverUrl"></div>
            <div class="card-info">
              <div class="card-title" :title="g.title">{{ g.title }}</div>
              <div class="price-row">
                <span class="discount-badge">-20%</span>
                <span class="card-price">$ {{ g.price }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="section-block">
        <div class="tabs-header">
          <div class="tab-item" :class="{active: activeTab==='new'}" @click="handleTabClick('new')">新品与热门商品</div>
          <div class="tab-item" :class="{active: activeTab==='top'}" @click="handleTabClick('top')">热销商品</div>
          <div class="tab-item" :class="{active: activeTab==='discount'}" @click="handleTabClick('discount')">正在打折</div>
        </div>
        <div class="tab-content">
          <div class="game-row-item" v-for="g in tabGames" :key="g.id" @click="router.push(`/game/${g.id}`)">
            <img :src="g.coverUrl" class="row-img">
            <div class="row-info">
              <div class="row-title">{{ g.title }}</div>
              <div class="row-tags">
                <span v-for="t in (g.tags?g.tags.split(',').slice(0,3):[])" :key="t">{{ t }}</span>
              </div>
            </div>
            <div class="row-price">$ {{ g.price }}</div>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<style scoped>
.home-container { padding-bottom: 50px; }

/* ★★★ 宽度升级：1200px ★★★ */
.content-width { width: 1200px; margin: 0 auto; }

/* 胶囊导航 */
.store-nav-capsule { display: flex; gap: 15px; margin: 25px 0; }
.nav-item { flex: 1; background: linear-gradient(90deg, #212c42 0%, #2a475e 100%); color: #fff; height: 44px; display: flex; align-items: center; justify-content: center; font-size: 15px; font-weight: bold; border-radius: 3px; cursor: pointer; box-shadow: 0 2px 5px rgba(0,0,0,0.3); transition: all 0.2s; text-shadow: 1px 1px 2px rgba(0,0,0,0.5); }
.nav-item:hover { background: linear-gradient(90deg, #47bfff 0%, #1a44c2 100%); transform: translateY(-2px); filter: brightness(1.1); }
.nav-item .icon { margin-right: 8px; font-size: 18px; color: #66c0f4; }

/* 轮播图 */
.main-carousel { margin-bottom: 50px; box-shadow: 0 0 15px rgba(0,0,0,0.6); }
.carousel-content { display: flex; height: 100%; cursor: pointer; background: #0f1922; }
.main-img { width: 68%; height: 100%; position: relative; } /* 图片占比加大 */
.main-img img { width: 100%; height: 100%; object-fit: cover; }
.info-panel { flex: 1; padding: 25px; display: flex; flex-direction: column; background: #0f1922; color: #fff; background: linear-gradient(to right, #0f1922 0%, #16202d 100%); }
.game-title { font-size: 26px; margin-bottom: 20px; overflow: hidden; white-space: nowrap; text-overflow: ellipsis; text-shadow: 0 2px 4px rgba(0,0,0,0.5); }
.screenshot-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 8px; margin-bottom: 20px; }
.ss-item { height: 80px; overflow: hidden; border-radius: 2px; }
.ss-item img { width: 100%; height: 100%; object-fit: cover; opacity: 0.7; transition: 0.2s; }
.ss-item img:hover { opacity: 1; transform: scale(1.05); }
.carousel-footer { margin-top: auto; }
.tag-list span { background: rgba(255,255,255,0.1); padding: 3px 8px; font-size: 11px; margin-right: 5px; border-radius: 2px; color: #8f98a0; }
.price-tag { margin-top: 15px; font-size: 18px; color: #66c0f4; font-weight: bold; }

/* 区域 */
.section-block { margin-bottom: 50px; }
.section-header { font-size: 20px; color: #fff; margin-bottom: 15px; letter-spacing: 0.5px; }

/* 4列网格 (特别优惠) */
.game-grid-4 { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; }
.game-card { background: #16202d; cursor: pointer; transition: 0.2s; position: relative; box-shadow: 0 4px 8px rgba(0,0,0,0.3); }
.game-card:hover { transform: translateY(-5px); background: #202d3f; z-index: 2; box-shadow: 0 8px 20px rgba(0,0,0,0.6); }
.card-img { height: 160px; overflow: hidden; }
.card-img img { width: 100%; height: 100%; object-fit: cover; }
.card-info { padding: 12px; }
.card-title { color: #c7d5e0; font-size: 14px; margin-bottom: 8px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.price-row { display: flex; align-items: center; justify-content: space-between; }
.discount-badge { background: #4c6b22; color: #beee11; padding: 1px 5px; font-size: 12px; font-weight: bold; border-radius: 2px; }
.card-price { color: #66c0f4; font-size: 14px; }

/* Tabs 列表 */
.tabs-header { display: flex; border-bottom: 1px solid #2a475e; margin-bottom: 15px; }
.tab-item { padding: 10px 25px; cursor: pointer; color: #8f98a0; font-size: 15px; transition: 0.2s; }
.tab-item:hover { color: #fff; }
.tab-item.active { color: #fff; border-bottom: 3px solid #1a9fff; margin-bottom: -1px; background: rgba(255,255,255,0.02); font-weight: bold; }
.game-row-item { display: flex; align-items: center; background: linear-gradient(to right, rgba(0,0,0,0.2) 0%, rgba(0,0,0,0) 100%); margin-bottom: 8px; padding: 10px 15px; cursor: pointer; transition: 0.2s; border-radius: 2px; }
.game-row-item:hover { background: linear-gradient(to right, #212d3d 0%, #16202d 100%); padding-left: 20px; }
.row-img { width: 130px; height: 55px; object-fit: cover; margin-right: 20px; box-shadow: 0 0 5px rgba(0,0,0,0.5); }
.row-info { flex: 1; }
.row-title { color: #c7d5e0; font-size: 15px; font-weight: 500; }
.row-tags span { font-size: 11px; color: #56707f; margin-right: 5px; }
.row-price { color: #66c0f4; font-size: 15px; font-weight: bold; }
</style>