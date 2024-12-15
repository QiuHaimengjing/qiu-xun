<script setup lang="ts">
import { useUserStore } from '@/stores'

const userStore = useUserStore()
</script>

<template>
  <van-cell-group inset>
    <van-field
      v-model="userStore.userUpdateInfo.profile"
      rows="2"
      autosize
      label="简介"
      type="textarea"
      maxlength="50"
      placeholder="请输入简介"
      show-word-limit
    />
  </van-cell-group>
</template>

<style scoped lang="less"></style>
