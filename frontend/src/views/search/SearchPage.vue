<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores'
import { searchTagListService } from '@/api/tags'
import ResponseCode from '@/ts/constant/ResponseCode'

interface TagVO {
  id: number
  tagName: string
  userId: string
  parentId: number
  isParent: number
  createTime: string
  children: TagVO[]
}

const router = useRouter()
const value = ref<string>('')
const userStore = useUserStore()

const onCancel = (): void => {
  // 返回上一级路由
  router.back()
}

// 获取标签列表
const tagList = ref<TagVO[] | any>()
const getTagList = async (): Promise<void> => {
  const res = await searchTagListService()
  if (res.code !== ResponseCode.SUCCESS) {
    return
  }
  tagList.value = res.data.tagList
}
getTagList()
// 标签页
const active = ref<number>(0)

// 选择标签
const selectedList = ref<TagVO[]>([])
const addTag = (tag: TagVO) => {
  if (selectedList.value.includes(tag)) {
    selectedList.value = selectedList.value.filter((item) => item !== tag)
  } else {
    selectedList.value.push(tag)
  }
}

// 已选标签列表
// 取消标签
const close = (tag: TagVO, index: number) => {
  selectedList.value.splice(index, 1)
}
// 清空已选标签
const clearSelectedList = () => {
  selectedList.value = [] as any
}

// 搜索
const onSearch = (val: string): void => {
  if (!val) {
    return
  }
  const selectedTagList = selectedList.value.map((item) => item.tagName)
  val = val + ',' + selectedTagList.join(',')
  // 去除首尾,
  val = val.replace(/^,|,$/g, '')
  userStore.searchUserParams = val
  router.push('/searchResults')
}
</script>

<template>
  <!-- 搜索框 -->
  <form action="/">
    <van-search
      v-model="value"
      show-action
      placeholder="根据标签搜索用户：大学,男"
      background="#edeff6"
      @search="onSearch"
      @cancel="onCancel"
    />
  </form>
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
