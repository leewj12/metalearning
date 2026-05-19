import React from "react";
import styles from "/src/css/OverviewCard.module.css";

function OverviewCard({ title, value }) {
  return (
    <div className={styles.card}>
      <h3 className={styles.title}>{title}</h3>
      <p className={styles.value}>{value}</p>
    </div>
  );
}

export default OverviewCard;
