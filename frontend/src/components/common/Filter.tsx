import { useState } from "react"
import styles from "./Filter.module.css"

interface Props<T extends string> {
    label: string
    options: { label: string; value: T | undefined }[]
    onSelect: (value: T | undefined) => void
    large: boolean
}


export default function Filter<T extends string>({ label, options, onSelect, large }: Props<T>) {
    const [isOpen, setIsOpen] = useState<boolean>(false)
    const [selected, setSelected] = useState<string | undefined>(undefined)

    function handelChannelFilter(value: T | undefined) {
        setSelected(value)
        setIsOpen(!isOpen)
        onSelect(value)
    }

    return <div className={styles.dropdownContainer}>
        <button
            className={`${styles.filter} ${large && styles.filterBig}`}
            onClick={() => setIsOpen(!isOpen)}>
            {label}: {selected ? selected : "All"}
        </button>
        <div className={`${styles.dropdown} ${large && styles.dropdownBig}`}>
            {isOpen && options.map(filter => (
                <button
                    key={filter.label}
                    onClick={() => handelChannelFilter(filter.value)}
                >
                    {filter.label}
                </button>
            ))}
        </div>
    </div>
}