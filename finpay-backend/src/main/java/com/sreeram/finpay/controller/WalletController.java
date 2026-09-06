package com.sreeram.finpay.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.validation.Valid;

import com.sreeram.finpay.dto.WalletResponse;
import com.sreeram.finpay.dto.AddMoneyRequest;
import com.sreeram.finpay.dto.DeductMoneyRequest;
import com.sreeram.finpay.dto.TransferMoneyRequest;

import com.sreeram.finpay.entity.User;

import com.sreeram.finpay.repository.UserRepository;

import com.sreeram.finpay.service.WalletService;

import com.sreeram.finpay.exception.UserNotFoundException;

@RestController
public class WalletController {

    private final WalletService walletService;
    private final UserRepository userRepository;

    public WalletController(
            WalletService walletService,
            UserRepository userRepository) {

        this.walletService = walletService;
        this.userRepository = userRepository;
    }

    @PostMapping("/wallets/create")
    public WalletResponse createWallet() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        return walletService.createWallet(user);
    }

    @PostMapping("/wallets/add-money")
    public WalletResponse addMoney(
            @Valid @RequestBody AddMoneyRequest request) {

        return walletService.addMoney(request);
    }

    @GetMapping("/wallets/balance")
    public WalletResponse getBalance() {

        return walletService.getBalance();
    }

    @PostMapping("/wallets/deduct-money")
    public WalletResponse deductMoney(
            @Valid @RequestBody DeductMoneyRequest request) {

        return walletService.deductMoney(request);
    }

    @PostMapping("/wallets/transfer")
    public void transferMoney(
            @Valid @RequestBody TransferMoneyRequest request) {

        walletService.transferMoney(request);
    }
}