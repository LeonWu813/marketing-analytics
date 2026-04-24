import React, { useEffect, useState } from 'react'
import { useDispatch } from 'react-redux'
import { useNavigate } from 'react-router-dom'
import axios from 'axios'
import { login } from '../api/authApi'
import { clearCredentials, setCredentials } from '../store/authSlice'
import styles from './LoginRegisterPage.module.css'
import Footer from '../components/layout/Footer'
import { APP_NAME } from '../src/constants'



export default function LoginPage() {
    const dispatch = useDispatch()
    const navigate = useNavigate()

    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)

    useEffect(() => {
        dispatch(clearCredentials())
    }, [dispatch])

    const handleSubmit = async (e: React.SyntheticEvent) => {
        e.preventDefault()
        setLoading(true)
        setError(null)
        try {
            const response = await login({ email, password })
            dispatch(setCredentials(response.token))
            navigate('/app/sites')
        } catch (err) {
            axios.isAxiosError(err) &&
                setError(err.response?.data?.message ?? 'Login failed')
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
                            <div className={`${styles.inputContainer} ${styles.inputEmail}`}>
                                <input type='email'
                                    id='email'
                                    autoComplete='on'
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                    placeholder='name@company.com'
                                    className={`input ${styles.loginInput}`} />
                            </div>
                        </div>
                        <div className="form-field-container">
                            <label htmlFor="password" className='label'>Password</label>
                            <div className={`${styles.inputContainer} ${styles.inputPassword}`}>
                                <input type='password'
                                    id='password'
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    placeholder=' • • • • • • • • '
                                    className={`input ${styles.loginInput}`} />
                            </div>
                        </div>
                        {error && <div className={styles.warningContainer}><p className="warning">{error}</p></div>}
                        <button type="submit"
                            className='button-primary'
                            disabled={loading}>
                            {loading ? 'Logging in...' : 'Login to Dashboard'}
                        </button>
                    </form>
                    <div className={styles.newUserContainer}>
                        <p className="label center"><span className={styles.newUserTitle}>New to {APP_NAME}?</span></p>
                        <div className={styles.divider}></div>
                    </div>
                    <button
                        onClick={() => {
                            navigate("/register")
                        }}
                        className='button-secondary'
                    >Register Account</button>
                </div>
            </div >
        </section >
        < Footer />
    </>;
}