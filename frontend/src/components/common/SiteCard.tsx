import type { SiteResponse } from "../../types/site";
import styles from "./SiteCard.module.css"

interface Props {
    site: SiteResponse
}

export default function SiteCard({ site }: Props) {
    return <div className={styles.siteCard} key={site.siteId}>
        <div className={styles.siteCardIcons}>
            <div className={styles.icon}></div>
            <p className={`unselectable center 
            ${styles.tag} 
            ${site.active ? styles.activeTag : styles.inactiveTag}`}>
                {site.active ? "active" : "draft"}
            </p>
        </div>
        <div>
            <p className="titles">{site.siteName}</p>
            <p className={styles.description}>{site.siteDomain}</p>
            <p className={styles.description}>{site.siteCode}</p>
        </div>
        <a href={`/app/sites/${site.siteCode}`}
            className={`linkWithIcon ${styles.siteCardLink}`}>
            <p>View</p>
        </a>
    </div>
}