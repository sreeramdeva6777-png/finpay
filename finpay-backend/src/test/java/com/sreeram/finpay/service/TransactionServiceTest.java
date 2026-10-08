package com.sreeram.finpay.service;

import com.sreeram.finpay.dto.TransactionResponse;
import com.sreeram.finpay.entity.Transaction;
import com.sreeram.finpay.entity.TransactionStatus;
import com.sreeram.finpay.entity.User;
import com.sreeram.finpay.exception.UserNotFoundException;
import com.sreeram.finpay.repository.TransactionRepository;
import com.sreeram.finpay.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void getTransactionHistory_success() {
        User user = new User();
        user.setId(1L);
        user.setName("Sreeram");
        user.setEmail("sreeram@gmail.com");

        User receiver = new User();
        receiver.setId(2L);
        receiver.setName("Receiver");
        receiver.setEmail("receiver@gmail.com");

        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setSender(user);
        transaction.setReceiver(receiver);
        transaction.setAmount(new BigDecimal("200.00"));
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setCreatedAt(LocalDateTime.now());

        setAuthentication("sreeram@gmail.com");

        when(userRepository.findByEmail("sreeram@gmail.com"))
                .thenReturn(Optional.of(user));

        when(transactionRepository.findBySenderOrReceiver(user, user))
                .thenReturn(List.of(transaction));

        List<TransactionResponse> response =
                transactionService.getTransactionHistory();

        assertThat(response).hasSize(1);
        assertThat(response.get(0).getTransactionId()).isEqualTo(1L);
        assertThat(response.get(0).getSenderId()).isEqualTo(1L);
        assertThat(response.get(0).getReceiverId()).isEqualTo(2L);
        assertThat(response.get(0).getAmount())
                .isEqualByComparingTo("200.00");
        assertThat(response.get(0).getStatus())
                .isEqualTo(TransactionStatus.SUCCESS);
    }

    @Test
    void getTransactionHistory_userNotFound_throwsException() {
        setAuthentication("unknown@gmail.com");

        when(userRepository.findByEmail("unknown@gmail.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                transactionService.getTransactionHistory())
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found");
    }

    private void setAuthentication(String email) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(email, null)
        );
    }
}