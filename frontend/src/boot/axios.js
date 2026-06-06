import { boot } from 'quasar/wrappers'
import axios from 'axios'

const api = axios.create({ baseURL: 'http://localhost:8080/api' })

export default boot(({ app }) => {

  api.interceptors.request.use(
    (config) => {
      const token = localStorage.getItem('jwt_token')
      if (token) {
        config.headers.Authorization = `Bearer ${token}`
      }
      return config
    },
    (error) => {
      return Promise.reject(error)
    }
  )

  api.interceptors.response.use(
    (response) => response,
    (error) => {
      if (error.response?.status === 401 && !error.config.url.includes('/auth/login')) {
         localStorage.removeItem('jwt_token');
         window.location.href = '#/login';
      }
      return Promise.reject(error)
    }
  )

  app.config.globalProperties.$api = api
})

export function logout() {
  localStorage.removeItem('jwt_token')
  localStorage.removeItem('user_role')
  window.location.href = '#/login'
  window.location.reload()
}

export { api }
