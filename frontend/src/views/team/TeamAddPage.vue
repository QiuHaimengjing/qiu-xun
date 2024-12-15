<script setup lang="ts">
import { ref } from 'vue'
import {
  formatSystemDate,
  formatSystemTime,
  formatSystemDateTime
} from '@/utils/formatTime'
import { TeamStatus } from '@/ts/enums/TeamStatusEnum'
import { teamAddService } from '@/api/team'
import ResponseCode from '@/ts/constant/ResponseCode'
import { showToast } from 'vant'
import { useRouter } from 'vue-router'
import { useTeamStore } from '@/stores'
import type { AddTeamParams } from '@/api/team/types'

const height = window.screen.height
const onClickLeft = (): void => history.back()
const router = useRouter()
const teamStore = useTeamStore()

const defaultTeamAddParams: AddTeamParams = {
  name: '',
  description: '',
  maxNum: 0,
  expireTime: '',
  status: 0,
  password: ''
}
const teamAddParams = ref<AddTeamParams>({ ...defaultTeamAddParams })

// 队伍有效期
// 展开日期/时间选择器
const showDate = ref<boolean>(false)
const showTime = ref<boolean>(false)
// 设置默认初始时间
const date = ref<string>(formatSystemDate(new Date()))
const time = ref<string>(formatSystemTime(new Date()))
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
const status = ref<string>('公开')
const showStatus = ref<boolean>(false)
const onConfirmStatus = ({ selectedValues }: any): void => {
  if (selectedValues[0] === TeamStatus.PUBLIC) {
    status.value = '公开'
  } else if (selectedValues[0] === TeamStatus.PRIVATE) {
    status.value = '私有'
  } else {
    status.value = '加密'
  }
  teamAddParams.value.status = selectedValues[0]
  showStatus.value = false
}

// 创建队伍
const onSubmit = async (): Promise<void> => {
  // 格式化日期时间
  teamAddParams.value.expireTime = formatSystemDateTime(date.value + time.value)
  const res = await teamAddService(teamAddParams.value)
  if (res.code !== ResponseCode.SUCCESS) {
    return
  }
  showToast('创建队伍成功')
  teamStore.removeTeamList()
  router.push('/team')
}
</script>

<template>
  <div :style="{ height: height + 'px' }">
    <van-nav-bar title="创建队伍" left-arrow @click-left="onClickLeft" />

    <van-form required="auto" @submit="onSubmit">
      <van-cell-group inset>
        <van-field
          v-model="teamAddParams.name"
          name="队伍名称"
          label="队伍名称"
          placeholder="队伍名称"
          :rules="[{ required: true, message: '请填写队伍名称' }]"
        />
        <van-field
          v-model="teamAddParams.description"
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
            <van-stepper v-model="teamAddParams.maxNum" min="3" max="20" />
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
          v-model="teamAddParams.password"
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

<style scoped lang="less"></style>
