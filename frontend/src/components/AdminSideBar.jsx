import React , { useState } from "react";
import styles from "../css/admin/AdminSideBar.module.css";

const AdminSideBar = () => {
  const [showSubMenu, setShowSubMenu] = useState(false);

  return (
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
          <a className={`${styles.link} sidebar-link`} href="/admin/mypage">
          <i className="bi bi-person-circle me-2"></i> {/* 개인정보 아이콘 */}
          개인정보
          </a>

         {/* 통계관리 섹션 
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
                전체 회원 증감율
              </a>
              <a href="#" className={styles.subLink}>
                학생 증감율
              </a>
              <a href="#" className={styles.subLink}>
                강사 증감율 
              </a>
              <a href="#" className={styles.subLink}>
                강의 증감율
              </a>
            </div>
          )}
          </div>
          */}
          
          <div className={`${styles.link} ${styles.dropdown}`}
             onMouseEnter={() => setShowSubMenu(true)}   // 마우스를 올리면 하위 메뉴 표시
             onMouseLeave={() => setShowSubMenu(false)}  // 마우스를 떼면 하위 메뉴 숨김
          >
          <i className="bi bi-people me-2"></i> {/* 회원관리 아이콘 */}
          <a className={`${styles.hlink} sidebar-link`} href="/view/admin/users/list">
          회원관리
          </a>
          {showSubMenu && (
            <div className={styles.subMenu}>
              <a href="/view/admin/users/list" className={styles.subLink}>
                전체관리
              </a>
              <a href="/view/admin/users/list?userRole=STUDENT" className={styles.subLink}>
                학생관리
              </a>
              <a href="/view/admin/users/list?userRole=INSTRUCTOR" className={styles.subLink}>
                강사관리
              </a>
              <a href="/view/admin/users/list?userRole=MANAGER" className={styles.subLink}>
                매니저관리
              </a>
              <a href="/admin/instr/list" className={styles.subLink}>
                강사신청조회
              </a>
            </div>
          )}
          </div>

          <div className={`${styles.link} ${styles.dropdown}`}
             onMouseEnter={() => setShowSubMenu(true)}   // 마우스를 올리면 하위 메뉴 표시
             onMouseLeave={() => setShowSubMenu(false)}  // 마우스를 떼면 하위 메뉴 숨김
          >
          <i className="bi bi-book"></i> {/* 국비관리 아이콘 */}
          <a className={`${styles.hlink} sidebar-link`} href="/view/admin/KDT/list">
            국비관리
          </a>
          {showSubMenu && (
            <div className={styles.subMenu}>
              <a href="/admin/KDT/accountmanagement" className={styles.subLink}>
                국비계정생성
              </a>
              <a href="/admin/KDT/course" className={styles.subLink}>
                국비과정등록
              </a>
              <a href="/admin/KDT/session" className={styles.subLink}>
                국비회차등록
              </a>
              <a href="/view/admin/KDT/list" className={styles.subLink}>
                국비과정 목록 조회
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

          <div className={`${styles.link} ${styles.dropdown}`}
             onMouseEnter={() => setShowSubMenu(true)}   // 마우스를 올리면 하위 메뉴 표시
             onMouseLeave={() => setShowSubMenu(false)}  // 마우스를 떼면 하위 메뉴 숨김
          >
          <i className="bi bi-person-workspace"></i> {/* 강의관리 아이콘 */}
          <a className={`${styles.hlink} sidebar-link`} href="/admin/course/list/approved">
          강의관리
          </a>
          {showSubMenu && (
            <div className={styles.subMenu}>
              <a href="/admin/course" className={styles.subLink}>
                강의등록
              </a>
              <a href="/admin/course/list/approved" className={styles.subLink}>
                강의목록조회  
              </a>
              <a href="/admin/course/list/pending" className={styles.subLink}>
                강의 승인 대기 목록
              </a>
            </div>
          )}
          </div>
          
          {/* 리뷰관리 섹션 
          <div className={`${styles.link} ${styles.dropdown}`}
            onMouseEnter={() => setShowSubMenu(true)}   // 마우스를 올리면 하위 메뉴 표시
            onMouseLeave={() => setShowSubMenu(false)}  // 마우스를 떼면 하위 메뉴 숨김
          >
          <i className="bi bi-star"></i>  // 강의관리 아이콘 
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

          <a className={`${styles.link} sidebar-link`} href="/admin/pay/list">
          <i className="bi bi-credit-card"></i> {/* 결제관리 아이콘 */}
          결제관리
          </a>
        </div>
      </div>
    </div>
  );
};

export default AdminSideBar;
