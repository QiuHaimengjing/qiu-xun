export interface User {
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

export interface Team {
  id: number
  name: string
  description: string
  maxNum: number
  currentNum: number
  expireTime: Record<string, unknown>
  leader: number
  status: number
  createTime: string
  joined: boolean
  createUser: User
  members: User[]
}
