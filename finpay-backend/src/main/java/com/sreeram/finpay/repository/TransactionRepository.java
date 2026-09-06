package com.sreeram.finpay.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sreeram.finpay.entity.Transaction;
import com.sreeram.finpay.entity.User;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {
	Optional<Transaction> findByIdempotencyKey(String idempotencyKey);
	List<Transaction> findBySenderOrReceiver(User sender, User receiver);
}