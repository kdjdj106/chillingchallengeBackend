package com.example.chilling0613.mission.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompleteMissionToBackDto {
    private String usercode;
    private Integer missionType;
    private Integer missionId;
    private List<String> stringAndPath;
    // 미션1  (단순 텍스트)


}
