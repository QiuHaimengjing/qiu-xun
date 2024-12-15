export interface TeamListParams {
  createTime?: Record<string, unknown>
  description?: string
  id?: number
  keyWords?: string
  leader?: number
  maxNum?: number
  name?: string
  pageNO: number
  pageSize: number
  status?: number
}

export interface TeamListRes {
  code: number
  data: {
    createTime: Record<string, unknown>
    createUser: {
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
    currentNum: number
    description: string
    expireTime: Record<string, unknown>
    id: number
    joined: boolean
    leader: number
    maxNum: number
    members: {
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
    }[]
    name: string
    status: number
  }[]
  description: string
  message: string
}

export interface AddTeamParams {
  description: string
  expireTime: Record<string, unknown> | string
  maxNum: number
  name: string
  password?: string
  status: number
}

export interface AddTeamRes {
  code: number
  data: number
  description: string
  message: string
}

export interface MyTeamsRes {
  code: number
  data: {
    createTime: Record<string, unknown>
    createUser: {
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
    currentNum: number
    description: string
    expireTime: Record<string, unknown>
    id: number
    joined: boolean
    leader: number
    maxNum: number
    members: {
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
    }[]
    name: string
    status: number
  }[]
  description: string
  message: string
}

export interface UpdateTeamParams {
  description?: string
  expireTime?: Record<string, unknown> | string
  id: number
  maxNum?: number
  name?: string
  password?: string
  status?: number
}

export interface UpdateTeamRes {
  code: number
  data: boolean
  description: string
  message: string
}

export interface DeleteTeamRes {
  code: number
  data: boolean
  description: string
  message: string
}

export interface QuitTeamRes {
  code: number
  data: boolean
  description: string
  message: string
}

export interface JoinTeamParams {
  id: number
  password?: string
}

export interface JoinTeamRes {
  code: number
  data: boolean
  description: string
  message: string
}
