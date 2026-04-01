import axios from 'axios'

const axiosInstance = axios.create(
    { baseURL: 'http://localhost:5173' }
)

axiosInstance.interceptors.request.use(
    (config) => {
        const token: string | null = localStorage.getItem('token')
        token && config.headers.setAuthorization(`Bearer ${token}`)
        return config
    },
    (error) => Promise.reject(error)
)

export default axiosInstance