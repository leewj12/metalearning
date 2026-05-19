import React, {useEffect, useState } from "react";
import styles from "/src/css/admin/UserList.module.css";
import { useLocation } from "react-router-dom";
import dayjs from "dayjs"; //날짜 포매팅 모듈듈
import axios from "axios";
import Pagination from "/src/components/Pagination";
import { Helmet } from "react-helmet-async";
 
const UserList = () => {
  const roleMap = {STUDENT: "학생",MANAGER: "매니저",INSTRUCTOR: "강사",ADMIN: "관리자"};
  const genderMap = {M: "남성",F: "여성",};
  const statusMap = {활성: "활동",비활성: "휴면",차단: "정지",};
  
  const location = useLocation();
  const searchParams = new URLSearchParams(location.search);
  const roleFilter = searchParams.get("userRole"); // "STUDENT", "TEACHER", "MANAGER" 등

  const [users, setUsers] = useState([]);

  const [currentPage, setCurrentPage] = useState(1);
  const [usersPerPage, setUsersPerPage] = useState(10);
  const usersPerPageOptions = [5, 10, 20, 50]; // 보여주는 목록 수 

  const [searchType, setSearchType] = useState(""); // 검색 조건
  const [searchQuery, setSearchQuery] = useState(""); // 검색어
  const [editingUserId, setEditingUserId] = useState(null); // 현재 수정 중인 사용자 ID
  const [editedUser, setEditedUser] = useState({}); // 수정 중인 사용자 데이터

  const formatDate = (isoString) => dayjs(isoString).format('YYYY-MM-DD');
  // 스프링부트에서 사용자 데이터를 불러오기
  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const response = await axios.get("/api/admin/user/list",{ // API 엔드포인트 수정
          headers: {
          "Content-Type": "application/json",
          },
          withCredentials: true, // 쿠키 포함
          });
          setUsers(response.data); // 데이터 배열로 설정
        } catch (error) {
         
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
  if (searchType === "userId") return user.userId.toString().includes(searchQuery);
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

  //간단삭제
  const handleDelete = (userId) => {
    setUsers(users.filter((user) => user.userId !== userId));
  };
  //간단수정
  const handleEdit = (user) => {
    setEditingUserId(user.userId);
    setEditedUser(user);
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setEditedUser((prev) => ({ ...prev, [name]: value }));
  };

  const handleSave = () => {
    setUsers((prevUsers) =>
      prevUsers.map((user) =>
        user.userId === editingUserId ? { ...user, ...editedUser } : user
      )
    );
    setEditingUserId(null);
    setEditedUser({});
  };

  return (
    <>
    <Helmet>
        <title>메타러닝 회원관리</title>
      </Helmet>
    <div className={styles.tableContainer}>
      
    {/* 검색창 */}
    <div className={styles.searchBar}>
        <div className={styles.searchButton}>실시간 검색</div>
        <select
          value={searchType}
          onChange={(e) => setSearchType(e.target.value)}
          className={styles.searchSelect}
        >
          <option value="">전체</option>
          <option value="userId">ID</option>
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
          <th>수정</th>
          <th>삭제</th>
        </tr>
      </thead>
      <tbody>
        {currentUsers.map((user,index) => (
          <tr key={user.userId}>
            <td>{startIndex + index + 1}</td>

            <td>
            {editingUserId === user.userId ? (
                  <input
                    type="text"
                    name="email"
                    value={editedUser.userEmail || user.userEmail}
                    onChange={handleChange}
                    className={styles.editInput}
                  />
                ) : (
                  user.userEmail
                )}
            </td>

            <td>
            {editingUserId === user.userId ? (
                  <input
                    type="text"
                    name="name"
                    value={editedUser.name || user.name}
                    onChange={handleChange}
                    className={styles.editInput}
                  />
                ) : (
                  user.name
                )}
            </td>

            <td>
            {editingUserId === user.userId ? (
              <select
                name="userGender"
                value={editedUser.userGender || user.userGender}
                onChange={handleChange}
                className={styles.editSelect}
              >
                <option value="M">남성</option>
                <option value="F">여성</option>
              </select>
            ) : (
              genderMap[user.userGender] || "알 수 없음"
            )}
            </td>

            <td>
            {editingUserId === user.userId ? (
              <select
                name="userRole"
                value={editedUser.userRole || user.userRole}
                onChange={handleChange}
                className={styles.editSelect}
              >
                <option value="STUDENT">학생</option>
                <option value="MANAGER">매니저</option>
                <option value="INSTRUCTOR">강사</option>
              </select>
            ) : (
              roleMap[user.userRole] || "알 수 없음"
            )}
            </td>
            <td>
            {editingUserId === user.userId ? (
              <select
                name="userStatus"
                value={editedUser.userStatus || user.userStatus}
                onChange={handleChange}
                className={styles.editSelect}
              >
                <option value="ACTIVE">활동</option>
                <option value="INACTIVE">휴면</option>
                <option value="BANNED">정지</option>
              </select>
            ) : (
              statusMap[user.userStatus] || "알 수 없음"
            )}
            </td>
            <td>{formatDate(user.userCreatedAt)}</td>
            <td>{formatDate(user.userUpdatedAt)}</td>
            <td><a className={styles.godetail} href="detail">상세정보</a></td>
            <td> 
            {editingUserId === user.userId ? (
                    <button className={styles.editButton} onClick={handleSave}>저장</button>
                ) : (
                  <button className={styles.editButton} onClick={() => handleEdit(user)}>수정</button>
                )}
            </td>
            <td>
              <button className={styles.deleteButton} onClick={() => handleDelete(user.userId)}>삭제</button>
            </td>
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
    </>
  );
};

export default UserList;