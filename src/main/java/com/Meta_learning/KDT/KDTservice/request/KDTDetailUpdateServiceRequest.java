package com.Meta_learning.KDT.KDTservice.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KDTDetailUpdateServiceRequest {

    private String kdtDetailContent;

    @Builder
    private KDTDetailUpdateServiceRequest(String kdtDetailContent) {
        this.kdtDetailContent = kdtDetailContent;
    }
}

