package com.sreeram.finpay.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Positive;

public class DeductMoneyRequest {


    @Positive
    private BigDecimal amount;

    

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}