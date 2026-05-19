package com.Meta_learning.board.boardservice;

import com.Meta_learning.KDT.KDTDTO.KDTBoardDTO.KDTBoardDTO;
import com.Meta_learning.KDT.KDTDTO.KDTBoardDTO.KDTBoardFileDTO;
import com.Meta_learning.KDT.KDTDTO.KDTBoardDTO.KDTBoardTitleDTO;
import com.Meta_learning.KDT.KDTDTO.KDTBoardDTO.KDTBoardviewDTO;
import com.Meta_learning.KDT.KDTentity.KDTBoardEntity.KDTBoardCategory;
import com.Meta_learning.board.boardDTO.BoardDTO;
import com.Meta_learning.board.boardDTO.BoardFileDTO;
import com.Meta_learning.board.boardDTO.BoardTitleDTO;
import com.Meta_learning.board.boardDTO.BoardviewDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BoardService {

    //게시판 글 저장하는 메서드임
    void writeEmployReview(BoardDTO boardDTO, List<BoardFileDTO> fileList, MultipartFile[] files);

    boolean kdtboardsave(KDTBoardDTO kdtboardDTO, List<KDTBoardFileDTO> fileList, MultipartFile[] files);

    // 간단보기 페이징처리 예정임
    List<BoardTitleDTO> boardTitleReview();


    //취업자 후기 자료 찾는 메서드임
    List<BoardviewDTO> boardReview();


    Page<BoardTitleDTO> getPagedBoardTitleReview(Pageable pageable);

    // 페이징 + 검색어 처리 메서드
    Page<KDTBoardTitleDTO> getPagedBoardmaterial(Long sessionId, String searchValue, String searchType, Pageable pageable);

    // 게시글 상세 보기 (단일 게시글 반환)
    KDTBoardviewDTO getBoardDetails(Long sessionId, Long boardId, KDTBoardCategory category);

    //게시글 삭제하는 메서드
    boolean boardDelete(Long postId);

    //조회수 증가하는 카운트 메서드
    void reviewcount(Long boardId);


}



