import React from "react";

function DataTable() {
  const data = [
    { name: "김철수", position: "student", status: "신청중" },
    { name: "이영희", position: "student", status: "신청중" },
    // Add more rows as needed
  ];

  return (
    <table className="table table-bordered">
      <thead>
        <tr>
          <th>이름</th>
          <th>직업</th>
          <th>상태</th>
        </tr>
      </thead>
      <tbody>
        {data.map((row, index) => (
          <tr key={index}>
            <td>{row.name}</td>
            <td>{row.position}</td>
            <td>{row.status}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}

export default DataTable;
