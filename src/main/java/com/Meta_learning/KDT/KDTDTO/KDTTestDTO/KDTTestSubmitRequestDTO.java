package com.Meta_learning.KDT.KDTDTO.KDTTestDTO;

import lombok.*;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class KDTTestSubmitRequestDTO {
    private List<KDTTestSubmitDTO> kdtTestSubmitList;
    private Long kdtPartId;
}
