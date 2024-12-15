import request from '@/utils/request'
import type {
  AddTeamParams,
  AddTeamRes,
  DeleteTeamRes,
  JoinTeamParams,
  JoinTeamRes,
  MyTeamsRes,
  QuitTeamRes,
  TeamListParams,
  TeamListRes,
  UpdateTeamParams,
  UpdateTeamRes
} from './types'

export const teamListService = (data: TeamListParams): Promise<TeamListRes> => {
  return request({
    url: '/team/list',
    method: 'post',
    data
  })
}

export const teamAddService = (data: AddTeamParams): Promise<AddTeamRes> => {
  return request({
    url: '/team/add',
    method: 'post',
    data
  })
}

export const myTeamsService = (): Promise<MyTeamsRes> => {
  return request({
    url: '/team/my',
    method: 'get'
  })
}

export const myTeamsServiceById = (
  id: number | undefined
): Promise<MyTeamsRes> => {
  return request({
    url: '/team/my',
    method: 'get',
    params: {
      id
    }
  })
}

export const teamUpdateService = (
  data: UpdateTeamParams
): Promise<UpdateTeamRes> => {
  return request({
    url: '/team/update',
    method: 'put',
    data
  })
}

export const teamDeleteService = (id: number): Promise<DeleteTeamRes> => {
  return request({
    url: '/team/delete',
    method: 'delete',
    params: {
      id
    }
  })
}

export const teamQuitService = (id: number): Promise<QuitTeamRes> => {
  return request({
    url: '/team/quit',
    method: 'delete',
    params: {
      id
    }
  })
}

export const teamJoinService = (data: JoinTeamParams): Promise<JoinTeamRes> => {
  return request({
    url: '/team/join',
    method: 'post',
    data
  })
}
