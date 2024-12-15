<script setup lang="ts">
import { ref } from 'vue'
import image from '@/assets/images/kiana.jpg'

const defaultAvatar = image
const props = defineProps({
  userInfo: {
    type: Object,
    required: true
  }
})
const tagArray = ref<string[]>([])
const setTagArray = (): void => {
  if (props.userInfo.tags) {
    tagArray.value = JSON.parse(props.userInfo.tags)
  } else {
    tagArray.value = []
  }
}
setTagArray()
const show = ref(false)
</script>

<template>
  <van-card>
    <template #title>
      <div style="font-size: 16px">
        <span style="font-weight: 700; color: #323233">{{
          userInfo.username
        }}</span>
      </div>
      <div style="color: #323233">账号: {{ userInfo.userAccount }}</div>
    </template>
    <template #desc>
      <div style="color: #969799">
        <span>简介：</span>
        <span>{{ userInfo.profile }}</span>
      </div>
    </template>
    <template #thumb>
      <van-image
        v-if="userInfo.avatarUrl"
        width="100%"
        height="100%"
        :src="userInfo.avatarUrl"
        fit="cover"
      />
      <van-image
        v-else
        width="100%"
        height="100%"
        :src="defaultAvatar"
        fit="cover"
      />
    </template>
    <template #tags>
      <van-tag
        plain
        type="primary"
        v-for="(tag, index) in tagArray"
        :key="index"
        >{{ tag }}</van-tag
      >
    </template>
    <template #num>
      <van-button size="mini" @click="show = true">联系我</van-button>
    </template>
  </van-card>
  <van-popup v-model:show="show" round :style="{ padding: '2rem' }">
    <div v-if="userInfo.email">
      邮箱：<span class="email">{{ userInfo.email }}</span>
    </div>
    <div v-else>
      <span class="email">该用户未设置联系方式</span>
    </div>
  </van-popup>
</template>

<style scoped lang="less">
.email {
  color: #1989fa;
}
</style>
