package com.sreeram.finpay.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.sreeram.finpay.dto.TransactionResponse;
import com.sreeram.finpay.entity.Transaction;
import com.sreeram.finpay.entity.User;
import com.sreeram.finpay.repository.TransactionRepository;
import com.sreeram.finpay.repository.UserRepository;
import com.sreeram.finpay.exception.UserNotFoundException;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionService(
            TransactionRepository transactionRepository,
            UserRepository userRepository) {

        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public List<TransactionResponse> getTransactionHistory() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        List<Transaction> transactions =
                transactionRepository.findBySenderOrReceiver(user, user);

        return transactions.stream()
                .map(transaction -> new TransactionResponse(
                        transaction.getId(),
                        transaction.getSender().getId(),
                        transaction.getReceiver().getId(),
                        transaction.getAmount(),
                        transaction.getStatus(),
                        transaction.getCreatedAt()
                ))
                .toList();
    }
}