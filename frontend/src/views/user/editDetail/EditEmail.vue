<script setup lang="ts">
import { useUserStore } from '@/stores'

const userStore = useUserStore()
</script>

<template>
  <van-field
    v-model="userStore.userUpdateInfo.email"
    label="邮箱"
    placeholder="请输入邮箱"
  />
</template>

<style scoped lang="less"></style>
