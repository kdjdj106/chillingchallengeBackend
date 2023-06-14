package com.example.chilling0613.mission.domain.service;

import com.example.chilling0613.mission.domain.dto.CompleteMissionToBackDto;
import com.example.chilling0613.mission.domain.entity.BoardMission1;
import com.example.chilling0613.mission.domain.entity.BoardMission2;
import com.example.chilling0613.mission.domain.entity.BoardMission3;
import com.example.chilling0613.mission.domain.repository.BoardMission1Repository;
import com.example.chilling0613.mission.domain.repository.BoardMission2Repository;
import com.example.chilling0613.mission.domain.repository.BoardMission3Repository;
import com.example.chilling0613.oauth2jwt.domain.entity.User;
import com.example.chilling0613.oauth2jwt.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MissionService {
    private final UserRepository userRepository;
    private final BoardMission3Repository boardMission3Repository;
    private final BoardMission2Repository boardMission2Repository;
    private final BoardMission1Repository boardMission1Repository;

    LocalDate today = LocalDate.now();
    int todayNum = today.getDayOfMonth();

    String lastAttendance = Integer.toString(todayNum);

    public User completeMission(List<CompleteMissionToBackDto> list) throws Exception {
        String usercode = list.get(0).getUsercode().toString();
        Optional<User> optionalUser = userRepository.findByUsercode(usercode);
        // 마지막 출석일자와 오늘을 비교하여연속 출석일수 증가 or 초기화



        if (optionalUser.isPresent()){
            User user = optionalUser.get();
            for (int i = 0; i < list.size(); i++) {
                switch (list.get(i).getMissionType()){
                    case 1:
                        writeBoardMission1(list.get(i).getStringAndPath(), user, list.get(i).getMissionId());
                        break;
                    case 2:
                        writeBoardMission2(list.get(i).getStringAndPath(), user, list.get(i).getMissionId());
                        break;
                    case 3:
                        writeBoardMission3(list.get(i).getStringAndPath(), user, list.get(i).getMissionId());
                        break;
                }

            }


            user.setMissionCnt(user.getMissionCnt()+1);

            // 마지막 출석일자가 없다면 새로 작성
            if (user.getLastAttendance() == null){
                user.setAttendance(lastAttendance);
                user.setContinuous(1);
                user.setTodayCheck(true);
                user.setLastAttendance(today);
            }else {
                // 오늘 출석 체크를 하지 않았다면
                if (!user.isTodayCheck()){
                    // 마지막 출석일자와 오늘의 달을 비교하여 다르면 출석일자 초기화 및 새로 삽입
                    if (user.getLastAttendance().getDayOfMonth() != today.getDayOfMonth()){
                        user.setAttendance(lastAttendance);

                    }else {
                        // 마지막 출석일자가 오늘과 다르다면 공백과 날짜 삽입

                        user.setAttendance(user.getAttendance()+" "+lastAttendance);

                    }

                    // 마지막 출석 일자와 오늘의 일자를 비교하여 연속일수 초기화 및 추가
                    if (user.getLastAttendance().plusDays(1L).equals(today)){
                        user.setContinuous(user.getContinuous()+1);
                    }else {
                        user.setContinuous(1);
                    }
                    user.setTodayCheck(true);
                    user.setLastAttendance(today);
                }

            }

            userRepository.save(user);

        }else {
            log.info("해당 유저코드에 대한 유저를 찾을수 없습니다.");
        }

        return optionalUser.get();
    }
    private void writeBoardMission1(List<String> getStringAndPath, User user, Integer missionId){
        BoardMission1 boardMission1 = BoardMission1.builder()
                .missionId(missionId)
                .imagePath(getStringAndPath.get(0).toString())
                .comment1(getStringAndPath.get(1).toString())
                .comment2(getStringAndPath.get(2).toString())
                .user(user)
                .uuid(UUID.randomUUID().toString())
                .createdDt(LocalDateTime.now())
                .build();

        boardMission1Repository.save(boardMission1);
    }
    private void writeBoardMission2(List<String> getStringAndPath, User user, Integer missionId){
        BoardMission2 boardMission2 = BoardMission2.builder()
                .missionId(missionId)
                .comment1(getStringAndPath.get(0).toString())
                .comment2(getStringAndPath.get(1).toString())
                .user(user)
                .uuid(UUID.randomUUID().toString())
                .createdDt(LocalDateTime.now())
                .build();

        boardMission2Repository.save(boardMission2);
    }
    private void writeBoardMission3(List<String> getStringAndPath, User user, Integer missionId) throws Exception {



        BoardMission3 boardMission3 = BoardMission3.builder()
                .missionId(missionId)
                .comment1(getStringAndPath.get(0).toString())
                .comment2(getStringAndPath.get(1).toString())
                .comment3(getStringAndPath.get(2).toString())
                .comment4(getStringAndPath.get(3).toString())
                .user(user)
                .uuid(UUID.randomUUID().toString())
                .createdDt(LocalDateTime.now())
                .build();

        boardMission3Repository.save(boardMission3);
    }
}
