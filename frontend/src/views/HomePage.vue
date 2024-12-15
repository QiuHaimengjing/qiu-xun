<script setup lang="ts">
import {
  recommendUsersService,
  userMatchService,
  getAllUserService
} from '@/api/user'
import { ref } from 'vue'
import UserCard from '@/views/components/UserCard.vue'
import { useHomeStore } from '@/stores'
import ResponseCode from '@/ts/constant/ResponseCode'

const height = window.screen.height
const homeStore = useHomeStore()
const loading = ref<boolean>(true)

// 分页参数
const showPage = ref<boolean>(false)

// 获取默认推荐用户
const getRecommendUsers = async () => {
  loading.value = true
  const res = await recommendUsersService(homeStore.pageRequestParams)
  if (res.code !== ResponseCode.SUCCESS) {
    return
  }
  loading.value = false
  homeStore.recommendUserList = res.data.userList
  homeStore.total = res.data.total
  homeStore.matchValue = 0
}

// 本地Store
if (homeStore.recommendUserList.length === 0) {
  homeStore.resetPageRequestParams()
  getRecommendUsers()
} else {
  loading.value = false
}

if (homeStore.matchValue === 2) {
  showPage.value = true
}

// 获取标签匹配用户
const getMatchUsers = async () => {
  loading.value = true
  const res = await userMatchService(10)
  if (res.code !== ResponseCode.SUCCESS) {
    return
  }
  loading.value = false
  homeStore.recommendUserList = res.data.userList
  homeStore.total = res.data.total
  homeStore.matchValue = 1
}

// 获取所有用户
const getAllUsers = async () => {
  loading.value = true
  const res = await getAllUserService(homeStore.pageRequestParams)
  if (res.code !== ResponseCode.SUCCESS) {
    return
  }
  loading.value = false
  homeStore.total = res.data.total
  homeStore.recommendUserList = res.data.userList
  homeStore.matchValue = 2
  // 回到顶部
  window.scrollTo(0, 0)
}

// 选择推荐模式
const value2 = ref('a')
const matchType = [
  { text: '默认推荐', value: 0 },
  { text: '标签匹配', value: 1 },
  { text: '所有用户', value: 2 }
]
const option2 = [{ text: '默认排序', value: 'a' }]
const onChange = (value: number) => {
  loading.value = true
  homeStore.removeRecommendUserList()
  if (value === 0) {
    showPage.value = false
    homeStore.resetPageRequestParams()
    getRecommendUsers()
  }
  if (value === 1) {
    showPage.value = false
    getMatchUsers()
  }
  if (value === 2) {
    homeStore.resetPageRequestParams()
    showPage.value = true
    getAllUsers()
  }
}
</script>

<template>
  <div :style="{ minHeight: height }">
    <van-dropdown-menu>
      <van-dropdown-item
        v-model="homeStore.matchValue"
        :options="matchType"
        @change="onChange"
      />
      <van-dropdown-item v-model="value2" :options="option2" />
    </van-dropdown-menu>
    <div style="padding-bottom: 4rem">
      <van-skeleton
        title
        avatar
        :row="3"
        :loading="loading"
        v-for="(item, index) in 10"
        :key="index"
        style="margin-bottom: 1rem"
      />
      <van-empty
        description="这里什么都没有"
        v-if="homeStore.recommendUserList.length <= 0"
      />

      <user-card
        v-for="item in homeStore.recommendUserList"
        :key="item.id"
        :userInfo="item"
      />
      <van-pagination
        v-if="showPage"
        v-model="homeStore.pageRequestParams.pageNO"
        :total-items="homeStore.total"
        :show-page-size="5"
        force-ellipses
        @change="getAllUsers"
      />
    </div>
  </div>
</template>

<style scoped lang="less"></style>
