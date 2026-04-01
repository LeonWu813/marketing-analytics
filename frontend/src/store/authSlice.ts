import { createSlice } from '@reduxjs/toolkit'
import type { PayloadAction } from '@reduxjs/toolkit'

interface AuthState {
    token: string | null
    isAuthenticated: boolean
}

const initialState: AuthState = {
    token: localStorage.getItem('token'),
    isAuthenticated: localStorage.getItem('token') !== null
}

const authSlice = createSlice({
    name: 'auth',

    initialState,

    reducers: {
        setCredentials: (state, action: PayloadAction<string>) => {
            state.token = action.payload
            state.isAuthenticated = true
            localStorage.setItem('token', action.payload)
        },

        clearCredentials: (state) => {
            state.token = null
            state.isAuthenticated = false
            localStorage.removeItem('token')
        }
    }
})

export const { setCredentials, clearCredentials } = authSlice.actions

export default authSlice.reducer