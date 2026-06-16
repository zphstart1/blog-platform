import axios from 'axios'
import type { UploadResult } from '@/types'

export function uploadImage(file: File, token: string) {
  const formData = new FormData()
  formData.append('file', file)

  return axios.post<{ code: number; message: string; data: UploadResult }>(
    '/api/upload/image',
    formData,
    {
      headers: {
        'Content-Type': 'multipart/form-data',
        Authorization: `Bearer ${token}`
      },
      timeout: 30000
    }
  ).then(res => {
    if (res.data.code !== 0) {
      return Promise.reject(new Error(res.data.message || '上传失败'))
    }
    return res.data.data
  })
}
