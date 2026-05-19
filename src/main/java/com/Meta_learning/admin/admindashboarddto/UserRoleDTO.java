package com.Meta_learning.admin.admindashboarddto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class UserRoleDTO {

    private Long adminTotal;
    private Long managerTotal;
    private Long instructorTotal;
    private Long studentTotal;



}
