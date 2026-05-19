import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { Link } from "react-router-dom";
import axios from "axios";
import styles from "/src/css/admin/SessionList.module.css";
import { Helmet } from "react-helmet-async";

const SessionList = () => {
  const { courseId } = useParams(); // URL에서 courseId 가져오기
  const [sessions, setSessions] = useState([]); // 회차 데이터 상태 관리
  const [errorMessage, setErrorMessage] = useState(""); // 오류 메시지 상태 관리
 

  useEffect(() => {
    const fetchSessions = async () => {
      try {
        const response = await axios.get(`/api/admin/KDT/course/${courseId}`, {
          headers: {
            "Content-Type": "application/json",
          },
          withCredentials: true, // 쿠키 포함
        });
        if (response.data && Array.isArray(response.data)) {
          setSessions(response.data); // 세션 데이터 설정
        } else {
          setErrorMessage(response.data.message || "회차 정보가 없습니다.");
        }
      } catch (error) {
        console.error("Error fetching sessions:", error);
        setErrorMessage("데이터를 불러오는 데 실패했습니다.");
      }
    };

    fetchSessions();
}, [courseId]);

// 회차 삭제 핸들러
const handleDelete = async (sessionId) => {
  const confirmDelete = window.confirm("정말로 이 회차를 삭제하시겠습니까?");
  if (!confirmDelete) return;

  try {
    const response = await axios.delete(`/api/admin/KDT/session/delete/${sessionId}`,
      {
        headers: {
          "Content-Type": "application/json",
        },
        withCredentials: true, // 쿠키 포함
      }
    );

    if (response.status === 200) {
      alert(response.data.message || "회차가 삭제되었습니다.");
      // 삭제된 회차를 제외한 새 상태 설정
      setSessions((prevSessions) =>
        prevSessions.filter((session) => session.kdtSessionId !== sessionId)
      );
    } else {
      alert(response.data.message || "회차 삭제에 실패했습니다.");
    }
  } catch (error) {
    alert("삭제 중 오류가 발생했습니다. 다시 시도해주세요.");
  }
};

  // 날짜 포맷팅 함수
  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}-${String(
      date.getDate()
    ).padStart(2, "0")} ${date.getHours()}:${String(date.getMinutes()).padStart(2, "0")}`;
  };

  return (
    <div className={styles.sessionlistcontainer}>
      <Helmet>
        <title>메타러닝 회차 목록 조회</title>
      </Helmet>
      {errorMessage && <div className={styles.errorMessage}>{errorMessage}</div>}
      <h1 className={styles.listheader}>{sessions.length > 0 && sessions[0].kdtSessionTitle} 회차 목록</h1>
      {sessions.length > 0 ? (
        <table className={styles.sessionlisttable}>
          <thead>
            <tr>
              <th scope="col">번호</th>
              <th scope="col" colSpan="2">회차 제목</th> 
              <th scope="col">회차</th>
              <th scope="col">상태</th>    
              <th scope="col">수강생 등록하기</th>
              <th scope="col">매니저 등록하기</th>
              <th scope="col">강사 등록하기</th>
              <th scope="col">상세보기</th>
              <th scope="col">수정</th>
              <th scope="col">삭제</th>
            </tr>
          </thead>
          <tbody>
            {sessions.map((session,index) => (
              <tr key={session.kdtSessionId}>
                <td>{index + 1}</td>

                <td colSpan="2">
                  <Link to={`/admin/KDT/session/${session.kdtSessionId}`}>
                    {session.kdtSessionTitle}
                  </Link>
                </td>

                <td>{session.kdtSessionNum}회차</td>

                <td>{session.kdtSessionStatus}</td>

                <td>
                  <a href={`/admin/KDT/${session.kdtSessionId}/part`}>수강생 등록하기</a>
                </td>
                <td>
                  <a href={`/admin/KDT/${session.kdtSessionId}/staff/manager`}>매니저 등록하기</a>
                </td>
                <td>
                  <a href={`/admin/KDT/${session.kdtSessionId}/staff/instr`}>강사 등록하기</a>
                </td>
                <td>
                  <Link to={`/admin/KDT/session/${session.kdtSessionId}`}>
                    상세 보기
                  </Link>
                </td>
                <td>
                  <button className={styles.editbtn}>
                    <a href={`/admin/KDT/session/update/${session.kdtSessionId}`}>수정</a></button>
                </td>
                <td>
                <button
                    onClick={() => handleDelete(session.kdtSessionId)}
                    className={styles.deletebtn}
                  >
                    삭제
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : (
        <p className="text-center">회차 정보가 없습니다.</p>
      )}
    </div>
  );
};

export default SessionList;
