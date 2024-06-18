package com.example.picpay.services;

import com.example.picpay.domain.blacklistedToken.BlacklistedToken;
import com.example.picpay.repositories.BlacklistedTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BlacklistedTokenService extends AbstractValidationService {

    @Autowired
    BlacklistedTokenRepository blacklistedTokenRepository;

    public void save(BlacklistedToken blacklistedToken) {
        blacklistedTokenRepository.save(blacklistedToken);
    }

    public Boolean findByTokenId(Long id) {
        Optional<BlacklistedToken> possibleToken = blacklistedTokenRepository.findByTokenId(id);

        return possibleToken.isPresent();
    }
}
