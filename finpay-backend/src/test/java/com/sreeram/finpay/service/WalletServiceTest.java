package com.sreeram.finpay.service;

import com.sreeram.finpay.dto.AddMoneyRequest;
import com.sreeram.finpay.dto.DeductMoneyRequest;
import com.sreeram.finpay.dto.TransferMoneyRequest;
import com.sreeram.finpay.dto.WalletResponse;
import com.sreeram.finpay.entity.Transaction;
import com.sreeram.finpay.entity.User;
import com.sreeram.finpay.entity.Wallet;
import com.sreeram.finpay.exception.DuplicateTransactionException;
import com.sreeram.finpay.exception.InsufficientBalanceException;
import com.sreeram.finpay.exception.UserNotFoundException;
import com.sreeram.finpay.exception.WalletAlreadyExistsException;
import com.sreeram.finpay.exception.WalletNotFoundException;
import com.sreeram.finpay.repository.TransactionRepository;
import com.sreeram.finpay.repository.UserRepository;
import com.sreeram.finpay.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private WalletService walletService;

    @Test
    void createWallet_success() {
        User user = new User();
        user.setId(1L);
        user.setName("Sreeram");
        user.setEmail("sreeram@gmail.com");

        when(walletRepository.existsByUser(user)).thenReturn(false);

        when(walletRepository.save(any(Wallet.class)))
                .thenAnswer(invocation -> {
                    Wallet wallet = invocation.getArgument(0);
                    wallet.setId(1L);
                    return wallet;
                });

        WalletResponse response = walletService.createWallet(user);

        assertThat(response.getWalletId()).isEqualTo(1L);
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);

        verify(walletRepository).save(any(Wallet.class));
    }

    @Test
    void createWallet_alreadyExists_throwsException() {
        User user = new User();
        user.setId(1L);

        when(walletRepository.existsByUser(user)).thenReturn(true);

        assertThatThrownBy(() -> walletService.createWallet(user))
                .isInstanceOf(WalletAlreadyExistsException.class)
                .hasMessage("Wallet already exists for this user");

        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    void addMoney_success() {
        User user = new User();
        user.setId(1L);
        user.setEmail("sreeram@gmail.com");

        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setUser(user);
        wallet.setBalance(new BigDecimal("500.00"));

        AddMoneyRequest request = new AddMoneyRequest();
        request.setAmount(new BigDecimal("200.00"));

        setAuthentication("sreeram@gmail.com");

        when(userRepository.findByEmail("sreeram@gmail.com"))
                .thenReturn(Optional.of(user));

        when(walletRepository.findByUser(user))
                .thenReturn(Optional.of(wallet));

        when(walletRepository.save(any(Wallet.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        WalletResponse response = walletService.addMoney(request);

        assertThat(response.getBalance())
                .isEqualByComparingTo("700.00");

        verify(walletRepository).save(wallet);
    }

    @Test
    void addMoney_userNotFound_throwsException() {
        AddMoneyRequest request = new AddMoneyRequest();
        request.setAmount(new BigDecimal("200.00"));

        setAuthentication("unknown@gmail.com");

        when(userRepository.findByEmail("unknown@gmail.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> walletService.addMoney(request))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found");

        verify(walletRepository, never()).findByUser(any(User.class));
    }

    @Test
    void addMoney_walletNotFound_throwsException() {
        User user = new User();
        user.setId(1L);
        user.setEmail("sreeram@gmail.com");

        AddMoneyRequest request = new AddMoneyRequest();
        request.setAmount(new BigDecimal("200.00"));

        setAuthentication("sreeram@gmail.com");

        when(userRepository.findByEmail("sreeram@gmail.com"))
                .thenReturn(Optional.of(user));

        when(walletRepository.findByUser(user))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> walletService.addMoney(request))
                .isInstanceOf(WalletNotFoundException.class)
                .hasMessage("Wallet not found");

        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    void deductMoney_success() {
        User user = new User();
        user.setId(1L);
        user.setEmail("sreeram@gmail.com");

        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setUser(user);
        wallet.setBalance(new BigDecimal("500.00"));

        DeductMoneyRequest request = new DeductMoneyRequest();
        request.setAmount(new BigDecimal("200.00"));

        setAuthentication("sreeram@gmail.com");

        when(userRepository.findByEmail("sreeram@gmail.com"))
                .thenReturn(Optional.of(user));

        when(walletRepository.findByUser(user))
                .thenReturn(Optional.of(wallet));

        when(walletRepository.save(any(Wallet.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        WalletResponse response = walletService.deductMoney(request);

        assertThat(response.getBalance())
                .isEqualByComparingTo("300.00");

        verify(walletRepository).save(wallet);
    }

    @Test
    void deductMoney_insufficientBalance_throwsException() {
        User user = new User();
        user.setId(1L);
        user.setEmail("sreeram@gmail.com");

        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setUser(user);
        wallet.setBalance(new BigDecimal("100.00"));

        DeductMoneyRequest request = new DeductMoneyRequest();
        request.setAmount(new BigDecimal("200.00"));

        setAuthentication("sreeram@gmail.com");

        when(userRepository.findByEmail("sreeram@gmail.com"))
                .thenReturn(Optional.of(user));

        when(walletRepository.findByUser(user))
                .thenReturn(Optional.of(wallet));

        assertThatThrownBy(() -> walletService.deductMoney(request))
                .isInstanceOf(InsufficientBalanceException.class)
                .hasMessage("Insufficient balance");

        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    void getBalance_success() {
        User user = new User();
        user.setId(1L);
        user.setEmail("sreeram@gmail.com");

        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setUser(user);
        wallet.setBalance(new BigDecimal("750.00"));

        setAuthentication("sreeram@gmail.com");

        when(userRepository.findByEmail("sreeram@gmail.com"))
                .thenReturn(Optional.of(user));

        when(walletRepository.findByUser(user))
                .thenReturn(Optional.of(wallet));

        WalletResponse response = walletService.getBalance();

        assertThat(response.getWalletId()).isEqualTo(1L);
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getBalance())
                .isEqualByComparingTo("750.00");
    }

    @Test
    void getBalance_walletNotFound_throwsException() {
        User user = new User();
        user.setId(1L);
        user.setEmail("sreeram@gmail.com");

        setAuthentication("sreeram@gmail.com");

        when(userRepository.findByEmail("sreeram@gmail.com"))
                .thenReturn(Optional.of(user));

        when(walletRepository.findByUser(user))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> walletService.getBalance())
                .isInstanceOf(WalletNotFoundException.class)
                .hasMessage("Wallet not found");
    }

    @Test
    void transferMoney_success() {
        User sender = new User();
        sender.setId(1L);
        sender.setEmail("sender@gmail.com");

        User receiver = new User();
        receiver.setId(2L);
        receiver.setEmail("receiver@gmail.com");

        Wallet senderWallet = new Wallet();
        senderWallet.setId(1L);
        senderWallet.setUser(sender);
        senderWallet.setBalance(new BigDecimal("1000.00"));

        Wallet receiverWallet = new Wallet();
        receiverWallet.setId(2L);
        receiverWallet.setUser(receiver);
        receiverWallet.setBalance(new BigDecimal("300.00"));

        TransferMoneyRequest request = new TransferMoneyRequest();
        request.setToUserId(2L);
        request.setAmount(new BigDecimal("200.00"));
        request.setIdempotencyKey("TXN-001");

        setAuthentication("sender@gmail.com");

        when(transactionRepository.findByIdempotencyKey("TXN-001"))
                .thenReturn(Optional.empty());

        when(userRepository.findByEmail("sender@gmail.com"))
                .thenReturn(Optional.of(sender));

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(receiver));

        when(walletRepository.findByUser(sender))
                .thenReturn(Optional.of(senderWallet));

        when(walletRepository.findByUser(receiver))
                .thenReturn(Optional.of(receiverWallet));

        walletService.transferMoney(request);

        assertThat(senderWallet.getBalance())
                .isEqualByComparingTo("800.00");

        assertThat(receiverWallet.getBalance())
                .isEqualByComparingTo("500.00");

        verify(walletRepository).save(senderWallet);
        verify(walletRepository).save(receiverWallet);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void transferMoney_duplicateIdempotency_throwsException() {
        TransferMoneyRequest request = new TransferMoneyRequest();
        request.setIdempotencyKey("TXN-001");

        Transaction transaction = new Transaction();

        when(transactionRepository.findByIdempotencyKey("TXN-001"))
                .thenReturn(Optional.of(transaction));

        assertThatThrownBy(() -> walletService.transferMoney(request))
                .isInstanceOf(DuplicateTransactionException.class)
                .hasMessage("Transaction already processed");

        verify(userRepository, never()).findByEmail(anyString());
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void transferMoney_senderNotFound_throwsException() {
        TransferMoneyRequest request = new TransferMoneyRequest();
        request.setIdempotencyKey("TXN-001");
        request.setToUserId(2L);
        request.setAmount(new BigDecimal("200.00"));

        setAuthentication("sender@gmail.com");

        when(transactionRepository.findByIdempotencyKey("TXN-001"))
                .thenReturn(Optional.empty());

        when(userRepository.findByEmail("sender@gmail.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> walletService.transferMoney(request))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("Sender not found");
    }

    @Test
    void transferMoney_receiverNotFound_throwsException() {
        User sender = new User();
        sender.setId(1L);
        sender.setEmail("sender@gmail.com");

        TransferMoneyRequest request = new TransferMoneyRequest();
        request.setIdempotencyKey("TXN-001");
        request.setToUserId(2L);
        request.setAmount(new BigDecimal("200.00"));

        setAuthentication("sender@gmail.com");

        when(transactionRepository.findByIdempotencyKey("TXN-001"))
                .thenReturn(Optional.empty());

        when(userRepository.findByEmail("sender@gmail.com"))
                .thenReturn(Optional.of(sender));

        when(userRepository.findById(2L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> walletService.transferMoney(request))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("Receiver not found");
    }

    @Test
    void transferMoney_senderWalletNotFound_throwsException() {
        User sender = new User();
        sender.setId(1L);
        sender.setEmail("sender@gmail.com");

        User receiver = new User();
        receiver.setId(2L);

        TransferMoneyRequest request = new TransferMoneyRequest();
        request.setIdempotencyKey("TXN-001");
        request.setToUserId(2L);
        request.setAmount(new BigDecimal("200.00"));

        setAuthentication("sender@gmail.com");

        when(transactionRepository.findByIdempotencyKey("TXN-001"))
                .thenReturn(Optional.empty());

        when(userRepository.findByEmail("sender@gmail.com"))
                .thenReturn(Optional.of(sender));

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(receiver));

        when(walletRepository.findByUser(sender))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> walletService.transferMoney(request))
                .isInstanceOf(WalletNotFoundException.class)
                .hasMessage("Sender wallet not found");
    }

    @Test
    void transferMoney_receiverWalletNotFound_throwsException() {
        User sender = new User();
        sender.setId(1L);
        sender.setEmail("sender@gmail.com");

        User receiver = new User();
        receiver.setId(2L);

        Wallet senderWallet = new Wallet();
        senderWallet.setId(1L);
        senderWallet.setUser(sender);
        senderWallet.setBalance(new BigDecimal("1000.00"));

        TransferMoneyRequest request = new TransferMoneyRequest();
        request.setIdempotencyKey("TXN-001");
        request.setToUserId(2L);
        request.setAmount(new BigDecimal("200.00"));

        setAuthentication("sender@gmail.com");

        when(transactionRepository.findByIdempotencyKey("TXN-001"))
                .thenReturn(Optional.empty());

        when(userRepository.findByEmail("sender@gmail.com"))
                .thenReturn(Optional.of(sender));

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(receiver));

        when(walletRepository.findByUser(sender))
                .thenReturn(Optional.of(senderWallet));

        when(walletRepository.findByUser(receiver))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> walletService.transferMoney(request))
                .isInstanceOf(WalletNotFoundException.class)
                .hasMessage("Receiver wallet not found");
    }

    @Test
    void transferMoney_insufficientBalance_throwsException() {
        User sender = new User();
        sender.setId(1L);
        sender.setEmail("sender@gmail.com");

        User receiver = new User();
        receiver.setId(2L);

        Wallet senderWallet = new Wallet();
        senderWallet.setId(1L);
        senderWallet.setUser(sender);
        senderWallet.setBalance(new BigDecimal("100.00"));

        Wallet receiverWallet = new Wallet();
        receiverWallet.setId(2L);
        receiverWallet.setUser(receiver);
        receiverWallet.setBalance(new BigDecimal("300.00"));

        TransferMoneyRequest request = new TransferMoneyRequest();
        request.setIdempotencyKey("TXN-001");
        request.setToUserId(2L);
        request.setAmount(new BigDecimal("200.00"));

        setAuthentication("sender@gmail.com");

        when(transactionRepository.findByIdempotencyKey("TXN-001"))
                .thenReturn(Optional.empty());

        when(userRepository.findByEmail("sender@gmail.com"))
                .thenReturn(Optional.of(sender));

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(receiver));

        when(walletRepository.findByUser(sender))
                .thenReturn(Optional.of(senderWallet));

        when(walletRepository.findByUser(receiver))
                .thenReturn(Optional.of(receiverWallet));

        assertThatThrownBy(() -> walletService.transferMoney(request))
                .isInstanceOf(InsufficientBalanceException.class)
                .hasMessage("Insufficient balance");

        verify(walletRepository, never()).save(any(Wallet.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    private void setAuthentication(String email) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(email, null)
        );
    }
}