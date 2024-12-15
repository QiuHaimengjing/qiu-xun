<script setup lang="ts">
import { showConfirmDialog, showToast } from 'vant'
import { ref } from 'vue'
import { useUserStore } from '@/stores'
import { updateUserService } from '@/api/user'
import ResponseCode from '@/ts/constant/ResponseCode'
import { uploadAvatarService } from '@/api/user'
import defaultAvatar from '@/assets/images/kiana.jpg'

const onClickLeft = () => {
  if (userStore.userUpdateInfo?.avatarUrl !== userStore.userInfo?.avatarUrl) {
    showConfirmDialog({
      title: '您修改了头像',
      message: '是否保存？（每天最多修改5次）',
      width: '80%',
      confirmButtonColor: '#a08cfa'
    })
      .then(async () => {
        // on confirm
        const formData = new FormData()
        formData.append('file', fileList.value[0].file)
        const res = await uploadAvatarService(formData)
        if (res.code !== ResponseCode.SUCCESS) {
          return
        }
        showToast('修改成功')
        await userStore.getCurrentUser()
        history.back()
      })
      .catch(() => {
        // on cancel
        userStore.setUpdateInfo()
        history.back()
      })
  } else {
    history.back()
  }
}

const showPicker = ref<boolean>(false)
const userStore = useUserStore()
// 处理性别显示
const columns = [
  { text: '男', value: 1 },
  { text: '女', value: 0 },
  { text: '未知', value: -1 }
]
const gender = ref<string>('')
const genderHandler = (): void => {
  if (userStore.userUpdateInfo?.gender === 1) {
    gender.value = '男'
  } else if (userStore.userUpdateInfo?.gender === 0) {
    gender.value = '女'
  } else {
    gender.value = '未知'
  }
}
genderHandler()
// 修改性别
const confirmGender = async ({ selectedOptions }: any): Promise<void> => {
  if (!userStore.userUpdateInfo) {
    return
  }
  userStore.userUpdateInfo.gender = selectedOptions[0].value
  const res = await updateUserService(userStore.userUpdateInfo)
  if (res.code !== ResponseCode.SUCCESS) {
    return
  }
  genderHandler()
  userStore.getCurrentUser()
  showToast('修改成功')
  showPicker.value = false
}

// 更换头像
const fileList = ref<any>([])
const afterRead = () => {
  if (!userStore.userUpdateInfo) {
    return
  }
  // 覆盖上一张
  fileList.value = fileList.value.slice(-1)
  userStore.userUpdateInfo.avatarUrl = fileList.value[0].objectUrl
}

const formData = ref<{
  username: string
  phone: string
  email: string
  profile: string
}>({
  username: '',
  phone: '',
  email: '',
  profile: ''
})
formData.value.username = userStore.userUpdateInfo?.username || ''
formData.value.phone = userStore.userUpdateInfo?.phone || ''
formData.value.email = userStore.userUpdateInfo?.email || ''
formData.value.profile = userStore.userUpdateInfo?.profile || ''
</script>

<template>
  <van-nav-bar title="修改信息" left-arrow @click-left="onClickLeft" />

  <div class="avatar">
    <van-image
      round
      width="10rem"
      height="10rem"
      fit="cover"
      :src="userStore.userUpdateInfo.avatarUrl || defaultAvatar"
    />
  </div>
  <van-uploader
    v-model="fileList"
    max-count="2"
    reupload
    :preview-image="false"
    :after-read="afterRead"
  >
    <van-field readonly name="avatarUrl" label="头像" placeholder="更换头像" />
  </van-uploader>
  <van-field
    v-model="formData.username"
    is-link
    readonly
    name="username"
    label="昵称"
    placeholder="请输入昵称"
    to="/editDetail/username"
  />
  <van-field
    v-model="formData.phone"
    is-link
    readonly
    name="phone"
    label="电话"
    placeholder="请输入电话号码"
    to="/editDetail/phone"
  />
  <van-field
    v-model="gender"
    is-link
    readonly
    name="gender"
    label="性别"
    placeholder="点击选择性别"
    @click="showPicker = true"
  />
  <van-field
    v-model="formData.email"
    is-link
    readonly
    name="email"
    label="邮箱"
    placeholder="请输入邮箱"
    to="/editDetail/email"
  />
  <van-field
    v-model="formData.profile"
    is-link
    readonly
    name="profile"
    label="简介"
    placeholder="请输入简介"
    to="/editDetail/profile"
  />
  <van-popup v-model:show="showPicker" position="bottom" round>
    <van-picker
      :columns="columns"
      @confirm="confirmGender"
      @cancel="showPicker = false"
    />
  </van-popup>
</template>

<style scoped lang="less">
.avatar {
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f7f8fa;
}
</style>
