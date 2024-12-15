<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { myTeamsService } from '@/api/team'
import ResponseCode from '@/ts/constant/ResponseCode'
import TeamCard from '@/views/components/TeamCard.vue'
import { useUserStore } from '@/stores'
import { useTeamStore } from '@/stores'
import type { Team } from './types'

const height = window.screen.height
const userStore = useUserStore()
const teamStore = useTeamStore()
const router = useRouter()
const active = ref<number>(0)
const loading = ref<boolean>(true)

// 我加入的队伍
const myTeams = ref<Team[]>([])
const myLeaderTeams = ref<Team[]>([])
// 队伍分类：我加入的、我管理的
const filterTeams = (teamList: Team[]): void => {
  myTeams.value = teamList.filter(
    (item) => item.leader !== userStore.userInfo?.id
  )
  myLeaderTeams.value = teamList.filter(
    (item) => item.leader === userStore.userInfo?.id
  )
  loading.value = false
}
// 获取我的队伍列表
const getMyTeams = async (): Promise<void> => {
  loading.value = true
  const res = await myTeamsService()
  if (res.code !== ResponseCode.SUCCESS) {
    return
  }
  teamStore.myTeamList = res.data
  filterTeams(teamStore.myTeamList)
}
// 初始化我的队伍列表
const initMyTeams = (): void => {
  // 如果本地存储为空，获取队伍列表
  if (teamStore.myTeamList.length === 0) {
    getMyTeams()
  } else {
    filterTeams(teamStore.myTeamList)
  }
}
initMyTeams()
</script>

<template>
  <div :style="{ height: height + 'px' }">
    <van-nav-bar
      title="我的队伍"
      left-arrow
      @click-left="router.replace('/team')"
    />
    <van-tabs v-model:active="active">
      <van-tab title="管理的队伍">
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
          v-if="myLeaderTeams.length <= 0"
        />
        <team-card
          v-for="item in myLeaderTeams"
          :key="item.id"
          :teamInfo="item"
          :isLeader="true"
        />
      </van-tab>
      <van-tab title="加入的队伍">
        <van-empty description="这里什么都没有" v-if="myTeams.length <= 0" />
        <team-card v-for="item in myTeams" :key="item.id" :teamInfo="item" />
      </van-tab>
    </van-tabs>
  </div>
</template>

<style scoped lang="less"></style>
