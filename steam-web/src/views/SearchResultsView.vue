<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import request from '@/utils/request'
import { Search } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const gameList = ref([])
const keyword = ref(route.query.q || '')
const sortBy = ref('relevance') // 默认排序

// 排序选项
const sortOptions = [
  { value: 'relevance', label: '相关性' },
  { value: 'released_desc', label: '发行日期' },
  { value: 'price_asc', label: '价格：从低到高' },
  { value: 'price_desc', label: '价格：从高到低' }
]

const doSearch = async () => {
  loading.value = true
  try {
    const params = { sort: sortBy.value }
    if (keyword.value) {
      params.q = keyword.value
    }
    const res = await request.get('/api/game/search', { params })
    // request.js 封装可能直接返回 data，也可能返回 result 对象，请根据实际情况调整
    // 假设 res.data 是游戏列表
    gameList.value = res.data || res
  } catch (error) {
    console.error("搜索失败", error)
    gameList.value = []
  } finally {
    loading.value = false
  }
}

const goToDetail = (id) => {
  router.push(`/game/${id}`)
}

// 监听路由参数变化 (再次搜索时触发)
watch(() => route.query.q, (newVal) => {
  keyword.value = newVal || ''
  doSearch()
})

// 监听排序变化
watch(sortBy, () => {
  doSearch()
})

onMounted(() => {
  doSearch()
})
</script>

<template>
  <div class="search-page-container">
    <div class="content-width">

      <div class="search-header">
        <h2 class="result-title" v-if="keyword">
          搜索结果：正在显示 "{{ keyword }}" 的匹配项
        </h2>
        <h2 class="result-title" v-else>
          所有产品
        </h2>

        <div class="sort-box">
          <span class="sort-label">排序依据</span>
          <el-select
              v-model="sortBy"
              class="steam-select"
              popper-class="steam-select-popper"
              size="small"
          >
            <el-option
                v-for="item in sortOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            />
          </el-select>
        </div>
      </div>

      <div class="result-list-area" v-loading="loading" element-loading-background="rgba(0,0,0,0)">

        <div v-if="!loading && (!gameList || gameList.length === 0)" class="no-results">
          <div class="no-results-icon"><el-icon><Search /></el-icon></div>
          <h3>0 个匹配的搜索结果。</h3>
          <p>您的搜索项没有返回任何结果。这就好像我们在寻找传说中的“半条命 3”一样……</p>
          <p>或者是您的输入有误？请尝试使用不同的关键词。</p>
        </div>

        <div class="search-result-row" v-for="game in gameList" :key="game.id" @click="goToDetail(game.id)">
          <div class="col-img">
            <img :src="game.coverUrl" alt="">
          </div>
          <div class="col-info">
            <div class="title">{{ game.title }}</div>
            <div class="details">
              <span class="platform-icon">WIN</span>
              <span class="date">{{ game.releaseDate }}</span>
            </div>
          </div>
          <div class="col-price">
            <div class="price-box">
              $ {{ game.price }}
            </div>
          </div>
        </div>

      </div>
    </div>
  </div>
</template>

<style scoped>
.search-page-container {
  padding: 30px 0;
  min-height: 500px;
}
.content-width { width: 940px; margin: 0 auto; }

.search-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #3c4450;
  padding-bottom: 15px;
  margin-bottom: 20px;
}
.result-title {
  font-size: 22px;
  color: #fff;
  font-weight: normal;
  margin: 0;
}

/* 排序框样式 */
.sort-box {
  display: flex;
  align-items: center;
  background: rgba( 0, 0, 0, 0.2 );
  padding: 5px 10px;
  border-radius: 3px;
  border: 1px solid rgba(0,0,0,0);
}
.sort-label {
  font-size: 12px;
  color: #556772;
  margin-right: 10px;
}
/* 定制 Element Select 样式以匹配 Steam */
:deep(.steam-select .el-input__wrapper) {
  background-color: transparent !important;
  box-shadow: none !important;
  padding: 0;
}
:deep(.steam-select .el-input__inner) {
  color: #66c0f4 !important;
  font-size: 12px;
  text-align: right;
  border: none;
  height: 24px;
  font-family: Arial, sans-serif;
}
:deep(.steam-select .el-select__caret) {
  color: #66c0f4;
}

/* 空状态 */
.no-results {
  background: rgba(0,0,0,0.2);
  padding: 40px;
  text-align: center;
  border-radius: 4px;
  margin-top: 20px;
}
.no-results-icon { font-size: 48px; color: #3c4450; margin-bottom: 20px; }
.no-results h3 { color: #fff; font-size: 18px; margin-bottom: 10px; }
.no-results p { color: #8f98a0; font-size: 14px; margin: 5px 0;}

/* 列表行样式 */
.search-result-row {
  display: flex;
  background: rgba(0, 0, 0, 0.2);
  margin-bottom: 5px;
  height: 69px;
  cursor: pointer;
  transition: background 0.2s;
}
.search-result-row:hover {
  background: #2a3f5a; /* Steam 高亮色 */
}

.col-img { width: 184px; height: 69px; flex-shrink: 0; }
.col-img img { width: 100%; height: 100%; object-fit: cover; }

.col-info {
  flex: 1;
  padding: 10px 15px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.col-info .title { font-size: 16px; color: #c7d5e0; overflow: hidden; white-space: nowrap; text-overflow: ellipsis; }
.col-info .details { font-size: 12px; color: #556772; display: flex; align-items: center; gap: 10px;}
.platform-icon { font-size: 10px; }

.col-price { width: 150px; display: flex; align-items: center; justify-content: flex-end; padding-right: 20px; }
.price-box { color: #c7d5e0; font-size: 14px; }
</style>

<style>
/* 全局覆盖 Popper 样式 */
.steam-select-popper.el-popper {
  background: #3d4450 !important;
  border: 1px solid #1b2838 !important;
}
.steam-select-popper .el-select-dropdown__item {
  color: #c6d4df !important;
  font-size: 12px;
}
.steam-select-popper .el-select-dropdown__item.hover,
.steam-select-popper .el-select-dropdown__item.selected {
  background: #66c0f4 !important;
  color: #fff !important;
}
.steam-select-popper .el-popper__arrow::before {
  background: #3d4450 !important;
  border-color: #1b2838 !important;
}
</style>