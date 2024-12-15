<script setup lang="ts">
import { useUserStore } from '@/stores'

const userStore = useUserStore()
</script>

<template>
  <van-field
    v-model="userStore.userUpdateInfo.username"
    label="昵称"
    placeholder="请输入昵称"
  />
</template>

<style scoped lang="less"></style>
