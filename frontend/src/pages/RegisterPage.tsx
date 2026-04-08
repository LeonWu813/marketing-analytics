import React, { useState } from 'react'
import { useDispatch } from 'react-redux'
import { useNavigate } from 'react-router-dom'
import axios from 'axios'
import styles from './LoginRegisterPage.module.css'
import { register } from '../api/authApi'
import { setCredentials } from '../store/authSlice'
import { APP_NAME } from '../src/constants'
import Footer from '../components/layout/Footer'

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

    return <>
        <section className={styles.loginPage}>
            <div className={styles.loginPannel}>
                <div className={`center ${styles.titleContainer}`}>
                    <h1 className='unselectable'>{APP_NAME}</h1>
                    <p>Precision analytics for the modern strategist.</p>
                </div>
                <div className={styles.formContainer}>
                    <form onSubmit={handleSubmit} className={styles.form}>
                        <div className="form-field-container">
                            <label htmlFor="email" className='label'>Email Address</label>
                            <input
                                id='email'
                                autoComplete='on'
                                type='email'
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                placeholder='name@company.com'
                                className="input" />
                        </div>
                        <div className="form-field-container">
                            <label htmlFor="password" className='label'>Password</label>
                            <input
                                id='password'
                                type='password'
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                placeholder=' • • • • • • • • '
                                className="input" />
                        </div>
                        <div className="form-field-container">
                            <label htmlFor="password-confirm" className='label'>Confirm Password</label>
                            <input
                                id='password-confirm'
                                type='password'
                                value={confirmPassword}
                                onChange={(e) => setConfirmPassword(e.target.value)}
                                placeholder=' • • • • • • • • '
                                className="input"
                            />
                        </div>
                        {error && <div className={styles.warningContainer}><p className="warning">{error}</p></div>}
                        <button
                            className='button-primary' type="submit"
                            disabled={loading}>
                            {loading ? 'Registering...' : 'Register'}
                        </button>
                    </form>
                    <div className={styles.newUserContainer}>
                        <div className={styles.divider}></div>
                    </div>
                    <p className='center'>Already have an account? <a href="/login" className="link">Login</a></p>
                </div>
            </div>
        </section>
        < Footer />
    </>;
}