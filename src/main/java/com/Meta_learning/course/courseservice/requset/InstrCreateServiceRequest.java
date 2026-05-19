package com.Meta_learning.course.courseservice.requset;

import com.Meta_learning.user.userentity.UserEntity;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class InstrCreateServiceRequest {

    private UserEntity userEntity;
    private String instrDescript;
    private String instrCompany;

    @Builder
    public InstrCreateServiceRequest(UserEntity userEntity, String instrDescript, String instrCompany) {
        this.userEntity = userEntity;
        this.instrDescript = instrDescript;
        this.instrCompany = instrCompany;
    }
}
