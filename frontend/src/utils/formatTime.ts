import dayjs from 'dayjs'

// 将系统日期格式化为 YYYY-MM-DD
export const formatSystemDate = (time: Date): string => {
  return dayjs(time).format('YYYY-MM-DD')
}

// 将系统时间格式化为 HH:mm
export const formatSystemTime = (time: Date): string => {
  return dayjs(time).format('HH:mm')
}

// 将系统时间格式化为 YYYY-MM-DDTHH:mm:ss
export const formatSystemDateTime = (time: string): string => {
  return dayjs(time).format('YYYY-MM-DDTHH:mm:ss')
}

// 将YYYY-MM-DDTHH:mm:ss格式化为 YYYY-MM-DD
export const formatDateTimeToDate = (time: string | any): string => {
  return dayjs(time).format('YYYY-MM-DD')
}

// 将YYYY-MM-DDTHH:mm:ss格式化为 HH:mm
export const formatDateTimeToTime = (time: string | any): string => {
  return dayjs(time).format('HH:mm')
}
