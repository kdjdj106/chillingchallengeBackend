package com.example.chilling0613.oauth2jwt.repository;

import com.example.chilling0613.oauth2jwt.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsercode(String userCode);
    Optional<User> findByEmail(String email);


}
