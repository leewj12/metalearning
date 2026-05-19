import React from "react";
import "../css/NotFound.css";

function NotFound() {
  return (
    <div className="not-found-container">
      <h1 className="error-title">해당 페이지를 찾지 못했습니다.</h1>
      <p className="error-subtitle">
        주소가 잘못되었거나 더 이상 제공되지 않는 페이지입니다.
      </p>

      {/* GIF 이미지 */}
      <div className="cat-gif-container">
        <img
          src=""
          alt="Coding Cat"
          className="cat-gif"
        />
      </div>

      <a href="/" className="back-to-home">
        메인페이지로 이동 🏠
      </a>
    </div>
  );
}

export default NotFound;
