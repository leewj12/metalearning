import React, { useState } from "react";
import styles from "/src/css/admin/UserList.module.css";
import { useLocation } from "react-router-dom";
import dayjs from "dayjs"; //날짜 포매팅 모듈듈

const initialUsers = [
    { userId: 1, userEmail: "chulsoo@example.com", name: "김철수", userGender: "M", userRole: "INSTRUCTOR", userStatus: "ACTIVE", userCreatedAt: "2024.12.05", userUpdatedAt: "2025-01-04 22:00:34.254655" },
    { userId: 2, userEmail: "younghee@example.com", name: "이영희", userGender: "F", userRole: "MANAGER", userStatus: "BANNED", userCreatedAt: "2024.12.05", userUpdatedAt: "2024.12.05" },
    { userId: 3, userEmail: "minjoon@example.com", name: "박민준", userGender: "M", userRole: "STUDENT", userStatus: "INACTIVE", userCreatedAt: "2024.12.05", userUpdatedAt: "2024.12.05" },
    { userId: 4, userEmail: "soojin@example.com", name: "김수진", userGender: "F", userRole: "INSTRUCTOR", userStatus: "ACTIVE", userCreatedAt: "2024.12.06", userUpdatedAt: "2024.12.07" },
    { userId: 5, userEmail: "hyunwoo@example.com", name: "최현우", userGender: "M", userRole: "STUDENT", userStatus: "ACTIVE", userCreatedAt: "2024.12.08", userUpdatedAt: "2024.12.09" },
    { userId: 6, userEmail: "jiho@example.com", name: "이지호", userGender: "M", userRole: "MANAGER", userStatus: "INACTIVE", userCreatedAt: "2024.12.10", userUpdatedAt: "2024.12.10" },
    { userId: 7, userEmail: "yujin@example.com", name: "박유진", userGender: "F", userRole: "STUDENT", userStatus: "ACTIVE", userCreatedAt: "2024.12.11", userUpdatedAt: "2024.12.11" },
    { userId: 8, userEmail: "seungwoo@example.com", name: "김승우", userGender: "M", userRole: "STUDENT", userStatus: "BANNED", userCreatedAt: "2024.12.12", userUpdatedAt: "2024.12.12" },
    { userId: 9, userEmail: "eunji@example.com", name: "최은지", userGender: "F", userRole: "MANAGER", userStatus: "ACTIVE", userCreatedAt: "2024.12.13", userUpdatedAt: "2024.12.14" },
    { userId: 10, userEmail: "taehyun@example.com", name: "조태현", userGender: "M", userRole: "STUDENT", userStatus: "INACTIVE", userCreatedAt: "2024.12.15", userUpdatedAt: "2024.12.16" },
    { userId: 11, userEmail: "sumin@example.com", name: "한수민", userGender: "F", userRole: "INSTRUCTOR", userStatus: "BANNED", userCreatedAt: "2024.12.17", userUpdatedAt: "2024.12.17" },
    { userId: 12, userEmail: "jongwoo@example.com", name: "이종우", userGender: "M", userRole: "STUDENT", userStatus: "ACTIVE", userCreatedAt: "2024.12.18", userUpdatedAt: "2024.12.19" },
    { userId: 13, userEmail: "hyejin@example.com", name: "김혜진", userGender: "F", userRole: "MANAGER", userStatus: "ACTIVE", userCreatedAt: "2024.12.20", userUpdatedAt: "2024.12.21" },
    { userId: 14, userEmail: "seungho@example.com", name: "박승호", userGender: "M", userRole: "STUDENT", userStatus: "INACTIVE", userCreatedAt: "2024.12.22", userUpdatedAt: "2024.12.22" },
    { userId: 15, userEmail: "ara@example.com", name: "윤아라", userGender: "F", userRole: "STUDENT", userStatus: "ACTIVE", userCreatedAt: "2024.12.23", userUpdatedAt: "2024.12.23" },
    { userId: 16, userEmail: "hyeonwoo@example.com", name: "김현우", userGender: "M", userRole: "INSTRUCTOR", userStatus: "INACTIVE", userCreatedAt: "2024.12.24", userUpdatedAt: "2024.12.24" },
    { userId: 17, userEmail: "jeongmin@example.com", name: "이정민", userGender: "F", userRole: "STUDENT", userStatus: "BANNED", userCreatedAt: "2024.12.25", userUpdatedAtt: "2024.12.25" },
    { userId: 18, userEmail: "jiwoo@example.com", name: "박지우", userGender: "M", userRole: "MANAGER", userStatus: "ACTIVE", userCreatedAt: "2024.12.26", userUpdatedAt: "2024.12.26" },
    { userId: 19, userEmail: "sungmin@example.com", name: "최성민", userGender: "M", userRole: "STUDENT", userStatus: "INACTIVE", userCreatedAt: "2024.12.27", userUpdatedAt: "2024.12.27" },
    { userId: 20, userEmail: "minji@example.com", name: "윤민지", userGender: "F", userRole: "MANAGER", userStatus: "ACTIVE", userCreatedAt: "2024.12.28", userUpdatedAt: "2024.12.28" },
    { userId: 21, userEmail: "yuna@example.com", name: "강유나", userGender: "F", userRole: "INSTRUCTOR", userStatus: "BANNED", userCreatedAt: "2024.12.29", userUpdatedAt: "2024.12.29" },
    { userId: 22, userEmail: "woojin@example.com", name: "정우진", userGender: "M", userRole: "INSTRUCTOR", userStatus: "ACTIVE", userCreatedAt: "2024.12.30", userUpdatedAt: "2024.12.30" },
    { userId: 23, userEmail: "jiyeon@example.com", name: "김지연", userGender: "F", userRole: "STUDENT", userStatus: "INACTIVE", userCreatedAt: "2024.12.31", userUpdatedAt: "2024.12.31" }

];

const UserList = () => {
  const location = useLocation();
  const searchParams = new URLSearchParams(location.search);
  const roleFilter = searchParams.get("userRole"); // "STUDENT", "TEACHER", "MANAGER" 등

  const [users, setUsers] = useState(initialUsers);
  const [currentPage, setCurrentPage] = useState(1);
  const usersPerPage = 10; //한번에 보여줄 목록 갯수수
  const [searchType, setSearchType] = useState(""); // 검색 조건
  const [searchQuery, setSearchQuery] = useState(""); // 검색어
  const [editingUserId, setEditingUserId] = useState(null); // 현재 수정 중인 사용자 ID
  const [editedUser, setEditedUser] = useState({}); // 수정 중인 사용자 데이터

  const formatDate = (isoString) => dayjs(isoString).format('YYYY-MM-DD');
 
  // 필터링된 사용자 목록
  const filteredUsers = users.filter((user) => {
    // 검색 조건에 따른 필터링
    if (searchType === "userId") return user.userId.toString().includes(searchQuery);
    if (searchType === "userEmail") return user.userEmail.toLowerCase().includes(searchQuery.toLowerCase());
    if (searchType === "name") return user.name.toLowerCase().includes(searchQuery.toLowerCase());
    if (searchType === "userGender") return user.userGender.toLowerCase().includes(searchQuery.toLowerCase());
    if (searchType === "userRole") return user.userRole.toLowerCase().includes(searchQuery.toLowerCase());
    if (searchType === "userStatus") return user.userStatus.toLowerCase().includes(searchQuery.toLowerCase());
    return true; // 전체 보기
  }).filter((user) => {
    // URL 기반 필터 (roleFilter)
    if (!roleFilter) return true;
    return user.role === roleFilter;
  });
 
  //간단삭제
  const handleDelete = (userId) => {
    setUsers(users.filter((user) => user.userId !== userId));
  };
  // 페이지네이션 계산
  const totalPages = Math.ceil(filteredUsers.length / usersPerPage);
  const indexOfLastUser = currentPage * usersPerPage;
  const indexOfFirstUser = indexOfLastUser - usersPerPage;
  const currentUsers = filteredUsers.slice(indexOfFirstUser, indexOfLastUser);

  const handlePageChange = (pageNumber) => {
    setCurrentPage(pageNumber);
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
    <div className={styles.tableContainer}>
    <h2>{roleFilter ? `${roleFilter} 관리` : "전체 회원 관리"}</h2>
    {/* 검색창 */}
    <div className={styles.searchBar}>
        검색하기
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
        {currentUsers.map((user) => (
          <tr key={user.userId}>
            <td>{user.userId}</td>
            <td>
            {editingUserId === user.userId ? (
                  <input
                    type="text"
                    name="email"
                    value={editedUser.userEmail || user.userEmail}
                    onChange={handleChange}
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
                  />
                ) : (
                  user.name
                )}
            </td>
            <td>
              {user.userGender === "M" ? "남성"
              : user.userGender === "F"? "여성"
              : "알 수 없음"}
            </td>
            
            <td>{user.userRole === "INSTRUCTOR" ? "강사"
                :user.userRole === "MANAGER" ? "매니저"
                :user.userRole === "STUDENT" ? "학생"
                : "알수없음"}</td>
            <td>{user.userStatus === "INACTIVE" ? "휴면"
                :user.userStatus === "BANNED" ? "정지"
                :user.userStatus === "ACTIVE" ? "활동"
                :"알수없음"}</td>
            <td>{formatDate(user.userCreatedAt)}</td>
            <td>{formatDate(user.userUpdatedAt)}</td>
            <td><a href="detail">상세정보</a></td>
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

    {/* 페이지네이션 */}
    <div className={styles.pagination}>
      {[...Array(totalPages).keys()].map((page) => (
        <button
          key={page + 1}
          className={currentPage === page + 1 ? "active" : ""}
          onClick={() => handlePageChange(page + 1)}
        >
          {page + 1}
        </button>
      ))}
    </div>
  </div>
  );
};

export default UserList;