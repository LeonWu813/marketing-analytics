import styles from './NavBar.module.css'
import { NavLink, useParams } from 'react-router-dom';
import { useSelector } from 'react-redux'
import type { RootState } from '../../store/index'
import { useDispatch } from 'react-redux'
import { clearCredentials } from '../../store/authSlice'
import { useNavigate } from 'react-router-dom'
import { APP_NAME } from '../../src/constants';



export default function NavBar() {
    const dispatch = useDispatch()
    const navigate = useNavigate()
    const { siteCode } = useParams<{ siteCode: string }>();
    const email = useSelector((state: RootState) => state.auth.email)

    const navLinkClass = ({ isActive }: { isActive: boolean }) =>
        isActive ? `${styles.navlink} ${styles.navlinkActive}` : styles.navlink

    return <nav className={styles.nav}>
        <div>
            <div className={styles.navleft}>
                <a className="logo" href='/app/sites'>{APP_NAME}</a>
                <div className={styles.navlinks}>
                    {!siteCode && (<>
                        <NavLink className={navLinkClass} to="/app/sites">All Sites</NavLink>
                    </>)
                    }
                    {siteCode && (<>
                        <NavLink className={navLinkClass} to={`/app/sites/${siteCode}`}>Site Hub</NavLink>
                        <NavLink className={navLinkClass} to={`/app/${siteCode}/campaigns`}>Campaigns</NavLink>
                        <NavLink className={navLinkClass} to={`/app/${siteCode}/seo/reports`}>Reports</NavLink>
                    </>)}
                </div>
            </div>
            <div className={styles.navright}>
                <p className={`unselectable ${styles.currentUser}`}>{email}</p>
                <button className={styles.logout}
                    onClick={() => {
                        dispatch(clearCredentials())
                        navigate('/login')
                    }}
                >Logout</button>
            </div>
        </div>
    </nav >
}