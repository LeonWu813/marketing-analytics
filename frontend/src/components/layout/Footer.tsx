import { APP_NAME } from "../../src/constants"
import style from "./Footer.module.css"

export default function Footer() {
    return <footer className={style.footer}>
        <div>
            <div>
                <p className={style.footerName}>{APP_NAME}</p>
                <p className={style.footerCredit}>© {new Date().getFullYear()} {APP_NAME}. Precise. Airy. Layered.</p>
            </div>
            <a href="mailto:leonwuya@gmail.com" className={style.contact}>Contact Owner</a>
        </div>
    </footer>
}