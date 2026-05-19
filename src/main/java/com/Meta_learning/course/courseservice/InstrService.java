package com.Meta_learning.course.courseservice;

import com.Meta_learning.KDT.KDTDTO.KDTSessionDTO.KDTInstrSessionDTO;
import com.Meta_learning.admin.dto.response.InstrCreateResponse;
import com.Meta_learning.course.courseservice.requset.InstrCreateServiceRequest;
import com.Meta_learning.user.userentity.UserEntity;

import java.util.List;

public interface InstrService {

    void createInstrUp(InstrCreateServiceRequest instrCreateServiceRequest);

    boolean hasInstrUp(UserEntity user);

    List<InstrCreateResponse> getAllInstrRequests();

    void approveInstr(String email);

    void rejectInstr(String email);

    boolean existsByUserId(Long userId);

    List<KDTInstrSessionDTO> getInstrSessionByUser(Long userId);

}
