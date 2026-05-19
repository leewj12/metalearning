import React from "react";
import { Line } from "react-chartjs-2";

function Graph({ title }) {
  const data = {
    labels: ["1월", "2월", "3월", "4월", "5월"],
    datasets: [
      {
        label: "출석률 (%)",
        data: [85, 90, 92, 88, 95],
        backgroundColor: "rgba(75,192,192,0.4)",
        borderColor: "rgba(75,192,192,1)",
        borderWidth: 2,
        tension: 0.3,
      },
    ],
  };

  const options = {
    responsive: true,
    plugins: {
      legend: {
        display: true,
        position: "top",
      },
    },
  };

  return (
    <div style={{ padding: "20px", background: "#fff", borderRadius: "8px", boxShadow: "0 4px 8px rgba(0,0,0,0.1)" }}>
      <h3 style={{ marginBottom: "20px" }}>{title}</h3>
      <Line data={data} options={options} />
    </div>
  );
}

export default Graph;
