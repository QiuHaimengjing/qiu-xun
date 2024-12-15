<script setup lang="ts">
import { useTeamStore } from '@/stores'
import { teamListService, teamJoinService } from '@/api/team'
import ResponseCode from '@/ts/constant/ResponseCode'
import { ref } from 'vue'
import { showConfirmDialog, showToast } from 'vant'
import image from '@/assets/images/kiana.jpg'
import type { Team } from './types'

const height = window.screen.height
const defaultAvatar = image
const defaultTeamAvatar = image
const loading = ref<boolean>(true)
// 队伍列表
const teamList = ref<Team[] | any>([])
const teamStore = useTeamStore()
const teamQueryParams = ref({
  pageNO: 1,
  pageSize: 10
})

const getTeamList = async () => {
  loading.value = true
  const res = await teamListService(teamQueryParams.value)
  if (res.code !== ResponseCode.SUCCESS) {
    return
  }
  loading.value = false
  teamList.value = res.data
  teamStore.setTeamList(res.data)
}
// 初始化队伍列表
const initTeamList = (): void => {
  if (teamStore.teamList.length === 0) {
    getTeamList()
  } else {
    loading.value = false
    teamList.value = teamStore.teamList
  }
}
initTeamList()

// 加入队伍
const joinTeam = (teamId: number): void => {
  showConfirmDialog({
    title: '温馨提示',
    width: '80%',
    confirmButtonColor: '#1989fa',
    message: '确定加入队伍吗？'
  })
    .then(async (): Promise<void> => {
      // on confirm
      const joinParams = {
        id: teamId
      }
      const res = await teamJoinService(joinParams)
      if (res.code !== ResponseCode.SUCCESS) {
        return
      }
      showToast('加入成功')
      getTeamList()
      teamStore.removeMyTeamList()
    })
    .catch(() => {
      // on cancel
    })
}
const keyword = ref<string>('')
const onSearch = async (): Promise<void> => {
  const params = {
    keyWords: keyword.value,
    pageNO: 1,
    pageSize: 10
  }
  loading.value = true
  const res = await teamListService(params)
  if (res.code !== ResponseCode.SUCCESS) {
    return
  }
  loading.value = false
  teamList.value = res.data
}
</script>

<template>
  <div :style="{ height: height + 'px' }">
    <van-search
      v-model="keyword"
      placeholder="请输入搜索关键词"
      @search="onSearch"
    />
    <div style="padding-bottom: 4rem">
      <!-- 队伍列表 -->
      <van-skeleton
        title
        avatar
        :row="3"
        :loading="loading"
        v-for="(item, index) in 10"
        :key="index"
        style="margin-bottom: 1rem"
      />
      <van-empty description="这里什么都没有" v-if="teamList.length <= 0" />
      <van-card
        v-for="item in teamList"
        :key="item.id"
        :thumb="defaultTeamAvatar"
      >
        <template #title>
          <div class="title">
            {{ item.name }}
            <span :class="item.currentNum >= item.maxNum ? 'full' : 'empty'"
              >队伍人数：{{ item.currentNum }}/{{ item.maxNum }}</span
            >
          </div>
        </template>
        <template #desc>
          <div class="desc">
            <van-text-ellipsis :content="item.description" />
          </div>
        </template>
        <template #price>
          <van-image
            v-for="member in item.members.slice(0, 4)"
            :key="member.id"
            round
            fit="cover"
            width="2rem"
            height="2rem"
            :src="member.avatarUrl || defaultAvatar"
          />
          <span v-if="item.members.length >= 4">...</span>
        </template>
        <template #num>
          <div>
            <van-button
              size="small"
              plain
              type="primary"
              v-if="!item.joined"
              @click="joinTeam(item.id)"
              >加入队伍</van-button
            >
            <div class="joined" v-else>已加入</div>
          </div>
        </template>
      </van-card>
    </div>
  </div>
</template>

<style scoped lang="less">
.title {
  font-size: 1rem;
  font-weight: bold;
  color: #323233;
  margin-bottom: 0.625rem;
  .full {
    font-size: 0.75rem;
    margin-left: 0.625rem;
    color: #646566;
  }
  .empty {
    font-size: 0.75rem;
    margin-left: 0.625rem;
    color: #67c23a;
  }
}
.desc {
  font-size: 0.75rem;
  color: #646566;
  margin-bottom: 0.625rem;
}
.joined {
  font-size: 0.75rem;
  font-weight: bold;
  color: #646566;
}
</style>
