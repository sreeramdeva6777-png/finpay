package com.sreeram.finpay.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Positive;

public class TransferMoneyRequest {

    private Long toUserId;

    @Positive
    private BigDecimal amount;
    private String idempotencyKey; 

    public String getIdempotencyKey() {
		return idempotencyKey;
	}

	public void setIdempotencyKey(String idempotencyKey) {
		this.idempotencyKey = idempotencyKey;
	}


    public Long getToUserId() {
        return toUserId;
    }

    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}