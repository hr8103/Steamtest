<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { addToCart, isInCart } from '@/utils/cart'

const route = useRoute()
const router = useRouter()
const game = ref({})
const screenshots = ref([])
const activeImage = ref('')
const showAllLanguages = ref(false)
const dialogVisible = ref(false)
const alreadyInCart = ref(false)

// Steam Icons (保持不变)
const steamIcons = {
  '单人': 'M9 11c-1.66 0-3-1.34-3-3s1.34-3 3-3 3 1.34 3 3-1.34 3-3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5z',
  '多人': 'M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5c-1.66 0-3 1.34-3 3s1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5C6.34 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5zm8 0c-.29 0-.62.02-.97.05 1.16.84 1.97 1.97 1.97 3.45V19h6v-2.5c0-2.33-4.67-3.5-7-3.5z',
  'default': 'M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-6h2v6zm0-8h-2V7h2v2z'
}
const getIconPath = (name) => {
  for (const k in steamIcons) if (name.includes(k)) return steamIcons[k]
  return steamIcons['default']
}

const parsedLanguages = computed(() => {
  if (!game.value.supportedLanguages) return []
  return game.value.supportedLanguages.replace(/<br>/g, '').split(', ').map(s => ({
    name: s.replace('<strong>*</strong>', '').trim(),
    audio: s.includes('*')
  }))
})
const visibleLanguages = computed(() => showAllLanguages.value ? parsedLanguages.value : parsedLanguages.value.slice(0, 3))

const handleAddToCart = () => {
  if (alreadyInCart.value) return

  const success = addToCart(game.value)
  if (success) {
    alreadyInCart.value = true
    dialogVisible.value = true
  } else {
    ElMessageBox.confirm(
        '您需要登录后才能将商品添加到购物车。是否立即去登录？',
        '请先登录',
        {
          confirmButtonText: '去登录',
          cancelButtonText: '取消',
          type: 'warning',
          customClass: 'steam-login-confirm'
        }
    ).then(() => {
      router.push('/login')
    }).catch(() => {})
  }
}

const fetchDetail = async () => {
  const res = await request.get('/api/game/list')
  const all = Array.isArray(res) ? res : (res.data || [])
  const found = all.find(g => g.id == route.params.id)
  if (found) {
    game.value = found
    try {
      if(found.screenshots) screenshots.value = JSON.parse(found.screenshots)
      activeImage.value = screenshots.value[0] || found.coverUrl
    } catch { activeImage.value = found.coverUrl }
    alreadyInCart.value = isInCart(found.id)
  }
}

const goToDev = (name) => name && router.push(`/developer/${encodeURIComponent(name.trim())}`)
const goToCategory = (tag) => tag && router.push(`/category/${encodeURIComponent(tag.trim())}`)

onMounted(fetchDetail)
</script>

<template>
  <div class="detail-wrapper-2025">
    <div class="bg-blur" :style="{ backgroundImage: `url(${game.coverUrl})` }"></div>

    <div class="container-1200">
      <div class="top-nav">
        <div class="crumbs">所有游戏 > {{ game.tags ? game.tags.split(',')[0] : '游戏' }} > {{ game.title }}</div>
        <h2 class="title">{{ game.title }}</h2>
      </div>

      <div class="highlight-box-wide">
        <div class="media-player-wide">
          <div class="screen-wide"><img :src="activeImage" class="full-media" /></div>
          <div class="thumbs-wide">
            <div v-for="(img, i) in screenshots" :key="i" class="thumb-item" :class="{active: activeImage === img}" @mouseover="activeImage = img">
              <img :src="img" />
            </div>
          </div>
        </div>
        <div class="info-sidebar-wide">
          <img :src="game.coverUrl" class="header-img-wide" />
          <div class="desc-wide">{{ game.shortDescription }}</div>
          <div class="meta-table">
            <div class="row"><span class="label">发行日期:</span> <span class="val">{{ game.releaseDate }}</span></div>
            <div class="row"><span class="label">开发商:</span> <span class="val link" @click="goToDev(game.developer)">{{ game.developer }}</span></div>
            <div class="row"><span class="label">发行商:</span> <span class="val link" @click="goToDev(game.developer)">{{ game.developer }}</span></div>
          </div>
          <div class="tags-cloud">
            <span class="tag-title">该产品的热门用户自定义标签：</span>
            <div><span class="tag" v-for="t in (game.tags?game.tags.split(',').slice(0,5):[])" :key="t" @click="goToCategory(t)">{{t}}</span></div>
          </div>
        </div>
      </div>

      <div class="buy-area-wide">
        <h1>购买 {{ game.title }}</h1>
        <div class="buy-btn-row">
          <div class="price">$ {{ game.price }}</div>
          <button class="btn-green" :class="{ 'btn-disabled': alreadyInCart }" @click="handleAddToCart">
            {{ alreadyInCart ? '已在购物车中' : '添加至购物车' }}
          </button>
        </div>
      </div>

      <div class="content-split-wide">
        <div class="left-main-wide">
          <div class="section-title">关于这款游戏</div><div class="line"></div>
          <div class="html-content-wide" v-html="game.aboutTheGame"></div>
        </div>
        <div class="right-sidebar-wide">
          <div class="block features" v-if="game.gameFeatures">
            <div class="feat-row" v-for="feat in game.gameFeatures.split(',')" :key="feat">
              <div class="icon-box"><svg viewBox="0 0 24 24" width="16" height="16" fill="#66c0f4"><path :d="getIconPath(feat)"></path></svg></div>
              <div class="feat-text">{{ feat }}</div>
            </div>
          </div>
          <div class="block langs">
            <div class="block-head">语言</div>
            <table class="lang-tbl">
              <thead><tr><th></th><th>界面</th><th>完全音频</th><th>字幕</th></tr></thead>
              <tbody><tr v-for="l in visibleLanguages" :key="l.name"><td class="lname">{{l.name}}</td><td>✔</td><td>{{l.audio?'✔':''}}</td><td>✔</td></tr></tbody>
            </table>
            <div class="more-link" @click="showAllLanguages = !showAllLanguages">{{ showAllLanguages ? '收起' : '查看所有支持语言' }}</div>
          </div>
        </div>
      </div>
    </div>

    <el-dialog
        v-model="dialogVisible"
        :show-close="false"
        width="680px"
        align-center
        class="steam-pro-cart-dialog"
    >
      <div class="pro-dialog-content">
        <div class="pro-header-title">
          已添加至您的购物车中！
        </div>

        <div class="inner-dark-card">
          <div class="card-cover">
            <img :src="game.coverUrl" />
          </div>

          <div class="card-info">
            <div class="card-title">{{ game.title }}</div>
            <div class="card-price">$ {{ game.price }}</div>
          </div>
        </div>

        <div class="pro-btn-actions">
          <button class="btn-steam-small-gray" @click="dialogVisible = false">继续购物</button>
          <button class="btn-steam-small-blue" @click="router.push('/cart')">查看我的购物车</button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<style scoped>
/* 基础样式保持不变 */
.detail-wrapper-2025 { background: #1b2838; min-height: 100vh; color: #c6d4df; position: relative; font-family: "Motiva Sans", Arial, sans-serif; }
.bg-blur { position: absolute; width: 100%; height: 600px; background-position: center top; background-size: cover; opacity: 0.15; mask-image: linear-gradient(to bottom, #000, transparent); z-index: 0; }
.container-1200 { width: 1200px; margin: 0 auto; position: relative; z-index: 1; padding-bottom: 50px; }
.top-nav { padding: 25px 0; }
.crumbs { font-size: 13px; color: #8f98a0; margin-bottom: 8px; }
.title { font-size: 32px; color: #fff; font-weight: normal; margin: 0; }
.highlight-box-wide { display: flex; justify-content: space-between; margin-bottom: 40px; }
.media-player-wide { width: 800px; }
.screen-wide { width: 800px; height: 450px; background: #000; display: flex; align-items: center; justify-content: center; }
.full-media { width: 100%; height: 100%; object-fit: contain; }
.thumbs-wide { display: flex; gap: 6px; margin-top: 6px; }
.thumb-item { width: 155px; height: 87px; background: #000; opacity: 0.6; cursor: pointer; border: 1px solid transparent; }
.thumb-item.active { opacity: 1; border-color: #fff; }
.thumb-item img { width: 100%; height: 100%; object-fit: cover; }
.info-sidebar-wide { width: 370px; font-size: 14px; }
.header-img-wide { width: 370px; height: 173px; object-fit: cover; margin-bottom: 15px; }
.desc-wide { color: #c6d4df; margin-bottom: 15px; line-height: 1.6; height: 85px; overflow: hidden; }
.row { display: flex; margin-bottom: 5px; }
.label { width: 100px; color: #556772; font-size: 11px; text-transform: uppercase; }
.val { color: #66c0f4; }
.link { text-decoration: underline; cursor: pointer; }
.link:hover { color: #fff; }
.tag { background: #223a4c; color: #67c1f5; padding: 4px 8px; margin: 0 4px 4px 0; display: inline-block; font-size: 12px; cursor: pointer; }
.tag:hover { background: #67c1f5; color: #fff; }
.buy-area-wide { background: linear-gradient(to right, rgba(0,0,0,0.6), rgba(0,0,0,0.3)); padding: 25px; border-radius: 4px; margin-bottom: 40px; position: relative; border: 1px solid rgba(255,255,255,0.05); }
.buy-area-wide h1 { margin: 0 0 10px 0; font-size: 26px; color: #fff; font-weight: normal; }
.buy-btn-row { background: #000; padding: 2px; position: absolute; right: 25px; top: 25px; border-radius: 2px; display: flex; align-items: center; }
.price { padding: 0 20px; font-size: 16px; color: #c6d4df; }
.btn-green { background: linear-gradient(to bottom, #75b022 5%, #588a1b 95%); border: 0; color: #d2efa9; padding: 0 25px; cursor: pointer; height: 40px; font-size: 16px; margin-right: 0; }
.btn-green:hover { background: linear-gradient(to bottom, #8ed629 5%, #6aa621 95%); color: #fff; }
.btn-disabled { background: #3d4450; color: #8f98a0; cursor: default; pointer-events: none; }
.content-split-wide { display: flex; justify-content: space-between; }
.left-main-wide { width: 800px; }
.right-sidebar-wide { width: 370px; }
.section-title { color: #fff; font-size: 16px; text-transform: uppercase; letter-spacing: 1px; }
.line { height: 1px; background: linear-gradient(to right, #363c44, #1b2838); margin: 8px 0 20px 0; }
:deep(.html-content-wide img) { max-width: 100% !important; height: auto !important; display: block; margin: 15px auto; }
.block { background: rgba(0,0,0,0.2); padding: 18px; margin-bottom: 20px; font-size: 13px; }
.feat-row { display: flex; align-items: center; gap: 10px; padding: 6px 10px; background: #1f2f42; margin-bottom: 3px; color: #66c0f4; }
.lang-tbl { width: 100%; border-collapse: collapse; }
.lang-tbl td { padding: 6px; color: #8f98a0; border-bottom: 1px solid #1b2838; }
.more-link { color: #66c0f4; cursor: pointer; padding-top: 10px; }

/* ★★★★★ 弹窗样式重构 ★★★★★ */
.pro-dialog-content {
  background: linear-gradient(to bottom, #222d3d 0%, #171d25 100%);
  padding: 30px;
  border: 1px solid #3c4450;
  box-shadow: 0 10px 30px rgba(0,0,0,0.8);
}

/* 标题：纯文字，大号加粗 */
.pro-header-title {
  font-family: "Motiva Sans", sans-serif;
  font-size: 24px;
  color: #fff;
  font-weight: 700; /* 加粗 */
  margin-bottom: 25px;
  text-shadow: 1px 1px 0 rgba(0,0,0,0.5);
}

/* ★★★ 核心：深色内嵌卡片 ★★★ */
.inner-dark-card {
  background-color: #10151b; /* 比外层更黑 */
  padding: 8px; /* 紧凑内边距 */
  display: flex;
  gap: 15px;
  border-radius: 2px;
  position: relative; /* 为了定位价格 */
  margin-bottom: 20px;
  box-shadow: inset 0 0 10px rgba(0,0,0,0.5); /* 内阴影增加深度 */
}

.card-cover img {
  width: 160px; /* 适当大小，不喧宾夺主 */
  height: auto;
  display: block;
}

.card-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.card-title {
  font-size: 18px;
  color: #fff;
  margin-top: 5px;
}

/* 价格：右下角，小字体，灰色 */
.card-price {
  position: absolute;
  bottom: 10px;
  right: 15px;
  font-size: 14px;
  color: #9099a1;
  font-weight: bold;
}

/* 按钮区：右对齐，小巧 */
.pro-btn-actions {
  display: flex;
  justify-content: flex-end; /* 靠右 */
  gap: 10px;
}

/* 小号蓝色按钮 */
.btn-steam-small-blue {
  background: linear-gradient(90deg, #47bfff 0%, #1a44c2 100%);
  border: none;
  color: #fff;
  padding: 8px 18px; /* 缩小内边距 */
  border-radius: 2px;
  font-size: 13px; /* 缩小字体 */
  cursor: pointer;
  font-weight: 500;
  transition: all 0.2s;
}
.btn-steam-small-blue:hover {
  background: linear-gradient(90deg, #66c0f4 0%, #1a44c2 100%);
  filter: brightness(1.2);
}

/* 小号灰色按钮 */
.btn-steam-small-gray {
  background: #3d4450;
  border: none;
  color: #c6d4df;
  padding: 8px 18px;
  border-radius: 2px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}
.btn-steam-small-gray:hover {
  background: #505969;
  color: #fff;
}
</style>

<style>
/* 强制去边框 */
.steam-pro-cart-dialog {
  background: transparent !important;
  box-shadow: none !important;
  border: none !important;
}
.steam-pro-cart-dialog .el-dialog__header { display: none; }
.steam-pro-cart-dialog .el-dialog__body { padding: 0; }
</style>