package com.example.picpay.controllers;

import com.example.picpay.domain.auth.LoginDTO;
import com.example.picpay.domain.auth.RefreshTokenDTO;
import com.example.picpay.domain.user.UserDTO;
import com.example.picpay.services.UserService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController extends AbstractController {

    @Autowired
    UserService userService;


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) {
        ObjectNode body = json();
        ObjectNode registerUserResponse = userService.createUser(userDTO, body);

        if (isErrorResponse(registerUserResponse)) {
            return badRequest(registerUserResponse);
        }

        return ok(registerUserResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginDTO loginDTO) {
        ObjectNode body = json();
        ObjectNode loginUserResponse = userService.authenticateUser(loginDTO, body);

        if (isErrorResponse(loginUserResponse)) {
            return badRequest(loginUserResponse);
        }

        return ok(loginUserResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshUserToken(@RequestBody RefreshTokenDTO refreshTokenDTO) {
        ObjectNode body = json();

        ObjectNode refreshResponse = userService.refreshToken(refreshTokenDTO, body);


        if (isErrorResponse(refreshResponse)) {
            return unauthorized(refreshResponse);
        }

        return ok(refreshResponse);

    }

}
