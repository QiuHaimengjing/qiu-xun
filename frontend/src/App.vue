<script setup lang="ts">
import { nextTick, provide, ref } from 'vue'
import { useHomeStore, useTeamStore } from './stores'

const homeStore = useHomeStore()
const teamStore = useTeamStore()
// 用于刷新路由
const isRouterAlive = ref<boolean>(true)
const reload = (): void => {
  isRouterAlive.value = false
  nextTick(() => {
    isRouterAlive.value = true
  })
}
provide('reload', reload)
const refresh = ref<boolean>(false)
const onRefresh = () => {
  setTimeout(() => {
    homeStore.removeRecommendUserList()
    teamStore.removeTeamList()
    teamStore.removeMyTeamList()
    refresh.value = false
    reload()
  }, 1000)
}
</script>

<template>
  <div>
    <van-pull-refresh
      v-model="refresh"
      @refresh="onRefresh"
      style="height: 100%"
    >
      <RouterView v-if="isRouterAlive" />
    </van-pull-refresh>
  </div>
</template>

<style scoped></style>
