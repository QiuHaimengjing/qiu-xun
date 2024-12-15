<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useUserStore, useHomeStore, useTeamStore } from '@/stores'
import { ref, inject } from 'vue'
import { showConfirmDialog } from 'vant'
import { userLogoutService } from '@/api/user'
import { showSuccessToast } from 'vant'
import ResponseCode from '@/ts/constant/ResponseCode'
import defaultAvatar from '@/assets/images/kiana.jpg'

interface User {
  avatarUrl: string
  createTime: Record<string, unknown>
  email: string
  gender: number
  id: number
  phone: string
  profile: string
  tags: string
  userAccount: string
  userRole: number
  userStatus: number
  username: string
}

const height = window.screen.height
const router = useRouter()
const userStore = useUserStore()
const homeStore = useHomeStore()
const teamStore = useTeamStore()

// 跳转编辑页面
const editUser = (): void => {
  router.push('/edit')
}
// 用户信息列表
const userInfo = ref<User>()
userInfo.value = userStore.getUserInfo()

// 右侧弹出层
const reload = inject('reload') as Function
const show: boolean = inject('showPopup') as boolean
// 退出登录
const logout = (): void => {
  // 弹出确认框
  showConfirmDialog({
    title: '温馨提示',
    message: '你确定要退出登录吗？',
    width: '80%',
    confirmButtonColor: '#a08cfa'
  })
    .then(async (): Promise<void> => {
      // 用户注销
      const res = await userLogoutService()
      if (res.code !== ResponseCode.SUCCESS) {
        return
      }
      showSuccessToast('注销成功')
      userStore.removeUserInfo()
      homeStore.removeRecommendUserList()
      teamStore.removeTeamList()
      teamStore.removeMyTeamList()
      reload()
    })
    .catch(() => {
      // on cancel
    })
}

// 用户标签
const tagArray = ref<string[]>()
if (userStore.isLogin) {
  // JSON字符串转数组
  tagArray.value = JSON.parse(userInfo.value?.tags as any)
}
</script>

<template>
  <div :style="{ height: height + 'px' }">
    <!-- 未登录页面 -->
    <div v-if="!userStore.isLogin" class="notLogin">
      <div class="title">欢迎访问湫寻</div>
      <van-image
        round
        width="10rem"
        height="10rem"
        fit="cover"
        :src="defaultAvatar"
      />
      <span class="tips">您还未登录哦</span>
      <van-button type="primary" to="/login" style="width: 80%"
        >去登录</van-button
      >
    </div>

    <!-- 登录后页面 -->
    <div class="info" v-else>
      <div class="banner">
        <van-image
          round
          width="7rem"
          height="7rem"
          fit="cover"
          :src="userInfo.avatarUrl || defaultAvatar"
        />
        <div class="content">
          <div class="nickname">
            {{ userInfo?.username || userInfo?.userAccount }}
          </div>
          <van-button
            plain
            hairline
            type="primary"
            class="edit-button"
            @click="editUser"
            >编辑资料</van-button
          >
        </div>
      </div>
      <van-divider />
      <div class="detail-info">
        <ul>
          <li>
            <span style="color: #699799">账号</span>
            {{ userInfo?.userAccount }}
          </li>
          <li>
            <span style="color: #699799">简介</span>
            {{ userInfo?.profile }}
          </li>
          <li>
            <span style="color: #699799">
              标签<van-button
                icon="plus"
                type="primary"
                size="small"
                style="margin-left: 1rem"
                @click="router.push('/editDetail/tags')"
                >添加标签</van-button
              ></span
            >
            <div style="margin-top: 0.5rem">
              <van-tag
                plain
                type="primary"
                size="large"
                v-for="(tag, index) in tagArray"
                :key="index"
                style="margin-left: 0.3rem"
                >{{ tag }}</van-tag
              >
            </div>
          </li>
        </ul>
      </div>
    </div>

    <!-- 右侧弹出层 -->
    <van-popup
      v-model:show="show"
      position="right"
      :style="{ width: '60%', height: '100%' }"
      class="right-popup"
    >
      <van-button block class="logout" square @click="logout"
        >退出登录</van-button
      >
    </van-popup>
  </div>
</template>

<style scoped lang="less">
.notLogin {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin-top: 1rem;
  .title {
    font-weight: 700;
    font-size: larger;
  }
  .tips {
    margin-top: 3rem;
    margin-bottom: 1rem;
    letter-spacing: 0.2rem;
  }
}
.info {
  margin: 1rem 1rem;
  .banner {
    display: flex;
    align-items: center;
    .content {
      display: flex;
      flex-direction: column;
      .nickname {
        margin-left: 1rem;
        font-size: 1.5rem;
        font-weight: 700;
      }
      .edit-button {
        position: absolute;
        height: 2rem;
        margin-left: 1rem;
        margin-top: 2.2rem;
      }
    }
  }
  .detail-info {
    ul li {
      margin-top: 1rem;
    }
  }
}
.right-popup {
  background-color: #eff2f5;
  .logout {
    // 固定底部
    position: fixed;
    bottom: 0;
  }
}
</style>
