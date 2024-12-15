export interface UserLoginParams {
  userAccount: string
  userPassword: string
}

export interface UserLoginRes {
  code: number
  data: {
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
  description: string
  message: string
}

export interface UserRegisterParams {
  userAccount: string
  userPassword: string
  verifyPassword: string
}

export interface UserRegisterRes {
  code: number
  data: boolean
  description: string
  message: string
}

export interface UserLogoutRes {
  code: number
  data: boolean
  description: string
  message: string
}

export interface UserCurrentRes {
  code: number
  data: {
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
  description: string
  message: string
}

export interface UpdateUserByAuthParams {
  avatarUrl?: string
  email?: string
  gender?: number
  id: number
  phone?: string
  profile?: string
  tags?: string
  username?: string
}

export interface UpdateUserByAuthRes {
  code: number
  data: boolean
  description: string
  message: string
}

export interface SearchUsersByTagsRes {
  code: number
  data: {
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
  description: string
  message: string
}

export interface RecommendUsersParams {
  pageNO: number
  pageSize: number
}

export interface RecommendUsersRes {
  code: number
  data: {
    total: number
    userList: {
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
  }
  description: string
  message: string
}

export interface MatchUsersRes {
  code: number
  data: {
    total: number
    userList: {
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
  }
  description: string
  message: string
}

export interface GetAllUserParams {
  pageNO: number
  pageSize: number
}

export interface GetAllUserRes {
  code: number
  data: {
    total: number
    userList: {
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
  }
  description: string
  message: string
}

export interface UploadAvatarRes {
  code: number
  data: string
  description: string
  message: string
}
