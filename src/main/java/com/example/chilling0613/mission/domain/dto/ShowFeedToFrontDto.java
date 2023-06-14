package com.example.chilling0613.mission.domain.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShowFeedToFrontDto {
    private Integer missionId;
    private LocalDateTime localDateTime;
    private String uuid;
    private List<String> stringAndPath;
}
