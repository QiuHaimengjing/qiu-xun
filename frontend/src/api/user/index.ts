import request from '@/utils/request'
import type {
  GetAllUserParams,
  GetAllUserRes,
  MatchUsersRes,
  RecommendUsersParams,
  RecommendUsersRes,
  SearchUsersByTagsRes,
  UpdateUserByAuthParams,
  UpdateUserByAuthRes,
  UploadAvatarRes,
  UserCurrentRes,
  UserLoginParams,
  UserLoginRes,
  UserLogoutRes,
  UserRegisterParams,
  UserRegisterRes
} from './types'

export const userLoginService = (
  data: UserLoginParams
): Promise<UserLoginRes> => {
  return request({
    url: '/user/login',
    method: 'post',
    data
  })
}

export const userRegisterService = (
  data: UserRegisterParams
): Promise<UserRegisterRes> => {
  return request({
    url: '/user/register',
    method: 'post',
    data
  })
}

export const userLogoutService = (): Promise<UserLogoutRes> => {
  return request.post('/user/logout')
}

export const getCurrentUserService = (): Promise<UserCurrentRes> => {
  return request.get('/user/current')
}

export const updateUserService = (
  data: UpdateUserByAuthParams
): Promise<UpdateUserByAuthRes> => {
  return request({
    url: '/user/update/auth',
    method: 'put',
    data
  })
}

export const searchUsersByTagsService = (
  tags: string | undefined
): Promise<SearchUsersByTagsRes> => {
  return request({
    url: '/user/search/tags',
    method: 'get',
    params: {
      tags
    }
  })
}

export const recommendUsersService = (
  data: RecommendUsersParams
): Promise<RecommendUsersRes> => {
  return request({
    url: '/user/recommend',
    method: 'post',
    data
  })
}

export const userMatchService = (num: number): Promise<MatchUsersRes> => {
  return request({
    url: '/user/match',
    method: 'get',
    params: {
      num
    }
  })
}

export const getAllUserService = (
  data: GetAllUserParams
): Promise<GetAllUserRes> => {
  return request({
    url: '/user/all',
    method: 'post',
    data
  })
}

export const uploadAvatarService = (
  file: FormData
): Promise<UploadAvatarRes> => {
  return request({
    url: '/user/avatar',
    method: 'post',
    data: file
  })
}
