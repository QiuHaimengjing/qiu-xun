<script setup lang="ts">
import image from '@/assets/images/kiana.jpg'
const images = [image, image, image, image]
const height = window.screen.height
</script>

<template>
  <div :style="{ height: height + 'px' }">
    <van-swipe
      class="my-swipe"
      :autoplay="3000"
      indicator-color="white"
      lazy-render
    >
      <van-swipe-item v-for="image in images" :key="image">
        <van-image fit="contain" :src="image" class="van-image" />
      </van-swipe-item>
    </van-swipe>
    <div style="margin-left: 1rem">
      <h1>HoYa！</h1>
      <div style="margin-top: 1rem">有bug联系我：</div>
      <ul>
        <li>
          GitHub:
          <span style="color: #3a8cfa">https://github.com/QiuHaimengjing</span>
        </li>
        <li>
          博客:
          <a
            href="https://blog.invictusqiu.top"
            target="_blank"
            style="color: #3a8cfa"
            >https://blog.invictusqiu.top</a
          >
        </li>
      </ul>
    </div>
  </div>
</template>

<style scoped lang="less">
.my-swipe .van-swipe-item {
  width: 100%;
  height: 10rem;
  color: #fff;
  font-size: 1.25rem;
  line-height: 9.375rem;
  text-align: center;
  background-color: #39a9ed;
}
.van-image {
  margin-top: 1rem;
  width: 8rem;
  height: 8rem;
}
</style>
