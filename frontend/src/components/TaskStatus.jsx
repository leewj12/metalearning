import React from "react";
import styles from "/src/css/TaskStatus.module.css";

function TaskStatus() {
  const tasks = [
    { name: "과제 1", progress: 80 },
    { name: "과제 2", progress: 60 },
    { name: "과제 3", progress: 100 },
  ];

  return (
    <div className={styles.container}>
      <h3>과제</h3>
      {tasks.map((task, index) => (
        <div key={index} className={styles.task}>
          <span className={styles.taskName}>{task.name}</span>
          <div className={styles.progressBar}>
            <div
              className={styles.progress}
              style={{ width: `${task.progress}%` }}
            ></div>
          </div>
          <span className={styles.progressValue}>{task.progress}%</span>
        </div>
      ))}
    </div>
  );
}

export default TaskStatus;