import React, { useEffect, useState, useRef } from "react";
import { useParams } from "react-router-dom";
import axios from "axios";
import dayjs from "dayjs";
import styles from "/src/css/manager/ManagerAttLog.module.css";
import Papa from "papaparse";
import utc from "dayjs/plugin/utc";
import timezone from "dayjs/plugin/timezone";
dayjs.extend(utc);
dayjs.extend(timezone);
import Backbutton from "/src/components/BackButton";
import { Helmet } from "react-helmet-async";

const getKSTday = () =>dayjs().tz("Asia/Seoul").format("YYYY-MM-DD");

const ManagerAttLog = () => {
    const { kdtSessionId, kdtPartId } = useParams(); // URL 파라미터

    const [logInfo, setLogInfo] = useState([]); // 상세 데이터

    const [filteredLogInfo, setFilteredLogInfo] = useState([]); // 필터링된 데이터

    const [loading, setLoading] = useState(true); // 로딩 상태
    const [errorMessage, setErrorMessage] = useState(""); // 오류 메시지

    const [studentInfo, setStudentInfo] = useState(null); // 학생 정보 저장

    const today = dayjs(); // 현재 날짜
    const [selectedYear, setSelectedYear] = useState(today.year().toString()); // 현재 연도
    const [selectedMonth, setSelectedMonth] = useState((today.month() + 1).toString()); // 현재 월 (month()는 0부터 시작)

    const printRef = useRef();

    const [editRow, setEditRow] = useState(null); // 수정 중인 행 ID
    const [editData, setEditData] = useState({}); // 수정할 데이터

    const statusMapping = {
      "입실": "ARRIVAL",
      "출석": "DEPARTURE",
      "외출": "OUTGOING",
      "조퇴": "EARLY_LEAVE",
      "휴가": "VACATION",
      "결석": "ABSENT",
      "병결": "SICK_LEAVE",
      "오류": "ERROR",
    };

    useEffect(() => {
      fetchLogData();
    }, [kdtSessionId, kdtPartId]);
    // 데이터 갱신 함수
    const fetchLogData = async () => {
      try {
        setLoading(true);
        const response = await axios.get(
          `/api/manager/KDT/${kdtSessionId}/att/detail/${kdtPartId}`,
          {
            headers: { "Content-Type": "application/json" },
            withCredentials: true,
          }
        );
        // 데이터 업데이트
        if (response.data.kdtAttListDTO && response.data.kdtAttListDTO.length > 0) {
          setStudentInfo(response.data.kdtAttListDTO[0]);
        }
        setLogInfo(Array.isArray(response.data.kdtAttDTOs) ? response.data.kdtAttDTOs : []);
        setFilteredLogInfo(Array.isArray(response.data.kdtAttDTOs) ? response.data.kdtAttDTOs : []);
      } catch (error) {
        console.error("데이터 재조회 중 오류 발생:", error);
        setErrorMessage("데이터를 다시 불러오는 데 실패했습니다.");
      } finally {
        setLoading(false);
      }
    };

     // 수정 버튼 클릭 시
    const handleEditClick = (log) => {
    setEditRow(log.kdtAttId); // 수정 중인 행 ID 저장
    setEditData({
      kdtAttDate : log.kdtAttDate,
      kdtAttEntryTime: log.kdtAttEntryTime ? formatTime(log.kdtAttEntryTime) : "", // 입실 시간
      kdtAttExitTime: log.kdtAttExitTime ? formatTime(log.kdtAttExitTime) : "", // 퇴실 시간
      kdtAttLeaveStart: log.kdtAttLeaveStart ? formatTime(log.kdtAttLeaveStart) : "", // 외출 시작 시간
      kdtAttLeaveEnd: log.kdtAttLeaveEnd ? formatTime(log.kdtAttLeaveEnd) : "", // 외출 종료 시간
      kdtAttStatus: log.kdtAttStatus || "", // 상태
    });
  };
  // 수정 저장
  const handleEditSave = async () => {
    try {
      const formatToKST = (date, time) => {
        if (!date || !time || date === "" || time === "") {
          console.warn("Invalid date or time:", { date, time });
          return null;
        }
        // 날짜와 시간을 결합하여 ISO 형식으로 변환
        return dayjs.tz(`${date}T${time}:00`, "Asia/Seoul").format("YYYY-MM-DDTHH:mm:ss.SSS[Z]");
      };
  
      const formattedData = {
        kdtPartId: kdtPartId,
        kdtAttDate: editData.kdtAttDate || "", 
        kdtAttEntryTime: editData.kdtAttEntryTime
          ? formatToKST(editData.kdtAttDate, editData.kdtAttEntryTime)
          : null,
        kdtAttExitTime: editData.kdtAttExitTime
          ? formatToKST(editData.kdtAttDate, editData.kdtAttExitTime)
          : null,
        kdtAttLeaveStart: editData.kdtAttLeaveStart
          ? formatToKST(editData.kdtAttDate, editData.kdtAttLeaveStart)
          : null,
        kdtAttLeaveEnd: editData.kdtAttLeaveEnd
          ? formatToKST(editData.kdtAttDate, editData.kdtAttLeaveEnd)
          : null,
        kdtAttStatus: statusMapping[editData.kdtAttStatus] || "ERROR", // 한글 상태를 영문 상태로 변환
      };
  
      // 디버깅용 로그
      console.log("변환된 데이터:", formattedData);

      await axios.put(
        `/api/manager/KDT/${kdtSessionId}/att/update/${editRow}`,
        formattedData,
        {
          headers: { "Content-Type": "application/json" },
          withCredentials: true,
        }
      );

      alert("수정이 완료되었습니다.");
      setEditRow(null); // 수정 모드 종료
      await fetchLogData(); // 데이터 갱신
    } catch (error) {
      console.error("수정 중 오류 발생:", error);
      alert("수정에 실패했습니다.");
    }
  };

    const [newAttData, setNewAttData] = useState({
      kdtAttDate: "",
      kdtAttEntryTime: "",
      kdtAttExitTime: "",
      kdtAttLeaveStart: "",
      kdtAttLeaveEnd: "",
      kdtAttStatus: "",
    });

    const [isAddModalOpen, setIsAddModalOpen] = useState(false);
    const handleEditChange = (e) => {
      const { name, value } = e.target;
      setEditData((prev) => ({ ...prev, [name]: value }));
      console.log("Updated editData:", { ...editData, [name]: value });
    };
    
    const handleAddInputChange = (e) => {
      const { name, value } = e.target;
      setNewAttData((prev) => ({ ...prev, [name]: value }));
    };

    // KST를 기준으로 결합
   const combineDateAndTime = (date, time) => {
    if (!date || !time) return null;
    return dayjs.tz(`${date}T${time}:00`, "Asia/Seoul").format("YYYY-MM-DDTHH:mm:ss.SSS[Z]");
  };
  
    const handleAddSubmit = async () => {
      let formattedData;
      try {
          formattedData = {
            kdtAttDate: newAttData.kdtAttDate || getKSTday(),
            kdtAttEntryTime: combineDateAndTime(newAttData.kdtAttDate, newAttData.kdtAttEntryTime),
            kdtAttExitTime: combineDateAndTime(newAttData.kdtAttDate, newAttData.kdtAttExitTime),
            kdtAttLeaveStart: combineDateAndTime(newAttData.kdtAttDate, newAttData.kdtAttLeaveStart),
            kdtAttLeaveEnd: combineDateAndTime(newAttData.kdtAttDate, newAttData.kdtAttLeaveEnd),
            kdtAttStatus: newAttData.kdtAttStatus,
            kdtPartId, // 참가자 ID 추가
          };
          
        await axios.post(
          `/api/manager/KDT/${kdtSessionId}/att/new`,
            formattedData, // 참가자 ID 추가
          {
            headers: { "Content-Type": "application/json" },
            withCredentials: true,
          }
        );
       
        alert("새 출석부가 추가되었습니다.");
        setIsAddModalOpen(false); // 모달 닫기
        await fetchLogData();

        setNewAttData({
          kdtAttDate: "",
          kdtAttEntryTime: "",
          kdtAttExitTime: "",
          kdtAttLeaveStart: "",
          kdtAttLeaveEnd: "",
          kdtAttStatus: "",
        });
      } catch (error) {
        console.log(formattedData);
        console.error("추가 중 오류 발생:", error);
        alert("출석부 추가에 실패했습니다.");
      }
    };
    

    const handlePrint = () => {
        if (printRef.current) {
          window.print(); // 브라우저 기본 출력
        }
      };

    // 시간 포맷팅 함수
    const formatTime = (dateTime) => {
      if (!dateTime) return ""; // 값이 없으면 빈 문자열 반환
      return dayjs(dateTime).format("HH:mm"); // 유효한 시간 형식 반환
    };

    

    // 연도 및 월별 데이터 필터링
   useEffect(() => {
        if (!selectedYear && !selectedMonth) {
            setFilteredLogInfo(logInfo);
        } else {
            const filtered = logInfo.filter((log) => {
                const date = dayjs(log.kdtAttDate);
                const yearMatch = selectedYear ? date.year() === parseInt(selectedYear) : true;
                const monthMatch = selectedMonth ? date.month() + 1 === parseInt(selectedMonth) : true;
                return yearMatch && monthMatch;
            });
            setFilteredLogInfo(filtered);
        }
    }, [logInfo, selectedYear, selectedMonth]);



  const exportToCSV = () => {
      // 필요한 필드만 선택하여 데이터 변환
      const filteredData = filteredLogInfo.map((log) => ({
      날짜: log.kdtAttDate || "N/A",
      입실시간: formatTime(log.kdtAttEntryTime) || "-",
      퇴실시간: formatTime(log.kdtAttExitTime) || "-",
      외출시간: formatTime(log.kdtAttLeaveStart) || "-",
      복귀시간: formatTime(log.kdtAttLeaveEnd) || "-",
      출석상태: log.kdtAttStatus || "-",
      }));  
      // CSV 변환
      const csv = Papa.unparse(filteredData); // 선택된 데이터만 포함
      const blob = new Blob([csv], { type: "text/csv;charset=utf-8;" });

      // 파일 다운로드
      const link = document.createElement("a");
      link.href = URL.createObjectURL(blob);
      link.setAttribute("download", "출석부.csv");
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    };

    const years = Array.from({ length: 6 }, (_, i) => today.year() - 5 + i);

    const handleDelete = async (id) => {
      try {
        await axios.delete(
          `/api/manager/KDT/${kdtSessionId}/att/delete/${id}`,
          {
            withCredentials: true,
          }
        );
        alert("삭제가 완료되었습니다.");
        setLogInfo((prev) => prev.filter((log) => log.kdtAttId !== id)); // 삭제된 데이터 제거
      } catch (error) {
        console.error("삭제 중 오류 발생:", error);
        alert("삭제에 실패했습니다.");
      }
    };

    if (loading) return <div className={styles.loading}>로딩 중...</div>;
    if (errorMessage) return <div className={styles.error}>{errorMessage}</div>;
  
    return (
        <div className={styles.attLogContainer}>
        <Helmet>
          <title>메타러닝 출석부 로그</title>
        </Helmet>
        <div ref={printRef}>
          
          <div className={styles.backButtonContainer}>
          <h2> {studentInfo?.kdtPartName || "학생 이름 없음"}{" "}</h2>
            <Backbutton label="Back" />
          </div>
          <h6>
            (출석: {studentInfo?.attCount || 0}, 지각: {studentInfo?.tardyCount || 0}, 결석:{studentInfo?.absenceCount || 0}
            ,조퇴:{studentInfo?.earlyLeaveCount || 0}, 외출: {studentInfo?.outgoingCount || 0})
          </h6>

          {/* 연도 및 월 선택 */}
        <div className={styles.filterContainer}>
        <label>
            연도:
            <select value={selectedYear} onChange={(e) => setSelectedYear(e.target.value)}>
              <option value="">전체</option>
              {years.map((year) => (
                <option key={year} value={year}>
                  {year}
                </option>
              ))}
            </select>
          </label>
          <label>
            월:
            <select value={selectedMonth} onChange={(e) => setSelectedMonth(e.target.value)}>
              <option value="">전체</option>
              {Array.from({ length: 12 }, (_, i) => (
                <option key={i + 1} value={i + 1}>
                  {i + 1}월
                </option>
              ))}
            </select>
          </label>
        </div>

          <table className={styles.attLogTable}>
            <thead>
              <tr>
                <th>날짜</th>
                <th>입실 시간</th>
                <th>퇴실 시간</th>
                <th>외출 시간</th>
                <th>복귀 시간</th>
                <th>출석 상태</th>
                <th className={styles.updateth}>수정</th>
                <th className={styles.deleteth}>삭제</th>
              </tr>
            </thead>
            <tbody>
              {filteredLogInfo.map((log, index) => (
                <tr key={index}>
                  <td>{log.kdtAttDate}</td> 

                  <td>{
                  editRow === log.kdtAttId ? (
                    <input
                      type="time"
                      name="kdtAttEntryTime"
                      value={editData.kdtAttEntryTime || ""}
                      onChange={handleEditChange}
                    />
                  ) : (
                    formatTime(log.kdtAttEntryTime) || "-"
                  )}
                  </td>

                  <td>
                  {editRow === log.kdtAttId ? (
                    <input
                      type="time"
                      name="kdtAttExitTime"
                      value={editData.kdtAttExitTime || ""}
                      onChange={handleEditChange}
                    />
                  ) : (
                    formatTime(log.kdtAttExitTime) || "-"
                  )}
                  </td>

                  <td> 
                  {editRow === log.kdtAttId ? (
                  <input
                    type="time"
                    name="kdtAttLeaveStart"
                    value={editData.kdtAttLeaveStart || ""}
                    onChange={handleEditChange}
                    />
                  ) : (
                    formatTime(log.kdtAttLeaveStart) || "-"
                  )}</td>

                  <td> 
                  {editRow === log.kdtAttId ? (
                    <input
                      type="time"
                      name="kdtAttLeaveEnd"
                      value={editData.kdtAttLeaveEnd || ""}
                      onChange={handleEditChange}
                    />
                  ) : (
                    formatTime(log.kdtAttLeaveEnd) || "-"
                  )}
                  </td>
                  
                  <td> {editRow === log.kdtAttId ? (
                    <select
                      name="kdtAttStatus"
                      value={editData.kdtAttStatus || ""}
                      onChange={handleEditChange}
                    >
                      <option value="">선택</option>
                      <option value="입실">입실</option>
                      <option value="출석">출석</option>
                      <option value="결석">결석</option>
                      <option value="조퇴">조퇴</option>
                      <option value="휴가">휴가</option>
                      <option value="병결">병결</option>
                    </select>
                  ) : (
                    log.kdtAttStatus || "-" 
                  )}
                  </td>

                  <td className={styles.updatetd}>
                      {editRow === log.kdtAttId ? (
                      <div className={styles.updatediv}>
                        <button onClick={handleEditSave} className={styles.savebtn}>저장</button>
                        <button onClick={() => setEditRow(null)} className={styles.cancelbtn}>취소</button>
                      </div>
                      ) : (
                      <button onClick={() => handleEditClick(log)} className={styles.editbtn}>수정</button>
                      )}
                  </td>

                  <td className={styles.deletetd}>
                    <button className={styles.deletebtn} onClick={() => handleDelete(log.kdtAttId)}>삭제</button>
                  </td>

                </tr>
              ))}
            </tbody>
          </table>
        </div>
        
        <button className={styles.exportButton} onClick={exportToCSV} disabled={editRow !== null} >
          CSV 파일로 저장
        </button>
  
        <button
          className={styles.printButton}
          onClick={handlePrint}
          disabled={logInfo.length === 0 || editRow !== null}  // 데이터 없으면 비활성화
        >
          출력
        </button>

        <button  className={styles.createbtn}
        onClick={() => setIsAddModalOpen(true)}
        disabled={editRow !== null}>
          +
        </button>
        {/* 모달 창 */}
        {isAddModalOpen && (
          <div className={styles.modal}>
            <div className={styles.modalContent}>
              <h3>출석부 추가</h3>
              <label><span>*</span>
                날짜:
                <input
                  type="date"
                  name="kdtAttDate"
                  value={newAttData.kdtAttDate}
                  onChange={handleAddInputChange}
                />
              </label>
              <label>
                입실 시간:
                <input
                  type="time"
                  name="kdtAttEntryTime"
                  value={newAttData.kdtAttEntryTime}
                  onChange={handleAddInputChange}
                />
              </label>
              <label>
                퇴실 시간:
                <input
                  type="time"
                  name="kdtAttExitTime"
                  value={newAttData.kdtAttExitTime}
                  onChange={handleAddInputChange}
                />
              </label>
              <label>
                외출 시간:
                <input
                  type="time"
                  name="kdtAttLeaveStart"
                  value={newAttData.kdtAttLeaveStart}
                  onChange={handleAddInputChange}
                />
              </label>
              <label>
                복귀 시간:
                <input
                  type="time"
                  name="kdtAttLeaveEnd"
                  value={newAttData.kdtAttLeaveEnd}
                  onChange={handleAddInputChange}
                />
              </label>
              <label><span>*</span>
                상태:
                <select
                  name="kdtAttStatus"
                  value={newAttData.kdtAttStatus}
                  onChange={handleAddInputChange}
                >
                  <option value="">선택</option>
                  <option value="ARRIVAL">입실</option>
                  <option value="DEPARTURE">출석</option>
                  <option value="ABSENT">결석</option>
                  <option value="EARLY_LEAVE">조퇴</option>
                  <option value="VACATION">휴가</option>
                  <option value="SICK_LEAVE">병결</option>
                </select>
              </label>
              <button onClick={handleAddSubmit}>저장</button>
              <button onClick={() => setIsAddModalOpen(false)}>취소</button>
            </div>
          </div>
        )}

      </div>
    );
  };
  
  
  export default ManagerAttLog;
  