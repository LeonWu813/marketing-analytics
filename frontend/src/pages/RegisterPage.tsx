import React, { useState } from 'react'
import { useDispatch } from 'react-redux'
import { useNavigate } from 'react-router-dom'
import axios from 'axios'
import { register } from '../api/authApi'
import { setCredentials } from '../store/authSlice'

export default function RegisterPage() {
    const dispatch = useDispatch()
    const navigate = useNavigate()

    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null)

    const handleSubmit = async (e: React.SyntheticEvent) => {
        e.preventDefault()
        setLoading(true)
        setError(null)
        if (password !== confirmPassword) {
            setError('Passwords do not match')
            setLoading(false)
            return
        }
        try {
            const response = await register({ email, password })
            dispatch(setCredentials(response.token))
            navigate('/app/sites')
        } catch (err) {
            axios.isAxiosError(err) && setError(err.response?.data?.message ?? 'Register failed')
        } finally {
            setLoading(false)
        }
    }

    return <div>
        <h1>Register</h1>
        <form onSubmit={handleSubmit}>
            <input type='email'
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder='Email' />
            <input type='password'
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder='Password' />
            <input
                type='password'
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                placeholder='Confirm Password'
            />
            {error && <p style={{ color: 'red' }}>{error}</p>}
            <button type="submit"
                disabled={loading}>
                {loading ? 'Registering...' : 'Register'}
            </button>
        </form>
    </div>;
}