import React, { useEffect, useState } from "react";
import axios from "axios";
import { Link } from "react-router-dom";
import styles from "/src/css/admin/CourseList.module.css";
import { Helmet } from "react-helmet-async";

const CourseList = () => {
  const [courses, setCourses] = useState([]); // 과정 데이터 상태 관리
  const [errorMessage, setErrorMessage] = useState(""); // 오류 메시지 상태 관리


  // 페이지 로드 시 데이터 가져오기
  useEffect(() => {
    const fetchCourses = async () => {
      try {
        const response = await axios.get("/api/admin/KDT/list", {
          headers: {
            "Content-Type": "application/json",
          },
          withCredentials: true, // 쿠키 포함
        });

        setCourses(response.data); // 과정 데이터 설정
      } catch (error) {
        setErrorMessage("데이터를 불러오는 데 실패했습니다.");
      }
    };

    fetchCourses();
  }, []);

  // 날짜 포맷팅 함수
  const formatDate = (dateString) => {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    const hours = date.getHours();
    const minutes = String(date.getMinutes()).padStart(2, "0");
    const ampm = hours >= 12 ? "오후" : "오전";
    const hour12 = hours > 12 ? hours - 12 : hours;

    return `${year}-${month}-${day} ${ampm} ${hour12}:${minutes}`;
  };

  // 과정 삭제 함수
  const deleteCourse = async (courseId) => {
  const confirmDelete = window.confirm("정말로 이 과정을 삭제하시겠습니까?");
  if (!confirmDelete) return;

  try {
    const response = await axios.delete(`/api/admin/KDT/course/delete/${courseId}`, {
      headers: {
        "Content-Type": "application/json",
      },
      withCredentials: true,
    });

    if (response.status === 200) {
      alert(response.data.message || "과정이 삭제되었습니다.");
      // 삭제된 과정을 제외한 새 상태 설정
      setCourses((prevCourses) =>
        prevCourses.filter((course) => course.kdtCourseId !== courseId)
      );
    } else {
      alert(response.data.message || "과정을 삭제할 수 없습니다.");
    }
  } catch (error) {
    alert("회차가 있는 경우에는 삭제가 불가합니다.");
  }
};

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
          <th scope="col">생성일</th>
          <th scope="col">수정일</th>
          <th scope="col">상세보기</th>
          <th scope="col">수정</th>
          <th scope="col">삭제</th>
        </tr>
      </thead>
      <tbody>
        {courses.map((course , index) => (
          <tr key={course.kdtCourseId}>
            <td>{index + 1}</td>
            <td>
              <Link to={`/admin/KDT/course/${course.kdtCourseId}`}>
                {course.kdtCourseTitle || "제목 없음"}
              </Link>
            </td>
            <td>{course.kdtCourseStatus ? "활성" : "비활성"}</td>
            <td>{course.kdtCourseType || "정보 없음"}</td>
            <td>{formatDate(course.kdtCourseCreatedAt)}</td>
            <td>{formatDate(course.kdtCourseUpdatedAt)}</td>
            <td>
              <Link to={`/admin/KDT/course/${course.kdtCourseId}`}
                className={`${styles.btn} ${styles.btnWarning}`}
              >
                상세보기
              </Link>
            </td>
            <td>
              <button
               
                className={styles.editbtn}
              ><a  href={`/admin/KDT/course/update/${course.kdtCourseId}`}>수정</a>
                
              </button>
            </td>
            <td>
              <button
                className={styles.deletebtn}
                onClick={() => deleteCourse(course.kdtCourseId)}
              >
                삭제
              </button>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  </div>
  );
};

export default CourseList;