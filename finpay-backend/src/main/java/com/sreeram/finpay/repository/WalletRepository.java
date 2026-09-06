package com.sreeram.finpay.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sreeram.finpay.entity.User;
import com.sreeram.finpay.entity.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
	boolean existsByUser(User user);
	Optional<Wallet> findByUser(User user);
}