import React , { useState } from "react";
import styles from "../css/instr/InstrSideBar.module.css";

const InstrSideBar = () => {
  const [showSubMenu, setShowSubMenu] = useState(false);

  return (
    <div className={`${styles.sidebar}`}>
      <div className="sb-sidenav-menu">     
        {/* Interface Section */}
        <div className={`${styles.sidebarSection}`}>
           
          {/* Home */}
          <a className={`${styles.link} sidebar-link`} href="/">
          <i className="bi bi-house-door me-2"></i> {/* Home 아이콘 */}
          Home
          </a>

          {/* 개인정보 */}
          <a className={`${styles.link} sidebar-link`} href="/instr/mypage">
          <i className="bi bi-person-circle me-2"></i> {/* 개인정보 아이콘 */}
          개인정보
          </a>

          {/* 통계관리 
          <div className={`${styles.link} ${styles.dropdown}`}
             onMouseEnter={() => setShowSubMenu(true)}   // 마우스를 올리면 하위 메뉴 표시
             onMouseLeave={() => setShowSubMenu(false)}  // 마우스를 떼면 하위 메뉴 숨김
          >
          <i className="bi bi-bar-chart me-2"></i>
          <a className={`${styles.hlink} sidebar-link`} href="#">
          통계관리
          </a>
          {showSubMenu && (
            <div className={styles.subMenu}>
              <a href="#" className={styles.subLink}>
                학생증감율
              </a>
              <a href="#" className={styles.subLink}>
                강의증감율
              </a>
              <a href="#" className={styles.subLink}>
                결제 통계
              </a>
            </div>
          )}
          </div>
          */}
          
        
          {/* 개인정보 */}
          <a className={`${styles.link} sidebar-link`} href="/view/instr/KDT/list">
          <i className="bi bi-book"></i> {/* 개인정보 아이콘 */}
          국비 과정 조회
          </a>
          

          <div className={`${styles.link} ${styles.dropdown}`}
             onMouseEnter={() => setShowSubMenu(true)}   // 마우스를 올리면 하위 메뉴 표시
             onMouseLeave={() => setShowSubMenu(false)}  // 마우스를 떼면 하위 메뉴 숨김
          >
          <i className="bi bi-person-workspace"></i> {/* 강의관리 아이콘 */}
          <a className={`${styles.hlink} sidebar-link`} href="/instr/course/list/approved">
          강의관리
          </a>
          {showSubMenu && (
            <div className={styles.subMenu}>
              <a href="/instr/course" className={styles.subLink}>
                강의등록
              </a>
              <a href="/instr/course/list/approved" className={styles.subLink}>
                강의목록조회  
              </a>
            </div>
          )}
          </div>

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

          {/* 결제관리
          <a className={`${styles.link} sidebar-link`} href="#">
          <i className="bi bi-credit-card"></i>
          결제관리
          </a>
          */}
          
        </div>
      </div>
    </div>
  );
};

export default InstrSideBar;
