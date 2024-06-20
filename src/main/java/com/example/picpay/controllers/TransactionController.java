package com.example.picpay.controllers;

import com.example.picpay.domain.transaction.TransactionDTO;
import com.example.picpay.services.TransactionService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/transfer")
public class TransactionController extends AbstractController {


    @Autowired
    TransactionService transactionService;
    
    @PostMapping
    public ResponseEntity<?> transferValue(@RequestBody TransactionDTO transactionDTO) {
        ObjectNode body = json();

        ObjectNode transferValueResponse = transactionService.transferValue(transactionDTO, body);

        if (isErrorResponse(transferValueResponse)) {
            return badRequest(transferValueResponse);
        }

        return ok(transferValueResponse);


    }
}
