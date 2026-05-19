import React from "react";
import OverviewCard from "/src/components/OverviewCard";
import Graph from "/src/components/Graph";
import TaskStatus from "/src/components/TaskStatus";
import styles from "/src/css/student/StudentDashboard.module.css" 

function StudentDashboard() {
  return (
    <div className={styles.studentmain}>
      <h1>학생 대시보드</h1>
      <div className={styles.dashboardOverview}>
        <OverviewCard title="출석률" value="95%" />
        <OverviewCard title="과제 완료율" value="80%" />
        <OverviewCard title="평균 학점" value="A" />
        <OverviewCard title="평균 학점" value="s+ " />
        <OverviewCard title="평균 학점" value="총장" />
      </div>
      <div className={styles.studentgraph}>
        <div className={styles.graphContainer}>
          <Graph title="출석 추이" />
        </div>
        <div className={styles.taskStatusContainer}>
          <div className={styles.taskStatus1}>
          <TaskStatus />
          </div>
          <div className={styles.taskStatus2}>
          <TaskStatus />
          </div>
        </div>
      </div>
    </div>
  );
}

export default StudentDashboard;
