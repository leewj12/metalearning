import React, { useEffect, useState } from "react";
import { useParams ,Link } from "react-router-dom";
import axios from "axios";
import CustomCalendar from "/src/components/CustomCalendar";
import styles from "/src/css/admin/AttListDetail.module.css";
import BackButton from "/src/components/BackButton";
import { Helmet } from "react-helmet-async";

const AttListDetail = () => {
  const { kdtSessionId, kdtPartId } = useParams(); // URL 파라미터
  const [detailInfo, setDetailInfo] = useState(null); // 상세 데이터
  const [loading, setLoading] = useState(true); // 로딩 상태
  const [errorMessage, setErrorMessage] = useState(""); // 오류 메시지
  const [events, setEvents] = useState([]); // 캘린더 이벤트 데이터

  const statusColorMap = {
    입실: "green",
    출석: "blue",
    조퇴: "orange",
    외출: "gray",
    휴가: "green",
    결석: "red",
    병결: "green",
    오류: "red",
  };
  useEffect(() => {
    const fetchDetail = async () => {
      try {
        setLoading(true);
        const response = await axios.get(
          `/api/admin/KDT/${kdtSessionId}/att/detail/${kdtPartId}`,
          {
            headers: {
              "Content-Type": "application/json",
            },
            withCredentials: true, // 쿠키 포함
          }
        );
        setDetailInfo(response.data);
         // 이벤트 데이터 가공
         const eventData = (response.data.kdtAttDTOs || []).map((record) => ({
          title: record.kdtAttStatus || "상태 없음",
          start: record.kdtAttDate,
          allDay: true,
          backgroundColor: statusColorMap[record.kdtAttStatus] || "blue",
        }));

        setEvents(eventData);
      } catch (error) {
        setErrorMessage("상세 데이터를 불러오는 데 실패했습니다.");
      } finally {
        setLoading(false);
      }
    };

    fetchDetail();
  }, [kdtSessionId, kdtPartId]);


  if (loading) return <div className={styles.loading}>로딩 중...</div>;
  if (errorMessage) return <div className={styles.error}>{errorMessage}</div>;

  return (
    <div className={styles.attDetailContainer}>
      <Helmet>
        <title>메타러닝 출석부 상세보기</title>
      </Helmet>
      {detailInfo && (
        <>
          <div className={styles.sessionInfo}>
            <div className={styles.headerContainer}>
              <div className={styles.sessiontitle}>학생 이름: {detailInfo.kdtAttListDTO[0]?.kdtPartName || "정보 없음"}</div>
              <div className={styles.headerActions}>
                <Link to={`/admin/KDT/${kdtSessionId}/att/log/${kdtPartId}`} className={styles.detailLink}>
                  로그보기
                </Link>
                <BackButton label="Back" />
              </div>
            </div>
            <p>{detailInfo.KDTSessionDTO?.kdtSessionNum || "정보 없음"}회차</p>
            <p>{detailInfo.KDTSessionDTO?.kdtSessionTitle || "정보 없음"}</p>
            <p className={styles.attendanceStats}>
              <span>출석율: {detailInfo.kdtAttListDTO[0]?.kdtAttRate.toFixed(2) || "정보 없음"}%</span>&nbsp;&nbsp;&nbsp;&nbsp;
              <span>출석: {detailInfo.kdtAttListDTO[0]?.attCount || 0}회</span>&nbsp;&nbsp;&nbsp;&nbsp;
              <span>외출: {detailInfo.kdtAttListDTO[0]?.outgoingCount || 0}회</span>&nbsp;&nbsp;&nbsp;&nbsp;
              <span>조퇴: {detailInfo.kdtAttListDTO[0]?.earlyLeaveCount || 0}회</span>&nbsp;&nbsp;&nbsp;&nbsp;
              <span>지각: {detailInfo.kdtAttListDTO[0]?.tardyCount || 0}회</span>&nbsp;&nbsp;&nbsp;&nbsp;
              <span>결석: {detailInfo.kdtAttListDTO[0]?.absenceCount || 0}회</span>
            </p>
          </div>
          {/* FullCalendar로 변경된 달력 */}
          <div className={styles.calendarContainer}>
            <CustomCalendar events={events}/>
          </div>   
        </>
      )}
    </div>
  );
};

export default AttListDetail;
