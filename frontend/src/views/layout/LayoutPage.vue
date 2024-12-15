<script setup lang="ts">
import { ref, provide, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 底部标签栏
const active = ref<string>('')
active.value = route.name as string
watch(
  () => route.name,
  (val) => {
    active.value = val as string
  }
)

const search = (): void => {
  router.push('/search')
}

// 点击导航栏按钮展示弹出层
const showPopup = ref<boolean>(false)
provide('showPopup', showPopup)
const show = (): void => {
  showPopup.value = !showPopup.value
}

// 队伍页面气泡弹出框
// 通过 actions 属性来定义菜单选项
const showPopover = ref<boolean>(false)
const actions = [
  { text: '创建队伍', icon: 'add-o' },
  { text: '我的队伍', icon: 'more-o' }
]
const onSelect = (action: any, index: number): void => {
  if (index === 0) {
    router.push('/teamAdd')
  } else if (index === 1) {
    router.push('/teamOperation')
  }
}
</script>

<template>
  <van-nav-bar :title="route.meta.title as string">
    <template #right>
      <van-icon
        name="search"
        size="18"
        @click="search"
        v-if="route.path === '/home'"
      />

      <!-- 气泡弹出框 -->
      <van-popover
        placement="bottom-end"
        v-model:show="showPopover"
        :actions="actions"
        @select="onSelect"
        v-if="route.path === '/team'"
      >
        <template #reference>
          <van-icon name="setting-o" size="18" />
        </template>
      </van-popover>

      <van-icon
        name="bars"
        size="18"
        v-else-if="route.path === '/user' && userStore.isLogin"
        @click="show"
      />
    </template>
  </van-nav-bar>
  <RouterView />

  <van-tabbar v-model="active">
    <van-tabbar-item name="public" icon="flag-o" to="/public"
      >首页</van-tabbar-item
    >
    <van-tabbar-item name="home" icon="home-o" to="/home">匹配</van-tabbar-item>
    <van-tabbar-item name="team" icon="friends-o" to="/team"
      >队伍</van-tabbar-item
    >
    <van-tabbar-item name="user" icon="setting-o" to="/user"
      >我的</van-tabbar-item
    >
  </van-tabbar>
</template>

<style scoped lang="less"></style>
