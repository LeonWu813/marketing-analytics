import { createSlice } from '@reduxjs/toolkit'
import type { PayloadAction } from '@reduxjs/toolkit'

interface AuthState {
    token: string | null
    isAuthenticated: boolean
    email: string | null
}

const initialState: AuthState = {
    token: localStorage.getItem('token'),
    isAuthenticated: localStorage.getItem('token') !== null,
    email: decodeEmail(localStorage.getItem('token'))
}

function decodeEmail(token: string | null): string | null {
    if (!token) return null
    try {
        const payload = JSON.parse(atob(token.split('.')[1]))
        return payload.sub ?? null
    } catch {
        return null
    }
}

const authSlice = createSlice({
    name: 'auth',

    initialState,

    reducers: {
        setCredentials: (state, action: PayloadAction<string>) => {
            state.token = action.payload
            state.isAuthenticated = true
            state.email = decodeEmail(action.payload)
            localStorage.setItem('token', action.payload)
        },

        clearCredentials: (state) => {
            state.token = null
            state.isAuthenticated = false
            state.email = null
            localStorage.removeItem('token')
        }
    }
})

export const { setCredentials, clearCredentials } = authSlice.actions

export default authSlice.reducer