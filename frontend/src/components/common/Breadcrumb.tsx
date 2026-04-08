import styles from "../common/Breadcrumb.module.css"

interface Props {
    first: string
    second: string
    third?: string
}

export default function Breadcrumb({ first, second, third }: Props) {
    return <div className={`${styles.breadcrumbContainer} unselectable`}>
        <p>{first}</p>
        <p>{second}</p>
        {third && <p>{third}</p>}
    </div>
}