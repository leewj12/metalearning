import React from "react";
import { useNavigate } from "react-router-dom";
import PropTypes from "prop-types"; // PropTypes를 사용하여 prop 검증
import styles from "../css/BackButton.module.css"; // 스타일 모듈
import { FaArrowLeft } from "react-icons/fa"; // 뒤로가기 아이콘


const BackButton = ({ label = "뒤로가기", className }) => {
  const navigate = useNavigate();

  // 뒤로가기 핸들러
  const handleBack = () => {
    navigate(-1); // 한 단계 이전 페이지로 이동
  };

  return (
    <button
      className={`${styles.backButton} ${className || ""}`}
      onClick={handleBack}
    >   
    <FaArrowLeft className={styles.icon} /> {/* 아이콘 추가 */}
    {label}
    </button>
  );
};

// PropTypes로 prop 검증 설정
BackButton.propTypes = {
  label: PropTypes.string, // 버튼에 표시될 텍스트
  className: PropTypes.string, // 추가 CSS 클래스
};

export default BackButton;
