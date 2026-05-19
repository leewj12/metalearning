import React, { useEffect, useState } from "react";
import { Line } from "react-chartjs-2";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import axios from "axios";

function AreaChart() {
  const [data, setData] = useState(null);
  const [filteredData, setFilteredData] = useState(null);
  const [startDate, setStartDate] = useState(new Date(new Date().getFullYear(), 0, 1)); // 기본 시작 날짜: 1월 1일
  const [endDate, setEndDate] = useState(new Date()); // 기본 종료 날짜: 오늘

  // API 호출로 데이터를 가져오기
  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await axios.get("http://localhost:8091/api/admin/user/count");
        console.log("API 데이터:", response.data);
        const apiData = response.data;

        // 데이터 정리: 월 이름 변환
        const months = ["1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"];
        const labels = apiData.map((item) => months[item.month - 1]);
        const userCounts = apiData.map((item) => item.userCount);

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

        // 필터링 데이터를 기본값으로 설정
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
      }
    };

    fetchData();
  }, []);

  // 날짜 필터링 함수
  const handleDateFilter = () => {
    if (!data) return;

    // 시작 월과 종료 월을 계산
    const startMonth = startDate.getMonth() + 1; // 1월이 0으로 시작하므로 +1
    const endMonth = endDate.getMonth() + 1;

    // 필터링된 데이터 계산
    const filteredLabels = data.labels.filter((_, index) => index + 1 >= startMonth && index + 1 <= endMonth);
    const filteredValues = data.datasets[0].data.filter((_, index) => index + 1 >= startMonth && index + 1 <= endMonth);

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
    return <p>로딩 중...</p>;
  }

  return (
    <div>
      <h2>📈 월별 회원수</h2>
      <div style={{ display: "flex", gap: "10px", marginBottom: "20px" }}>
        <div>
          <label>시작 날짜:</label>
          <DatePicker
            selected={startDate}
            onChange={(date) => setStartDate(date)}
            dateFormat="yyyy-MM-dd"
          />
        </div>
        <div>
          <label>종료 날짜:</label>
          <DatePicker
            selected={endDate}
            onChange={(date) => setEndDate(date)}
            dateFormat="yyyy-MM-dd"
          />
        </div>
        <button onClick={handleDateFilter} style={{ alignSelf: "center", padding: "10px 15px", background: "#4f46e5", color: "#fff", border: "none", borderRadius: "5px", cursor: "pointer" }}>
          필터 적용
        </button>
      </div>
      <Line data={filteredData} options={options} />
    </div>
  );
}

export default AreaChart;
