import React from "react";
import styles from "../css/Pagination.module.css";

const Pagination = ({
  currentPage,
  totalPages,
  pagesPerGroup = 5,
  onPageChange,
}) => {
   // 👇 페이지가 없거나 1개 이하라면 페이징을 안 보이도록 설정
   if (totalPages < 1) return null;
  // 현재 그룹 계산
  const currentGroup = Math.ceil(currentPage / pagesPerGroup);
  const groupStartPage = (currentGroup - 1) * pagesPerGroup + 1;
  const groupEndPage = Math.min(groupStartPage + pagesPerGroup - 1, totalPages);
  const totalGroups = Math.ceil(totalPages / pagesPerGroup);

  // 그룹 이동 핸들러
  const handlePreviousGroup = () => {
    if (currentGroup > 1) {
      const newGroup = currentGroup - 1;
      onPageChange(newGroup * pagesPerGroup);
    }
  };

  const handleNextGroup = () => {
    if (currentGroup < totalGroups) {
      const newGroup = currentGroup + 1;
      onPageChange((newGroup - 1) * pagesPerGroup + 1);
    }
  };

  return (
    <div className={styles.pagination}>
      {/* 처음 버튼 */}
      <button
        onClick={() => onPageChange(1)}
        disabled={currentPage === 1}
        className={styles.firstButton}
      >
        처음
      </button>

      {/* 이전 그룹 버튼 */}
      <button
        onClick={handlePreviousGroup}
        disabled={currentGroup === 1}
        className={`${styles.groupNav} ${styles.previousButton}`}
      >
        {"<<"}
      </button>

      {/* 페이지 번호 */}
      {Array.from({ length: groupEndPage - groupStartPage + 1 }, (_, i) => {
        const page = groupStartPage + i;
        return (
          <button
            key={page}
            onClick={() => onPageChange(page)}
            className={`${styles.pageButton} ${
              currentPage === page ? styles.activePage : ""
            }`}
          >
            {page}
          </button>
        );
      })}

      {/* 다음 그룹 버튼 */}
      <button
        onClick={handleNextGroup}
        disabled={currentGroup === totalGroups}
        className={`${styles.groupNav} ${styles.nextButton}`}
      >
        {">>"}
      </button>

      {/* 끝 버튼 */}
      <button
        onClick={() => onPageChange(totalPages)}
        disabled={currentPage === totalPages}
        className={styles.lastButton}
      >
        끝
      </button>
    </div>
  );
};

export default Pagination;
