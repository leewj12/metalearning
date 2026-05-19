import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Link } from "react-router-dom";
import axios from "axios";
import styles from "/src/css/instr/InstrSessionList.module.css";
import { Helmet } from "react-helmet-async";

const SessionList = () => {
  const { courseId } = useParams(); // URL에서 courseId 가져오기
  const navigate = useNavigate(); // 히스토리 백을 위한 useNavigate
  const [sessions, setSessions] = useState([]); // 회차 데이터 상태 관리
  const [errorMessage, setErrorMessage] = useState(""); // 오류 메시지 상태 관리
 

  useEffect(() => {
    const fetchSessions = async () => {
      try {
        const response = await axios.get("/api/instr/KDT/list", {
          headers: {
            "Content-Type": "application/json",
          },
          withCredentials: true, // 쿠키 포함
        });
        const data = response.data; // 응답 데이터 추출
        console.log("서버 응답 데이터:", data); // 데이터 로그 출력
        console.log("응답 데이터:", response.data);
        
        if (response.status === 403 && data.message === "회차에 등록된 매니저가 아닙니다.") {
          // 403 에러 발생 시 알림 표시 후 리다이렉트
          alert(data.message); // 메시지 표시
          navigate(-1); // 리다이렉트
          return;
        }

        // 세션 데이터가 정상일 경우 상태 업데이트
        setSessions(data);
      } catch (error) {
        console.error("데이터 요청 중 오류 발생:", error);

        // 오류 메시지를 설정
        if (error.response) {
          // 서버가 응답을 반환했을 경우
          setErrorMessage(error.response.data.message || "데이터 요청 중 문제가 발생했습니다.");
        } else {
          // 네트워크 또는 기타 문제
          setErrorMessage("서버와의 연결에 문제가 발생했습니다.");
        }
      }
    };

    fetchSessions(); // 데이터 요청
  }, [courseId, navigate]);


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
        <title>메타러닝 국비과정 목록조회</title>
      </Helmet>
      <h1 className={styles.listheader}>국비 과정 목록</h1>
      {errorMessage && <div className={styles.errorMessage}>{errorMessage}</div>}
      
      {sessions.length > 0 ? (
        <table className={styles.sessionlisttable}>
          <thead>
            <tr>
              <th scope="col">번호</th>
              <th scope="col" colSpan="2">과정명</th> 
              <th scope="col" colSpan="2">회차 제목</th> 
              <th scope="col">회차</th>
              <th scope="col">강의 자료실</th>
              <th scope="col">상태</th>  
              <th scope="col">상세보기</th>
            
            </tr>
          </thead>
          <tbody>
            {sessions.map((session,index) => (
              <tr key={session.kdtSessionId}>
                <td>{index + 1}</td>
                <td colSpan="2">
                    {session.kdtCourseTitle}
                </td>
                <td colSpan="2">
                  <Link to={`/instr/KDT/session/${session.kdtSessionId}`}>
                    {session.kdtSessionTitle}
                  </Link>
                </td>

                <td>{session.kdtSessionNum}회차</td>
                <td>
                  <a href={`/instr/KDT/${session.kdtSessionId}/board/materiallist`}>강의 자료등록</a>
                </td>
              
                <td>{session.kdtSessionStatus}</td>

                <td>
                  <Link to={`/instr/KDT/session/${session.kdtSessionId}`}>
                    상세 보기
                  </Link>
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
