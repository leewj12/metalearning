package com.Meta_learning.course.courseentity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CourseDifficulty {
    BEGINNER("입문"),  // 입문
    BASIC("초급"),     // 초급
    INTERMEDIATE("중급"), // 중급
    ADVANCED("고급");   // 고급

    private final String text;
}
