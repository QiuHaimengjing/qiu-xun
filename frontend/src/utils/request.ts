import axios, {
  type AxiosResponse,
  type InternalAxiosRequestConfig
} from 'axios'
import { showToast } from 'vant'
import { useUserStore } from '@/stores'
import router from '@/router'
import ResponseCode from '@/ts/constant/ResponseCode'

interface ApiResponse<T = any> {
  code: number
  data: T
  description: string
  message: string
}

const instance = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 添加请求拦截器
instance.interceptors.request.use(
  function (config: InternalAxiosRequestConfig) {
    // 在发送请求之前做些什么
    return config
  },
  function (error) {
    // 对请求错误做些什么
    return Promise.reject(error)
  }
)

// 添加响应拦截器
instance.interceptors.response.use(
  function (response: AxiosResponse<ApiResponse<any>>): Promise<any> {
    // 2xx 范围内的状态码都会触发该函数。
    // 对响应数据做点什么
    if (response?.data?.code !== ResponseCode.SUCCESS) {
      // 如果用户未登录清空本地存储
      if (response?.data?.code === ResponseCode.NOT_LOGIN) {
        const userStore = useUserStore()
        userStore.removeUserInfo()
        // 1s 后跳转到用户页
        setTimeout(() => {
          // 路由跳转
          router.replace('/user')
        }, 1000)
      }
      if (response?.data?.description !== '') {
        showToast(response.data.description)
      } else if (response?.data?.message !== '') {
        showToast(response.data.message)
      } else {
        showToast('哎呀, 出错了')
      }
      return Promise.reject(response.data)
    }
    return Promise.resolve(response.data)
  },
  function (error) {
    // 超出 2xx 范围的状态码都会触发该函数。
    // 对响应错误做点什么
    showToast('哎呀, 出错了')
    return Promise.reject(error)
  }
)

export default instance
