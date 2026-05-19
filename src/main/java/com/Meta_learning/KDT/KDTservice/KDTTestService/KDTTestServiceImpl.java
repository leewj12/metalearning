package com.Meta_learning.KDT.KDTservice.KDTTestService;


import com.Meta_learning.KDT.KDTDTO.KDTTestDTO.*;
import com.Meta_learning.KDT.KDTentity.KDTPartEntity.KDTPartEntity;
import com.Meta_learning.KDT.KDTentity.KDTTestEntity.KDTTestEntity;
import com.Meta_learning.KDT.KDTentity.KDTTestEntity.KDTTestGradingEntity;
import com.Meta_learning.KDT.KDTentity.KDTTestEntity.KDTTestItemEntity;
import com.Meta_learning.KDT.KDTentity.KDTTestEntity.KDTTestSubmitEntity;
import com.Meta_learning.KDT.KDTrepository.KDTPartRepository.KDTPartRepository;
import com.Meta_learning.KDT.KDTrepository.KDTSessionRepository.KDTSessionRepository;
import com.Meta_learning.KDT.KDTrepository.KDTTestRepositoy.KDTTestGradingRepository;
import com.Meta_learning.KDT.KDTrepository.KDTTestRepositoy.KDTTestItemRepository;
import com.Meta_learning.KDT.KDTrepository.KDTTestRepositoy.KDTTestRepository;
import com.Meta_learning.KDT.KDTrepository.KDTTestRepositoy.KDTTestSubmitRepository;
import com.Meta_learning.user.userentity.UserEntity;
import com.Meta_learning.user.userrepository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KDTTestServiceImpl implements KDTTestService {

    private final KDTTestRepository kdtTestRepository;
    private final UserRepository userRepository;
    private final KDTSessionRepository kdtSessionRepository;
    private final KDTTestItemRepository kdtTestItemRepository;
    private final KDTPartRepository kdtPartRepository;
    private final KDTTestSubmitRepository kdtTestSubmitRepository;
    private final KDTTestGradingRepository kdtTestGradingRepository;

    @Override
    public KDTTestEntity kdtTestSave(KDTTestDTO kdtTestDTO) {
        KDTTestEntity kdtTestEntity= KDTTestEntity.builder()
                .kdtTestId(kdtTestDTO.getKdtTestId())
                .kdtSessionEntity(kdtSessionRepository.findByKdtSessionId(kdtTestDTO.getKdtSessionId()))
                .userEntity(userRepository.findByUserId(kdtTestDTO.getUserId()))  // 이미 가져온 UserEntity
                .kdtTestTitle(kdtTestDTO.getKdtTestTitle())
                .kdtTestStartDate(kdtTestDTO.getKdtTestStartDate())
                .kdtTestEndDate(kdtTestDTO.getKdtTestEndDate())
                .kdtTestCreatedAt(kdtTestDTO.getKdtTestCreatedAt())
                .build();
        return kdtTestRepository.save(kdtTestEntity);
    }

    @Override
    public List<KDTTestItemEntity> kdtTestItemSave(List<KDTTestItemDTO> kdtTestItemDTOS, Long kdtTestId) {
        List<KDTTestItemEntity> kdtTestItemEntities = new ArrayList<>();

        for (KDTTestItemDTO dto : kdtTestItemDTOS) {
            KDTTestItemEntity entity = createNewTestItemEntity(kdtTestId, dto);
            kdtTestItemEntities.add(entity);
        }

        // KDTTestItemEntity를 DB에 저장
        return kdtTestItemRepository.saveAll(kdtTestItemEntities);
    }

    @Override
    public KDTTestDTO findKdtTestDTOTestById(Long kdtTestId) {
        KDTTestEntity kdtTestEntity = kdtTestRepository.findByKdtTestId(kdtTestId);
        if(kdtTestEntity== null) return null;
        KDTTestDTO kdtTestDTO = new KDTTestDTO();
        kdtTestDTO.setKdtTestId(kdtTestEntity.getKdtTestId());
        kdtTestDTO.setKdtSessionId(kdtTestEntity.getKdtSessionEntity().getKdtSessionId());  // KDTSessionEntity에서 ID 가져오기
        kdtTestDTO.setUserId(kdtTestEntity.getUserEntity().getUserId());  // UserEntity에서 ID 가져오기
        kdtTestDTO.setKdtTestTitle(kdtTestEntity.getKdtTestTitle());
        kdtTestDTO.setKdtTestStartDate(kdtTestEntity.getKdtTestStartDate());
        kdtTestDTO.setKdtTestEndDate(kdtTestEntity.getKdtTestEndDate());
        kdtTestDTO.setKdtTestCreatedAt(kdtTestEntity.getKdtTestCreatedAt());

        return kdtTestDTO;
    }

    @Override
    public KDTTestEntity findKdtTestEntityById(Long kdtTestId) {
        return kdtTestRepository.findByKdtTestId(kdtTestId);
    }

    @Override
    public List<KDTTestItemDTO> findKdtTestItemDTOByKdtTestId(Long kdtTestId) {
        List<KDTTestItemEntity> kdtTestItemEntities = kdtTestItemRepository.findByKdtTestEntity_kdtTestId(kdtTestId);
        return kdtTestItemEntities.stream().map(entity -> {
            KDTTestItemDTO kdtTestItemDTO = new KDTTestItemDTO();
            kdtTestItemDTO.setKdtTestItemId(entity.getKdtTestItemId());
            kdtTestItemDTO.setKdtTestId(entity.getKdtTestEntity().getKdtTestId()); // KDTTestEntity의 kdtTestId를 설정
            kdtTestItemDTO.setKdtTestItemQuest(entity.getKdtTestItemQuest());
            kdtTestItemDTO.setKdtTestItemAnswer(entity.getKdtTestItemAnswer());
            kdtTestItemDTO.setKdtTestItemAnsw1(entity.getKdtTestItemAnsw1());
            kdtTestItemDTO.setKdtTestItemAnsw2(entity.getKdtTestItemAnsw2());
            kdtTestItemDTO.setKdtTestItemAnsw3(entity.getKdtTestItemAnsw3());
            kdtTestItemDTO.setKdtTestItemAnsw4(entity.getKdtTestItemAnsw4());
            kdtTestItemDTO.setKdtTestItemScore(entity.getKdtTestItemScore());
            kdtTestItemDTO.setKdtTestItemCategory(entity.getKdtTestItemCategory());
            return kdtTestItemDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<KDTTestItemEntity> findKdtTestItemEntityByKdtTestId(Long kdtTestId) {
        return kdtTestItemRepository.findByKdtTestEntity_kdtTestId(kdtTestId);
    }

    @Override
    public List<KDTTestListDTO> findKdtTestListBySessionId(Long kdtSessionId) {
        List<KDTTestEntity> kdtTestEntities = kdtTestRepository.findByKdtSessionEntity_KdtSessionId(kdtSessionId);

        // 각 엔티티를 DTO로 변환하여 리스트에 담기
        List<KDTTestListDTO> kdtTestListDTOs = new ArrayList<>();
        for (KDTTestEntity entity : kdtTestEntities) {
            KDTTestListDTO dto = convertToTestListDTO(entity);
            kdtTestListDTOs.add(dto);
        }
        return kdtTestListDTOs;
    }

    @Override
    public List<KDTTestListDTO> findKdtTestListBySessionIdAndUserId(Long kdtSessionId, Long userId) {
        List<KDTTestEntity> kdtTestEntities = kdtTestRepository.findByKdtSessionEntity_KdtSessionIdAndUserEntity_userId(kdtSessionId, userId);

        // 각 엔티티를 DTO로 변환하여 리스트에 담기
        List<KDTTestListDTO> kdtTestListDTOs = new ArrayList<>();
        for (KDTTestEntity entity : kdtTestEntities) {
            KDTTestListDTO dto = convertToTestListDTO(entity);
            kdtTestListDTOs.add(dto);
        }
        return kdtTestListDTOs;
    }

    @Override
    public KDTTestListDTO findKdtTestListByTestId(Long kdtTestId) {
        KDTTestEntity kdtTestEntity = kdtTestRepository.findByKdtTestId(kdtTestId);
        return convertToTestListDTO(kdtTestEntity);
    }

    @Override
    public List<KDTTestSubmitListDTO> findKdtTestSubmitListByTestId(Long kdtSessionId, Long kdtTestId) {
        List<KDTPartEntity> kdtPartEntities = kdtPartRepository.findByKdtSessionEntity_KdtSessionId(kdtSessionId);
        List<KDTTestSubmitListDTO> kdtTestSubmitListDTOS = new ArrayList<>();
        List<Long> partIds = kdtPartEntities.stream()
                .map(KDTPartEntity::getKdtPartId) // KDTPartEntity에서 kdtPartId 추출
                .collect(Collectors.toList());

        // 백분위 계산을 위한 준비 : 점수 구하기
        Map<Long, Integer> partIdToScoreMap = calculateScores(kdtTestId, partIds);

        // 점수의 List화
        List<Integer> scores = new ArrayList<>(partIdToScoreMap.values());

        // 점수 정렬 후 순위 계산
        Collections.sort(scores, Collections.reverseOrder());  // 내림차순 정렬 (높은 점수부터)


        for (KDTPartEntity partEntity : kdtPartEntities) {
            Long partId = partEntity.getKdtPartId();
            KDTTestSubmitListDTO dto = new KDTTestSubmitListDTO();

            // 답변 작성자 정보
            dto.setKdtPartId(partEntity.getKdtPartId());
            dto.setUserId(partEntity.getUserEntity().getUserId());
            dto.setKdtPartName(partEntity.getUserEntity().getName());

            // 제출일/수정일
            List<KDTTestSubmitEntity> kdtTestSubmitEntities = kdtTestSubmitRepository.findByKdtTestItemEntity_KdtTestEntity_kdtTestIdAndKdtPartEntity_kdtPartId(kdtTestId, partId);
            if(kdtTestSubmitEntities!=null && !kdtTestSubmitEntities.isEmpty()){
                dto.setKdtTestSubmitCreatedAt(kdtTestSubmitEntities.get(0).getKdtTestSubmitCreatedAt());
                dto.setKdtTestSubmitUpdatedAt(kdtTestSubmitEntities.get(0).getKdtTestSubmitUpdatedAt());
            }

            // 실제 점수
            int score = 0;
            if(partIdToScoreMap.get(partId) != null) {
                score = partIdToScoreMap.get(partId);
            }
            dto.setActualScore(score);

            // 최대 점수
            List<KDTTestItemEntity> testItemEntities = kdtTestItemRepository.findByKdtTestEntity_kdtTestId(kdtTestId);
            int maxScore = 0;
            for(KDTTestItemEntity testItemEntity : testItemEntities){
                maxScore+= testItemEntity.getKdtTestItemScore();
            }
            dto.setMaxScore(maxScore);

            if(partIdToScoreMap.get(partId) != null) {
                // 백분위 계산
                int rank = scores.indexOf(score) + 1; // 순위는 1부터 시작
                double percentile = ((double) rank / scores.size()) * 100;
                dto.setPercentile(percentile);
            }

            kdtTestSubmitListDTOS.add(dto);
        }
        return kdtTestSubmitListDTOS;
    }

    @Override
    public List<KDTTestStudentListDTO> findKdtTestStudentListBySessionId(Long kdtSessionId, Long kdtPartId) {
        List<KDTTestEntity> kdtTestEntities = kdtTestRepository.findByKdtSessionEntity_KdtSessionId(kdtSessionId);

        // 각 엔티티를 DTO로 변환하여 리스트에 담기
        List<KDTTestStudentListDTO> kdtTestStudentListDTOS = new ArrayList<>();
        for (KDTTestEntity entity : kdtTestEntities) {
            KDTTestStudentListDTO dto = convertToTestStudentListDTO(entity, kdtPartId);
            kdtTestStudentListDTOS.add(dto);
        }
        return kdtTestStudentListDTOS;
    }

    // 시험을 지우면, 채점, 답안, 문제, 시험을 한번에 지워야 함
    @Override
    public void deleteTest(Long kdtTestId) {
        List<KDTTestItemEntity> kdtTestItemEntities= kdtTestItemRepository.findByKdtTestEntity_kdtTestId(kdtTestId);
        List<KDTTestSubmitEntity> kdtTestSubmitEntities = kdtTestSubmitRepository.findByKdtTestItemEntity_KdtTestEntity_kdtTestId(kdtTestId);
        List<KDTTestGradingEntity> kdtTestGradingEntities = kdtTestGradingRepository.findByKdtTestSubmitEntity_KdtTestItemEntity_KdtTestEntity_kdtTestId(kdtTestId);

        kdtTestGradingRepository.deleteAll(kdtTestGradingEntities);
        kdtTestSubmitRepository.deleteAll(kdtTestSubmitEntities);
        kdtTestItemRepository.deleteAll(kdtTestItemEntities);
        kdtTestRepository.deleteById(kdtTestId);
    }

    @Override
    public KDTTestEntity kdtTestUpdate(KDTTestDTO kdtTestDTO) {
        KDTTestEntity kdtTestEntity =kdtTestRepository.findByKdtTestId(kdtTestDTO.getKdtTestId());
        kdtTestEntity.update(kdtTestDTO);
        return kdtTestRepository.save(kdtTestEntity);
    }

    @Override
    public List<KDTTestItemEntity> kdtTestItemUpdate(Long kdtTestItemId, List<KDTTestItemDTO> kdtTestItemDTOs) {
        List<KDTTestItemEntity> updatedEntities = new ArrayList<>();

        for (KDTTestItemDTO dto : kdtTestItemDTOs) {
            KDTTestItemEntity entity;

            if (dto.getKdtTestItemId() !=null){
                entity = kdtTestItemRepository.findByKdtTestItemId(dto.getKdtTestItemId());
                entity.update(dto);
            }
            else{
                entity = createNewTestItemEntity(kdtTestItemId, dto);
            }
            updatedEntities.add(entity);
        }
        return kdtTestItemRepository.saveAll(updatedEntities);
    }

    private KDTTestItemEntity createNewTestItemEntity(Long kdtTestId, KDTTestItemDTO dto){
        return KDTTestItemEntity.builder()
                .kdtTestEntity(kdtTestRepository.findByKdtTestId(kdtTestId))
                .kdtTestItemQuest(dto.getKdtTestItemQuest())
                .kdtTestItemAnswer(dto.getKdtTestItemAnswer())
                .kdtTestItemAnsw1(dto.getKdtTestItemAnsw1())
                .kdtTestItemAnsw2(dto.getKdtTestItemAnsw2())
                .kdtTestItemAnsw3(dto.getKdtTestItemAnsw3())
                .kdtTestItemAnsw4(dto.getKdtTestItemAnsw4())
                .kdtTestItemScore(dto.getKdtTestItemScore())
                .kdtTestItemCategory(dto.getKdtTestItemCategory())
                .build();
    }

    @Transactional
    @Override
    public void kdtTestItemDelete(List<Long> deleteIds) {
        kdtTestGradingRepository.deleteByKdtTestSubmitEntity_KdtTestItemEntity_KdtTestItemIdIn(deleteIds);
        kdtTestSubmitRepository.deleteByKdtTestItemEntity_KdtTestItemIdIn(deleteIds);
        kdtTestItemRepository.deleteByKdtTestItemIdIn(deleteIds);
    }


    @Override
    public List<KDTTestSubmitEntity> kdtTestSubmitSave(KDTTestSubmitRequestDTO requestDTO) {
        List<KDTTestSubmitEntity> entities = requestDTO.getKdtTestSubmitList().stream()
                .map(dto -> {
                    // DTO에 kdtPartId와 현재 시간을 추가
                    dto.setKdtPartId(requestDTO.getKdtPartId());
                    dto.setKdtTestSubmitCreatedAt(LocalDateTime.now());

                    return convertToTestSubmitEntity(dto);
                })
                .collect(Collectors.toList());

        // saveAll은 해당 엔티티들을 한 번에 저장하는 JPA의 메서드
         kdtTestSubmitRepository.saveAll(entities);

        return entities;
    }

    @Override
    public List<KDTTestSubmitDTO> findKdtTestSubmitDTOByTestIdAndPartId(Long kdtTestId, Long kdtPartId) {
        List<KDTTestSubmitEntity> entities = kdtTestSubmitRepository.findByKdtTestItemEntity_KdtTestEntity_kdtTestIdAndKdtPartEntity_kdtPartId(kdtTestId, kdtPartId);
        // 엔티티 리스트를 DTO 리스트로 변환
        List<KDTTestSubmitDTO> dtoList = entities.stream()
                .map(this::convertToTestSubmitDTO)  // convertToTestSubmitDTO 메서드를 이용해 각 엔티티를 DTO로 변환
                .collect(Collectors.toList());

        return dtoList;

    }

    @Override
    public List<KDTTestSubmitEntity> kdtTestSubmitUpdate(KDTTestSubmitRequestDTO requestDTO) {
        List<KDTTestSubmitEntity> entities = requestDTO.getKdtTestSubmitList().stream()
                .map(dto -> {
                    // DTO에 업데이트 시간을 추가
                    dto.setKdtTestSubmitUpdatedAt(LocalDateTime.now());
                    return convertToTestSubmitEntity(dto);
                })
                .collect(Collectors.toList());

        // saveAll은 해당 엔티티들을 한 번에 저장하는 JPA의 메서드
        kdtTestSubmitRepository.saveAll(entities);

        return entities;
    }

    @Override
    public List<KDTTestGradingEntity> kdtTestGradingAutoSave(Long kdtTestId, Long kdtPartId) {
        // partID와 testID로 testSubmitEntity 찾기
        List<KDTTestSubmitEntity> kdtTestSubmitEntities = kdtTestSubmitRepository.findByKdtTestItemEntity_KdtTestEntity_kdtTestIdAndKdtPartEntity_kdtPartId(kdtTestId, kdtPartId);
        UserEntity userEntity = kdtTestSubmitEntities.get(0).getKdtTestItemEntity().getKdtTestEntity().getUserEntity();
        List<KDTTestGradingEntity> kdtTestGradingEntities = new ArrayList<>();

        // testSubmitEntities에 대해 반복문 실행
        for (KDTTestSubmitEntity submitEntity : kdtTestSubmitEntities) {
            KDTTestItemEntity testItemEntity = submitEntity.getKdtTestItemEntity();
            int gradingScore = (submitEntity.getKdtTestSubmitAnswer() == testItemEntity.getKdtTestItemAnswer())
                    ? testItemEntity.getKdtTestItemScore()  // 정답일 경우 문제의 점수를 부여
                    : 0;  // 오답일 경우 0점
            // 채점 결과 엔티티 생성
            KDTTestGradingEntity gradingEntity = KDTTestGradingEntity.builder()
                    .kdtTestSubmitEntity(submitEntity)
                    .userEntity(userEntity)  // 시험 출제자로 채점자 설정
                    .kdtTestGradingScore(gradingScore)
                    .kdtTestGradingCreatedAt(LocalDateTime.now())  // 현재 시간 설정
                    .build();

            // 채점 결과를 리스트에 추가
            kdtTestGradingEntities.add(gradingEntity);
        }
        // 생성한 모든 채점 결과 저장
        kdtTestGradingRepository.saveAll(kdtTestGradingEntities);
        return kdtTestGradingEntities;
    }

    @Override
    public List<KDTTestGradingEntity> kdtTestGradingAutoUpdate(Long kdtTestId, Long kdtPartId) {
        // partID와 testID로 testSubmitEntity 찾기
        List<KDTTestGradingEntity> kdtTestGradingEntities = kdtTestGradingRepository.findByKdtTestSubmitEntity_KdtTestItemEntity_KdtTestEntity_kdtTestIdAndKdtTestSubmitEntity_KdtPartEntity_kdtPartId(kdtTestId, kdtPartId);

        for (KDTTestGradingEntity gradingEntity : kdtTestGradingEntities) {
            KDTTestSubmitEntity submitEntity = gradingEntity.getKdtTestSubmitEntity();
            KDTTestItemEntity testItemEntity = submitEntity.getKdtTestItemEntity();

            // 정답과 제출한 답안을 비교하여 점수 계산
            int gradingScore = (submitEntity.getKdtTestSubmitAnswer() == testItemEntity.getKdtTestItemAnswer())
                    ? testItemEntity.getKdtTestItemScore()  // 정답일 경우 문제의 점수를 부여
                    : 0;  // 오답일 경우 0점

            KDTTestGradingDTO gradingDTO = new KDTTestGradingDTO();
            gradingDTO.setKdtTestGradingScore(gradingScore);
            gradingDTO.setKdtTestGradingUpdatedAt(LocalDateTime.now());
            gradingEntity.update(gradingDTO);
        }
        kdtTestGradingRepository.saveAll(kdtTestGradingEntities);
        return kdtTestGradingEntities;  // 업데이트된 채점 결과 리스트 반환
    }

    @Override
    public List<KDTTestGradingEntity> kdtTestGradingsAutoUpdate(Long kdtTestId) {
        // testId로 submitEntity 찾기
        List<KDTTestGradingEntity> kdtTestGradingEntities = kdtTestGradingRepository.findByKdtTestSubmitEntity_KdtTestItemEntity_KdtTestEntity_kdtTestId(kdtTestId);
        for (KDTTestGradingEntity gradingEntity : kdtTestGradingEntities) {
            KDTTestSubmitEntity submitEntity = gradingEntity.getKdtTestSubmitEntity();
            KDTTestItemEntity testItemEntity = submitEntity.getKdtTestItemEntity();

            // 정답과 제출한 답안을 비교하여 점수 계산
            int gradingScore = (submitEntity.getKdtTestSubmitAnswer() == testItemEntity.getKdtTestItemAnswer())
                    ? testItemEntity.getKdtTestItemScore()  // 정답일 경우 문제의 점수를 부여
                    : 0;  // 오답일 경우 0점

            KDTTestGradingDTO gradingDTO = new KDTTestGradingDTO();
            gradingDTO.setKdtTestGradingScore(gradingScore);
            gradingDTO.setKdtTestGradingUpdatedAt(LocalDateTime.now());
            gradingEntity.update(gradingDTO);
        }
        kdtTestGradingRepository.saveAll(kdtTestGradingEntities);
        return kdtTestGradingEntities;  // 업데이트된 채점 결과 리스트 반환
    }

    @Override
    public List<KDTTestGradingEntity> kdtTestGradingUpdate(KDTTestGradingRequestDTO requestDTO) {
        List<KDTTestGradingEntity> updatedEntities = new ArrayList<>();

        // requestDTO의 kdtTestGradingList를 순회하면서 각각의 엔티티를 업데이트
        for (KDTTestGradingDTO gradingDTO : requestDTO.getKdtTestGradingList()) {
            KDTTestGradingEntity gradingEntity = kdtTestGradingRepository.findByKdtTestGradingId(gradingDTO.getKdtTestGradingId());
            gradingDTO.setKdtTestGradingUpdatedAt(LocalDateTime.now());
            gradingEntity.update(gradingDTO);
            updatedEntities.add(gradingEntity);
        }
        kdtTestGradingRepository.saveAll(updatedEntities);

        // 업데이트된 엔티티 리스트 반환
        return updatedEntities;  // 업데이트된 채점 결과 리스트 반환
    }

    @Override
    public List<KDTTestGradingDTO> findKdtTestGradingDTOByTestIdAndPartId(Long kdtTestId, Long kdtPartId) {
        List<KDTTestGradingEntity> entities = kdtTestGradingRepository.findByKdtTestSubmitEntity_KdtTestItemEntity_KdtTestEntity_kdtTestIdAndKdtTestSubmitEntity_KdtPartEntity_kdtPartId(kdtTestId, kdtPartId);

        // 엔티티 리스트를 DTO 리스트로 변환
        List<KDTTestGradingDTO> dtoList = entities.stream()
                .map(this::convertToTestGradingDTO)  // convertToTestSubmitDTO 메서드를 이용해 각 엔티티를 DTO로 변환
                .collect(Collectors.toList());

        return dtoList;
    }

    @Override
    public List<Map<String, Object>> getAllStudentsTestStats(Long kdtSessionId) {
        // 1. 해당 kdtSessionId에 속한 모든 시험을 조회
        List<KDTTestEntity> testEntities = kdtTestRepository.findByKdtSessionEntity_KdtSessionId(kdtSessionId);

        List<Map<String, Object>> resultList = new ArrayList<>();

        // 2. 각 시험에 대해 정답률 평균과 표준편차 계산
        for (KDTTestEntity testEntity : testEntities) {
            Long testId = testEntity.getKdtTestId();

            // 3. 시험에 해당하는 모든 시험문제를 조회
            List<KDTTestItemEntity> testItems = kdtTestItemRepository.findByKdtTestEntity_kdtTestId(testId);

            // 4. 해당 시험에 대한 채점 결과 조회
            List<KDTTestGradingEntity> allGradingEntities = kdtTestGradingRepository.findByKdtTestSubmitEntity_KdtTestItemEntity_KdtTestEntity_kdtTestId(testId);

            // 5. 학생들의 정답률 계산
            List<Double> studentCorrectRates = new ArrayList<>();

            // 카테고리별 정답률 목록 (카테고리별로 나눠서 계산)
            Map<String, List<Double>> categoryCorrectRates = new HashMap<>();

            // 각 채점 결과에 대해 계산
            for (KDTTestGradingEntity grading : allGradingEntities) {
                // 해당 채점에서 학생이 얻은 정답률을 계산
                double totalCorrectRate = 0;

                for (KDTTestItemEntity testItem : testItems) {
                    // 해당 문제의 배점
                    double itemScore = testItem.getKdtTestItemScore();
                    // 해당 문제의 학생 채점 점수
                    double studentScore = grading.getKdtTestGradingScore();

                    // 정답률 계산: 학생 점수 / 문제 배점
                    double correctRate = studentScore / itemScore;
                    totalCorrectRate += correctRate;

                    // 카테고리별로 정답률 저장
                    String category = testItem.getKdtTestItemCategory();
                    categoryCorrectRates
                            .computeIfAbsent(category, k -> new ArrayList<>())
                            .add(correctRate);
                }

                // 평균 정답률을 계산한 후 리스트에 추가
                double averageCorrectRate = totalCorrectRate / testItems.size();
                studentCorrectRates.add(averageCorrectRate);
            }

            // 6. 평균 정답률 계산
            double averageCorrectRate = studentCorrectRates.stream().mapToDouble(Double::doubleValue).average().orElse(0);

            // 7. 정답률 표준편차 계산
            double variance = studentCorrectRates.stream()
                    .mapToDouble(rate -> Math.pow(rate - averageCorrectRate, 2))
                    .sum() / studentCorrectRates.size();
            double standardDeviation = Math.sqrt(variance);

            // 8. 카테고리별 평균 정답률 및 표준편차 계산
            Map<String, Map<String, Double>> categoryStats = new HashMap<>();
            for (Map.Entry<String, List<Double>> entry : categoryCorrectRates.entrySet()) {
                String category = entry.getKey();
                List<Double> correctRates = entry.getValue();

                // 평균 정답률 계산
                double categoryAverage = correctRates.stream().mapToDouble(Double::doubleValue).average().orElse(0);

                // 표준편차 계산
                double categoryVariance = correctRates.stream()
                        .mapToDouble(rate -> Math.pow(rate - categoryAverage, 2))
                        .sum() / correctRates.size();
                double categoryStandardDeviation = Math.sqrt(categoryVariance);

                // 카테고리별 통계 저장
                Map<String, Double> categoryResult = new HashMap<>();
                categoryResult.put("averageCorrectRate", categoryAverage);
                categoryResult.put("standardDeviation", categoryStandardDeviation);

                categoryStats.put(category, categoryResult);
            }

            // 9. 결과를 Map에 저장
            Map<String, Object> result = new HashMap<>();
            result.put("testId", testId);
            result.put("testTitle", testEntity.getKdtTestTitle()); // testTitle 추가
            result.put("averageCorrectRate", averageCorrectRate);
            result.put("standardDeviation", standardDeviation);

            // 카테고리 통계가 존재할 경우에만 포함시킴
            if (!categoryStats.isEmpty()) {
                result.put("categories", categoryStats); // 카테고리별 통계 추가
            }
            // 10. 결과를 List에 추가
            resultList.add(result);
        }

        return resultList;
    }

    @Override
    public List<Map<String, Object>> getAgeGroupStudentsTestStats(Long kdtSessionId) {
        // 1. 해당 kdtSessionId에 속한 모든 시험을 조회
        List<KDTTestEntity> testEntities = kdtTestRepository.findByKdtSessionEntity_KdtSessionId(kdtSessionId);

        List<Map<String, Object>> resultList = new ArrayList<>();

        // 2. 각 시험에 대해 연령대별 성적 통계를 계산
        for (KDTTestEntity testEntity : testEntities) {
            Long testId = testEntity.getKdtTestId();

            // 3. 시험에 해당하는 모든 시험문제를 조회
            List<KDTTestItemEntity> testItems = kdtTestItemRepository.findByKdtTestEntity_kdtTestId(testId);

            // 4. 해당 시험에 대한 채점 결과 조회
            List<KDTTestGradingEntity> allGradingEntities = kdtTestGradingRepository.findByKdtTestSubmitEntity_KdtTestItemEntity_KdtTestEntity_kdtTestId(testId);

            // 5. 연령대별 성적 통계 계산
            Map<String, List<Double>> ageGroupCorrectRates = new HashMap<>();
            // 연령대별 카테고리별 성적 통계
            Map<String, Map<String, List<Double>>> ageGroupCategoryCorrectRates = new HashMap<>();

            // 각 채점 결과에 대해 계산
            for (KDTTestGradingEntity grading : allGradingEntities) {
                // 해당 학생의 생년월일을 가져와서 연령대 그룹핑
                LocalDate birthDate = grading.getKdtTestSubmitEntity().getKdtPartEntity().getUserEntity().getUserBirth(); // 예시로 getUserEntity로부터 birthDate를 가져옴
                String ageGroup = getAgeGroup(birthDate); // 연령대 그룹을 얻음

                // 해당 채점에서 학생이 얻은 정답률을 계산
                double totalCorrectRate = 0;

                for (KDTTestItemEntity testItem : testItems) {
                    // 해당 문제의 배점
                    double itemScore = testItem.getKdtTestItemScore();
                    // 해당 문제의 학생 채점 점수
                    double studentScore = grading.getKdtTestGradingScore();

                    // 정답률 계산: 학생 점수 / 문제 배점
                    double correctRate = studentScore / itemScore;
                    totalCorrectRate += correctRate;

                    // 연령대별 카테고리별 정답률 저장
                    String category = testItem.getKdtTestItemCategory();
                    ageGroupCategoryCorrectRates
                            .computeIfAbsent(ageGroup, k -> new HashMap<>())
                            .computeIfAbsent(category, k -> new ArrayList<>())
                            .add(correctRate);
                }

                // 평균 정답률을 계산한 후 연령대별로 추가
                double averageCorrectRate = totalCorrectRate / testItems.size();
                ageGroupCorrectRates.computeIfAbsent(ageGroup, k -> new ArrayList<>()).add(averageCorrectRate);
            }

            // 6. 연령대별 평균 정답률과 표준편차 계산
            Map<String, Map<String, Double>> ageGroupStats = new HashMap<>();
            Map<String, Map<String, Map<String, Double>>> ageGroupCategoryStats = new HashMap<>();

            for (Map.Entry<String, List<Double>> entry : ageGroupCorrectRates.entrySet()) {
                String ageGroup = entry.getKey();
                List<Double> correctRates = entry.getValue();

                // 평균 정답률 계산
                double ageGroupAverage = correctRates.stream().mapToDouble(Double::doubleValue).average().orElse(0);

                // 표준편차 계산
                double ageGroupVariance = correctRates.stream()
                        .mapToDouble(rate -> Math.pow(rate - ageGroupAverage, 2))
                        .sum() / correctRates.size();
                double ageGroupStandardDeviation = Math.sqrt(ageGroupVariance);

                // 연령대별 통계 저장
                Map<String, Double> ageGroupResult = new HashMap<>();
                ageGroupResult.put("averageCorrectRate", ageGroupAverage);
                ageGroupResult.put("standardDeviation", ageGroupStandardDeviation);

                ageGroupStats.put(ageGroup, ageGroupResult);

                // 카테고리별 통계 계산
                Map<String, Map<String, Double>> categoryStats = new HashMap<>();
                Map<String, List<Double>> categoryRates = ageGroupCategoryCorrectRates.get(ageGroup);
                if (categoryRates != null) {
                    for (Map.Entry<String, List<Double>> categoryEntry : categoryRates.entrySet()) {
                        String category = categoryEntry.getKey();
                        List<Double> categoryCorrectRates = categoryEntry.getValue();

                        // 카테고리별 평균 정답률
                        double categoryAverage = categoryCorrectRates.stream().mapToDouble(Double::doubleValue).average().orElse(0);

                        // 카테고리별 표준편차
                        double categoryVariance = categoryCorrectRates.stream()
                                .mapToDouble(rate -> Math.pow(rate - categoryAverage, 2))
                                .sum() / categoryCorrectRates.size();
                        double categoryStandardDeviation = Math.sqrt(categoryVariance);

                        // 카테고리별 통계 저장
                        Map<String, Double> categoryResult = new HashMap<>();
                        categoryResult.put("averageCorrectRate", categoryAverage);
                        categoryResult.put("standardDeviation", categoryStandardDeviation);

                        categoryStats.put(category, categoryResult);
                    }
                }

                // 연령대별 카테고리별 통계 저장
                if (!categoryStats.isEmpty()) {
                    ageGroupCategoryStats.put(ageGroup, categoryStats);
                }
            }

            // 7. 결과를 Map에 저장
            Map<String, Object> result = new HashMap<>();
            result.put("testId", testId);
            result.put("testTitle", testEntity.getKdtTestTitle()); // testTitle 추가

            if (!ageGroupStats.isEmpty()) {
                result.put("ageGroups", ageGroupStats); // 연령대별 통계 추가
            }
            if (!ageGroupCategoryStats.isEmpty()) {
                result.put("ageGroupCategories", ageGroupCategoryStats);// 연령대별 카테고리별 통계 추가
            }

            // 8. 결과를 List에 추가
            resultList.add(result);
        }

        return resultList;
    }

    @Override
    public List<Map<String, Object>> getGenderGroupStudentsTestStats(Long kdtSessionId) {
        // 1. 해당 kdtSessionId에 속한 모든 시험을 조회
        List<KDTTestEntity> testEntities = kdtTestRepository.findByKdtSessionEntity_KdtSessionId(kdtSessionId);

        List<Map<String, Object>> resultList = new ArrayList<>();

        // 2. 각 시험에 대해 성별별 성적 통계를 계산
        for (KDTTestEntity testEntity : testEntities) {
            Long testId = testEntity.getKdtTestId();

            // 3. 시험에 해당하는 모든 시험문제를 조회
            List<KDTTestItemEntity> testItems = kdtTestItemRepository.findByKdtTestEntity_kdtTestId(testId);

            // 4. 해당 시험에 대한 채점 결과 조회
            List<KDTTestGradingEntity> allGradingEntities = kdtTestGradingRepository.findByKdtTestSubmitEntity_KdtTestItemEntity_KdtTestEntity_kdtTestId(testId);

            // 5. 성별별 성적 통계 계산
            Map<String, List<Double>> genderGroupCorrectRates = new HashMap<>();
            // 성별별 카테고리별 성적 통계
            Map<String, Map<String, List<Double>>> genderGroupCategoryCorrectRates = new HashMap<>();

            // 각 채점 결과에 대해 계산
            for (KDTTestGradingEntity grading : allGradingEntities) {
                // 해당 학생의 성별을 가져와서 성별 그룹핑
                String gender = grading.getKdtTestSubmitEntity().getKdtPartEntity().getUserEntity().getUserGender(); // 예시로 getUserEntity로부터 gender를 가져옴

                // 해당 채점에서 학생이 얻은 정답률을 계산
                double totalCorrectRate = 0;

                for (KDTTestItemEntity testItem : testItems) {
                    // 해당 문제의 배점
                    double itemScore = testItem.getKdtTestItemScore();
                    // 해당 문제의 학생 채점 점수
                    double studentScore = grading.getKdtTestGradingScore();

                    // 정답률 계산: 학생 점수 / 문제 배점
                    double correctRate = studentScore / itemScore;
                    totalCorrectRate += correctRate;

                    // 성별별 카테고리별 정답률 저장
                    String category = testItem.getKdtTestItemCategory();
                    genderGroupCategoryCorrectRates
                            .computeIfAbsent(gender, k -> new HashMap<>())
                            .computeIfAbsent(category, k -> new ArrayList<>())
                            .add(correctRate);
                }

                // 평균 정답률을 계산한 후 성별별로 추가
                double averageCorrectRate = totalCorrectRate / testItems.size();
                genderGroupCorrectRates.computeIfAbsent(gender, k -> new ArrayList<>()).add(averageCorrectRate);
            }

            // 6. 성별별 평균 정답률과 표준편차 계산
            Map<String, Map<String, Double>> genderGroupStats = new HashMap<>();
            Map<String, Map<String, Map<String, Double>>> genderGroupCategoryStats = new HashMap<>();

            for (Map.Entry<String, List<Double>> entry : genderGroupCorrectRates.entrySet()) {
                String gender = entry.getKey();
                List<Double> correctRates = entry.getValue();

                // 평균 정답률 계산
                double genderAverage = correctRates.stream().mapToDouble(Double::doubleValue).average().orElse(0);

                // 표준편차 계산
                double genderVariance = correctRates.stream()
                        .mapToDouble(rate -> Math.pow(rate - genderAverage, 2))
                        .sum() / correctRates.size();
                double genderStandardDeviation = Math.sqrt(genderVariance);

                // 성별별 통계 저장
                Map<String, Double> genderResult = new HashMap<>();
                genderResult.put("averageCorrectRate", genderAverage);
                genderResult.put("standardDeviation", genderStandardDeviation);

                genderGroupStats.put(gender, genderResult);

                // 카테고리별 통계 계산
                Map<String, Map<String, Double>> categoryStats = new HashMap<>();
                Map<String, List<Double>> categoryRates = genderGroupCategoryCorrectRates.get(gender);
                if (categoryRates != null) {
                    for (Map.Entry<String, List<Double>> categoryEntry : categoryRates.entrySet()) {
                        String category = categoryEntry.getKey();
                        List<Double> categoryCorrectRates = categoryEntry.getValue();

                        // 카테고리별 평균 정답률
                        double categoryAverage = categoryCorrectRates.stream().mapToDouble(Double::doubleValue).average().orElse(0);

                        // 카테고리별 표준편차
                        double categoryVariance = categoryCorrectRates.stream()
                                .mapToDouble(rate -> Math.pow(rate - categoryAverage, 2))
                                .sum() / categoryCorrectRates.size();
                        double categoryStandardDeviation = Math.sqrt(categoryVariance);

                        // 카테고리별 통계 저장
                        Map<String, Double> categoryResult = new HashMap<>();
                        categoryResult.put("averageCorrectRate", categoryAverage);
                        categoryResult.put("standardDeviation", categoryStandardDeviation);

                        categoryStats.put(category, categoryResult);
                    }
                }

                // 성별별 카테고리별 통계 저장
                if (!categoryStats.isEmpty()) {
                    genderGroupCategoryStats.put(gender, categoryStats);
                }
            }

            // 7. 결과를 Map에 저장
            Map<String, Object> result = new HashMap<>();
            result.put("testId", testId);
            result.put("testTitle", testEntity.getKdtTestTitle()); // testTitle 추가

            if (!genderGroupStats.isEmpty()) {
                result.put("genderGroups", genderGroupStats); // 성별별 통계 추가
            }
            if (!genderGroupCategoryStats.isEmpty()) {
                result.put("genderGroupCategories", genderGroupCategoryStats); // 성별별 카테고리별 통계 추가
            }

            // 8. 결과를 List에 추가
            resultList.add(result);
        }

        return resultList;
    }

    public KDTTestSubmitEntity convertToTestSubmitEntity(KDTTestSubmitDTO dto) {
        KDTTestItemEntity kdtTestItemEntity = kdtTestItemRepository.findByKdtTestItemId(dto.getKdtTestItemId());
        KDTPartEntity kdtPartEntity = kdtPartRepository.findByKdtPartId(dto.getKdtPartId());

        return KDTTestSubmitEntity.builder()
                .kdtTestSubmitId(dto.getKdtTestSubmitId())
                .kdtTestItemEntity(kdtTestItemEntity)
                .kdtPartEntity(kdtPartEntity)
                .kdtTestSubmitAnswer(dto.getKdtTestSubmitAnswer())
                .kdtTestSubmitCreatedAt(dto.getKdtTestSubmitCreatedAt())
                .kdtTestSubmitUpdatedAt(dto.getKdtTestSubmitUpdatedAt())
                .build();
    }

    public KDTTestSubmitDTO convertToTestSubmitDTO(KDTTestSubmitEntity entity) {
        // 새로운 KDTTestSubmitDTO 객체 생성
        KDTTestSubmitDTO dto = new KDTTestSubmitDTO();

        // 엔티티의 값을 DTO로 복사
        dto.setKdtTestSubmitId(entity.getKdtTestSubmitId());
        dto.setKdtTestItemId(entity.getKdtTestItemEntity().getKdtTestItemId());
        dto.setKdtPartId(entity.getKdtPartEntity().getKdtPartId());
        dto.setKdtTestSubmitAnswer(entity.getKdtTestSubmitAnswer());
        dto.setKdtTestSubmitCreatedAt(entity.getKdtTestSubmitCreatedAt());
        dto.setKdtTestSubmitUpdatedAt(entity.getKdtTestSubmitUpdatedAt());

        return dto;
    }

    public KDTTestGradingDTO convertToTestGradingDTO(KDTTestGradingEntity entity) {
        // 새로운 KDTTestSubmitDTO 객체 생성
        KDTTestGradingDTO dto = new KDTTestGradingDTO();

        // 엔티티의 값을 DTO로 복사
        dto.setKdtTestGradingId(entity.getKdtTestGradingId());
        dto.setKdtTestSubmitId(entity.getKdtTestSubmitEntity().getKdtTestSubmitId());
        dto.setUserId(entity.getUserEntity().getUserId());
        dto.setKdtTestGradingScore(entity.getKdtTestGradingScore());
        dto.setKdtTestGradingCreatedAt(entity.getKdtTestGradingCreatedAt());
        dto.setKdtTestGradingUpdatedAt(entity.getKdtTestGradingUpdatedAt());
        dto.setKdtTestGradingComment(entity.getKdtTestGradingComment());
        return dto;
    }

    public KDTTestListDTO convertToTestListDTO(KDTTestEntity entity){
        Long sessionId = entity.getKdtSessionEntity().getKdtSessionId();
        Long kdtTestId = entity.getKdtTestId();
        int totalCnt = kdtPartRepository.findByKdtSessionEntity_KdtSessionId(sessionId).size();
        int actCnt = (int) kdtTestSubmitRepository.countDistinctParticipants(kdtTestId);

        // 학생들의 점수를 계산하고, 해당 점수와 partId를 매핑한 맵을 반환
        List<Long> partIds = kdtPartRepository.findByKdtSessionEntity_KdtSessionId(sessionId).stream()
                .map(KDTPartEntity::getKdtPartId)
                .collect(Collectors.toList());

        // calculateScores 메서드를 이용하여 점수를 계산하고 맵으로 반환받음
        Map<Long, Integer> partIdToScoreMap = calculateScores(kdtTestId, partIds);

        // 점수 리스트로 변환
        List<Integer> studentScores = new ArrayList<>(partIdToScoreMap.values());

        // 표준편차 계산
        double stdDev = calculateStandardDeviation(studentScores);

        KDTTestListDTO dto = new KDTTestListDTO();

        // 필수 값들을 DTO에 셋팅
        dto.setKdtTestId(kdtTestId);
        dto.setKdtTestTitle(entity.getKdtTestTitle());
        dto.setUserId(entity.getUserEntity().getUserId());
        dto.setAuthorName(entity.getUserEntity().getName());
        dto.setKdtTestCreatedAt(entity.getKdtTestCreatedAt());
        dto.setKdtTestStartDate(entity.getKdtTestStartDate());
        dto.setKdtTestEndDate(entity.getKdtTestEndDate());
        dto.setActualCnt(actCnt);
        dto.setTotalCnt(totalCnt);
        dto.setStdDev(stdDev);

        return dto;
    }

    public KDTTestStudentListDTO convertToTestStudentListDTO(KDTTestEntity entity, Long partId){
        Long kdtTestId = entity.getKdtTestId();
        Long sessionId = entity.getKdtSessionEntity().getKdtSessionId();
        KDTTestStudentListDTO dto = new KDTTestStudentListDTO();

        // 필수 값들을 DTO에 셋팅
        dto.setKdtTestId(kdtTestId);        // TestId
        dto.setKdtTestTitle(entity.getKdtTestTitle());      // TestTitle
        dto.setAuthorName(entity.getUserEntity().getName());        // 시험 출제자 이름
        dto.setAuthorUserId(entity.getUserEntity().getUserId());    // 시험 출제자 ID


        dto.setKdtTestCreatedAt(formatDateTime(entity.getKdtTestCreatedAt()));      // 시험 출제일
        dto.setKdtTestStartDate(formatDateTime(entity.getKdtTestStartDate()));      // 시험 시작 시간
        dto.setKdtTestEndDate(formatDateTime(entity.getKdtTestEndDate()));          // 시험 마감 시간

        dto.setStatus(false);
        dto.setAvailable(false);

        if(LocalDateTime.now().isAfter(entity.getKdtTestStartDate())) {
            // 만약 시험 마감시간이 지나지 않았다면, 점수 및 백분율 정보 전달 x
            if (LocalDateTime.now().isBefore(entity.getKdtTestEndDate())) {
                dto.setAvailable(true);
            }else {
                dto.setStatus(true);
            }
        }

        // 제출일/수정일
        List<KDTTestSubmitEntity> kdtTestSubmitEntities = kdtTestSubmitRepository.findByKdtTestItemEntity_KdtTestEntity_kdtTestIdAndKdtPartEntity_kdtPartId(kdtTestId, partId);
        if(kdtTestSubmitEntities==null || kdtTestSubmitEntities.isEmpty()){
                dto.setKdtTestSubmitLastDate("미응시");
                dto.setStatus(false);
                return dto;
        }


        if(kdtTestSubmitEntities.get(0).getKdtTestSubmitUpdatedAt() != null){
            dto.setKdtTestSubmitLastDate(formatDateTime(kdtTestSubmitEntities.get(0).getKdtTestSubmitUpdatedAt()));
        }else{
            dto.setKdtTestSubmitLastDate(formatDateTime(kdtTestSubmitEntities.get(0).getKdtTestSubmitCreatedAt()));
        }



        // 최대 점수
        List<KDTTestItemEntity> testItemEntities = kdtTestItemRepository.findByKdtTestEntity_kdtTestId(kdtTestId);
        int maxScore = 0;
        for(KDTTestItemEntity testItemEntity : testItemEntities){
            maxScore+= testItemEntity.getKdtTestItemScore();
        }
        dto.setMaxScore(maxScore);

        // 학생들의 점수를 계산하고, 해당 점수와 partId를 매핑한 맵을 반환
        List<Long> partIds = kdtPartRepository.findByKdtSessionEntity_KdtSessionId(sessionId).stream()
                .map(KDTPartEntity::getKdtPartId)
                .collect(Collectors.toList());

        // 백분위 계산을 위한 준비 : 점수 구하기
        Map<Long, Integer> partIdToScoreMap = calculateScores(kdtTestId, partIds);

        // 점수의 List화
        List<Integer> scores = new ArrayList<>(partIdToScoreMap.values());

        // 점수 정렬 후 순위 계산
        Collections.sort(scores, Collections.reverseOrder());  // 내림차순 정렬 (높은 점수부터)
            // 실제 점수
        int score = 0;
        if(partIdToScoreMap.get(partId) != null) {
            score = partIdToScoreMap.get(partId);
        }
        dto.setActualScore(score);

        if(partIdToScoreMap.get(partId) != null) {
            // 백분위 계산
            int rank = scores.indexOf(score) + 1; // 순위는 1부터 시작
            double percentile = ((double) rank / scores.size()) * 100;
            dto.setPercentile(percentile);
        }
        return dto;
    }

    public double calculateStandardDeviation(List<Integer> scores) {
        if (scores == null || scores.isEmpty()) {
            return 0.0;
        }

        // 평균 계산
        double sum = 0.0;
        for (int score : scores) {
            sum += score;
        }
        double mean = sum / scores.size();

        // 분산 계산 (제곱차의 평균)
        double varianceSum = 0.0;
        for (int score : scores) {
            varianceSum += Math.pow(score - mean, 2);
        }
        double variance = varianceSum / scores.size();

        // 표준 편차 계산 (분산의 제곱근)
        return Math.sqrt(variance);
    }

    // 학생들의 점수를 계산하고, 해당 점수와 partId를 매핑한 맵을 반환하는 메서드
    private Map<Long, Integer> calculateScores(Long kdtTestId, List<Long> kdtPartIds) {
        Map<Long, Integer> partIdToScoreMap = new HashMap<>();

        // 각 학생의 점수 계산
        for (Long partId : kdtPartIds) {
            int score = 0;
            List<KDTTestGradingEntity> gradingEntities = kdtTestGradingRepository
                    .findByKdtTestSubmitEntity_KdtTestItemEntity_KdtTestEntity_kdtTestIdAndKdtTestSubmitEntity_KdtPartEntity_kdtPartId(kdtTestId, partId);
            // 미 응시자의 경우 백분위 계산에서 제외
            if (gradingEntities== null || !gradingEntities.isEmpty()){
                for (KDTTestGradingEntity gradingEntity : gradingEntities) {
                    score += gradingEntity.getKdtTestGradingScore();
                }
                partIdToScoreMap.put(partId, score);  // 점수를 partId와 매핑하여 저장
            }
        }
        return partIdToScoreMap;
    }
    // KDTTestItemEntity를 KDTTestItemDTO로 변환하는 메서드
    public KDTTestItemDTO convertToTestItemDTO(KDTTestItemEntity entity) {
        KDTTestItemDTO dto = new KDTTestItemDTO();

        dto.setKdtTestItemId(entity.getKdtTestItemId());
        dto.setKdtTestItemQuest(entity.getKdtTestItemQuest());
        dto.setKdtTestItemAnswer(entity.getKdtTestItemAnswer());
        dto.setKdtTestItemAnsw1(entity.getKdtTestItemAnsw1());
        dto.setKdtTestItemAnsw2(entity.getKdtTestItemAnsw2());
        dto.setKdtTestItemAnsw3(entity.getKdtTestItemAnsw3());
        dto.setKdtTestItemAnsw4(entity.getKdtTestItemAnsw4());
        dto.setKdtTestItemScore(entity.getKdtTestItemScore());
        dto.setKdtTestItemCategory(entity.getKdtTestItemCategory());

        return dto;
    }

    private String getAgeGroup(LocalDate birthDate) {
        // 나이 계산
        int age = Period.between(birthDate, LocalDate.now()).getYears();

        // 나이대 그룹 반환
        if (age >= 16 && age <= 19) {
            return "10대 후반";
        } else if (age >= 20 && age <= 24) {
            return "20대 초반";
        } else if (age >= 25 && age <= 29) {
            return "20대 중반";
        } else if (age >= 30 && age <= 34) {
            return "20대 후반";
        } else if (age >= 35 && age <= 39) {
            return "30대 초반";
        } else if (age >= 40 && age <= 44) {
            return "30대 중반";
        } else {
            return "기타";
        }
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "N/A"; // null 값 처리
        }
        // yyyy년 MM월 dd일 hh:mm a 형식으로 포맷
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd a hh:mm");
        return dateTime.format(formatter);
    }


}
