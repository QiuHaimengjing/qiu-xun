import request from '@/utils/request'
import type { TagListRes } from './types'

export const searchTagListService = (): Promise<TagListRes> => {
  return request.get('/tag/list')
}
