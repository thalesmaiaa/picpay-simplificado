package com.example.picpay.services;

import com.example.picpay.domain.transaction.Transaction;
import com.example.picpay.domain.user.User;
import com.example.picpay.domain.user.UserTypes;
import com.example.picpay.repositories.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setupTests() {
        MockitoAnnotations.openMocks(this);
    }

    private static final User sender = new User(
            "joao",
            "silva",
            "123",
            "j@s.com",
            "12345",
            new BigDecimal(10),
            UserTypes.COMUM);

    private static final User receiver = new User(
            "maria",
            "santos",
            "456",
            "m@s.com",
            "12345",
            new BigDecimal(10),
            UserTypes.COMUM);

    @Test
    @DisplayName("Should transfer value with success")
    void transferValueCase1() {

        BigDecimal transactionAmount = new BigDecimal(10);

        when(userService.findUserById(1L)).thenReturn(sender);
        when(userService.findUserById(2L)).thenReturn(receiver);

        doNothing().when(userService).downgradeBalance(sender, transactionAmount);
        doNothing().when(userService).upgradeBalance(receiver, transactionAmount);

        userService.downgradeBalance(sender, transactionAmount);
        userService.upgradeBalance(receiver, transactionAmount);

        Transaction transaction = new Transaction(transactionAmount, sender, receiver, LocalDateTime.now());

        transactionRepository.save(transaction);

        verify(userService, times(1)).downgradeBalance(sender, transactionAmount);
        verify(userService, times(1)).upgradeBalance(receiver, transactionAmount);
        verify(transactionRepository, times(1)).save(any(Transaction.class));

    }

    @Test
    @DisplayName("Should try to transfer value without balance")
    void transferValueCase2() {

        BigDecimal transactionAmount = new BigDecimal(30);

        when(userService.findUserById(1L)).thenReturn(sender);
        when(userService.findUserById(2L)).thenReturn(receiver);

        Boolean isTransferPossible = transactionService.hasEnoughBalanceForTransfer(sender, transactionAmount);

        assertThat(isTransferPossible).isFalse();

    }

}