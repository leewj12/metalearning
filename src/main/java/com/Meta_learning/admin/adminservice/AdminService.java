package com.Meta_learning.admin.adminservice;

import com.Meta_learning.admin.admindashboarddto.UserCountDTO;
import com.Meta_learning.admin.admindashboarddto.UserRoleDTO;

import java.util.List;

public interface AdminService {

    //권한별로 유저 카운터 하는거임 통계자료
    UserRoleDTO userRoleAll(); // 유저 정보 가져오기

    //년 월 별로 유저 카운터 하는거임 통계자료
    List<UserCountDTO> userCount();

}
