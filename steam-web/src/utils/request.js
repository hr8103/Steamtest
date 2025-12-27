import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建 axios 实例
const request = axios.create({
    baseURL: 'http://localhost:8080', // 指向你的 Spring Boot 后端
    timeout: 5000
})

// 响应拦截器 (自动处理后端返回的 JSON 格式)
request.interceptors.response.use(
    response => {
        const res = response.data
        // 如果后端返回 code 不是 200，说明出错了
        if (res.code !== 200) {
            ElMessage.error(res.msg || '系统错误')
            return Promise.reject(new Error(res.msg || 'Error'))
        }
        // 只返回 data 部分，方便组件使用
        return res.data
    },
    error => {
        console.log('err' + error)
        ElMessage.error(error.message)
        return Promise.reject(error)
    }
)

export default request