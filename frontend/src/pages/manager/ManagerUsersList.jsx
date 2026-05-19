import React, {useEffect, useState } from "react";
import styles from "/src/css/manager/ManagerUserList.module.css";
import { useLocation } from "react-router-dom";
import dayjs from "dayjs"; //날짜 포매팅 모듈듈
import axios from "axios";
import Pagination from "/src/components/Pagination";
import { Helmet } from "react-helmet-async";
 
const ManagerUserList = () => {
  const roleMap = {STUDENT: "학생",MANAGER: "매니저",INSTRUCTOR: "강사"};
  const genderMap = {M: "남성",F: "여성",};
  const statusMap = {활동중: "활동",휴면계정: "휴면",정지계정: "정지",};
  
  const location = useLocation();
  const searchParams = new URLSearchParams(location.search);
  const roleFilter = searchParams.get("userRole"); // "STUDENT", "TEACHER", "MANAGER" 등

  const [users, setUsers] = useState([]);

  const [currentPage, setCurrentPage] = useState(1);
  const [usersPerPage, setUsersPerPage] = useState(10);
  const usersPerPageOptions = [5, 10, 20, 50]; // 보여주는 목록 수 

  const [searchType, setSearchType] = useState(""); // 검색 조건
  const [searchQuery, setSearchQuery] = useState(""); // 검색어

  const formatDate = (isoString) => dayjs(isoString).format('YYYY-MM-DD');
  // 스프링부트에서 사용자 데이터를 불러오기
  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const response = await axios.get("/api/manager/users/list",{ // API 엔드포인트 수정
          headers: {
          "Content-Type": "application/json",
          },
          withCredentials: true, // 쿠키 포함
          });
          setUsers(response.data); // 데이터 배열로 설정
        } catch (error) {
          console.error("사용자 데이터를 불러오는 중 오류 발생:", error);
        }
      };

    fetchUsers();
  }, []);

  // 필터링된 사용자 목록
  const filteredUsers = users.filter((user) => {
  if (roleFilter) {
    return user.userRole === roleFilter;
  }

  if (searchType === "userRole") {
    const displayRole = roleMap[user.userRole] || ""; // 매핑된 한글 값
    return displayRole.includes(searchQuery);
  }

  if (searchType === "userGender") {
    const displayGender = genderMap[user.userGender] || ""; // 매핑된 한글 값
    return displayGender.includes(searchQuery);
  }

  if (searchType === "userStatus") {
    const displayStatus = statusMap[user.userStatus] || ""; // 매핑된 한글 값
    return displayStatus.includes(searchQuery);
  }

  // 다른 검색 조건은 원래 데이터 기준으로 필터링
  if (searchType === "userEmail") return user.userEmail.toLowerCase().includes(searchQuery.toLowerCase());
  if (searchType === "name") return user.name.toLowerCase().includes(searchQuery.toLowerCase());

  return true; // 전체 보기
});

  const totalPages = Math.ceil(filteredUsers.length / usersPerPage);
  const startIndex = (currentPage - 1) * usersPerPage;
  const currentUsers = filteredUsers.slice(startIndex, startIndex + usersPerPage);
  
  // 보여줄 목록수 
  const handleUsersPerPageChange = (e) => {
    setUsersPerPage(Number(e.target.value)); // 드롭다운에서 선택한 값을 상태로 설정
    setCurrentPage(1); // 페이지 번호를 초기화
  };

  return (
    <div className={styles.tableContainer}>
    <Helmet>
      <title>메타러닝 회원관리</title>
    </Helmet>
    {/* 검색창 */}
    <h1>메타러닝 회원관리</h1>
    <div className={styles.searchBar}>
        <select
          value={searchType}
          onChange={(e) => setSearchType(e.target.value)}
          className={styles.searchSelect}
        >
          <option value="">전체</option>
          <option value="userEmail">이메일</option>
          <option value="name">이름</option>
          <option value="userGender">성별</option>
          <option value="userRole">등급</option>
          <option value="userStatus">상태</option>
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
          <label htmlFor="usersPerPage">목록 개수 </label>
          <select
            id="usersPerPage"
            value={usersPerPage}
            onChange={handleUsersPerPageChange}
            className={styles.dropdown}
          >
            {usersPerPageOptions.map((option) => (
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
          <th>이메일</th>
          <th>이름</th>
          <th>성별</th>
          <th>등급</th>
          <th>상태</th>
          <th>등록일</th>
          <th>수정일</th>
          <th>상세정보</th>
        </tr>
      </thead>
      <tbody>
        {currentUsers.map((user,index) => (
          <tr key={user.userId}>
            <td>{startIndex + index + 1}</td>
            <td>{user.userEmail}</td>
            <td>{user.name}</td>
            <td>{genderMap[user.userGender] || "알 수 없음"}</td>
            <td>{roleMap[user.userRole] || "알 수 없음"}</td>
            <td>{statusMap[user.userStatus] || "알 수 없음"}</td>
            <td>{formatDate(user.userCreatedAt)}</td>
            <td>{formatDate(user.userUpdatedAt)}</td>
            <td><a className={styles.godetail} href={`/managers/users/${user.userId}`}>상세정보</a></td>
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

    </div>
  
  );
};

export default ManagerUserList;