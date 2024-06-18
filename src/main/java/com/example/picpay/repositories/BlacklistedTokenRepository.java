package com.example.picpay.repositories;

import com.example.picpay.domain.blacklistedToken.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Long> {
    Optional<BlacklistedToken> findByTokenId(Long id);
}
