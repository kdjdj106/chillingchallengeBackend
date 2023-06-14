package com.example.chilling0613.mission.domain.repository;

import com.example.chilling0613.mission.domain.entity.BoardMission1;
import com.example.chilling0613.mission.domain.entity.BoardMission3;
import com.example.chilling0613.oauth2jwt.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardMission3Repository extends JpaRepository <BoardMission3, Long>{
    List<BoardMission3> findByUser(User user);
}
