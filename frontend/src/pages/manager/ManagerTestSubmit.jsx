import React, {useEffect, useState } from "react";
import styles from "/src/css/manager/ManagerTestSubmit.module.css";
import { useParams } from "react-router-dom";
import dayjs from "dayjs"; //날짜 포매팅 모듈듈
import axios from "axios";
import Pagination from "/src/components/Pagination";
import BackButton from "/src/components/BackButton";
import { Helmet } from "react-helmet-async";
 
const TestSubmit = () => {
  const { kdtSessionId , kdtTestId } = useParams(); // URL에서 kdtSessionId 가져오기
  const [submitList, setSubmitList] = useState([]); // 제출 목록
  const [sessionInfo, setSessionInfo] = useState({}); // 회차 정보
  const [testInfo, setTestInfo] = useState({}); // 시험 정보
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(10);
  const itemsPerPageOptions = [5, 10, 20, 50];

  const formatDate = (isoString) => dayjs(isoString).format('YYYY-MM-DD');

  // 스프링부트에서 사용자 데이터를 불러오기
  useEffect(() => {
    const fetchSubmitList = async () => {
      try {
        const response = await axios.get(`/api/manager/KDT/${kdtSessionId}/test/submit/${kdtTestId}/list`,{ // API 엔드포인트 수정
          headers: {
          "Content-Type": "application/json",
          },
          withCredentials: true, // 쿠키 포함
          });
          console.log(response.data); // 응답 데이터 확인
          setSubmitList(response.data.kdtTestSubmitListDTOs || []); // 제출 목록 설정
          setSessionInfo(response.data.KDTSessionDTO || {}); // 회차 정보 설정
          setTestInfo(response.data.kdtTestListDTO || {}); // 시험 정보 설정
        } catch (error) {
          console.error("사용자 데이터를 불러오는 중 오류 발생:", error);
          setTrainList([]); // 오류 발생 시 빈 배열로 설정
        }
      };

      fetchSubmitList();
  }, [kdtSessionId, kdtTestId]);


  // 페이지네이션
  const totalPages = Math.ceil(submitList.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const currentItems = submitList.slice(startIndex, startIndex + itemsPerPage);

  // 페이지당 항목 수 변경
  const handleItemsPerPageChange = (e) => {
    setItemsPerPage(Number(e.target.value));
    setCurrentPage(1); // 페이지 초기화
  };

  return (
    <div className={styles.container}>
      <Helmet>
        <title>메타러닝 시험 제출 내역</title>
      </Helmet>
      {/* 제목 */}
      <h1>
        {sessionInfo.kdtSessionTitle} {sessionInfo.kdtSessionNum}회차 - {testInfo.kdtTestTitle} 제출 내역
      </h1>

      

      {/* 검색 및 설정 */}
      <div className={styles.searchBar}>
        
        <div className={styles.itemsPerPageSelector}>
          
          <label htmlFor="itemsPerPage">페이지 당 항목 수: </label>
          <select
            id="itemsPerPage"
            value={itemsPerPage}
            onChange={handleItemsPerPageChange}
            className={styles.dropdown}
          >
            {itemsPerPageOptions.map((option) => (
              <option key={option} value={option}>
                {option}개
              </option>
            ))}
          </select>
          <BackButton label="Back" />
        </div>
        
      </div>

      {/* 제출 목록 테이블 */}
      <table className={styles.submitTable}>
        <thead>
          <tr>
            <th>번호</th>
            <th>참여자</th>
            <th>시험 제출일</th>
            <th>최종 수정일</th>
            <th>점수</th>
            <th>백분율</th>
            <th>상세보기</th>
          </tr>
        </thead>
        <tbody>
          {currentItems.map((submit, index) => (
            <tr key={submit.kdtPartId}>
              <td>{startIndex + index + 1}</td>
              <td>
                <a href={`/managers/KDT/${sessionInfo.kdtSessionId}/test/submit/detail/${testInfo.kdtTestId}/${submit.kdtPartId}`}>
                  {submit.kdtPartName}
                </a>
              </td>
              <td>{submit.kdtTestSubmitCreatedAt ? formatDate(submit.kdtTestSubmitCreatedAt) : "-"}</td>
              <td>{submit.kdtTestSubmitUpdatedAt ? formatDate(submit.kdtTestSubmitUpdatedAt) : "-"}</td>
              <td>
                {submit.actualScore}/{submit.maxScore}
              </td>
              <td>
                {submit.percentile? submit.percentile % 1 === 0
                  ? submit.percentile // 정수인 경우 그대로 표시
                  : submit.percentile.toFixed(2) // 소수점이 있는 경우 포매팅
                  : "0"}%
              </td>
              <td>
                {submit.kdtTestSubmitCreatedAt && (
                  <a href={`/managers/KDT/${sessionInfo.kdtSessionId}/test/submit/detail/${testInfo.kdtTestId}/${submit.kdtPartId}`}>
                    상세보기
                  </a>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {/* 페이지네이션 */}
      <Pagination
        currentPage={currentPage}
        totalPages={totalPages}
        onPageChange={setCurrentPage}
        pagesPerGroup={5}
      />
    </div>
  );
};

export default TestSubmit;