import axiosInstance from './axiosInstance'
import type { LoginRequest, RegisterRequest, AuthResponse } from '../types/auth'

export function login(data: LoginRequest): Promise<AuthResponse> {
    return axiosInstance.post<AuthResponse>('/api/auth/login', data)
        .then(res => res.data)
}

export function register(data: RegisterRequest): Promise<AuthResponse> {
    return axiosInstance.post<AuthResponse>('/api/auth/register', data)
        .then(res => res.data)
}