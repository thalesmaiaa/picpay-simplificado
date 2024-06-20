package com.example.picpay.services;

import com.example.picpay.domain.transaction.Transaction;
import com.example.picpay.domain.transaction.TransactionDTO;
import com.example.picpay.domain.user.User;
import com.example.picpay.domain.user.UserTypes;
import com.example.picpay.exceptions.InvalidSenderUserException;
import com.example.picpay.exceptions.InvalidTransactionException;
import com.example.picpay.exceptions.UserNotFoundException;
import com.example.picpay.repositories.TransactionRepository;
import com.example.picpay.repositories.UserRepository;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class TransactionService extends AbstractValidationService {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    MockyService mockyService;

    public ObjectNode transferValue(TransactionDTO transactionDTO, ObjectNode body) {

        validateFields(transactionDTO);

        User sender = userRepository.findById(transactionDTO.payer()).orElseThrow(() -> new UserNotFoundException());
        User receiver = userRepository.findById(transactionDTO.payee()).orElseThrow(() -> new UserNotFoundException());

        BigDecimal amount = transactionDTO.value();

        Boolean isSenderLojista = sender.getUserType() == UserTypes.LOJISTA;

        if (isSenderLojista) {
            throw new InvalidSenderUserException("Seu tipo de usuário não pode realizar transação");
        }

        Boolean isTransferPossible = hasEnoughBalanceForTransfer(sender, amount);

        ObjectNode credentialsResponse = mockyService.getTransferCrendentials();

        Boolean isAuthorized = credentialsResponse.get("message").asText().equals("Autorizado");

        if (!isTransferPossible || !isAuthorized) {
            throw new InvalidTransactionException();
        }

        proceedTransaction(transactionDTO, sender, amount, receiver);
        body.put("message", "Valor transferido com sucesso");
        body.put("status_code", 200);

        return body;

    }

    private void proceedTransaction(TransactionDTO transactionDTO, User sender, BigDecimal amount, User receiver) {
        userService.downgradeBalance(sender, amount);
        userService.upgradeBalance(receiver, amount);

        Transaction transaction = new Transaction(transactionDTO.value(), sender, receiver, LocalDateTime.now());
        transactionRepository.save(transaction);
        mockyService.notifyPostTransfer();
    }

    public Boolean hasEnoughBalanceForTransfer(User sender, BigDecimal amount) {
        BigDecimal userBalance = sender.getBalance();

        return userBalance.compareTo(amount) >= 0;

    }
}
