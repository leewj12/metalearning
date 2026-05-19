import React, {useEffect, useState } from "react";
import styles from "/src/css/manager/ManagerPartList.module.css";
import { useParams } from "react-router-dom";
import dayjs from "dayjs"; //날짜 포매팅 모듈듈
import axios from "axios";
import Pagination from "/src/components/Pagination";
import BackButton from "/src/components/BackButton";
import { Helmet } from "react-helmet-async";
 
const PartList = () => {
  const roleMap = {STUDENT: "학생",MANAGER: "매니저",INSTRUCTOR: "강사",ADMIN: "관리자"};
  const genderMap = {M: "남성",F: "여성",};
  const statusMap = {WAITING: "대기",IN_PROGRESS: "수료중",DISMISSED: "제적",COMPLETED: "수료완료"};
  const EmpMap = {false: "구직중",true: "취직중",};


  const { kdtSessionId } = useParams(); // URL에서 kdtSessionId 가져오기
  const [users, setUsers] = useState([]);

  const [currentPage, setCurrentPage] = useState(1);
  const [usersPerPage, setUsersPerPage] = useState(5);
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
        const response = await axios.get(`/api/manager/KDT/${kdtSessionId}/part/list`,{ // API 엔드포인트 수정
          headers: {
          "Content-Type": "application/json",
          },
          withCredentials: true, // 쿠키 포함
          });
          console.log(response.data);  // API 응답 데이터 구조 확인
          setUsers(response.data); // 데이터 배열로 설정  
        } catch (error) {
          console.error("사용자 데이터를 불러오는 중 오류 발생:", error);
        }
      };

    fetchUsers();
  }, []);

  // 필터링된 사용자 목록
  const filteredUsers = users.filter((user) => {
    if (searchType === "userRole") {
      const displayRole = roleMap[user.userRole] || ""; // 매핑된 한글 값
      return displayRole.includes(searchQuery);
    }

    if (searchType === "userGender") {
      const displayGender = genderMap[user.userGender] || ""; // 매핑된 한글 값
      return displayGender.includes(searchQuery);
    }

    if (searchType === "kdtPartStatus") {
      const displayStatus = statusMap[user.kdtPartStatus] || ""; // 매핑된 한글 값
      return displayStatus.includes(searchQuery);
    }

    if (searchType === "kdtPartEmp") {
      const displayEmp = EmpMap[user.kdtPartEmp] || ""; // 매핑된 한글 값
      return displayEmp.includes(searchQuery);
    }

    // 다른 검색 조건은 원래 데이터 기준으로 필터링
    if (searchType === "userId") return user.userId.toString().includes(searchQuery);
    if (searchType === "userEmail") return user.userEmail.toLowerCase().includes(searchQuery.toLowerCase());
    if (searchType === "name") return user.name.toLowerCase().includes(searchQuery.toLowerCase());

    return true; // 전체 보기
  });


  const totalPages = Math.ceil(filteredUsers.length / usersPerPage);
  const startIndex = (currentPage - 1) * usersPerPage;
  const currentUsers = filteredUsers.slice(
    startIndex,
    startIndex + usersPerPage
  );

  // 보여줄 목록 수
  const handleUsersPerPageChange = (e) => {
    setUsersPerPage(Number(e.target.value)); // 드롭다운에서 선택한 값을 상태로 설정
    setCurrentPage(1); // 페이지 번호를 초기화
  };

  // 참가자 삭제 함수
  const handleDelete = async (kdtPartId) => {
    const confirmDelete = window.confirm("정말로 이 참가자를 명단에서 삭제하시겠습니까?");
    if (!confirmDelete) return;

    try {
      const response = await axios.delete(
        `/api/manager/KDT/${kdtSessionId}/part/delete/${kdtPartId}`,
        {
          headers: {
            "Content-Type": "application/json",
          },
          withCredentials: true,
        }
      );

      if (response.status === 200) {
        alert(response.data.message || "참가자가 삭제되었습니다.");
        // 삭제된 참가자를 제외한 새 상태 설정
        setUsers((prevUsers) => prevUsers.filter((user) => user.kdtPartId !== kdtPartId));
      } else {
        alert(response.data.message || "참가자를 삭제할 수 없습니다.");
      }
    } catch (error) {

      alert("이미 정보가 있는 학생은 삭제가 불가합니다.");
    }
  };

   // 참가자 수정 함수
   const handleSave = async () => {
    try {
      const response = await axios.put(
        `/api/manager/KDT/${kdtSessionId}/part/update/${editingUserId}`,
        {
          newStatus: editedUser.kdtPartStatus,
          newEmploymentStatus: editedUser.kdtPartEmp,
        },
        {
          headers: {
            "Content-Type": "application/json",
          },
          withCredentials: true,
        }
      );

      if (response.status === 200) {
        alert(response.data.message || "참가자 상태가 수정되었습니다.");
        setUsers((prevUsers) =>
          prevUsers.map((user) =>
            user.kdtPartId === editingUserId ? { ...user, ...editedUser } : user
          )
        );
        setEditingUserId(null);
        setEditedUser({});
      } else {
        alert(response.data.message || "수정을 실패했습니다.");
      }
    } catch (error) {
      console.error("수정 요청 중 오류 발생:", error);
      alert("수정 요청 중 오류가 발생했습니다.");
    }
  };

  return (
    <div className={styles.tableContainer}>
    <Helmet>
      <title>메타러닝 회차 참가자 명단</title>
    </Helmet>
    <h1>참가자 명단</h1>
    {/* 검색창 */}
    <div className={styles.searchBar}>
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
          <option value="kdtPartStatus">상태</option>
          <option value="kdtPartEmp">취업 여부</option>
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
            <th>생년월일</th>
            <th>전화번호</th>
            <th>등급</th>
            <th>상태</th>
            <th>취업 여부</th>          
            <th>상세정보</th>
            <th>수정</th>
            <th>삭제</th>
        </tr>
      </thead>
      <tbody>
      {currentUsers.length>0 ?(
        currentUsers.map((user, index) => (
            <tr key={user.userId}>
              <td>{startIndex + index + 1}</td>
              <td>{user.userEmail}</td>
              <td>{user.name}</td>
              <td>{genderMap[user.userGender] || "알 수 없음"}</td>
              <td>{formatDate(user.userBirth)}</td>
              <td>{user.userPhone}</td>
              <td>{roleMap[user.userRole] || "알 수 없음"}</td>
              <td>
                {editingUserId === user.kdtPartId ? (
                  <select
                    value={editedUser.kdtPartStatus || user.kdtPartStatus}
                    onChange={(e) =>
                      setEditedUser((prev) => ({
                        ...prev,
                        kdtPartStatus: e.target.value,
                      }))
                    }
                  >
                    {Object.entries(statusMap).map(([key, value]) => (
                      <option key={key} value={key}>
                        {value}
                      </option>
                    ))}
                  </select>
                ) : (
                  statusMap[user.kdtPartStatus] || "알 수 없음"
                )}
              </td>
              <td>
                {editingUserId === user.kdtPartId ? (
                  <select
                    value={editedUser.kdtPartEmp || user.kdtPartEmp}
                    onChange={(e) =>
                      setEditedUser((prev) => ({
                        ...prev,
                        kdtPartEmp: e.target.value === "true",
                      }))
                    }
                  >
                    {Object.entries(EmpMap).map(([key, value]) => (
                      <option key={key} value={key}>
                        {value}
                      </option>
                    ))}
                  </select>
                ) : (
                  EmpMap[user.kdtPartEmp] || "알 수 없음"
                )}
              </td>
              <td><a href={`/managers/users/${user.userId}`}>상세보기</a></td>
              <td>
                {editingUserId === user.kdtPartId ? (
                  <button className={styles.saveButton} onClick={handleSave}>
                    저장
                  </button>
                ) : (
                  <button
                    className={styles.editButton}
                    onClick={() => {
                      setEditingUserId(user.kdtPartId);
                      setEditedUser(user);
                    }}
                  >
                    수정
                  </button>
                )}
              </td>
              <td>
              <button
                  className={styles.deleteButton}
                  onClick={() => handleDelete(user.kdtPartId)}
                >
                  삭제
                </button>
              </td>
              
            </tr>
          ))
        ):(
          <tr>
              <td colSpan="12" className={styles.noResults}>
                검색 결과가 없습니다.
              </td>
          </tr>
        )}
      </tbody>
    </table>
    {/* Pagination 컴포넌트 */}
    <Pagination
        currentPage={currentPage}
        totalPages={totalPages}
        onPageChange={setCurrentPage}
        pagesPerGroup={5}
      />

      <div className={styles.buttonContainer}>
        <a
          href={`/managers/KDT/${kdtSessionId}/part`}
          className={styles.createButton}
        >
          수강생등록
        </a>
        <BackButton label="Back" />
      </div>

    </div>
  
  );
};

export default PartList;