import { ref } from 'vue'
import { defineStore } from 'pinia'
import { getCurrentUserService } from '@/api/user'
import { showToast } from 'vant'
import type { UpdateUserByAuthParams } from '@/api/user/types'

interface User {
  avatarUrl: string
  createTime: Record<string, unknown>
  email: string
  gender: number
  id: number
  phone: string
  profile: string
  tags: string
  userAccount: string
  userRole: number
  userStatus: number
  username: string
}

export const useUserStore = defineStore(
  'user',
  () => {
    const defaultUserInfo = {
      avatarUrl: '',
      email: '',
      gender: 0,
      id: 0,
      phone: '',
      profile: '',
      tags: '',
      username: ''
    }
    // 用户是否登录
    const isLogin = ref<boolean>(false)
    // 当前用户信息
    const userInfo = ref<User>()
    // 用户更新信息
    const userUpdateInfo = ref<UpdateUserByAuthParams>({ ...defaultUserInfo })
    // 搜索用户参数
    const searchUserParams = ref<string>()

    // 设置更新信息
    const setUpdateInfo = () => {
      if (!userInfo.value) return
      userUpdateInfo.value = JSON.parse(JSON.stringify(userInfo.value))
    }
    // 登录后设置用户信息
    const setUserInfo = (newUserInfo: User) => {
      userInfo.value = newUserInfo
      isLogin.value = true
      setUpdateInfo()
    }
    // 获取用户信息
    const getUserInfo = () => {
      if (!userInfo.value) return
      return userInfo.value
    }
    // 退出登录清空用户信息
    const removeUserInfo = () => {
      userInfo.value = undefined
      userUpdateInfo.value = { ...defaultUserInfo }
      searchUserParams.value = ''
      isLogin.value = false
    }
    // 登录请求
    const getCurrentUser = async () => {
      const res = await getCurrentUserService()
      if (res.code !== 20000) {
        showToast('获取用户信息失败')
        return
      }
      setUserInfo(res.data)
    }
    return {
      userInfo,
      isLogin,
      userUpdateInfo,
      searchUserParams,
      setUserInfo,
      setUpdateInfo,
      getUserInfo,
      removeUserInfo,
      getCurrentUser
    }
  },
  // 持久化
  {
    persist: {
      paths: ['isLogin', 'userInfo', 'userUpdateInfo']
    }
  }
)
