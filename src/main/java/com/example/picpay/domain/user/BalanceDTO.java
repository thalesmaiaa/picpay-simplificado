package com.example.picpay.domain.user;

import java.math.BigDecimal;


public record BalanceDTO(BigDecimal balance, UserOperation operation) {
}
