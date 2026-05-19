import React, { useEffect, useState } from "react";
import { useParams,Link } from "react-router-dom";
import axios from "axios";
import "bootstrap/dist/css/bootstrap.min.css";
import styles from "/src/css/manager/ManagerAttList.module.css";
import dayjs from "dayjs";
import Backbutton from "/src/components/BackButton";
import { Helmet } from "react-helmet-async";

const ManagerAttList = () => {
  const { kdtSessionId } = useParams(); // URL 파라미터에서 sessionId 가져오기
  const [sessionInfo, setSessionInfo] = useState(null); // 회차 정보 상태
  const [attendanceList, setAttendanceList] = useState([]); // 출석부 데이터
  const [loading, setLoading] = useState(true); // 로딩 상태
  const [errorMessage, setErrorMessage] = useState(""); // 오류 메시지
  const [selectedDate, setSelectedDate] = useState(dayjs().format("YYYY-MM-DD")); // 선택된 날짜

  const statusMap = {
    ARRIVAL: "입실",
    DEPARTURE: "출석", // 퇴실을 출석으로 변경
    OUTGOING: "외출",
    EARLY_LEAVE: "조퇴",
    VACATION: "휴가",
    ABSENT: "결석",
    SICK_LEAVE: "병결",
    ERROR: "오류", // 오류 추가
  };
  const getCurrentDate = () => {
    return dayjs().format("YYYY-MM-DD");
  };

  const formatTime = (isoString) => {
    if (!isoString) return null;
    return dayjs(isoString).format("HH:mm"); // 'HH:mm' 형식 반환
  };

  // 데이터 로드
  const fetchAttendanceList = async (date) => {
    try {
      setLoading(true);
      const response = await axios.get(
        `/api/manager/KDT/${kdtSessionId}/att/list`,
        {
          params: { date }, // 선택된 날짜를 쿼리 파라미터로 전달
          headers: {
            "Content-Type": "application/json",
          },
          withCredentials: true, // 쿠키 포함
        }
      );
      setSessionInfo(response.data.KDTSessionDTO || null); // 회차 정보
      setAttendanceList(response.data.attendanceList || []); // 출석부 데이터
      setErrorMessage(""); // 오류 메시지 초기화
    } catch (error) {
      setSessionInfo(null); // 회차 정보 초기화
      setAttendanceList([]); // 출석부 초기화
      setErrorMessage("출석부 데이터를 불러오는 데 실패했습니다.");
    } finally {
      setLoading(false);
    }
  };

  // 선택된 날짜나 세션 ID가 변경되면 데이터 로드
  useEffect(() => {
    fetchAttendanceList(selectedDate); // 선택된 날짜 전달
  }, [selectedDate, kdtSessionId]);


  const handleDateChange = (e) => {
    setSelectedDate(e.target.value); // 선택된 날짜 업데이트
  };

  if (loading) return <div className="text-center">로딩 중...</div>;
  if (errorMessage) return <div className="text-danger text-center">{errorMessage}</div>;

  return (
    <div className={styles.attListContainer}>
    <Helmet>
      <title>메타러닝 회차 출석부</title>
    </Helmet>
      
      {/* 회차 정보 출력 */}
      {sessionInfo && (
        <div className={styles.sessionheader}>
        <div className={styles.sessionInfo}>
          <h2>{sessionInfo.kdtSessionTitle || "회차 정보 없음"} {sessionInfo.kdtSessionNum}회차 출석부</h2>
          <p>현재 날짜: {getCurrentDate()}</p>
        </div>
        <div className={styles.backButtonContainer}>
          <Backbutton label="Back" />
        </div>
        </div>
      )}
      {/* 날짜 선택 */}
      <div className={styles.dateSelector}>
        <label htmlFor="date" className="form-label">날짜 선택:</label>
        <input
          type="date"
          id="date"
          value={selectedDate}
          onChange={handleDateChange}
          className="form-control"
        />
      </div>

      
      {/* 출석부 테이블 */}
      <table className={`table table-bordered ${styles.attendanceTable}`}>
        <thead>
          <tr>
            <th>번호</th>
            <th>학생 이름</th>
            <th>출석 상태</th>
            <th>출석률</th>
            <th>입실시간</th>
            <th>퇴실시간</th>
            <th>상세정보</th>
          </tr>
        </thead>
        <tbody>
          {attendanceList.length > 0 ? (
            attendanceList.map((attendee, index) => (
              <tr key={attendee.kdtPartId}>
                <td>{index + 1}</td>
                <td>{attendee.kdtPartName || "이름 없음"}</td>
                <td>{statusMap[attendee.kdtAttStatus] || "-"}</td>
                <td>{attendee.kdtAttRate ? `${parseFloat(attendee.kdtAttRate).toFixed(2)}%`: "-"}</td>
                <td>{attendee.kdtAttEntryTime ? formatTime(attendee.kdtAttEntryTime) : "-"}</td>
                <td>{attendee.kdtAttExitTime ? formatTime(attendee.kdtAttExitTime) : "-"}</td>
                <td>
                  <Link
                    to={`/manager/KDT/${kdtSessionId}/att/detail/${attendee.kdtPartId}`}
                    className={styles.detailLink}
                  >
                    상세보기
                  </Link>
                </td>
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan="7" className={styles.noResults}>
                출석 정보가 없습니다.
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
};

export default ManagerAttList;
