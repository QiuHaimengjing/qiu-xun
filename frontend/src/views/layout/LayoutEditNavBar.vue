<script setup lang="ts">
import { showToast } from 'vant'
import { useRoute } from 'vue-router'
import { updateUserService } from '@/api/user'
import { useUserStore } from '@/stores'
import ResponseCode from '@/ts/constant/ResponseCode'

const userStore = useUserStore()
const route = useRoute()
const onClickLeft = (): void => {
  userStore.setUpdateInfo()
  history.back()
}
const onClickRight = async (): Promise<void> => {
  const res = await updateUserService(userStore.userUpdateInfo)
  if (res.code !== ResponseCode.SUCCESS) {
    return
  }
  await userStore.getCurrentUser()
  showToast('保存成功')
  history.back()
}

const title = route.meta.title
</script>

<template>
  <van-nav-bar
    :title="title as string"
    left-text="返回"
    right-text="保存"
    left-arrow
    @click-left="onClickLeft"
    @click-right="onClickRight"
  />
  <RouterView />
</template>

<style scoped lang="less"></style>
