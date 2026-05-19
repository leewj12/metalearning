package com.Meta_learning.admin.adminservice;

import com.Meta_learning.admin.admindashboarddto.UserCountDTO;
import com.Meta_learning.admin.admindashboarddto.UserRoleDTO;
import com.Meta_learning.user.userrepository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminServiceImpl implements  AdminService {

    private final UserRepository userRepository;

    @Override
    public UserRoleDTO userRoleAll() {
        // 각 권한별 사용자 수를 카운트
        long adminTotal = userRepository.countByUserRole("ADMIN");
        long managerTotal = userRepository.countByUserRole("MANAGER");
        long instructorTotal = userRepository.countByUserRole("INSTRUCTOR");
        long studentTotal = userRepository.countByUserRole("STUDENT");

        // UserRoleDTO에 카운트 값 설정
        UserRoleDTO userRoleDTO = new UserRoleDTO(adminTotal, managerTotal, instructorTotal, studentTotal);

        return userRoleDTO;
    }

    @Override
    public List<UserCountDTO> userCount() {
        List<UserCountDTO> userCountList = new ArrayList<>();

        // DB에서 '학생' 권한을 가진 유저만 연도별, 월별로 카운트
        List<Object[]> results = userRepository.countStudentsByYearAndMonth();

        // 결과가 있으면 DTO로 변환하여 리스트에 추가
        for (Object[] result : results) {
            int year = (int) result[0];  // 년도
            int month = (int) result[1];  // 월
            long count = (long) result[2];  // 학생 수

            userCountList.add(new UserCountDTO(year, month, count));
        }

        return userCountList;
    }
}

