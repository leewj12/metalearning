import React, {useEffect, useState } from "react";
import styles from "/src/css/manager/ManagerTestList.module.css";
import { useParams , Link } from "react-router-dom";
import dayjs from "dayjs"; //날짜 포매팅 모듈듈
import axios from "axios";
import Pagination from "/src/components/Pagination";
import BackButton from "/src/components/BackButton";
import { Helmet } from "react-helmet-async";
 
const InstrTestList = () => {
  const { kdtSessionId } = useParams(); // URL에서 kdtSessionId 가져오기
  const [sessionInfo, setSessionInfo] = useState({});  // 회차 정보
  const [testList, setTestList] = useState([]); // 시험 목록
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(10);
  const itemsPerPageOptions = [5, 10, 20, 50];

  const [searchType, setSearchType] = useState("kdtTestTitle"); // 기본 검색 조건: 시험 제목
  const [searchQuery, setSearchQuery] = useState(""); // 검색어 상태

  const formatDate = (isoString) => dayjs(isoString).format('YYYY-MM-DD');

  // 스프링부트에서 사용자 데이터를 불러오기
  useEffect(() => {
    const  fetchTestList = async () => {
      try {
        const response = await axios.get(`/api/instr/KDT/${kdtSessionId}/test/list`,{ // API 엔드포인트 수정
          headers: {
          "Content-Type": "application/json",
          },
          withCredentials: true, // 쿠키 포함
          });
          setSessionInfo(response.data.KDTSessionDTO || {}); // 회차 정보
          setTestList(response.data.kdtTestListDTOs || []); // 시험 목록
        } catch (error) {
          console.error("사용자 데이터를 불러오는 중 오류 발생:", error);
          setTestList([]); // 오류 발생 시 빈 배열로 설정
        }
      };

    fetchTestList();
  }, [kdtSessionId]);

  // 검색 필터링
  const filteredTestList = testList.filter((test) => {
    if (searchType === "kdtTestTitle") {
      return test.kdtTestTitle.toLowerCase().includes(searchQuery.toLowerCase());
    }
    if (searchType === "authorName") {
      return test.authorName.toLowerCase().includes(searchQuery.toLowerCase());
    }
    return true; // 기본적으로 모든 데이터를 반환
  });


  // 페이지네이션
  const totalPages = Math.ceil(filteredTestList.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const currentItems = filteredTestList.slice(
    startIndex,
    startIndex + itemsPerPage
  );


   // 페이지 당 항목 수 변경
   const handleItemsPerPageChange = (e) => {
    setItemsPerPage(Number(e.target.value));
    setCurrentPage(1); // 페이지 초기화
  };
  return (
    <div className={styles.container}>
       <Helmet>
        <title>메타러닝 시험 목록 조회</title>
      </Helmet>
      <h1>
        {sessionInfo.kdtSessionTitle} {sessionInfo.kdtSessionNum}회차 시험 목록
      </h1>

      {/* 검색창 */}
      <div className={styles.searchBar}>
        {/* 검색 유형 선택 */}
        <select
          value={searchType}
          onChange={(e) => setSearchType(e.target.value)}
          className={styles.searchSelect}
        >
          <option value="kdtTestTitle">시험 제목</option>
          <option value="authorName">출제자</option>
        </select>

        {/* 검색 입력 */}
        <input
          type="text"
          placeholder={`검색어 입력 (${searchType === "kdtTestTitle" ? "시험 제목" : "출제자"})`}
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          className={styles.searchInput}
        />

        {/* 페이지당 항목 수 선택 */}
        <div className={styles.itemsPerPageSelector}>
          
          <label htmlFor="itemsPerPage">목록 수: </label>
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
        </div>
      </div>

      {/* 시험 목록 테이블 */}
      <table className={styles.testTable}>
        <thead>
          <tr>
            <th>번호</th>
            <th>시험 제목</th>
            <th>출제자</th>
            <th>표준편차</th>
            <th>시험 시작 날짜</th>
            <th>시험 종료 날짜</th>
            <th>응시인원</th>
            <th>상세보기</th>
          </tr>
        </thead>
        <tbody>
          {currentItems.map((test, index) => (
            <tr key={test.kdtTestId}>
              <td>{startIndex + index + 1}</td>
              <td>
                <a href={`/instr/KDT/${kdtSessionId}/test/${test.kdtTestId}`}>
                  {test.kdtTestTitle}
                </a>
              </td>
              <td>{test.authorName}</td>
              
              <td>{test.stdDev}</td>
              <td>{formatDate(test.kdtTestStartDate)}</td>
              <td>{formatDate(test.kdtTestEndDate)}</td>

              <td>
                <Link to={`/instr/KDT/${kdtSessionId}/test/submit/${test.kdtTestId}/list`}>{test.actualCnt}/{test.totalCnt}</Link>
              </td>
              <td>
                <a href={`/instr/KDT/${kdtSessionId}/test/${test.kdtTestId}`}>
                  상세보기
                </a>
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

      <div className={styles.buttonContainer}>
          <a
            href={`/instr/KDT/${kdtSessionId}/test`}
            className={styles.createButton}
          >
            시험출제
          </a>
          <BackButton label="Back" />
      </div>
    </div>
  );
};
  
export default InstrTestList;