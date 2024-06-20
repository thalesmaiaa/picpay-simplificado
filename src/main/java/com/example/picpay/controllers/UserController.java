package com.example.picpay.controllers;

import com.example.picpay.domain.user.BalanceDTO;
import com.example.picpay.domain.user.User;
import com.example.picpay.domain.user.UserDTO;
import com.example.picpay.services.UserService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController extends AbstractController {

    @Autowired
    UserService userService;

    @Autowired
    AuthenticationManager authenticationManager;


    @PostMapping
    @Operation(tags = "users", summary = "Create user", method = "POST")

    public ResponseEntity<?> createUser(@RequestBody UserDTO userDTO) {
        ObjectNode body = json();

        ObjectNode createUserResponse = userService.createUser(userDTO, body);

        if (isErrorResponse(createUserResponse)) {
            return badRequest(createUserResponse);
        }

        return ok(createUserResponse);
    }

    @Operation(tags = "users", summary = "List all users", method = "GET")
    @GetMapping
    public ResponseEntity<?> getAllUsers() {

        ObjectNode body = json();
        ObjectNode getAllUsersResponse = userService.getAllUsers(body);

        if (isErrorResponse(getAllUsersResponse)) {
            return badRequest(getAllUsersResponse);
        }

        return ok(getAllUsersResponse);
    }


    @PutMapping("/balance/{id}")
    @Operation(tags = "users", summary = "Update user balance", method = "PUT")
    public ResponseEntity<?> updateUserBalance(@PathVariable Long id, @RequestBody BalanceDTO balanceDTO) {
        ObjectNode body = json();

        User user = userService.findUserById(id);

        ObjectNode updateUserBalanceResponse = userService.updateUserBalanceResponse(user, balanceDTO, body);

        if (isErrorResponse(updateUserBalanceResponse)) {
            return badRequest(updateUserBalanceResponse);
        }

        return ok(updateUserBalanceResponse);
    }


}
