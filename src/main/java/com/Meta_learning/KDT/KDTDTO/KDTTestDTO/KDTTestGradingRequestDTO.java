package com.Meta_learning.KDT.KDTDTO.KDTTestDTO;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class KDTTestGradingRequestDTO {
    private List<KDTTestGradingDTO> kdtTestGradingList;
    private Long kdtTestId;
    private Long kdtPartId;
}
