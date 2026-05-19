package com.Meta_learning.KDT.KDTDTO.KDTTestDTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class KDTTestSubmitDTO {
    private Long kdtTestSubmitId;
    private Long kdtTestItemId;
    private Long kdtPartId;
    private int kdtTestSubmitAnswer;
    private LocalDateTime kdtTestSubmitCreatedAt;
    private LocalDateTime kdtTestSubmitUpdatedAt;
}

