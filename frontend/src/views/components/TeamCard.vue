<script setup lang="ts">
import { useRouter } from 'vue-router'
import { showToast, showConfirmDialog } from 'vant'
import { teamQuitService } from '@/api/team'
import ResponseCode from '@/ts/constant/ResponseCode'
import { useTeamStore } from '@/stores'
import { inject } from 'vue'
import image from '@/assets/images/kiana.jpg'

const defaultAvatar = image
const defaultTeamAvatar = image
const teamStore = useTeamStore()
defineProps({
  teamInfo: {
    type: Object,
    required: true
  },
  isLeader: {
    type: Boolean,
    default: false
  }
})

const router = useRouter()
const reload = inject('reload') as Function

// 退出队伍
const quitTeam = (teamId: number): void => {
  showConfirmDialog({
    title: '温馨提示',
    message: '你确定要退出队伍吗？该操作不可逆',
    width: '80%',
    confirmButtonColor: '#ee0a24',
    cancelButtonColor: '#1989fa'
  })
    .then(async () => {
      // on confirm
      const res = await teamQuitService(teamId)
      if (res.code !== ResponseCode.SUCCESS) {
        return
      }
      showToast('退出成功')
      teamStore.removeTeamList()
      teamStore.removeMyTeamList()
      reload()
    })
    .catch(() => {
      // on cancel
    })
}
</script>

<template>
  <van-card v-if="isLeader" :thumb="defaultTeamAvatar">
    <template #title>
      <div class="title">
        {{ teamInfo.name }}
        <span :class="teamInfo.currentNum >= teamInfo.maxNum ? 'full' : 'empty'"
          >队伍人数：{{ teamInfo.currentNum }}/{{ teamInfo.maxNum }}</span
        >
      </div>
    </template>
    <template #desc>
      <div class="desc">
        <div class="text">{{ teamInfo.description }}</div>
      </div>
    </template>
    <template #price>
      <van-image
        v-for="member in teamInfo.members.slice(0, 4)"
        :key="member.id"
        round
        fit="cover"
        width="2rem"
        height="2rem"
        :src="member.avatarUrl || defaultAvatar"
      />
      <span v-if="teamInfo.members.length >= 4">...</span>
    </template>
    <template #num>
      <div>
        <van-button
          size="small"
          plain
          type="primary"
          @click="router.push(`/teamEdit/${teamInfo.id}`)"
          >管理队伍</van-button
        >
      </div>
    </template>
  </van-card>
  <van-card v-else :thumb="defaultTeamAvatar">
    <template #title>
      <div class="title">
        {{ teamInfo.name }}
        <span :class="teamInfo.currentNum >= teamInfo.maxNum ? 'full' : 'empty'"
          >队伍人数：{{ teamInfo.currentNum }}/{{ teamInfo.maxNum }}</span
        >
      </div>
    </template>
    <template #desc>
      <div class="desc">
        <div class="text">{{ teamInfo.description }}</div>
      </div>
    </template>
    <template #price>
      <van-image
        v-for="member in teamInfo.members.slice(0, 4)"
        :key="member.id"
        round
        fit="cover"
        width="2rem"
        height="2rem"
        :src="member.avatarUrl || defaultAvatar"
      />
      <span v-if="teamInfo.members.length >= 4">...</span>
    </template>
    <template #num>
      <div>
        <van-button
          size="small"
          plain
          type="danger"
          @click="quitTeam(teamInfo.id)"
          >退出队伍</van-button
        >
      </div>
    </template>
  </van-card>
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
  .text {
    width: 100%;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
}
.joined {
  font-size: 0.75rem;
  font-weight: bold;
  color: #646566;
}
</style>
