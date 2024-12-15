import type { RecommendUsersParams } from '@/api/user/types'
import type { User } from '@/views/team/types'
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useHomeStore = defineStore(
  'home',
  () => {
    // 推荐用户列表
    const recommendUserList = ref<User[]>([])
    const matchValue = ref<number>()

    // 推荐用户请求参数
    const total = ref<number>()
    const defaultPageRequestParams = {
      pageNO: 1,
      pageSize: 20
    }
    const pageRequestParams = ref<RecommendUsersParams>({
      ...defaultPageRequestParams
    })

    const resetPageRequestParams = () => {
      pageRequestParams.value = { ...defaultPageRequestParams }
    }

    // 清空推荐用户列表
    const removeRecommendUserList = () => {
      recommendUserList.value = []
      total.value = 0
    }

    return {
      recommendUserList,
      removeRecommendUserList,
      matchValue,
      pageRequestParams,
      resetPageRequestParams,
      total
    }
  },
  // 持久化
  {
    persist: {
      paths: []
    }
  }
)
