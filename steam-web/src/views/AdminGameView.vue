<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Plus, Back, Edit, Delete, UserFilled, Goods } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

// === 状态定义 ===
const activeTab = ref('game-manage') // 默认显示游戏管理
const gameList = ref([])
const userList = ref([])
const dialogVisible = ref(false) // 编辑窗口开关
const isEditMode = ref(false)    // 是否是编辑模式

// 表单数据 (复用于发布和编辑)
const form = reactive({
  id: null,
  title: '',
  price: 0,
  developer: '',
  description: '',
  coverUrl: '',
  releaseDate: '2025-01-01'
})

// === 数据获取 ===
// 1. 获取所有游戏
const fetchGames = async () => {
  const data = await request.get('/api/game/list')
  gameList.value = data
}

// 2. 获取所有用户
const fetchUsers = async () => {
  const data = await request.get('/api/user/list')
  userList.value = data
}

// === 游戏管理逻辑 ===
// 点击“编辑”按钮
const handleEdit = (row) => {
  isEditMode.value = true
  // 数据回显
  Object.assign(form, row)
  dialogVisible.value = true
}

// 点击“下架”按钮
const handleDeleteGame = (id) => {
  ElMessageBox.confirm('确定要永久下架这款游戏吗？', '警告', {
    confirmButtonText: '确定下架',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    request.delete(`/api/game/delete/${id}`).then(() => {
      ElMessage.success('游戏已下架')
      fetchGames() // 刷新列表
    })
  })
}

// 提交表单 (新增 或 更新)
const onSubmit = () => {
  if (isEditMode.value) {
    // 编辑逻辑
    request.post('/api/game/update', form).then(() => {
      ElMessage.success('游戏信息更新成功')
      dialogVisible.value = false
      fetchGames()
    })
  } else {
    // 新增逻辑
    request.post('/api/game/add', form).then(() => {
      ElMessage.success('新游戏发布成功')
      // 重置表单
      Object.assign(form, { title: '', price: 0, coverUrl: '', description: '' })
      // 切换回列表
      activeTab.value = 'game-manage'
      fetchGames()
    })
  }
}

// 切换Tab时的逻辑
const handleTabClick = (tab) => {
  if (tab.props.name === 'game-manage') fetchGames()
  if (tab.props.name === 'user-manage') fetchUsers()
}

// 图片上传回调
const handleUploadSuccess = (res) => {
  if(res.code === 200) form.coverUrl = res.data
}

// === 用户管理逻辑 ===
const handleDeleteUser = (id) => {
  ElMessageBox.confirm('确定要封禁并删除该用户吗？', '严重警告', { type: 'error' })
      .then(() => {
        request.delete(`/api/user/delete/${id}`).then(() => {
          ElMessage.success('用户已封禁')
          fetchUsers()
        })
      })
}

// 初始化
onMounted(() => {
  fetchGames()
})
</script>

<template>
  <div class="admin-container">
    <div class="admin-header">
      <div class="content-width">
        <div class="header-left">
          <h2>STEAM <span style="font-weight:100; opacity:0.7">WORKS</span></h2>
          <span class="badge">SUPER ADMIN</span>
        </div>
        <el-button link type="primary" @click="$router.push('/')">
          <el-icon><Back /></el-icon> 返回商店
        </el-button>
      </div>
    </div>

    <div class="content-width main-area">
      <el-tabs v-model="activeTab" tab-position="left" class="admin-tabs" @tab-click="handleTabClick">

        <el-tab-pane name="game-manage">
          <template #label><span class="custom-tab"><el-icon><Goods /></el-icon> 游戏库管理</span></template>

          <div class="panel-header">
            <h3>已上架游戏 ({{ gameList.length }})</h3>
            <el-button type="primary" size="small" @click="activeTab='publish'">+ 发布新游戏</el-button>
          </div>

          <el-table :data="gameList" style="width: 100%; background: transparent" :row-style="{background:'#1b2838', color:'#fff'}" :header-cell-style="{background:'#171a21', color:'#8f98a0'}">
            <el-table-column prop="id" label="ID" width="60" />
            <el-table-column label="封面" width="100">
              <template #default="scope">
                <img :src="scope.row.coverUrl" style="width: 80px; height: 40px; object-fit: cover; border-radius: 2px;">
              </template>
            </el-table-column>
            <el-table-column prop="title" label="游戏名称" />
            <el-table-column prop="price" label="价格" width="100">
              <template #default="scope">$ {{ scope.row.price }}</template>
            </el-table-column>
            <el-table-column prop="developer" label="开发商" />
            <el-table-column label="操作" width="180">
              <template #default="scope">
                <el-button size="small" type="primary" :icon="Edit" circle @click="handleEdit(scope.row)" />
                <el-button size="small" type="danger" :icon="Delete" circle @click="handleDeleteGame(scope.row.id)" />
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane name="user-manage">
          <template #label><span class="custom-tab"><el-icon><UserFilled /></el-icon> 用户管理</span></template>

          <div class="panel-header">
            <h3>注册用户列表 ({{ userList.length }})</h3>
          </div>

          <el-table :data="userList" style="width: 100%" :row-style="{background:'#1b2838', color:'#fff'}" :header-cell-style="{background:'#171a21', color:'#8f98a0'}">
            <el-table-column prop="id" label="UID" width="80" />
            <el-table-column prop="username" label="用户名" />
            <el-table-column prop="role" label="身份">
              <template #default="scope">
                <el-tag :type="scope.row.role === 1 ? 'danger' : 'info'">
                  {{ scope.row.role === 1 ? '管理员' : '玩家' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作">
              <template #default="scope">
                <el-button v-if="scope.row.role !== 1" type="danger" size="small" @click="handleDeleteUser(scope.row.id)">封禁账号</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane name="publish">
          <template #label><span class="custom-tab"><el-icon><Plus /></el-icon> 发布新作品</span></template>
          <div class="publish-wrapper">
            <h3>发布新游戏到 Steam 商店</h3>
            <el-form :model="form" label-width="100px" class="dark-form">
              <el-form-item label="游戏名称">
                <el-input v-model="form.title" />
              </el-form-item>
              <el-form-item label="商店封面">
                <el-upload class="cover-uploader" action="http://localhost:8080/api/game/upload" :show-file-list="false" :on-success="handleUploadSuccess" name="file">
                  <img v-if="form.coverUrl" :src="form.coverUrl" class="cover-img" />
                  <el-icon v-else class="cover-uploader-icon"><Plus /></el-icon>
                </el-upload>
              </el-form-item>
              <el-form-item label="价格">
                <el-input-number v-model="form.price" :min="0" />
              </el-form-item>
              <el-form-item label="开发商">
                <el-input v-model="form.developer" />
              </el-form-item>
              <el-form-item label="简介">
                <el-input v-model="form.description" type="textarea" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="isEditMode=false; onSubmit()">立即发布</el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>

    <el-dialog v-model="dialogVisible" title="编辑游戏信息" width="500px" custom-class="dark-dialog">
      <el-form :model="form" label-width="80px">
        <el-form-item label="游戏名称"><el-input v-model="form.title"/></el-form-item>
        <el-form-item label="价格"><el-input-number v-model="form.price"/></el-form-item>
        <el-form-item label="开发商"><el-input v-model="form.developer"/></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea"/></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="onSubmit">保存修改</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<style scoped>
.admin-container {
  min-height: 100vh;
  background: #0d0f12;
  color: #c6d4df;
}
.admin-header {
  background: #171a21;
  border-bottom: 3px solid #212429;
  padding: 15px 0;
  margin-bottom: 20px;
}
.admin-header .content-width {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.header-left h2 { margin: 0; display: inline-block; color:#fff;}
.badge {
  background: #cb2929;
  color: #fff;
  font-size: 10px;
  padding: 2px 5px;
  border-radius: 3px;
  margin-left: 10px;
  vertical-align: top;
}

.main-area {
  background: #1b2838;
  min-height: 600px;
  box-shadow: 0 0 20px rgba(0,0,0,0.5);
  display: flex;
}

/* Tabs 样式改造 */
.admin-tabs {
  width: 100%;
}
:deep(.el-tabs__item) {
  color: #8f98a0;
  text-align: left;
  padding-left: 30px !important;
  height: 60px;
  line-height: 60px;
  font-size: 16px;
}
:deep(.el-tabs__item.is-active) {
  color: #66c0f4;
  background: #212c3d;
  border-right: 3px solid #66c0f4;
}
:deep(.el-tabs__nav-wrap::after) {
  background-color: transparent;
}
:deep(.el-tabs__content) {
  padding: 30px;
}

.custom-tab {
  display: flex;
  align-items: center;
  gap: 10px;
}

/* 内容区 */
.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  border-bottom: 1px solid #3c4450;
  padding-bottom: 10px;
}

/* 深色表格适配 */
:deep(.el-table), :deep(.el-table tr), :deep(.el-table th) {
  background-color: transparent !important;
}
:deep(.el-table td.el-table__cell), :deep(.el-table th.el-table__cell.is-leaf) {
  border-bottom: 1px solid #3c4450;
}
:deep(.el-table--enable-row-hover .el-table__body tr:hover>td) {
  background-color: #2a3f5a !important;
}

/* 上传组件 */
.cover-uploader {
  border: 1px dashed #417a9b;
  width: 200px; height: 100px;
  display: flex; justify-content: center; align-items: center;
  background: #101216;
}
.cover-img { width: 100%; height: 100%; object-fit: cover; }
</style>