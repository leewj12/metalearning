package com.Meta_learning.user.userentity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserStatus {
    ACTIVE("활동중"),
    INACTIVE("휴면계정"),
    BANNED("정지계정");

    private final String text;
}