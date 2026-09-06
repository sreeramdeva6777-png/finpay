package com.sreeram.finpay.controller;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sreeram.finpay.exception.DuplicateTransactionException;
import com.sreeram.finpay.exception.EmailAlreadyExistsException;
import com.sreeram.finpay.exception.InsufficientBalanceException;

import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import com.sreeram.finpay.exception.InvalidCredentialsException;
import com.sreeram.finpay.exception.UserNotFoundException;
import com.sreeram.finpay.exception.WalletAlreadyExistsException;
import com.sreeram.finpay.exception.WalletNotFoundException;
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(EmailAlreadyExistsException.class)
	public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
	    return ResponseEntity
	            .status(409)
	            .body(ex.getMessage());
	}
	
	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<String> handleInvalidCredentials(
	        InvalidCredentialsException ex) {

	    return ResponseEntity
	            .status(401)
	            .body(ex.getMessage());
	}
	
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<String> handleUserNotFound(UserNotFoundException ex) {
	    return ResponseEntity.status(404).body(ex.getMessage());
	}
	
	@ExceptionHandler(WalletAlreadyExistsException.class)
	public ResponseEntity<String> handleWalletAlreadyExists(WalletAlreadyExistsException ex) {
	    return ResponseEntity.status(409).body(ex.getMessage());
	}
	
	@ExceptionHandler(WalletNotFoundException.class)
	public ResponseEntity<String> handleWalletNotFound(WalletNotFoundException ex) {
	    return ResponseEntity.status(404).body(ex.getMessage());
	}
	
	@ExceptionHandler(InsufficientBalanceException.class)
	public ResponseEntity<String> handleInsufficientBalance(
	        InsufficientBalanceException ex) {

	    return ResponseEntity
	            .status(400)
	            .body(ex.getMessage());
	}
	@ExceptionHandler(ObjectOptimisticLockingFailureException.class)
	public ResponseEntity<String> handleOptimisticLocking(
	        ObjectOptimisticLockingFailureException ex) {

	    return ResponseEntity
	            .status(409)
	            .body("Wallet was modified by another request. Please try again.");
	}
	@ExceptionHandler(DuplicateTransactionException.class)
	public ResponseEntity<String> handleDuplicateTransaction(
	        DuplicateTransactionException ex) {

	    return ResponseEntity
	            .status(409)
	            .body(ex.getMessage());
	}
}