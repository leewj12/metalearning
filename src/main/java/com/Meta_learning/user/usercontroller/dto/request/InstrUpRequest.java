package com.Meta_learning.user.usercontroller.dto.request;

import com.Meta_learning.course.courseentity.CourseEntity;
import com.Meta_learning.course.courseservice.requset.CourseDescriptCreateServiceRequest;
import com.Meta_learning.course.courseservice.requset.InstrCreateServiceRequest;
import com.Meta_learning.user.userentity.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class InstrUpRequest {

    @NotBlank(message = "강사 소개는 반드시 입력해야 합니다.")
    private String instrDescript;

    @NotBlank(message = "강사 소속은 반드시 입력해야 합니다.")
    private String instrCompany;

    @Builder
    public InstrUpRequest(String instrDescript, String instrCompany) {
        this.instrDescript = instrDescript;
        this.instrCompany = instrCompany;
    }

    public InstrCreateServiceRequest toInstrCreateServiceRequest(UserEntity userEntity){
        return InstrCreateServiceRequest.builder()
                .userEntity(userEntity)
                .instrDescript(instrDescript)
                .instrCompany(instrCompany)
                .build();
    }
}

