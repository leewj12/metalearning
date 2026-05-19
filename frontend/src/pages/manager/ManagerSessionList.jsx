import React, { useEffect, useState } from "react";
import { useParams, useNavigate,Link } from "react-router-dom";
import axios from "axios";
import styles from "/src/css/manager/ManagerSessionList.module.css";
import { Helmet } from "react-helmet-async";

const ManagerSessionList = () => {
  const { courseId } = useParams(); // URL에서 courseId 가져오기
  const navigate = useNavigate(); // 히스토리 백을 위한 useNavigate
  const [sessions, setSessions] = useState([]); // 회차 데이터 상태 관리
  const [errorMessage, setErrorMessage] = useState(""); // 오류 메시지 상태 관리
  const statusMap = {WAITING: "대기", ONGOING: "진행중",FINISHED: "종료",};

  useEffect(() => {

    const fetchSessions = async () => {
      try {
        const response = await axios.get(`/api/manager/KDT/course/${courseId}`, {
          headers: {
            "Content-Type": "application/json",
          },
          withCredentials: true, // 쿠키 포함
        });
        // console.log(response);
        const data = response.data; // 응답 데이터 추출
        console.log("서버 응답 데이터:", data); // 데이터 로그 출력
        console.log("응답 데이터:", response.data);
        
        
        // 세션 데이터가 정상일 경우 상태 업데이트
        setSessions(data);
          
      } catch (error) {
        console.error("데이터 요청 중 오류 발생:", error);
       
        if (error.response.status === 403) {
        setErrorMessage(error.response.data.message);
        
      } else {
        // 네트워크 또는 기타 문제
        setErrorMessage("서버와의 연결에 문제가 발생했습니다.");
      }
    }
    };

    fetchSessions(); // 데이터 요청
  }, [courseId]);

  useEffect(() => {
    if (errorMessage === "회차에 등록된 매니저가 아닙니다.") {
      alert(errorMessage);
      navigate("/manager/KDT/list");
    }
  }, [errorMessage, navigate]);


  return (
    <div className={styles.sessionlistcontainer}>
      <Helmet>
        <title>메타러닝 회차 목록 조회</title>
      </Helmet>
      <h1 className={styles.listheader}>{sessions.length > 0 && sessions[0].kdtSessionTitle} 회차 목록</h1>
      {errorMessage && <div className={styles.errorMessage}>{errorMessage}</div>}
      
      {sessions.length > 0 ? (
        <table className={styles.sessionlisttable}>
          <thead>
            <tr>
              <th scope="col">번호</th>
              <th scope="col" colSpan="2">회차 제목</th> 
              <th scope="col">회차</th>
              <th scope="col">상태</th>
              <th scope="col">강의 자료실</th>
              <th scope="col">강의 영상 자료실</th>
              <th scope="col">수강생 등록</th>    
              <th scope="col">강사 등록</th> 
              <th scope="col">수정</th>
            </tr>
          </thead>
          <tbody>
            {sessions.map((session,index) => (
              <tr key={session.kdtSessionId}>
                <td>{index + 1}</td>

                <td colSpan="2">
                  <Link to={`/manager/KDT/session/${session.kdtSessionId}`}>
                    {session.kdtSessionTitle}
                  </Link>
                </td>

                <td>{session.kdtSessionNum}회차</td>

                <td>{statusMap[session.kdtSessionStatus]}</td>
                
                <td>
                  <a href={`/managers/KDT/${session.kdtSessionId}/board/materiallist`}>강의 자료등록</a>
                </td>
                <td>
                  <a href={`/managers/KDT/${session.kdtSessionId}/courseoutline/list`}>강의 영상등록</a>
                </td>
               
                <td>
                  <a href={`/managers/KDT/${session.kdtSessionId}/part`}>수강생 등록하기</a>
                </td>
        
                <td>
                  <a href={`/managers/KDT/${session.kdtSessionId}/staff/instr`}>강사 등록하기</a>
                </td>
                
                <td>
                  <button className={styles.editbtn}>
                    <a href={`/managers/KDT/session/update/${session.kdtSessionId}`} >수정</a></button>
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

export default ManagerSessionList;
