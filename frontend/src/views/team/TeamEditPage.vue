<script setup lang="ts">
import { useRouter, useRoute } from 'vue-router'
import { ref } from 'vue'

import {
  formatSystemDate,
  formatSystemTime,
  formatSystemDateTime,
  formatDateTimeToDate,
  formatDateTimeToTime
} from '@/utils/formatTime'
import { TeamStatus } from '@/ts/enums/TeamStatusEnum'
import {
  myTeamsServiceById,
  teamUpdateService,
  teamDeleteService,
  teamQuitService
} from '@/api/team'
import ResponseCode from '@/ts/constant/ResponseCode'
import { showToast, showConfirmDialog } from 'vant'
import { useTeamStore } from '@/stores'
import type { UpdateTeamParams } from '@/api/team/types'
import type { Team } from './types'

const height = window.screen.height
const route = useRoute()
const router = useRouter()
const teamStore = useTeamStore()
const loading = ref<boolean>(true)
const teamId = route.params.id as unknown as number
// 队伍更新信息
const defaultTeamUpdateParams: UpdateTeamParams = {
  id: 0,
  name: '',
  description: '',
  maxNum: 0,
  expireTime: '',
  status: 0,
  password: ''
}
const teamUpdateParams = ref<UpdateTeamParams>({ ...defaultTeamUpdateParams })
const current = ref<number>(0)

// 初始化队伍更新信息
// 确认队伍状态
const status = ref<string>('')
const selectStatus = (status: number): string => {
  if (status === TeamStatus.PUBLIC) {
    return '公开'
  } else if (status === TeamStatus.PRIVATE) {
    return '私有'
  } else {
    return '加密'
  }
}
// 设置默认初始时间
const date = ref<string>('')
const time = ref<string>('')
const initTeamUpdateParams = (data: Team | any): void => {
  teamUpdateParams.value.id = data.id
  teamUpdateParams.value.name = data.name
  teamUpdateParams.value.description = data.description
  teamUpdateParams.value.maxNum = data.maxNum
  teamUpdateParams.value.expireTime = data.expireTime
  teamUpdateParams.value.status = data.status
  current.value = data.currentNum
  date.value = formatDateTimeToDate(data.expireTime)
  time.value = formatDateTimeToTime(data.expireTime)
  status.value = selectStatus(data.status)
}
// 根据id查询队伍信息
const initformDate = async (): Promise<void> => {
  const res = await myTeamsServiceById(teamId)
  if (res.code !== ResponseCode.SUCCESS) {
    return
  }
  initTeamUpdateParams(res.data[0])
  loading.value = false
}
initformDate()

// 队伍有效期
// 展开日期/时间选择器
const showDate = ref<boolean>(false)
const showTime = ref<boolean>(false)
// 最小日期/时间
const minDate = new Date()
const minTime = formatSystemTime(new Date())
// 确认日期
const onConfirmDate = ({ selectedValues }: any): void => {
  date.value = selectedValues.join('-')
  showDate.value = false
}
// 确认时间
const onConfirmTime = ({ selectedValues }: any): void => {
  time.value = `${selectedValues.join(':')}`
  showTime.value = false
}

// 队伍状态
const statusColumns = [
  { text: '公开', value: TeamStatus.PUBLIC },
  { text: '私有', value: TeamStatus.PRIVATE },
  { text: '加密', value: TeamStatus.SECRET }
]
const showStatus = ref<boolean>(false)
const onConfirmStatus = ({ selectedValues }: any): void => {
  status.value = selectStatus(selectedValues[0])
  teamUpdateParams.value.status = selectedValues[0]
  showStatus.value = false
}

// 更新队伍
const onSubmit = async (): Promise<void> => {
  // 格式化日期时间
  teamUpdateParams.value.expireTime = formatSystemDateTime(
    date.value + time.value
  )
  const res = await teamUpdateService(teamUpdateParams.value)
  if (res.code !== ResponseCode.SUCCESS) {
    return
  }
  showToast('更新成功')
  teamStore.removeTeamList()
  teamStore.removeMyTeamList()
  router.replace('/teamOperation')
}

// 退出队伍
const quitTeam = (): void => {
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
      router.replace('/teamOperation')
    })
    .catch(() => {
      // on cancel
    })
}

// 解散队伍
const deleteTeam = () => {
  showConfirmDialog({
    title: '温馨提示',
    message: '你确定要解散队伍吗？该操作不可逆',
    width: '80%',
    confirmButtonColor: '#ee0a24',
    cancelButtonColor: '#1989fa'
  })
    .then(async () => {
      // on confirm
      const res = await teamDeleteService(teamId)
      if (res.code !== ResponseCode.SUCCESS) {
        return
      }
      showToast('成功解散队伍')
      teamStore.removeTeamList()
      teamStore.removeMyTeamList()
      router.replace('/teamOperation')
    })
    .catch(() => {
      // on cancel
    })
}

// 气泡弹出框
const showPopover = ref<boolean>(false)
const actions = [{ text: '解散队伍' }, { text: '退出队伍' }]
const onSelect = (action: any, index: number): void => {
  if (index === 0) {
    deleteTeam()
  } else if (index === 1) {
    quitTeam()
  }
}
</script>

<template>
  <div :style="{ height: height + 'px' }">
    <van-nav-bar title="编辑队伍信息" left-arrow @click-left="router.back">
      <template #right>
        <!-- 气泡弹出框 -->
        <van-popover
          placement="bottom-end"
          v-model:show="showPopover"
          :actions="actions"
          @select="onSelect"
        >
          <template #reference>
            <van-icon name="delete-o" size="18" color="#ee0a24" />
          </template>
        </van-popover>
      </template>
    </van-nav-bar>

    <van-loading color="#1989fa" vertical v-if="loading" class="loading"
      >加载中...</van-loading
    >

    <van-form required="auto" @submit="onSubmit" v-else>
      <van-cell-group inset>
        <van-field
          v-model="teamUpdateParams.name"
          name="队伍名称"
          label="队伍名称"
          placeholder="队伍名称"
          :rules="[{ required: true, message: '请填写队伍名称' }]"
        />
        <van-field
          v-model="teamUpdateParams.description"
          rows="2"
          autosize
          label="描述"
          name="描述"
          type="textarea"
          maxlength="50"
          placeholder="请输入队伍描述"
          show-word-limit
        />
        <van-field name="人数" label="人数">
          <template #input>
            <van-stepper
              v-model="teamUpdateParams.maxNum"
              :min="current"
              max="20"
            />
          </template>
        </van-field>
        <van-field
          v-model="date"
          is-link
          readonly
          name="datePicker"
          label="队伍有效期"
          placeholder="点击选择日期"
          @click="showDate = true"
        />
        <van-popup v-model:show="showDate" position="bottom" round>
          <van-date-picker
            title="选择日期"
            :min-date="minDate"
            @confirm="onConfirmDate"
            @cancel="showDate = false"
          />
        </van-popup>
        <van-field
          v-model="time"
          readonly
          is-link
          name="timePicker"
          label=""
          placeholder="点击选择时间"
          style="margin-left: 6.2rem"
          @click="showTime = true"
        />
        <van-popup v-model:show="showTime" position="bottom" round>
          <van-time-picker
            title="选择时间"
            :min-time="
              date === formatSystemDate(new Date()) ? minTime : '00:00:00'
            "
            @confirm="onConfirmTime"
            @cancel="showTime = false"
          />
        </van-popup>
        <van-field
          v-model="status"
          is-link
          readonly
          name="statusPicker"
          label="状态"
          placeholder="点击选择队伍状态"
          @click="showStatus = true"
        />
        <van-popup v-model:show="showStatus" position="bottom" round>
          <van-picker
            title="队伍状态"
            :columns="statusColumns"
            @confirm="onConfirmStatus"
            @cancel="showStatus = false"
          />
        </van-popup>
        <van-notice-bar
          color="#1989fa"
          background="#ecf9ff"
          left-icon="info-o"
          v-if="status === '私有'"
        >
          私有状态下队伍不会对外开放, 只能邀请加入
        </van-notice-bar>
        <van-field
          v-if="status === '加密'"
          v-model="teamUpdateParams.password"
          type="password"
          name="密码"
          label="密码"
          placeholder="密码"
          :rules="[{ required: true, message: '请填写密码' }]"
        />
      </van-cell-group>
      <div style="margin: 16px">
        <van-button round block type="primary" native-type="submit">
          提交
        </van-button>
      </div>
    </van-form>
  </div>
</template>

<style scoped lang="less">
.loading {
  margin: 50% auto;
}
</style>
