import React, { useEffect, useState } from "react";
import axios from "axios";
import { Link } from "react-router-dom";
import styles from "/src/css/manager/ManagerCourseList.module.css";
import { Helmet } from "react-helmet-async";

const ManagerCourseList = () => {
  const [courses, setCourses] = useState([]); // 과정 데이터 상태 관리
  const [errorMessage, setErrorMessage] = useState(""); // 오류 메시지 상태 관리
 

  // 페이지 로드 시 데이터 가져오기
  useEffect(() => {
    const fetchCourses = async () => {
      try {
        const response = await axios.get("/api/manager/KDT/list", {
          headers: {
            "Content-Type": "application/json",
          },
          withCredentials: true, // 쿠키 포함
        });
        setCourses(response.data); // 과정 데이터 설정
      } catch (error) {
        console.log("Test1",error.response.data.message);
        console.log("Test2",error.response.status);
        if (error.response) {
          if (error.response.status === 403 && error.response.data.message === "회차 정보가 없습니다.") {
            // 403 에러 발생 시 알림 표시 후 리다이렉트
            setCourses([]);
          }
        }else {
          // 네트워크 또는 기타 문제
          setErrorMessage("서버와의 연결에 문제가 발생했습니다.");
        }
      }
    };

    fetchCourses();
  }, []);

  return (
    <div className={styles.courselistcontainer}>
      <Helmet>
        <title>메타러닝 국비 과정 조회</title>
      </Helmet>
    <h1 className={styles.listheader}>국비 과정 목록</h1>
    {errorMessage && <div className={styles.errorMessage}>{errorMessage}</div>}

    
    <table className={styles.courselisttable}>
      <thead>
        <tr>
          <th scope="col">번호</th>
          <th scope="col">과정명</th>
          <th scope="col">상태</th>
          <th scope="col">종류</th>
          <th scope="col">상세보기</th>
        </tr>
      </thead>
      <tbody>
      {courses.length > 0 ? (
        courses.map((course , index) => (
          <tr key={course.kdtCourseId}>
            <td>{index + 1}</td>
            <td>
              <Link to={`/manager/KDT/course/${course.kdtCourseId}`}>
                {course.kdtCourseTitle || "제목 없음"}
              </Link>
            </td>
            <td>{course.kdtCourseStatus ? "활성" : "비활성"}</td>
            <td>{course.kdtCourseType || "정보 없음"}</td>
            <td>
              <Link to={`/manager/KDT/course/${course.kdtCourseId}`}
                className={`${styles.btn} ${styles.btnWarning}`}
              >
                상세보기
              </Link>
            </td>
          </tr>
        ))
      
    ):(
      <tr>
        <td colSpan="5" className={styles.noResults}>
          등록된 과정이 없습니다.
        </td>
      </tr>
    )}
      </tbody>
    </table>
  </div>
  );
};

export default ManagerCourseList;