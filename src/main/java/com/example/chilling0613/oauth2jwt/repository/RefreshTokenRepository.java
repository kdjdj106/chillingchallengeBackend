package com.example.chilling0613.oauth2jwt.repository;

import com.example.chilling0613.oauth2jwt.domain.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
}
