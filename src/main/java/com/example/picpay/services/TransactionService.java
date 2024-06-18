package com.example.picpay.services;

import com.example.picpay.domain.transaction.Transaction;
import com.example.picpay.domain.transaction.TransactionDTO;
import com.example.picpay.domain.user.User;
import com.example.picpay.domain.user.UserTypes;
import com.example.picpay.exceptions.InvalidSenderUserException;
import com.example.picpay.repositories.TransactionRepository;
import com.example.picpay.repositories.UserRepository;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

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


        Optional<User> possibleSender = userRepository.findById(transactionDTO.payer());
        Optional<User> possibleReceiver = userRepository.findById(transactionDTO.payee());


        if (possibleSender.isPresent() && possibleReceiver.isPresent()) {
            User sender = possibleSender.get();
            User receiver = possibleReceiver.get();
            BigDecimal amount = transactionDTO.value();

            if (sender.getUserType() == UserTypes.LOJISTA) {
                throw new InvalidSenderUserException("Seu tipo de usuário não pode realizar transação");
            }

            Boolean isTransferPossible = isTransferPossible(sender, amount);

            if (isTransferPossible) {

                ObjectNode credentialsResponse = mockyService.getTransferCrendentials();

                if (credentialsResponse.get("message").asText().equals("Autorizado")) {
                    userService.downgradeBalance(sender, amount);
                    userService.upgradeBalance(receiver, amount);

                    Transaction transaction = new Transaction(transactionDTO.value(), sender, receiver, LocalDateTime.now());


                    transactionRepository.save(transaction);

                    mockyService.notifyPostTransfer();


                    body.put("message", "Valor transferido com sucesso");
                    body.put("status_code", 200);

                    return body;
                }

                body.put("message", "Erro ao tentar realizar a transação, tente novamente mais tarde");
                body.put("status_code", 500);


            }

            body.put("message", "Você não possui saldo suficiente para realizar a transação");
            body.put("status_code", 400);

            return body;


        }

        body.put("message", "Requisição inválida");
        body.put("status_code", 400);

        return body;

    }


    public Boolean isTransferPossible(User sender, BigDecimal amount) {
        BigDecimal userBalance = sender.getBalance();

        return userBalance.compareTo(amount) >= 0;

    }
}
