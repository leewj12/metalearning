import React, {useEffect, useState } from "react";
import styles from "/src/css/manager/ManagerTrainList.module.css";
import { useParams } from "react-router-dom";
import dayjs from "dayjs"; //날짜 포매팅 모듈듈
import axios from "axios";
import Pagination from "/src/components/Pagination";
import BackButton from "/src/components/BackButton";
import { Helmet } from "react-helmet-async";
 
const ManagerTrainList = () => {
  const { kdtSessionId } = useParams(); // URL에서 kdtSessionId 가져오기
  const [trainList, setTrainList] = useState([]);
  const [sessionInfo, setSessionInfo] = useState(""); // 회차 제목
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(10);
  const itemsPerPageOptions = [5, 10, 20, 50];

  const [searchType, setSearchType] = useState(""); // 검색 조건
  const [searchQuery, setSearchQuery] = useState(""); // 검색어

  const formatDate = (isoString) => dayjs(isoString).format('YYYY-MM-DD');

  // 스프링부트에서 사용자 데이터를 불러오기
  useEffect(() => {
    const fetchTrainList = async () => {
      try {
        const response = await axios.get(`/api/manager/KDT/${kdtSessionId}/train/list`,{ // API 엔드포인트 수정
          headers: {
          "Content-Type": "application/json",
          },
          withCredentials: true, // 쿠키 포함
          });
          setTrainList(response.data.trainList|| []); // 데이터 배열로 설정
          setSessionInfo(response.data.KDTSessionDTO|| []);
        } catch (error) {
          console.error("사용자 데이터를 불러오는 중 오류 발생:", error);
          setTrainList([]); // 오류 발생 시 빈 배열로 설정
        }
      };

      fetchTrainList();
  }, [kdtSessionId]);

  // 필터링된 사용자 목록
 const filteredTrainList = trainList.filter((item) => {
    if (searchType === "kdtTrainTitle") {
      return item.kdtTrainTitle.toLowerCase().includes(searchQuery.toLowerCase());
    }
    if (searchType === "kdtTrainSubject") {
      return item.kdtTrainSubject.toLowerCase().includes(searchQuery.toLowerCase());
    }
    if (searchType === "kdtTrainContent") {
      return item.kdtTrainContent.toLowerCase().includes(searchQuery.toLowerCase());
    }
    return true; // 전체 보기
  });

  // 페이지네이션
  const totalPages = Math.ceil(filteredTrainList.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const currentItems = filteredTrainList.slice(startIndex, startIndex + itemsPerPage);


  // 목록 개수 변경
  const handleItemsPerPageChange = (e) => {
    setItemsPerPage(Number(e.target.value));
    setCurrentPage(1); // 페이지 초기화
  };

  return (
    <div className={styles.tableContainer}>
      <Helmet>
          <title>메타러닝 훈련일지 목록</title>
      </Helmet>
    <h1>{sessionInfo.kdtSessionTitle} {sessionInfo.kdtSessionNum}회차 훈련일지 목록</h1>
    {/* 검색창 */}
    <div className={styles.searchBar}>
        <select
          value={searchType}
          onChange={(e) => setSearchType(e.target.value)}
          className={styles.searchSelect}
        >
          <option value="">전체</option>
          <option value="kdtTrainTitle">강의 제목</option>
          <option value="kdtTrainSubject">강의 주제</option>
          <option value="kdtTrainContent">강의 내용</option>
        </select>
        <input
          type="text"
          placeholder="검색어 입력"
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          className={styles.searchInput}
        />

        {/* 페이지당 목록 수 선택 */}
        <div className={styles.usersPerPageSelector}>
          <label htmlFor="itemsPerPage">목록 개수 </label>
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

    <table>
      <thead>
        <tr>
          <th>번호</th>
              <th>강의 제목</th>
              <th>강의 주제</th>
              <th>강의 날짜</th>
              <th>상세보기</th>
        </tr>
      </thead>
      <tbody>
        {currentItems.map((item, index) => (
              <tr key={item.kdtTrainId}>
                <td>{startIndex + index + 1}</td>
                <td><a href={`/managers/KDT/${item.kdtSessionId}/train/${item.kdtTrainId}`}>{item.kdtTrainTitle}</a></td>
                <td>{item.kdtTrainSubject}</td>
                <td>{formatDate(item.kdtTrainDate)}</td>
                <td><a href={`/managers/KDT/${item.kdtSessionId}/train/${item.kdtTrainId}`}>상세보기</a></td>
              </tr>
            ))}
      </tbody>
    </table>
    {/* Pagination 컴포넌트 */}
    <Pagination
        currentPage={currentPage}
        totalPages={totalPages}
        onPageChange={setCurrentPage}
        pagesPerGroup={5}
      />
      {/* 훈련일지 작성 버튼 */}
    <div className={styles.buttonContainer}>
      <a
        href={`/managers/KDT/${kdtSessionId}/train`}
        className={styles.createButton}
      >
        훈련일지 작성
      </a>
      <BackButton label="Back" />
    </div>
    

    </div>
  
  );
};

export default ManagerTrainList;