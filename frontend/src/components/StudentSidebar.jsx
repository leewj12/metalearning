import React , { useEffect,useState } from "react";
import axios from "axios";
import styles from "../css/student/StudentSideBar.module.css";
import ComingSoonToast from "./ComingSoonToast";

const StudentSideBar = () => {
  const [showSubMenu, setShowSubMenu] = useState(false);
  const [kdtsessionId, setKdtsessionId] = useState(null); // kdtsessionId 상태 관리
  const [toastVisible, setToastVisible] = useState(false);

  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const response = await axios.get("/api/student/KDT/sessionlist",{ // API 엔드포인트 수정
          headers: {
          "Content-Type": "application/json",
          },
          withCredentials: true, // 쿠키 포함
          });
          //console.log( "세션아이디 : " , response.data[0].kdtSessionId);
          setKdtsessionId(response.data[0].kdtSessionId); // 데이터 배열로 설정

        } catch (error) {
   
        }
      };

    fetchUsers();
  }, []);

  return (
    <>
    <div className={`${styles.sidebar} sb-sidenav accordion sb-sidenav-dark`}>
      <div className="sb-sidenav-menu">     
        {/* Interface Section */}
        <div className={`${styles.sidebarSection}`}>
           
          {/* Home */}
          <a className={`${styles.link} sidebar-link`} href="/">
          <i className="bi bi-house-door me-2"></i> {/* Home 아이콘 */}
          Home
          </a>

          {/* 개인정보 */}
          <a className={`${styles.link} sidebar-link`} href="/student/mypage">
          <i className="bi bi-person-circle me-2"></i> {/* 개인정보 아이콘 */}
          개인정보
          </a>

          <div className={`${styles.link} ${styles.dropdown}`}
             onMouseEnter={() => setShowSubMenu(true)}   // 마우스를 올리면 하위 메뉴 표시
             onMouseLeave={() => setShowSubMenu(false)}  // 마우스를 떼면 하위 메뉴 숨김
          >
          <i className="bi bi-card-list"></i>  
          <a className={`${styles.hlink} sidebar-link`} href="/student/KDT/list">
          국비 과정 조회
          </a>
          {showSubMenu && (
            <div className={styles.subMenu}>
              <a href={`/view/student/KDT/${kdtsessionId}/att/detail`} className={styles.subLink}>
                출석부
              </a>
              <a href={`/student/KDT/${kdtsessionId}/courseoutline/list`} className={styles.subLink}>
                강의 영상
              </a>
              <a href={`/student/KDT/${kdtsessionId}/board/materiallist`} className={styles.subLink}>
                자료실
              </a>
              <a href={`/student/KDT/${kdtsessionId}/test/list`} className={styles.subLink}>
                시험
              </a>
            </div>
          )}
          </div>

          {/* 게시판관리
          <div className={`${styles.link} ${styles.dropdown}`}
             onMouseEnter={() => setShowSubMenu(true)}   // 마우스를 올리면 하위 메뉴 표시
             onMouseLeave={() => setShowSubMenu(false)}  // 마우스를 떼면 하위 메뉴 숨김
          >
          <i className="bi bi-card-list"></i>  
          <a className={`${styles.hlink} sidebar-link`} href="#">
          게시판관리
          </a>
          {showSubMenu && (
            <div className={styles.subMenu}>
              <a href="#" className={styles.subLink}>
                게시글 작성
              </a>
              <a href="#" className={styles.subLink}>
                게시판 목록 조회
              </a>
            </div>
          )}
          </div>
          */}
          
          {/* 강의목록 */}
          <a className={`${styles.link} sidebar-link`} href="#"
            onClick={e => { e.preventDefault(); setToastVisible(true); }}>
          <i className="bi bi-person-workspace"></i>
          강의 목록 조회
          </a>
          
          
          {/* 리뷰관리
          <div className={`${styles.link} ${styles.dropdown}`}
            onMouseEnter={() => setShowSubMenu(true)}   // 마우스를 올리면 하위 메뉴 표시
            onMouseLeave={() => setShowSubMenu(false)}  // 마우스를 떼면 하위 메뉴 숨김
          >
          <i className="bi bi-star"></i>  
          <a className={`${styles.hlink} sidebar-link`} href="#">
          리뷰관리
          </a>
          {showSubMenu && (
            <div className={styles.subMenu}>
              <a href="#" className={styles.subLink}>
                국비 리뷰 목록 조회
              </a>
              <a href="#" className={styles.subLink}>
                강사 리뷰 목록 조회
              </a>
            </div>
          )}
          </div>
          */}

          <a className={`${styles.link} sidebar-link`} href="/user/cart">
          <i className="bi bi-cart"></i> {/* 결제관리 아이콘 */}
          장바구니
          </a>
          
          {/* 구매내역
          <a className={`${styles.link} sidebar-link`} href="#">
          <i className="bi bi-credit-card"></i>  
          구매 내역
          </a>
          */}
        </div>
      </div>
    </div>
    <ComingSoonToast visible={toastVisible} onHide={() => setToastVisible(false)} />
    </>
  );
};

export default StudentSideBar;
