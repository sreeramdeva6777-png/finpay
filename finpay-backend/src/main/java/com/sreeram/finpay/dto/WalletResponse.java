package com.sreeram.finpay.dto;

import java.math.BigDecimal;

public class WalletResponse {

    private Long walletId;
    private Long userId;
    private BigDecimal balance;

    public WalletResponse(Long walletId, Long userId, BigDecimal balance) {
        this.walletId = walletId;
        this.userId = userId;
        this.balance = balance;
    }

    public Long getWalletId() {	
        return walletId;
    }

    public Long getUserId() {
        return userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}
