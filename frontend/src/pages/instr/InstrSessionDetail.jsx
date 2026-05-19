import React, { useEffect, useState } from "react";
import "bootstrap/dist/css/bootstrap.min.css";
import styles from "/src/css/manager/ManagerSessionDetail.module.css";
import { useParams,Link, useNavigate } from "react-router-dom";
import axios from "axios";
import { Helmet } from "react-helmet-async";


const InstrSessionDetail = () => {
  const { sessionId } = useParams();
  const navigate = useNavigate();

  const [sessionDetail, setSessionDetail] = useState([]); // 회차 상세 정보
  const [participantCount, setParticipantCount] = useState(null);
  const [managers, setManagers] = useState([]);
  const [errorMessage, setErrorMessage] = useState(""); // 오류 메시지
  const [loading, setLoading] = useState(true); // 로딩 상태
  
  useEffect(() => {
    // 더미 데이터
    const fetchSessionDetail = async()=>{
      setLoading(true);
      try {

        // ✅ 3개의 API 요청을 병렬로 실행 (성능 최적화)
        const [sessionRes, countRes, staffRes] = await Promise.all([
          axios.get(`/api/instr/KDT/session/${sessionId}`, { withCredentials: true }),
          axios.get(`/api/instr/part/${sessionId}/count`, { withCredentials: true }),
          axios.get(`/api/instr/KDT/${sessionId}/staff/list`, { withCredentials: true }),
        ]);

        console.log(countRes.data.studentCount);
        // ✅ 데이터 저장
        setSessionDetail(sessionRes.data);
        setParticipantCount(countRes.data.studentCount || 0);
        setManagers(staffRes.data.managers || []);
      
      } catch (error) {
        console.error("Error fetching courses:", error);
        setErrorMessage("데이터를 불러오는 데 실패했습니다.");
      }finally {
        setLoading(false);
      }
    };

    fetchSessionDetail();
  }, [sessionId]);

   useEffect(() => {
    if (errorMessage === "강사 권한이 없습니다.") {
      alert(errorMessage);
      navigate("/instr/KDT/list");
    }
  }, [errorMessage, navigate]);

  if (loading) return <div className="text-center">로딩 중...</div>; // 로딩 상태 표시

  return (
    <div className={styles.sessioncontainer}>
      <Helmet>
        <title>메타러닝 회차 상세정보</title>
      </Helmet>
      <div>
        {/* 네비게이션 바 */}
        <nav className={`${styles.sessionNavbar}`}>
          <Link
          className={`${styles.attsessionNavLink}`}
          to={`/instr/KDT/${sessionDetail.kdtSessionId}/part/list`}
          >
          참가자명단
          </Link>

          <div className={styles.testsessionNavLink}>
          <Link
          className={`${styles.attsessionNavLink}`}
          to={`/instr/KDT/${sessionDetail.kdtSessionId}/test/list`}
           >
          시험
          </Link>        
          </div>

          <div className={styles.boardsessionNavLink}>
          <a>게시판</a>
            {/* 하위 메뉴 */}
            <div className={styles.boarddropdownMenu}>
              <a href={`/instr/KDT/${sessionDetail.kdtSessionId}/board/materiallist`}>강의 자료실</a>
              <a href={`/instr/KDT/${sessionDetail.kdtSessionId}/detail/detail`}>홍보게시글</a>
            </div>
          </div>  
        </nav>
      </div>
      <div className={styles.sessionContent}>
  {sessionDetail ? (
    <div className={`card ${styles.card}`}>
      <div className={`card-header ${styles.cardHeader}`}>
        <h2>{sessionDetail.kdtSessionTitle || "제목 없음"} {sessionDetail.kdtSessionNum}회차</h2>
      </div>
      <div className={`card-body ${styles.cardBody}`}>
        {/* 기본 정보 섹션 */}
        <h3>정보</h3>
        <table className={styles.infoTable}>
          <tbody>
            <tr>
              <th>회차 번호</th>
              <td>{sessionDetail.kdtSessionNum}회차</td>
              <th>상태</th>
              <td>{sessionDetail.kdtSessionStatus}</td>
            </tr>
            <tr>
              <th>카테고리</th>
              <td>{sessionDetail.kdtSessionCategory || "정보 없음"}</td>
              <th>담당매니저</th>
              <td>{managers.length > 0 ? managers.map(m => m.name).join(", ") : "매니저 없음"}</td>
            </tr>
            <tr>
              
              <th>설명</th>
              <td colSpan="3">{sessionDetail.kdtSessionDescript || "강사 없음"}</td>
            </tr>
          </tbody>
        </table>

        {/* 시간 정보 섹션 */}
        <h3>시간 </h3>
        <table className={styles.infoTable}>
          <tbody>
            <tr>
              <th>시작일</th>
              <td>{sessionDetail.kdtSessionStartDate || "정보 없음"}</td>
              <th>종료일</th>
              <td>{sessionDetail.kdtSessionEndDate || "정보 없음"}</td>
              
            </tr>
            <tr>
            <th>시작 시간</th>
            <td>{sessionDetail.kdtSessionStartTime || "정보 없음"}</td>
              <th>종료 시간</th>
              <td>{sessionDetail.kdtSessionEndTime || "정보 없음"}</td>
            </tr>
        
            <tr>
              <th>총 교육 일수</th>
              <td>{sessionDetail.kdtSessionTotalDay || 0}일</td>
              <th>총 교육 시간</th>
              <td>{sessionDetail.kdtSessionTotalTime || 0}시간</td>
            </tr>
            <tr>
              <th>하루 교육 시간</th>
              <td>{sessionDetail.kdtSessionOnedayTime || 0}시간</td>
              <th>최대 수강 인원</th>
              <td>{participantCount || 0 }/{sessionDetail.kdtSessionMaxCapacity || "정보 없음"}</td>
            </tr>
          </tbody>
        </table>

        {/* 장소 정보 섹션 */}
        <h3>장소</h3>
        <table className={styles.infoTable}>
          <tbody>
            <tr>
              <th>우편번호</th>
              <td>{sessionDetail.kdtSessionPostcode}</td>
              <th>주소</th>
              <td>{sessionDetail.kdtSessionAddress}</td>
            </tr>
            <tr>
              <th>상세주소</th>
              <td colSpan="3">{sessionDetail.kdtSessionAddressDetail}</td>
            </tr>
            <tr>
              <th >온라인 여부</th>
              <td colSpan="3">{sessionDetail.kdtSessionOnline ? "온라인" : "오프라인"}</td>
            </tr>
            <tr>
              
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  ) : (
    <div>회차 정보를 찾을 수 없습니다.</div>
  )}
</div>
</div>
  );
};

export default InstrSessionDetail;
