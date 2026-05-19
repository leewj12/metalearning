package com.Meta_learning.KDT.KDTDTO.KDTTestDTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class KDTTestItemDTO {
    private Long kdtTestItemId;
    private Long kdtTestId;  // 시험id
    private String kdtTestItemQuest;
    private int kdtTestItemAnswer;
    private String kdtTestItemAnsw1;
    private String kdtTestItemAnsw2;
    private String kdtTestItemAnsw3;
    private String kdtTestItemAnsw4;
    private int kdtTestItemScore;
    private String kdtTestItemCategory;

    public void deleteTestItemAnswer(){
        this.kdtTestItemAnswer=0;
    }
}
