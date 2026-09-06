package com.sreeram.finpay.service;

import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import com.sreeram.finpay.repository.WalletRepository;
import com.sreeram.finpay.repository.TransactionRepository;
import com.sreeram.finpay.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import com.sreeram.finpay.dto.WalletResponse;
import com.sreeram.finpay.dto.AddMoneyRequest;
import com.sreeram.finpay.dto.DeductMoneyRequest;
import com.sreeram.finpay.dto.TransferMoneyRequest;

import com.sreeram.finpay.entity.Transaction;
import com.sreeram.finpay.entity.TransactionStatus;
import com.sreeram.finpay.entity.User;
import com.sreeram.finpay.entity.Wallet;

import com.sreeram.finpay.exception.DuplicateTransactionException;
import com.sreeram.finpay.exception.InsufficientBalanceException;
import com.sreeram.finpay.exception.UserNotFoundException;
import com.sreeram.finpay.exception.WalletAlreadyExistsException;
import com.sreeram.finpay.exception.WalletNotFoundException;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public WalletService(
            WalletRepository walletRepository,
            UserRepository userRepository,
            TransactionRepository transactionRepository) {

        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    public WalletResponse createWallet(User user) {

        if (walletRepository.existsByUser(user)) {
            throw new WalletAlreadyExistsException(
                    "Wallet already exists for this user");
        }

        Wallet wallet = new Wallet();

        wallet.setBalance(BigDecimal.ZERO);
        wallet.setUser(user);

        Wallet savedWallet = walletRepository.save(wallet);

        return new WalletResponse(
                savedWallet.getId(),
                savedWallet.getUser().getId(),
                savedWallet.getBalance()
        );
    }

    public WalletResponse addMoney(AddMoneyRequest request) {

    	Authentication authentication =
    	        SecurityContextHolder.getContext().getAuthentication();

    	String email = authentication.getName();

    	User user = userRepository.findByEmail(email)
    	        .orElseThrow(() ->
    	                new UserNotFoundException("User not found"));

        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() ->
                        new WalletNotFoundException("Wallet not found"));

        BigDecimal newBalance =
                wallet.getBalance().add(request.getAmount());

        wallet.setBalance(newBalance);

        Wallet savedWallet = walletRepository.save(wallet);

        return new WalletResponse(
                savedWallet.getId(),
                savedWallet.getUser().getId(),
                savedWallet.getBalance()
        );
    }

    public WalletResponse deductMoney(DeductMoneyRequest request) {

    	Authentication authentication =
    	        SecurityContextHolder.getContext().getAuthentication();

    	String email = authentication.getName();

    	User user = userRepository.findByEmail(email)
    	        .orElseThrow(() ->
    	                new UserNotFoundException("User not found"));

        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() ->
                        new WalletNotFoundException("Wallet not found"));

        if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException(
                    "Insufficient balance");
        }

        BigDecimal newBalance =
                wallet.getBalance().subtract(request.getAmount());

        wallet.setBalance(newBalance);

        Wallet savedWallet = walletRepository.save(wallet);

        return new WalletResponse(
                savedWallet.getId(),
                savedWallet.getUser().getId(),
                savedWallet.getBalance()
        );
    }

    public WalletResponse getBalance() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() ->
                        new WalletNotFoundException("Wallet not found"));

        return new WalletResponse(
                wallet.getId(),
                wallet.getUser().getId(),
                wallet.getBalance()
        );
    }

    @Transactional
    public void transferMoney(TransferMoneyRequest request) {

        Optional<Transaction> existingTransaction =
                transactionRepository.findByIdempotencyKey(
                        request.getIdempotencyKey());

        if (existingTransaction.isPresent()) {
            throw new DuplicateTransactionException(
                    "Transaction already processed");
        }

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User sender = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("Sender not found"));

        User receiver = userRepository.findById(request.getToUserId())
                .orElseThrow(() ->
                        new UserNotFoundException("Receiver not found"));

        Wallet senderWallet = walletRepository.findByUser(sender)
                .orElseThrow(() ->
                        new WalletNotFoundException(
                                "Sender wallet not found"));

        Wallet receiverWallet = walletRepository.findByUser(receiver)
                .orElseThrow(() ->
                        new WalletNotFoundException(
                                "Receiver wallet not found"));

        if (senderWallet.getBalance()
                .compareTo(request.getAmount()) < 0) {

            throw new InsufficientBalanceException(
                    "Insufficient balance");
        }

        senderWallet.setBalance(
                senderWallet.getBalance()
                        .subtract(request.getAmount())
        );

        receiverWallet.setBalance(
                receiverWallet.getBalance()
                        .add(request.getAmount())
        );

        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);
        
        //throw new RuntimeException("Testing transaction rollback");
        
        Transaction transaction = new Transaction();

        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAmount(request.getAmount());
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setIdempotencyKey(request.getIdempotencyKey());

        transactionRepository.save(transaction);
    }
}