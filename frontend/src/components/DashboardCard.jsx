import React, { useEffect, useState } from "react";
import { Line } from "react-chartjs-2";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import axios from "axios";
import styles from "../css/admin/AreaChart.module.css"; // CSS 모듈 불러오기

function AreaChart() {
  const [data, setData] = useState(null);
  const [filteredData, setFilteredData] = useState(null);
  const [startDate, setStartDate] = useState(new Date(new Date().getFullYear(), 0, 1)); // 1월 1일
  const [endDate, setEndDate] = useState(new Date()); // 오늘
  const [errorMessage, setErrorMessage] = useState(""); // 에러 메시지 상태

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await axios.get("/api/admin/user/count", {
          headers: {
            "Content-Type": "application/json",
          },
          withCredentials: true,
        });

        const apiData = response.data; // 서버로부터 가져온 데이터
        console.log("API 데이터:", apiData);

        // 데이터 가공 (월별 데이터 생성)
        const months = ["1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"];
        const labels = apiData.map((item) => `${item.year}년 ${months[item.month - 1]}`);
        const userCounts = apiData.map((item) => item.userCount);

        // 차트 데이터 상태 설정
        setData({
          labels,
          datasets: [
            {
              label: "회원수",
              data: userCounts,
              fill: true,
              backgroundColor: "rgba(75,192,192,0.2)",
              borderColor: "rgb(70, 8, 170)",
            },
          ],
        });

        // 필터링 데이터 초기값 설정
        setFilteredData({
          labels,
          datasets: [
            {
              label: "회원수",
              data: userCounts,
              fill: true,
              backgroundColor: "rgba(75,192,192,0.2)",
              borderColor: "rgb(70, 8, 170)",
            },
          ],
        });
      } catch (error) {
        console.error("데이터 로드 실패:", error);
        setErrorMessage("데이터를 불러오는 중 문제가 발생했습니다.");
      }
    };

    fetchData();
  }, []);

  // 날짜 필터링
  const handleDateFilter = () => {
    if (!data) return;

    // 시작 월과 종료 월 계산
    const startMonth = startDate.getMonth() + 1; // 1월은 0부터 시작하므로 +1
    const endMonth = endDate.getMonth() + 1;

    // 데이터 필터링
    const filteredLabels = data.labels.filter((label, index) => {
      const month = parseInt(data.labels[index].split(" ")[1].replace("월", ""), 10);
      return month >= startMonth && month <= endMonth;
    });
    const filteredValues = data.datasets[0].data.filter((_, index) => {
      const month = parseInt(data.labels[index].split(" ")[1].replace("월", ""), 10);
      return month >= startMonth && month <= endMonth;
    });

    setFilteredData({
      labels: filteredLabels,
      datasets: [
        {
          ...data.datasets[0],
          data: filteredValues,
        },
      ],
    });
  };

  const options = {
    responsive: true,
    plugins: {
      legend: {
        position: "top",
      },
      title: {
        display: true,
        text: "월별 회원수",
      },
    },
  };

  if (!data || !filteredData) {
    return <p className={styles.errorMessage}>{errorMessage || "로딩 중..."}</p>;
  }

  return (
    <div className={styles.chartContainer}>
      <h2 className={styles.title}>📈 월별 회원수</h2>
      <div className={styles.filterContainer}>
        <div className={styles.datePicker}>
          <label>시작 날짜:</label>
          <DatePicker
            selected={startDate}
            onChange={(date) => setStartDate(date)}
            dateFormat="yyyy-MM-dd"
          />
        </div>
        <div className={styles.datePicker}>
          <label>종료 날짜:</label>
          <DatePicker
            selected={endDate}
            onChange={(date) => setEndDate(date)}
            dateFormat="yyyy-MM-dd"
          />
        </div>
        <button onClick={handleDateFilter} className={styles.filterButton}>
          필터 적용
        </button>
      </div>
      <Line data={filteredData} options={options} />
    </div>
  );
}

export default AreaChart;
