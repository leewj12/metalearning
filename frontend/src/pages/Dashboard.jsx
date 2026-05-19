import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  BarElement,
  Title,
  Tooltip,
  Legend,
} from "chart.js";
import { Chart, Filler } from "chart.js";
import React, { useEffect, useState } from "react";
import "bootstrap/dist/css/bootstrap.min.css";
import styles from "../css/Dashboard.module.css"; // 스타일 모듈
import axios from "axios";
import { Line, Bar } from "react-chartjs-2";
import { Helmet } from "react-helmet-async";

// Chart.js 플러그인 등록
ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  BarElement,
  Title,
  Tooltip,
  Legend
);
Chart.register(Filler);

function Dashboard() {
  // 데이터 상태 관리
  const [userCount, setUserCount] = useState(null); // 회원 수 카드 데이터
  const [userGrowth, setUserGrowth] = useState(null); // 회원 증감율 데이터
  const [upload, setUpload] = useState(null); // 강의 증감율 데이터
  const [errorMessage, setErrorMessage] = useState(""); // 에러 메시지 상태

  const [announcements, setAnnouncements] = useState([
    { date: "2025-02-26", text: "시스템 점검 예정: 2025년 3월 4일 오후 2시 ~ 4시" },
    { date: "2025-02-26", text: "새로운 강의 'React 고급 과정'이 추가되었습니다." },
    { date: "2025-02-24", text: "삼일절 이벤트: 수강료 20% 할인 (~2025년 1월 20일)" },
    { date: "2025-02-21", text: "관리자 대시보드 기능이 업데이트되었습니다." },
  ]);

  // 데이터 가져오기
  useEffect(() => {
    const fetchData = async () => {
      try {
        // 회원 수 요약 데이터
        const userCountResponse = await axios.get("/api/admin/user/role/list", {
          headers: {
            "Content-Type": "application/json",
          },
          withCredentials: true,
        });

        // 회원 증감율 데이터
        const userGrowthResponse = await axios.get("/api/admin/user/count", {
          headers: {
            "Content-Type": "application/json",
          },
          withCredentials: true,
        });

        // 강의 증감율 데이터
        const uploadResponse = await axios.get("/api/admin/course/monthlycount", {
          headers: {
            "Content-Type": "application/json",
          },
          withCredentials: true,
        });

        // 데이터 상태 업데이트
        setUserCount(userCountResponse.data);
        setUserGrowth(userGrowthResponse.data);
        setUpload(uploadResponse.data);
      } catch (error) {
        console.error("데이터 로드 실패:", error);
        setErrorMessage("데이터를 불러오는 중 문제가 발생했습니다.");
      }
    };

    fetchData();
  }, []);

  if (!userCount || !userGrowth || !upload) {
    return <p className={styles.errorMessage}>{errorMessage || "로딩 중..."}</p>;
  }

  // 회원 증감율 차트 데이터 생성
  const userGrowthChartData = {
    labels: userGrowth.map((growth) => `${growth.month}월`),
    datasets: [
      {
        label: "회원 증감율",
        data: userGrowth.map((growth) => growth.userCount),
        borderColor: "rgba(75, 192, 192, 1)",
        backgroundColor: "rgba(75, 192, 192, 0.2)",
        fill: true,
        tension: 0.4,
      },
    ],
  };

  // 강의 증감율 차트 데이터 생성
  const uploadChartData = {
    labels: upload.data.map((stat) => `${stat.month}월`),
    datasets: [
      {
        label: "강의 등록 수",
        data: upload.data.map((stat) => stat.courseCount),
        backgroundColor: "rgba(54, 162, 235, 0.5)",
        borderColor: "rgba(54, 162, 235, 1)",
        borderWidth: 1,
      },
    ],
  };

  return (
    
    <div className={styles.dashboardContainer}>
      <Helmet>
        <title>메타러닝 관리자 대시보드</title>
      </Helmet>
      <div className="row">
        {/* 왼쪽 위: 회원 수 카드 */}
        <div className="col-md-6">
          <div className={styles.card}>
            <h3 className={styles.cardTitle}>회원 수 요약</h3>
            <p><strong>관리자:</strong> {userCount.adminTotal}명</p>
            <p><strong>매니저:</strong> {userCount.managerTotal}명</p>
            <p><strong>강사:</strong> {userCount.instructorTotal}명</p>
            <p><strong>학생:</strong> {userCount.studentTotal}명</p>
          </div>
        </div>

        {/* 오른쪽 위: 빈 공간 */}
        <div className="col-md-6">
          <div className={styles.card}>
            <h3 className={styles.cardTitle}>공지사항</h3>
            <ul className={styles.announcementList}>
              {announcements.map((item, index) => (
                <li key={index}>
                  <strong>[{item.date}]</strong> {item.text}
                </li>
              ))}
            </ul>
          </div>
        </div>
      </div>

      <div className="row mt-4">
        {/* 왼쪽 아래: 회원 증감율 차트 */}
        <div className="col-md-6">
          <div className={styles.chartContainer}>
            <h3 className={styles.chartTitle}>회원 증감율</h3>
            <Line
              data={userGrowthChartData}
              options={{
                responsive: true,
                plugins: {
                  legend: { position: "top" },
                  title: { display: true, text: "월별 회원 증감율" },
                  tooltip: {
                    callbacks: {
                      label: function (tooltipItem) {
                        return `${tooltipItem.raw}명`; // 툴팁 값 뒤에 '명' 추가
                      },
                    },
                  },
                },
                scales: {
                  y: {
                    ticks: {
                      callback: function (value) {
                        return Number.isInteger(value) ? `${value}명` : ""; // 정수만 표시 + '명' 추가
                      },
                    },
                  },
                },
              }}
            />
          </div>
        </div>

        {/* 오른쪽 아래: 강의 증감율 차트 */}
        <div className="col-md-6">
          <div className={styles.chartContainer}>
            <h3 className={styles.chartTitle}>강의 등록 수</h3>
            <Bar
              data={uploadChartData}
              options={{
                responsive: true,
                plugins: {
                  legend: { position: "top" },
                  title: { display: true, text: "월별 강의 등록 수" },
                  tooltip: {
                    callbacks: {
                      label: function (tooltipItem) {
                        return `${tooltipItem.raw}개`; // 툴팁 값 뒤에 '명' 추가
                      },
                    },
                  },
                },
                scales: {
                  y: {
                    ticks: {
                      callback: function (value) {
                        return Number.isInteger(value) ? `${value}개` : ""; // 정수만 표시 + '명' 추가
                      },
                    },
                  },
                },
              }}
            />
          </div>
        </div>
      </div>
    </div>
  );
}

export default Dashboard;
