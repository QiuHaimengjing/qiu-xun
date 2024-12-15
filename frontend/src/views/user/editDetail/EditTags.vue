<script setup lang="ts">
import { searchTagListService } from '@/api/tags'
import ResponseCode from '@/ts/constant/ResponseCode'
import { ref } from 'vue'
import { useUserStore } from '@/stores'

interface TagVO {
  id: number
  tagName: string
  userId: string
  parentId: number
  isParent: number
  createTime: string
  children: TagVO[]
}

const userStore = useUserStore()

// 选择标签
// 已选标签列表
const selectedList = ref<TagVO[] | any>([])
const addTag = (tag: TagVO) => {
  if (selectedList.value.includes(tag)) {
    selectedList.value = selectedList.value.filter(
      (item: TagVO) => item !== tag
    )
  } else {
    selectedList.value.push(tag)
  }
  userStore.userUpdateInfo.tags = JSON.stringify(
    selectedList.value.map((item: { tagName: any }) => item.tagName)
  )
}

// 用户更新信息中的标签
const selectedArr = ref<string[]>([])
// 将用户已有标签选上
const getUpdateTags = (): void => {
  if (!userStore.userUpdateInfo.tags) {
    return
  }
  selectedArr.value = JSON.parse(userStore.userUpdateInfo.tags as any)
  selectedList.value = tagList.value
    ?.map((item: { children: any }) => item.children)
    .flat()
    .filter((tag: { tagName: string }) =>
      selectedArr.value.includes(tag.tagName)
    )
}

// 获取标签列表
const tagList = ref<TagVO[] | any>()
const getTagList = async (): Promise<void> => {
  const res = await searchTagListService()
  if (res.code !== ResponseCode.SUCCESS) {
    return
  }
  tagList.value = res.data.tagList
  getUpdateTags()
}
getTagList()
// 标签页
const active = ref<number>(0)

// 取消标签
const close = (tag: TagVO, index: number) => {
  selectedList.value.splice(index, 1)
  userStore.userUpdateInfo.tags = JSON.stringify(
    selectedList.value.map((item: { tagName: any }) => item.tagName)
  )
}
// 清空已选标签
const clearSelectedList = () => {
  selectedList.value = [] as any
  userStore.userUpdateInfo.tags = JSON.stringify(
    selectedList.value.map((item: { tagName: any }) => item.tagName)
  )
}
</script>

<template>
  <div class="selected">
    <span style="font-weight: 700">已选标签</span>
    <span @click="clearSelectedList">清空</span>
  </div>
  <!-- 已选标签 -->
  <van-tag
    v-for="(tag, index) in selectedList"
    :key="tag.id"
    closeable
    size="medium"
    type="primary"
    @close="close(tag, index)"
    style="margin-left: 1rem; margin-top: 0.5rem"
  >
    {{ tag.tagName }}
  </van-tag>
  <!-- 标签页 -->
  <van-tabs v-model:active="active">
    <van-tab v-for="item in tagList" :title="item.tagName" :key="item.id">
      <van-tag
        size="large"
        color="#eff2f5"
        text-color="#000"
        v-for="tag in item.children"
        :key="tag.id"
        class="tag"
        :class="selectedList?.includes(tag) ? 'tag-click' : 'tag-no-click'"
        @click="addTag(tag)"
      >
        {{ tag.tagName }}
      </van-tag>
    </van-tab>
  </van-tabs>
</template>

<style scoped lang="less">
.tag {
  margin-top: 1rem;
  margin-left: 1rem;
  padding: 0.25rem 0.5rem;
}

.tag-click {
  color: #1e90ff !important;
}

.selected {
  display: flex;
  justify-content: space-between;
  padding: 0.5rem 1rem;
  background: #fff;
  font-size: 0.8rem;
  color: #666;
}
</style>
