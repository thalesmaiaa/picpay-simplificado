package com.example.picpay.services;


import com.example.picpay.domain.auth.LoginDTO;
import com.example.picpay.domain.auth.RefreshTokenDTO;
import com.example.picpay.domain.blacklistedToken.BlacklistedToken;
import com.example.picpay.domain.token.Token;
import com.example.picpay.domain.user.BalanceDTO;
import com.example.picpay.domain.user.User;
import com.example.picpay.domain.user.UserDTO;
import com.example.picpay.domain.user.UserOperation;
import com.example.picpay.exceptions.UserNotFoundException;
import com.example.picpay.repositories.UserRepository;
import com.example.picpay.security.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService extends AbstractValidationService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TokenService tokenService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    protected ObjectMapper jsonMapper;

    @Autowired
    BlacklistedTokenService blacklistedTokenService;


    @Transactional
    public void downgradeBalance(User sender, BigDecimal amount) {
        userRepository.downgradeBalanceById(sender.getId(), amount);
    }

    @Transactional
    public void upgradeBalance(User receiver, BigDecimal amount) {
        userRepository.upgradeBalanceById(receiver.getId(), amount);
    }

    public ObjectNode createUser(UserDTO userDTO, ObjectNode body) {
        if (checkRepeatedEmail(userDTO.email())) {
            body.put("message", "Usuário com esse email já cadastrado");
            body.put("status_code", 400);
            return body;

        }

        if (checkRepeatedDocument(userDTO.documento())) {
            body.put("message", "Usuário com esse documento já cadastrado");
            body.put("status_code", 400);
            return body;
        }

        validateFields(userDTO);


        String encryptedPassword = new BCryptPasswordEncoder().encode(userDTO.password());

        User newUser = new User(
                userDTO.firstName(),
                userDTO.lastName(),
                userDTO.documento(),
                userDTO.email(),
                encryptedPassword,
                userDTO.balance(),
                userDTO.userType());

        userRepository.save(newUser);

        ObjectNode data = jsonMapper.createObjectNode();

        data.put("firstName", newUser.getFirstName());
        data.put("lastName", newUser.getLastName());
        data.put("email", newUser.getEmail());
        data.putPOJO("userType", newUser.getUserType());


        body.putPOJO("data", data);

        body.put("message", "Usuário cadastrado com sucesso");
        body.put("status_code", 200);

        return body;
    }


    public Boolean checkRepeatedEmail(String email) {
        Optional<User> possibleUser = userRepository.findByEmail(email);
        return possibleUser.isPresent();
    }

    public Boolean checkRepeatedDocument(String document) {
        Optional<User> possibleUser = userRepository.findByDocument(document);
        return possibleUser.isPresent();
    }

    public ObjectNode getAllUsers(ObjectNode body) {
        List<User> usersList = userRepository.findAll();
        body.putPOJO("data", usersList);
        body.put("status_code", 200);

        return body;

    }


    @Transactional
    public ObjectNode updateUserBalanceResponse(User user, BalanceDTO balanceDTO, ObjectNode body) {

        UserOperation operation = balanceDTO.operation();

        if (operation == UserOperation.ADD) {
            upgradeBalance(user, balanceDTO.balance());

            body.put("message", "Saldo atualizado com sucesso");
            body.put("status_code", 200);

            return body;

        }

        if (operation == UserOperation.REMOVE) {
            Boolean hasEnoughBalance = isTransferPossible(user, balanceDTO.balance());

            if (hasEnoughBalance) {
                downgradeBalance(user, balanceDTO.balance());

                body.put("message", "Saldo atualizado com sucesso");
                body.put("status_code", 200);


            } else {
                body.put("message", "Saldo insuficiente");
                body.put("status_code", 400);

            }

            return body;

        }

        if (operation != UserOperation.ADD && operation != UserOperation.REMOVE) {
            body.put("message", "Tipo de operação inválida");
            body.put("status_code", 400);
        }

        return body;

    }

    public User findUserById(Long id) {
        Optional<User> possibleUser = userRepository.findById(id);

        if (possibleUser.isEmpty()) {
            throw new UserNotFoundException("Usuário nao encontrado");
        }

        return possibleUser.get();
    }

    public User findUserByEmail(String email) {
        Optional<User> possibleUser = userRepository.findByEmail(email);

        if (possibleUser.isEmpty()) {
            throw new UserNotFoundException("Usuário nao encontrado");
        }

        return possibleUser.get();
    }

    private Boolean isTransferPossible(User sender, BigDecimal amount) {
        BigDecimal userBalance = sender.getBalance();

        return userBalance.compareTo(amount) >= 0;

    }

    public ObjectNode authenticateUser(LoginDTO loginDTO, ObjectNode body) {
        User user = findUserByEmail(loginDTO.email());
        String accessToken = tokenService.generateToken(user, 1);
        String refreshToken = tokenService.generateToken(user, 3);


        Boolean passwordMatch = passwordEncoder.matches(loginDTO.password(), user.getPassword());


        if (passwordMatch) {
            Token refreshTokenObject = new Token(refreshToken, user.getId(), LocalDateTime.now());

            tokenService.saveToken(refreshTokenObject);

            body.put("status_code", 200);
            body.put("message", "usuário autenticado com sucesso");
            body.put("access_token", accessToken);
            body.put("refresh_token", refreshToken);


            return body;

        }

        body.put("status_code", 400);
        body.put("message", "e-mail ou senha inválidos, tente novamente!");

        return body;
    }

    public ObjectNode refreshToken(RefreshTokenDTO refreshTokenDTO, ObjectNode body) {
        String token = refreshTokenDTO.refresh_token();
        Token refreshToken = tokenService.findByToken(token);

        if (!isRefreshBlacklisted(refreshToken.getId())) {
            User user = findUserById(refreshToken.getUserId());

            String newAccessToken = tokenService.generateToken(user, 1);
            String newRefreshToken = tokenService.generateToken(user, 3);

            BlacklistedToken blacklistedToken = new BlacklistedToken(refreshToken.getToken(), refreshToken.getId());

            blacklistedTokenService.save(blacklistedToken);

            body.put("access_token", newAccessToken);
            body.put("refresh_token", newRefreshToken);
            body.put("status_code", 200);

            return body;

        }

        body.put("message", "token inválido");
        body.put("status_code", 401);

        return body;

    }

    public Boolean isRefreshBlacklisted(Long tokenId) {
        return blacklistedTokenService.findByTokenId(tokenId);
    }
}
