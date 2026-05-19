package com.Meta_learning.board.boardservice;

import com.Meta_learning.KDT.KDTDTO.KDTBoardDTO.KDTBoardDTO;
import com.Meta_learning.KDT.KDTDTO.KDTBoardDTO.KDTBoardFileDTO;
import com.Meta_learning.KDT.KDTDTO.KDTBoardDTO.KDTBoardTitleDTO;
import com.Meta_learning.KDT.KDTDTO.KDTBoardDTO.KDTBoardviewDTO;
import com.Meta_learning.KDT.KDTentity.KDTBoardEntity.KDTBoardCategory;
import com.Meta_learning.KDT.KDTentity.KDTBoardEntity.KDTBoardEntity;
import com.Meta_learning.KDT.KDTentity.KDTBoardEntity.KDTBoardFileEntity;
import com.Meta_learning.KDT.KDTentity.KDTSessionEntity.KDTSessionEntity;
import com.Meta_learning.KDT.KDTrepository.KDTBoardrepository.KDTBoardFileRepository;
import com.Meta_learning.KDT.KDTrepository.KDTBoardrepository.KDTBoardRepository;
import com.Meta_learning.KDT.KDTrepository.KDTSessionRepository.KDTSessionRepository;
import com.Meta_learning.board.boardDTO.BoardDTO;
import com.Meta_learning.board.boardDTO.BoardFileDTO;
import com.Meta_learning.board.boardDTO.BoardTitleDTO;
import com.Meta_learning.board.boardDTO.BoardviewDTO;
import com.Meta_learning.board.boardentity.BoardCategory;
import com.Meta_learning.board.boardentity.BoardEntity;
import com.Meta_learning.board.boardentity.BoardFileEntity;
import com.Meta_learning.board.boardrepository.BoardFileRepository;
import com.Meta_learning.board.boardrepository.BoardRepository;
import com.Meta_learning.user.userentity.UserEntity;
import com.Meta_learning.user.userrepository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final BoardFileRepository boardFileRepository;
    private final UserRepository userRepository;  // UserRepository 추가
    private final KDTSessionRepository kdtSessionRepository;
    private final KDTBoardRepository kdtBoardRepository;
    private final KDTBoardFileRepository kdtBoardFileRepository;

    //게시판 저장하는 메서드
    @Override
    public void writeEmployReview(BoardDTO boardDTO, List<BoardFileDTO> fileList, MultipartFile[] files) {
        Long userId = boardDTO.getUserId();  // BoardDTO에서 userId 가져오기

        // 1. 실제 UserEntity를 조회하여 가져오기
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 2. BoardEntity 생성
        BoardEntity boardEntity = BoardEntity.builder()
                .userEntity(userEntity)  // 실제 유저 정보 설정
                .boardTitle(boardDTO.getBoardTitle())
                .boardContent(boardDTO.getBoardContent())
                .boardView(0L)  // 조회수 초기값 0L (Long 타입)
                .boardCategory(BoardCategory.valueOf(boardDTO.getBoardCategory()))  // BoardCategory enum으로 변환
                .boardHidden(false)  // 기본값 (숨김 여부)
                .boardAnswer(false)  // 기본값 (답변 여부)
                .build();

        // 3. 게시글 엔티티 저장
        boardEntity = boardRepository.save(boardEntity);

        // 4. 파일 업로드 처리 (여러 파일 처리)
        if (fileList != null && !fileList.isEmpty()) {
            for (BoardFileDTO fileDTO : fileList) {
                try {
                    // 파일을 서버에 저장
                    String fileUUID = fileDTO.getFileUUID();
                    String fileName = fileDTO.getFileName();
                    String fileType = fileDTO.getFileType();
                    long fileSize = fileDTO.getFileSize();

                    String uploadDir = "tomcat/webapps/ROOT/WEB-INF/classes/static/images";  // 실제 서버의 파일 저장 경로
                    Path uploadPath = Paths.get(uploadDir);

                    // 디렉토리가 존재하지 않으면 생성
                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }

                    // 파일을 서버에 저장
                    Path targetPath = uploadPath.resolve(fileUUID + "_" + fileName);
                    Files.write(targetPath, files[0].getBytes()); // 여기에 files 배열에서 첫 번째 파일을 저장

                    // BoardFileEntity 생성 (BoardEntity와 연결)
                    BoardFileEntity boardFileEntity = BoardFileEntity.builder()
                            .boardEntity(boardEntity)  // 방금 저장된 게시글과 연결
                            .fileName(fileName)
                            .fileUUID(fileUUID)
                            .fileSize(fileSize)
                            .fileType(fileType)
                            .fileTime(LocalDateTime.now()) // 현재 시간으로 설정
                            .build();

                    // 파일 엔티티 저장
                    boardFileRepository.save(boardFileEntity);
                } catch (IOException e) {
                    // 파일 저장 예외 처리
                    e.printStackTrace();
                    throw new RuntimeException("파일 저장 실패", e);
                }
            }
        }
    }

    //간단보기 페이징처리 예정
    @Override
    public List<BoardTitleDTO> boardTitleReview() {
        // BoardCategory.REVIEW 카테고리의 게시글을 조회
        List<BoardEntity> boardEntities = boardRepository.findByBoardCategoryWithFiles(BoardCategory.REVIEW);

        // BoardEntity를 BoardTitleDTO로 변환
        return boardEntities.stream()
                .map(boardEntity -> BoardTitleDTO.builder()
                        .boardId(boardEntity.getBoardId())  // 게시글 ID
                        .boardTitle(boardEntity.getBoardTitle())  // 게시글 제목
                        .name(boardEntity.getUserEntity() != null ? boardEntity.getUserEntity().getName() : "알 수 없음")  // 작성자 이름
                        .boardView(boardEntity.getBoardView())  // 조회수
                        .boardCreatedAt(boardEntity.getBoardCreatedAt())  // 생성일
                        .boardUpdatedAt(boardEntity.getBoardUpdatedAt())  // 수정일
                        .build())
                .collect(Collectors.toList());
    }



    //취업자 후기 자료 불러오는 메서드
    @Override
    public List<BoardviewDTO> boardReview() {
        // 게시글과 관련된 파일들을 함께 조회
        List<BoardEntity> boardEntities = boardRepository.findByBoardCategoryWithFiles(BoardCategory.REVIEW);

        // BoardEntity를 BoardviewDTO로 변환
        return boardEntities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<BoardTitleDTO> getPagedBoardTitleReview(Pageable pageable) {
        // 페이징된 BoardEntity 가져오기
        Page<BoardEntity> boardPage = boardRepository.findByBoardCategoryWithFilesAndUser(BoardCategory.REVIEW, pageable);

        // BoardEntity -> BoardTitleDTO 변환
        return boardPage.map(boardEntity -> BoardTitleDTO.builder()
                .boardId(boardEntity.getBoardId())
                .boardTitle(boardEntity.getBoardTitle())
                .name(boardEntity.getUserEntity() != null ? boardEntity.getUserEntity().getName() : "알 수 없음")
                .boardView(boardEntity.getBoardView())
                .boardCreatedAt(boardEntity.getBoardCreatedAt())
                .boardUpdatedAt(boardEntity.getBoardUpdatedAt())
                .build());
    }

    private BoardviewDTO convertToDTO(BoardEntity boardEntity) {
        // 게시글에 관련된 파일들을 추출하여 DTO로 변환
        List<String> fileNames = boardEntity.getBoardFiles().stream()
                .map(boardFile -> boardFile.getFileName())
                .collect(Collectors.toList());

        List<String> fileUUIDs = boardEntity.getBoardFiles().stream()
                .map(boardFile -> boardFile.getFileUUID())
                .collect(Collectors.toList());

        // 파일 타입도 추출하여 리스트에 담기
        List<String> fileTypes = boardEntity.getBoardFiles().stream()
                .map(boardFile -> boardFile.getFileType())  // fileType 추가
                .collect(Collectors.toList());

        // 작성자의 이름을 가져옴 (작성자가 없을 경우 기본값 "알 수 없음" 사용)
        String authorName = boardEntity.getUserEntity() != null ? boardEntity.getUserEntity().getName() : "알 수 없음";

        // DTO 빌더로 변환하여 반환
        return BoardviewDTO.builder()
                .boardId(boardEntity.getBoardId())
                .boardTitle(boardEntity.getBoardTitle())  // 게시글 제목 추가
                .boardContent(boardEntity.getBoardContent())
                .boardCreatedAt(boardEntity.getBoardCreatedAt())
                .boardUpdatedAt(boardEntity.getBoardUpdatedAt())
                .boardView(boardEntity.getBoardView())
                .boardCategory(boardEntity.getBoardCategory().name())  // Enum -> String 변환
                .name(authorName)  // 작성자 이름 추가 (null 체크)
                .fileNames(fileNames)  // 파일 이름 목록
                .fileUUIDs(fileUUIDs)  // 파일 UUID 목록
                .fileTypes(fileTypes)  // 파일 타입 목록
                .build();
    }



//kdt 저장하는 메서드
@Override
public boolean kdtboardsave(KDTBoardDTO kdtboardDTO, List<KDTBoardFileDTO> fileList, MultipartFile[] files) {
    Long userId = kdtboardDTO.getUserId();  // BoardDTO에서 userId 가져오기
    Long kdtSessionId = kdtboardDTO.getKdtSessionId();  // BoardDTO에서 kdtSessionId 가져오기

    // userId 또는 kdtSessionId가 null이면 예외를 던짐
    if (userId == null || kdtSessionId == null) {
        return false;  // 예외 대신 false 반환
    }

    try {
        // 1. 실제 UserEntity를 조회하여 가져오기
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 2. 실제 KDTSessionEntity를 조회하여 가져오기
        KDTSessionEntity kdtSessionEntity = kdtSessionRepository.findById(kdtSessionId)
                .orElseThrow(() -> new RuntimeException("세션을 찾을 수 없습니다."));

        // 3. KDTBoardEntity 생성
        KDTBoardEntity kdtBoardEntity = KDTBoardEntity.builder()
                .userEntity(userEntity)  // 실제 유저 정보 설정
                .kdtSessionEntity(kdtSessionEntity)  // 실제 세션 정보 설정
                .kdtBoardTitle(kdtboardDTO.getKdtBoardTitle())
                .kdtBoardContent(kdtboardDTO.getKdtBoardContent())
                .kdtBoardViewCount(0L)  // 조회수 초기값 0L (Long 타입)
                .kdtBoardCategory(KDTBoardCategory.valueOf(kdtboardDTO.getKdtBoardCategory()))  // BoardCategory enum으로 변환
                .kdtBoardHidden(false)  // 기본값 (숨김 여부)
                .kdtBoardAnswerCompleted(false)  // 기본값 (답변 여부)
                .build();

        // 4. 게시글 엔티티 저장
        kdtBoardEntity = kdtBoardRepository.save(kdtBoardEntity);

        // 5. 파일 업로드 처리 (여러 파일 처리)
        if (fileList != null && !fileList.isEmpty()) {
            for (int i = 0; i < fileList.size(); i++) {
                KDTBoardFileDTO fileDTO = fileList.get(i);
                try {
                    // 파일을 서버에 저장
                    String fileUUID = fileDTO.getKdtFileUUID();
                    String fileName = fileDTO.getKdtFileName();
                    String fileType = fileDTO.getKdtFileType();
                    long fileSize = fileDTO.getKdtFileSize();

                    String uploadDir = "tomcat/webapps/ROOT/WEB-INF/classes/KDT/course";  // 실제 서버의 파일 저장 경로
                    Path uploadPath = Paths.get(uploadDir);

                    // 디렉토리가 존재하지 않으면 생성
                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }

                    // 파일을 서버에 저장
                    Path targetPath = uploadPath.resolve(fileUUID + "_" + fileName);
                    Files.write(targetPath, files[i].getBytes()); // 여러 파일을 처리하기 위해 files[i] 사용

                    // KDTBoardFileEntity 생성 (KDTBoardEntity와 연결)
                    KDTBoardFileEntity kdtBoardFileEntity = KDTBoardFileEntity.builder()
                            .kdtBoardEntity(kdtBoardEntity)  // 방금 저장된 게시글과 연결
                            .kdtFileName(fileName)
                            .kdtFileUUID(fileUUID)
                            .kdtFileSize(fileSize)
                            .kdtFileType(fileType)
                            .kdtFileTime(LocalDateTime.now()) // 현재 시간으로 설정
                            .build();

                    // 파일 엔티티 저장
                    kdtBoardFileRepository.save(kdtBoardFileEntity);
                } catch (IOException e) {
                    // 파일 저장 예외 처리
                    e.printStackTrace();
                    return false;  // 파일 저장 실패 시 false 반환
                }
            }
        }

        // 모든 작업이 성공적으로 완료된 경우 true 반환
        return true;
    } catch (Exception e) {
        // 예외가 발생한 경우 false 반환
        e.printStackTrace();
        return false;
    }
}


    @Override
    public Page<KDTBoardTitleDTO> getPagedBoardmaterial(Long sessionId, String searchValue, String searchType, Pageable pageable) {
        if (searchValue != null && !searchValue.isEmpty()) {
            if ("title".equals(searchType)) {
                // 제목으로만 검색
                return kdtBoardRepository.findByKdtBoardCategoryAndKdtBoardTitleContainingAndSessionId(
                                KDTBoardCategory.MATERIAL, "%" + searchValue + "%", sessionId, pageable)
                        .map(kdtBoardEntity -> KDTBoardTitleDTO.builder()
                                .kdtBoardId(kdtBoardEntity.getKdtBoardId())  // 게시글 ID
                                .kdtBoardTitle(kdtBoardEntity.getKdtBoardTitle())  // 게시글 제목
                                .name(kdtBoardEntity.getUserEntity() != null ? kdtBoardEntity.getUserEntity().getName() : "알 수 없음")  // 작성자 이름
                                .kdtBoardView(kdtBoardEntity.getKdtBoardViewCount() != null ? kdtBoardEntity.getKdtBoardViewCount() : 0L)  // 조회수
                                .kdtBoardCategory(kdtBoardEntity.getKdtBoardCategory() != null ? kdtBoardEntity.getKdtBoardCategory().name() : "알 수 없음")  // 카테고리
                                .kdtBoardCreatedAt(kdtBoardEntity.getKdtBoardCreatedAt())  // 생성일
                                .kdtBoardUpdatedAt(kdtBoardEntity.getKdtBoardUpdatedAt())  // 수정일
                                .build());
            } else if ("name".equals(searchType)) {
                // 작성자 이름으로만 검색
                return kdtBoardRepository.findByKdtBoardCategoryAndUserEntityNameContainingAndSessionId(
                                KDTBoardCategory.MATERIAL, "%" + searchValue + "%", sessionId, pageable)
                        .map(kdtBoardEntity -> KDTBoardTitleDTO.builder()
                                .kdtBoardId(kdtBoardEntity.getKdtBoardId())  // 게시글 ID
                                .kdtBoardTitle(kdtBoardEntity.getKdtBoardTitle())  // 게시글 제목
                                .name(kdtBoardEntity.getUserEntity() != null ? kdtBoardEntity.getUserEntity().getName() : "알 수 없음")  // 작성자 이름
                                .kdtBoardView(kdtBoardEntity.getKdtBoardViewCount() != null ? kdtBoardEntity.getKdtBoardViewCount() : 0L)  // 조회수
                                .kdtBoardCategory(kdtBoardEntity.getKdtBoardCategory() != null ? kdtBoardEntity.getKdtBoardCategory().name() : "알 수 없음")  // 카테고리
                                .kdtBoardCreatedAt(kdtBoardEntity.getKdtBoardCreatedAt())  // 생성일
                                .kdtBoardUpdatedAt(kdtBoardEntity.getKdtBoardUpdatedAt())  // 수정일
                                .build());
            } else {
                // 검색어가 없거나 잘못된 `searchType`이 들어왔을 때
                return kdtBoardRepository.findByKdtBoardCategoryAndSessionId(KDTBoardCategory.MATERIAL, sessionId, pageable)
                        .map(kdtBoardEntity -> KDTBoardTitleDTO.builder()
                                .kdtBoardId(kdtBoardEntity.getKdtBoardId())  // 게시글 ID
                                .kdtBoardTitle(kdtBoardEntity.getKdtBoardTitle())  // 게시글 제목
                                .name(kdtBoardEntity.getUserEntity() != null ? kdtBoardEntity.getUserEntity().getName() : "알 수 없음")  // 작성자 이름
                                .kdtBoardView(kdtBoardEntity.getKdtBoardViewCount() != null ? kdtBoardEntity.getKdtBoardViewCount() : 0L)  // 조회수
                                .kdtBoardCategory(kdtBoardEntity.getKdtBoardCategory() != null ? kdtBoardEntity.getKdtBoardCategory().name() : "알 수 없음")  // 카테고리
                                .kdtBoardCreatedAt(kdtBoardEntity.getKdtBoardCreatedAt())  // 생성일
                                .kdtBoardUpdatedAt(kdtBoardEntity.getKdtBoardUpdatedAt())  // 수정일
                                .build());
            }
        } else {
            // 검색어가 없을 경우
            return kdtBoardRepository.findByKdtBoardCategoryAndSessionId(KDTBoardCategory.MATERIAL, sessionId, pageable)
                    .map(kdtBoardEntity -> KDTBoardTitleDTO.builder()
                            .kdtBoardId(kdtBoardEntity.getKdtBoardId())  // 게시글 ID
                            .kdtBoardTitle(kdtBoardEntity.getKdtBoardTitle())  // 게시글 제목
                            .name(kdtBoardEntity.getUserEntity() != null ? kdtBoardEntity.getUserEntity().getName() : "알 수 없음")  // 작성자 이름
                            .kdtBoardView(kdtBoardEntity.getKdtBoardViewCount() != null ? kdtBoardEntity.getKdtBoardViewCount() : 0L)  // 조회수
                            .kdtBoardCategory(kdtBoardEntity.getKdtBoardCategory() != null ? kdtBoardEntity.getKdtBoardCategory().name() : "알 수 없음")  // 카테고리
                            .kdtBoardCreatedAt(kdtBoardEntity.getKdtBoardCreatedAt())  // 생성일
                            .kdtBoardUpdatedAt(kdtBoardEntity.getKdtBoardUpdatedAt())  // 수정일
                            .build());
        }
    }


    // 세션 ID, 보드 ID, 카테고리로 게시글과 관련된 파일을 조회하는 메서드
    // 게시글 상세보기 (단일 게시글 반환)
    @Override
    public KDTBoardviewDTO getBoardDetails(Long sessionId, Long boardId, KDTBoardCategory category) {

        // 게시글을 조회하는 부분 (Optional 처리)
        Optional<KDTBoardEntity> optionalBoardEntity = kdtBoardRepository.findBySessionIdAndKdtBoardIdAndBoardCategory(sessionId, boardId, category);

        // 게시글이 존재하지 않는 경우 처리
        if (!optionalBoardEntity.isPresent()) {
            throw new RuntimeException("게시글을 찾을 수 없습니다.");
        }

        // 게시글이 존재하는 경우, 엔티티를 DTO로 변환하여 반환
        KDTBoardEntity boardEntity = optionalBoardEntity.get();

        return convertToDTO(boardEntity);
    }



    private KDTBoardviewDTO convertToDTO(KDTBoardEntity boardEntity) {
        // 파일 이름과 UUID 리스트 추출
        List<String> fileNames = boardEntity.getFiles().stream()
                .map(file -> file.getKdtFileName())
                .collect(Collectors.toList());

        List<String> fileUUIDs = boardEntity.getFiles().stream()
                .map(file -> file.getKdtFileUUID())
                .collect(Collectors.toList());

        // DTO 생성
        return KDTBoardviewDTO.builder()
                .kdtBoardId(boardEntity.getKdtBoardId())
                .kdtBoardTitle(boardEntity.getKdtBoardTitle())
                .kdtBoardContent(boardEntity.getKdtBoardContent())
                .kdtBoardCreatedAt(boardEntity.getKdtBoardCreatedAt())
                .kdtBoardUpdatedAt(boardEntity.getKdtBoardUpdatedAt())
                .kdtBoardView(boardEntity.getKdtBoardViewCount())
                .name(boardEntity.getUserEntity().getName())  // 작성자 이름
                .kdtFileNams(fileNames) // 파일 이름 리스트
                .kdtfileUUIDs(fileUUIDs) // 파일 UUID 리스트
                .build();
    }

    @Override
    public boolean boardDelete(Long postId) {
        try {
            // 게시글이 존재하는지 확인
            Optional<KDTBoardEntity> optionalBoard = kdtBoardRepository.findById(postId);

            // 게시글이 존재하지 않으면 삭제할 수 없음
            if (!optionalBoard.isPresent()) {
                return false;  // 게시글이 없으면 false 반환
            }

            // 게시글 삭제
            kdtBoardRepository.deleteById(postId);

            return true;  // 삭제 성공 시 true 반환
        } catch (Exception e) {
            // 예외가 발생한 경우
            e.printStackTrace();
            return false;  // 예외 발생 시 false 반환
        }
    }

    @Override
    public void reviewcount(Long boardId) {
        KDTBoardEntity board = kdtBoardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("게시판을 찾을 수 없습니다."));

        // 조회수 증가 메서드 호출
        board.incrementViewCount();

        // 변경된 게시판 엔티티 저장
        kdtBoardRepository.save(board);
    }


}

