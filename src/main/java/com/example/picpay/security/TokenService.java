package com.example.picpay.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.picpay.domain.token.Token;
import com.example.picpay.domain.user.User;
import com.example.picpay.exceptions.UserNotFoundException;
import com.example.picpay.repositories.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {
    @Value("${api.security.token.secret}")
    private String secret;

    @Autowired
    TokenRepository tokenRepository;

    public String generateToken(User user, int expirationDate) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            String token = JWT.create()
                    .withIssuer("auth-api")
                    .withSubject(user.getEmail())
                    .withExpiresAt(this.generateExpirationDate(expirationDate))
                    .sign(algorithm);
            return token;
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Error while authenticating");
        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("auth-api")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            return null;
        }
    }

    private Instant generateExpirationDate(int expirationDate) {
        return LocalDateTime.now().plusHours(expirationDate).toInstant(ZoneOffset.of("-03:00"));
    }


    public Token saveToken(Token token) {
        return tokenRepository.save(token);
    }


    public Token findByToken(String token) {
        Token currentToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new UserNotFoundException("Token inválido"));
        return currentToken;
    }


    public Token findByUserId(Long id) {
        Token currentToken = tokenRepository.findByUserId(id).orElseThrow(() -> new UserNotFoundException("Token inválido"));
        return currentToken;
    }
}

