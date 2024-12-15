<script setup lang="ts">
import { useUserStore } from '@/stores'
import UserCard from '@/views/components/UserCard.vue'
import { ref } from 'vue'
import { searchUsersByTagsService } from '@/api/user'
import ResponseCode from '@/ts/constant/ResponseCode'
import type { User } from '../team/types'

const loading = ref<boolean>(true)
const userStore = useUserStore()
const onClickLeft = (): void => history.back()
const resultList = ref<User[]>([])

// 搜索框
const getSearchList = async (): Promise<void> => {
  const res = await searchUsersByTagsService(userStore.searchUserParams)
  if (res.code !== ResponseCode.SUCCESS) {
    return
  }
  loading.value = false
  resultList.value = res.data
}
getSearchList()
</script>

<template>
  <van-nav-bar title="标签匹配的用户" left-arrow @click-left="onClickLeft" />
  <van-skeleton
    title
    avatar
    :row="3"
    :loading="loading"
    v-for="(item, index) in 10"
    :key="index"
    style="margin-bottom: 1rem"
  />
  <van-empty description="这里什么都没有" v-if="resultList.length <= 0" />
  <user-card v-for="item in resultList" :key="item.id" :userInfo="item" />
</template>

<style scoped lang="less"></style>
