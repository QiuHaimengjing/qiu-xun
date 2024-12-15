<script setup lang="ts">
import { useUserStore } from '@/stores'

const userStore = useUserStore()
</script>

<template>
  <van-field
    v-model="userStore.userUpdateInfo.phone"
    label="电话"
    placeholder="请输入电话"
  />
</template>

<style scoped lang="less"></style>
