import { ref } from 'vue'

// 响应式购物车状态
export const cartState = ref([])

/**
 * 获取当前登录用户的存储Key
 * 格式：steam_cart_{userId}
 * 如果未登录，返回 null
 */
const getCartKey = () => {
    const userStr = localStorage.getItem('user')
    if (!userStr) return null
    try {
        const user = JSON.parse(userStr)
        // 确保有 ID，否则视为无效
        return user.id ? `steam_cart_${user.id}` : null
    } catch (e) {
        return null
    }
}

/**
 * 初始化购物车
 * 每次页面加载或登录状态变化时调用
 */
export const initCart = () => {
    const key = getCartKey()
    if (key) {
        // 已登录：读取专属数据
        const stored = localStorage.getItem(key)
        cartState.value = stored ? JSON.parse(stored) : []
    } else {
        // 未登录：购物车为空
        cartState.value = []
    }
}

/**
 * 添加到购物车
 * 返回值：true=成功/已存在，false=未登录
 */
export const addToCart = (game) => {
    const key = getCartKey()
    if (!key) {
        // 未登录，拒绝添加
        return false
    }

    const current = cartState.value
    // 查重
    const exists = current.find(item => item.id === game.id)

    if (!exists) {
        // 构造购物车商品对象
        current.push({
            id: game.id,
            title: game.title,
            price: game.price,
            coverUrl: game.coverUrl
        })
        // 存入当前用户的 Key 下
        localStorage.setItem(key, JSON.stringify(current))
    }
    return true
}

/**
 * 从购物车移除
 */
export const removeFromCart = (id) => {
    const key = getCartKey()
    if (!key) return

    const current = cartState.value.filter(item => item.id !== id)
    cartState.value = current
    localStorage.setItem(key, JSON.stringify(current))
}

/**
 * 检查是否在购物车中
 */
export const isInCart = (id) => {
    // 如果没登录，肯定不在购物车
    if (!getCartKey()) return false
    return cartState.value.some(item => item.id === id)
}

/**
 * 清空购物车 (支付成功后调用)
 */
export const clearCart = () => {
    const key = getCartKey()
    if (key) {
        localStorage.removeItem(key)
        cartState.value = []
    }
}

/**
 * 获取当前购物车数据 (兼容旧代码)
 */
export const getCart = () => {
    initCart()
    return cartState.value
}

// 自动初始化一次
initCart()