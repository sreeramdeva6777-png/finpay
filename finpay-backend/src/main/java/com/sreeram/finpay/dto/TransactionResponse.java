package com.sreeram.finpay.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.sreeram.finpay.entity.TransactionStatus;

public class TransactionResponse {

    private Long transactionId;
    private Long senderId;
    private Long receiverId;
    private BigDecimal amount;
    private TransactionStatus status;
    private LocalDateTime createdAt;

    public TransactionResponse(
            Long transactionId,
            Long senderId,
            Long receiverId,
            BigDecimal amount,
            TransactionStatus status,
            LocalDateTime createdAt) {

        this.transactionId = transactionId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TransactionStatus getStatus() {
        return status;
    }
}