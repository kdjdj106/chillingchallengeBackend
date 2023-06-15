package com.example.chilling0613.mission.domain.service;

import com.example.chilling0613.mission.domain.dto.ShowFeedToFrontDto;
import com.example.chilling0613.mission.domain.dto.UserImageUrlUpdateRequestDto;
import com.example.chilling0613.mission.domain.dto.UserInfoDto;
import com.example.chilling0613.mission.domain.dto.UserNicknameUpdateRequestDto;
import com.example.chilling0613.mission.domain.entity.BoardMission1;
import com.example.chilling0613.mission.domain.entity.BoardMission2;
import com.example.chilling0613.mission.domain.entity.BoardMission3;
import com.example.chilling0613.mission.domain.repository.BoardMission1Repository;
import com.example.chilling0613.mission.domain.repository.BoardMission2Repository;
import com.example.chilling0613.mission.domain.repository.BoardMission3Repository;
import com.example.chilling0613.oauth2jwt.domain.entity.User;
import com.example.chilling0613.oauth2jwt.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BoardMission1Repository boardMission1Repository;
    private final BoardMission2Repository boardMission2Repository;
    private final BoardMission3Repository boardMission3Repository;
    private String pwd = null;

    public UserInfoDto showMyInfo(String usercode){
        User user = userRepository.findByUsercode(usercode).get();
        UserInfoDto infoDto = UserInfoDto.fromEntity(user);

        return infoDto;
    }

    public User updateUserNickname(String usercode, UserNicknameUpdateRequestDto request){
        Optional<User> optionalUser = userRepository.findByUsercode(usercode);

        if (optionalUser.isEmpty()) {
            throw new NotFoundException("User not found");
        }

        User user = optionalUser.get();
        user.setNickname(request.getNickname());
        return userRepository.save(user);
    }
    public User updateUserImageUrl(String usercode, UserImageUrlUpdateRequestDto request){
        Optional<User> optionalUser = userRepository.findByUsercode(usercode);

        if (optionalUser.isEmpty()) {
            throw new NotFoundException("User not found");
        }

        User user = optionalUser.get();
        user.setImageUrl(request.getImageUrl());
        return userRepository.save(user);
    }
    public List<ShowFeedToFrontDto> getMyHistory(String usercode){


        List<ShowFeedToFrontDto> myHistory = new ArrayList<>();
        User user = userRepository.findByUsercode(usercode).get();


        List<BoardMission1> list1 = new ArrayList<>();
        list1 = boardMission1Repository.findByUser(user);
        if (list1 != null){

            for (int i = 0; i < list1.size(); i++) {
                ShowFeedToFrontDto form = new ShowFeedToFrontDto();
                form.setMissionId(list1.get(i).getMissionId());
                List<String> stringAndPath = new ArrayList<>();
                stringAndPath.add(list1.get(i).getImagePath());
                stringAndPath.add(list1.get(i).getComment1());
                //stringAndPath.add(list1.get(i).getComment2());
                form.setUuid(list1.get(i).getUuid());
                form.setStringAndPath(stringAndPath);
                form.setLocalDateTime(list1.get(i).getCreatedDt());

                myHistory.add(form);
            }

        }


        List<BoardMission2> list2 = new ArrayList<>();
        list2 = boardMission2Repository.findByUser(user);
        if (list2 != null){

            for (int i = 0; i < list2.size(); i++) {
                ShowFeedToFrontDto form = new ShowFeedToFrontDto();
                form.setMissionId(list2.get(i).getMissionId());
                List<String> stringAndPath = new ArrayList<>();
                stringAndPath.add(list2.get(i).getComment1());
                stringAndPath.add(list2.get(i).getComment2());
                form.setUuid(list2.get(i).getUuid());
                form.setStringAndPath(stringAndPath);
                form.setLocalDateTime(list2.get(i).getCreatedDt());

                myHistory.add(form);
            }

        }

        List<BoardMission3> list3 = new ArrayList<>();
        list3 = boardMission3Repository.findByUser(user);
        if (list3 != null){

            for (int i = 0; i < list3.size(); i++) {
                ShowFeedToFrontDto form = new ShowFeedToFrontDto();
                form.setMissionId(list3.get(i).getMissionId());
                List<String> stringAndPath = new ArrayList<>();
                stringAndPath.add(list3.get(i).getComment1());
                stringAndPath.add(list3.get(i).getComment2());
                stringAndPath.add(list3.get(i).getComment3());
                stringAndPath.add(list3.get(i).getComment4());
                form.setUuid(list3.get(i).getUuid());
                form.setStringAndPath(stringAndPath);
                form.setLocalDateTime(list3.get(i).getCreatedDt());

                myHistory.add(form);
            }

        }

        myHistory.sort(Comparator.comparing(ShowFeedToFrontDto::getLocalDateTime));

        return myHistory;
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void resetCheckToday(){
        List<User> users = userRepository.findAll();
        for (User user : users) {
            user.setTodayCheck(false);
        }
        ResponseEntity.ok().build();
    }
}
