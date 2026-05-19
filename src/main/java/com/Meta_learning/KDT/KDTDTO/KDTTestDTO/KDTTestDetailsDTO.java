package com.Meta_learning.KDT.KDTDTO.KDTTestDTO;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class KDTTestDetailsDTO {
    private KDTTestDTO kdtTest;
    private List<KDTTestItemDTO> kdtTestItems;
    private List<Long> kdtTestItemDeleteId; //삭제할 리스트 Id;
}
