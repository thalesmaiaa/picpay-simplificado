package com.example.picpay.domain.user;

import java.math.BigDecimal;

public record UserDTO(String firstName, String lastName, String email, String documento, String password,
                      BigDecimal balance, UserTypes userType) {
}
