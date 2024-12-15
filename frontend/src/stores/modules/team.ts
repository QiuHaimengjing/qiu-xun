import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { Team } from '@/types/team'

export const useTeamStore = defineStore(
  'team',
  () => {
    // 队伍列表
    const teamList = ref<Team[]>([])
    // 我的队伍列表
    const myTeamList = ref<Team[]>([])

    // 设置队伍列表
    const setTeamList = (newTeamList: Team[]) => {
      teamList.value = newTeamList
    }
    // 移除队伍列表
    const removeTeamList = () => {
      teamList.value = []
    }

    // 设置我的队伍列表
    const setMyTeamList = (newTeamList: Team[]) => {
      myTeamList.value = newTeamList
    }
    // 移除我的队伍列表
    const removeMyTeamList = () => {
      myTeamList.value = []
    }

    return {
      teamList,
      setTeamList,
      removeTeamList,
      myTeamList,
      setMyTeamList,
      removeMyTeamList
    }
  },
  {
    persist: {
      paths: []
    }
  }
)
