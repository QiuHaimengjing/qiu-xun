<script setup lang="ts">
import { ref } from 'vue'
import { userLoginService, userRegisterService } from '@/api/user'
import type { UserLoginParams, UserRegisterParams } from '@/types/user'
import { showToast } from 'vant'
import { useUserStore } from '@/stores'
import { useRouter } from 'vue-router'
import ResponseCode from '@/ts/constant/ResponseCode'

const userStore = useUserStore()
const router = useRouter()
const isLogin = ref<boolean>(true)
// 用户登录
const userLoginParams = ref<UserLoginParams>({
  userAccount: '',
  userPassword: ''
})
const onSubmit = async (formData: UserLoginParams): Promise<void> => {
  const res = await userLoginService(formData)
  if (res.code !== ResponseCode.SUCCESS) {
    return
  }
  userStore.setUserInfo(res.data)
  showToast('欢迎回来')
  router.replace('/')
}

// 用户注册
const registerUserAccount = ref<string>('')
const registerPassword = ref<string>('')
const registerVerifyPassword = ref<string>('')
const onClickLeft = () => router.replace('/user')
const register = async (FormData: UserRegisterParams): Promise<void> => {
  const res = await userRegisterService(FormData)
  if (res.code !== ResponseCode.SUCCESS) {
    return
  }
  showToast('注册成功，请登录')
  isLogin.value = true
}

// 输入框校验
const minUserAccount = (val: string) => {
  return val.length >= 4
}
const userAccountPattern = /^[a-zA-Z0-9_]+$/
const minPassword = (val: string) => {
  return val.length >= 6
}
const verifyPassword = (val: string) => {
  return val === registerPassword.value
}
</script>

<template>
  <van-nav-bar
    title="登录湫寻"
    left-text="返回"
    left-arrow
    @click-left="onClickLeft"
  />
  <van-notice-bar color="#1989fa" background="#ecf9ff" left-icon="info-o">
    如果注册过湫的其他项目，可以使用原本账号登录
  </van-notice-bar>
  <!-- 登录 -->
  <van-form @submit="onSubmit" v-if="isLogin">
    <van-cell-group inset>
      <van-field
        v-model="userLoginParams.userAccount"
        name="userAccount"
        label="账号"
        placeholder="账号"
        :rules="[
          { required: true, message: '请填写账号' },
          { validator: minUserAccount, message: '账号不得小于4位' },
          {
            pattern: userAccountPattern,
            message: '用户名必须是字母、数字或下划线组成'
          }
        ]"
      />
      <van-field
        v-model="userLoginParams.userPassword"
        type="password"
        name="userPassword"
        label="密码"
        placeholder="密码"
        :rules="[
          { required: true, message: '请填写密码' },
          { validator: minPassword, message: '密码不得小于6位' }
        ]"
      />
    </van-cell-group>
    <div style="margin: 16px">
      <van-button round block type="primary" native-type="submit">
        登录
      </van-button>
      <van-button
        round
        block
        type="default"
        @click="isLogin = false"
        style="margin-top: 1rem"
        >去注册</van-button
      >
    </div>
  </van-form>
  <!-- 注册 -->
  <van-form @submit="register" v-else>
    <van-cell-group inset>
      <van-field
        v-model="registerUserAccount"
        name="userAccount"
        label="账号"
        placeholder="账号"
        :rules="[
          { required: true, message: '请填写账号' },
          { validator: minUserAccount, message: '账号不得小于4位' },
          {
            pattern: userAccountPattern,
            message: '用户名必须是字母、数字或下划线组成'
          }
        ]"
      />
      <van-field
        v-model="registerPassword"
        type="password"
        name="userPassword"
        label="密码"
        placeholder="密码"
        :rules="[
          { required: true, message: '请填写密码' },
          { validator: minPassword, message: '密码不得小于6位' }
        ]"
      />
      <van-field
        v-model="registerVerifyPassword"
        type="password"
        name="verifyPassword"
        label="确认密码"
        placeholder="确认密码"
        :rules="[
          { required: true, message: '请填写密码' },
          { validator: minPassword, message: '密码不得小于6位' },
          { validator: verifyPassword, message: '两次密码不一致' }
        ]"
      />
    </van-cell-group>
    <div style="margin: 16px">
      <van-button round block type="primary" native-type="submit">
        注册
      </van-button>
      <van-button
        round
        block
        type="default"
        @click="isLogin = true"
        style="margin-top: 1rem"
        >去登录</van-button
      >
    </div>
  </van-form>
</template>

<style scoped lang="less"></style>
