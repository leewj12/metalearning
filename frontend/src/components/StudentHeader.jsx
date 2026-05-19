import React, {useEffect, useState } from "react";
import axios from "axios";
import styles from "../css/student/StudentHeader.module.css";

const StudentHeader = () => {

    const [userInfo, setUserInfo] = useState({ email: "", role: "" }); // 사용자 이름 상태
    const roleMap = {
      STUDENT: "학생",
    };

    useEffect(() => {
      const fetchUserInfo = async () => {
        try {
          const response = await axios.get("/api/student/user/profile", {
            headers: { "Content-Type": "application/json" },
            withCredentials: true,
          });
    
          if (response.status === 200) {
            setUserInfo({
              name: response.data.name || "알 수 없음",
              email: response.data.userEmail || "이메일 없음",
              role: roleMap[response.data.userRole] || "권한 없음",
            });
          } else {
            setErrorMessage("사용자 정보를 불러오지 못했습니다.");
          }
        } catch (error) {
          console.error("사용자 정보 요청 중 에러 발생:", error);
          setErrorMessage("사용자 정보를 불러오는 중 오류가 발생했습니다.");
        }
      };
    
      fetchUserInfo();
    }, []);

    return (
    <nav className={`${styles.navbar} navbar`}>
      <div className={styles.leftSection}>
      
    
      </div>
      
      <div className={styles.rightSection}>
        <span className={styles.userName}>환영합니다! {userInfo.name} ( {userInfo.role} ) <i className="bi bi-person-fill text-primary"></i> 로그인중 </span> 
        <button className={`${styles.logoutButton}`} onClick={() => window.location.href="/logout"} >
        <i className="bi bi-box-arrow-right"></i> 로그아웃
        </button>
      </div>
    </nav>
  );
};

export default StudentHeader;
