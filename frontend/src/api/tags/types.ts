export interface TagListRes {
  code: number
  data: {
    tagList: {
      id: number
      tagName: string
      userId: number
      parentId: number
      isParent: number
      createTime: Record<string, unknown>
      children: {
        id: number
        tagName: string
        userId: number
        parentId: number
        isParent: number
        createTime: Record<string, unknown>
      }[]
    }[]
  }
  description: string
  message: string
}
