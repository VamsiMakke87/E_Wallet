package org.wallet.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.wallet.TransactionStatusEnum;
import org.wallet.common.TransactionPayload;
import org.wallet.dto.TransactionDTO;
import org.wallet.entity.Transaction;
import org.wallet.repo.ITransactionRepo;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Service
public class TransactionService {

    @Autowired
    private ITransactionRepo repo;

    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;

    private static String TXN_INIT_TOPIC="TXN-INIT";

    private static Logger LOGGER= LoggerFactory.getLogger(TransactionService.class);

    public String doTransaction(TransactionDTO transactionDTO){
        Transaction transaction=mapToEntity(transactionDTO);
        transaction.setStatus(TransactionStatusEnum.INPROGRESS);
        transaction.setTransactionId(UUID.randomUUID().toString());

        repo.save(transaction);

        TransactionPayload payload=TransactionPayload.builder()
                .id(transaction.getId())
                .fromUserId(transaction.getFromUserId())
                .toUserId(transaction.getToUserId())
                .amount(transaction.getAmount())
                .comment(transaction.getComment())
                .requestId(MDC.get("requestId"))
                .build();

        Future<SendResult<String, Object>> send = kafkaTemplate.send(TXN_INIT_TOPIC, payload.getFromUserId().toString(), payload);

        return transaction.getTransactionId();
    }


    public Transaction mapToEntity(TransactionDTO transactionDTO){

        return Transaction.builder()
                .fromUserId(transactionDTO.getFromUserId())
                .toUserId(transactionDTO.getToUserId())
                .comment(transactionDTO.getComment())
                .amount(transactionDTO.getAmount())
                .build();

    }

    public TransactionDTO mapToDTO(Transaction transaction){
        return TransactionDTO.builder()
                .fromUserId(transaction.getFromUserId())
                .toUserId(transaction.getToUserId())
                .amount(transaction.getAmount())
                .comment(transaction.getComment())
                .build();
    }

}
