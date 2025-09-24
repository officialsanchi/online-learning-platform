package payment.Transactions;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service

public class TransactionServiceImpl implements TransactionService {
    public TransactionServiceImpl(WalletRepository walletRepo, TransactionRepository txRepo, WebClient.Builder webClientBuilder) {
        this.walletRepo = walletRepo;
        this.txRepo = txRepo;
        this.webClientBuilder = webClientBuilder;
    }

    private final WalletRepository walletRepo;
    private final TransactionRepository txRepo;
    private final WebClient.Builder webClientBuilder;
    private static final Logger logger = LoggerFactory.getLogger(PaystackWebhookService.class);


    @Value("${paystack.secret.key}")
    private String paystackSecret;

    private WebClient getWebClient() {
        return webClientBuilder.baseUrl("https://api.paystack.co").build();
    }

    private void validateAmount(Double amount) {
        if (amount == null || amount <= 0) {
            throw new RuntimeException("Invalid amount. Must be greater than 0");
        }
    }

    private String generateReference(String prefix) {
        return prefix + "-" + System.currentTimeMillis();
    }

    @Override
    @Transactional
    public DepositResponse deposit(DepositRequest request) {
        logger.info("Deposit request received: userId={}, amount={}", request.getUserId(), request.getAmount());
        validateAmount(request.getAmount());

        Wallet wallet = walletRepo.findByUserId(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        // Create PENDING transaction
        Transaction tx = new Transaction();
        tx.setReference(generateReference("DEP"));
        tx.setAmount(request.getAmount());
        tx.setStatus("PENDING");
        tx.setType("DEPOSIT");
        tx.setWallet(wallet);
        tx = txRepo.save(tx);

        logger.info("Transaction created: reference={}, status=PENDING", tx.getReference());

        // Initialize Paystack payment
        Map<String, Object> body = new HashMap<>();
        body.put("amount", (int) (request.getAmount() * 100)); // kobo
        body.put("email", request.getEmail());
        body.put("reference", tx.getReference());
        body.put("callback_url", request.getCallbackUrl());

        logger.info("Initializing Paystack payment: {}", body);

        Map response;
        try {
            response = getWebClient().post()
                    .uri("/transaction/initialize")
                    .header("Authorization", "Bearer " + paystackSecret)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            tx.setStatus("FAILED");
            txRepo.save(tx);
            logger.error("Paystack initialization failed: reference={}, error={}", tx.getReference(), e.getMessage());
            throw new RuntimeException("Failed to initialize Paystack payment", e);
        }

        logger.info("Paystack response: {}", response);

        if (response != null && Boolean.TRUE.equals(response.get("status"))) {
            Map data = (Map) response.get("data");
            String paymentUrl = (String) data.get("authorization_url");

            DepositResponse depositResponse = new DepositResponse();
            depositResponse.setStatus("PENDING");
            depositResponse.setMessage("Payment initialized. Complete payment via Paystack");
            depositResponse.setReference(tx.getReference());
            depositResponse.setPaymentUrl(paymentUrl);
            depositResponse.setNewBalance(wallet.getBalance()); // Balance not yet updated
            logger.info("Deposit initialized: reference={}, paymentUrl={}", tx.getReference(), paymentUrl);
            return depositResponse;
        } else {
            tx.setStatus("FAILED");
            txRepo.save(tx);
            logger.error("Failed to initialize Paystack payment: reference={}", tx.getReference());
            throw new RuntimeException("Failed to initialize Paystack payment");

        }
    }

    @Override
    public TransactionStatusResponse checkDepositTransactionStatus(String reference) {
        Transaction tx = txRepo.findByReference(reference)
                .orElseThrow(() -> new RuntimeException("Transaction not found for reference: " + reference));

        Long userId = tx.getWallet().getUserId();
        Wallet wallet = walletRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found for userId: " + userId));

        Map response;
        try {
            response = getWebClient().get()
                    .uri("/transaction/verify/"+ reference)
                    .header("Authorization", "Bearer " + paystackSecret)
                    .header( "Content-Type","application/json" )
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            tx.setStatus("FAILED");
            txRepo.save(tx);
            logger.error("Paystack verification failed: reference={}, error={}", tx.getReference(), e.getMessage());
            throw new RuntimeException("Failed to verify Paystack payment", e);
        }
        logger.info("Paystack verification response: {}", response);

        if (response != null && Boolean.TRUE.equals(response.get("status"))) {
            tx.setStatus( "SUCCESS" );
           tx = txRepo.save(tx);
            wallet.setBalance( wallet.getBalance() + tx.getAmount() );
           wallet = walletRepo.save( wallet );
        } else {
            tx.setStatus("FAILED");
            txRepo.save(tx);
            logger.error("Failed to verify Paystack payment: reference={}", tx.getReference());
            throw new RuntimeException("Failed to verify Paystack payment");

        }
        TransactionStatusResponse resp = new TransactionStatusResponse();
        resp.setReference(tx.getReference());
        resp.setTransactionStatus(tx.getStatus());
        resp.setWalletBalance(wallet.getBalance());
        return resp;
    }

    @Override
    public Map<String, Object> parsePayload(String payload) {
        try {
            return new ObjectMapper().readValue(payload, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse webhook payload", e);
        }
    }

    @Override
    public TransactionStatusResponse checkWithdrawTransactionStatus(String reference) {
        Transaction tx = txRepo.findByReference(reference)
                .orElseThrow(() -> new RuntimeException("Transaction not found for reference: " + reference));

        Long userId = tx.getWallet().getUserId();
        Wallet wallet = walletRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found for userId: " + userId));

        Map response;
        try {
            response = getWebClient().get()
                    .uri("/transfer?reference=" + reference)
                    .header("Authorization", "Bearer " + paystackSecret)
                    .header("Content-Type", "application/json")
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            logger.error("Paystack transfer verification failed: reference={}, error={}", reference, e.getMessage());
            throw new RuntimeException("Failed to verify Paystack withdrawal", e);
        }

        logger.info("Paystack withdrawal verification response: {}", response);

        if (response != null && Boolean.TRUE.equals(response.get("status"))) {
            Map data = (Map) ((List) response.get("data")).get(0); // response.data is a list
            String paystackStatus = (String) data.get("status");   // can be success, failed, pending, etc.

            switch (paystackStatus) {
                case "success":
                    tx.setStatus("SUCCESS");
                    txRepo.save(tx);
                    logger.info("Withdrawal SUCCESS: reference={}", reference);
                    break;

                case "failed":
                    tx.setStatus("FAILED");
                    txRepo.save(tx);
                    // Optionally refund the wallet
                    wallet.setBalance(wallet.getBalance() + tx.getAmount());
                    walletRepo.save(wallet);
                    logger.warn("Withdrawal FAILED, refunded: reference={}", reference);
                    break;

                default: // queued, pending, processing
                    tx.setStatus("PROCESSING");
                    txRepo.save(tx);
                    logger.info("Withdrawal still PROCESSING: reference={}, paystackStatus={}", reference, paystackStatus);
            }
        } else {
            logger.error("Withdrawal verification failed: reference={}, response={}", reference, response);
            throw new RuntimeException("Failed to verify Paystack withdrawal");
        }

        TransactionStatusResponse resp = new TransactionStatusResponse();
        resp.setReference(tx.getReference());
        resp.setTransactionStatus(tx.getStatus());
        resp.setWalletBalance(wallet.getBalance());
        return resp;
    }

    @Override
    public WithdrawResponse withdraw(WithdrawRequest request) {
        logger.info("Withdraw request received: userId={}, amount={}", request.getUserId(), request.getAmount());
        validateAmount(request.getAmount());

        Wallet wallet = walletRepo.findByUserId(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        if (wallet.getBalance() < request.getAmount()) {
            throw new RuntimeException("Insufficient balance");
        }


        Transaction tx = new Transaction();
        tx.setReference(generateReference("WDR"));
        tx.setAmount(request.getAmount());
        tx.setStatus("PENDING");
        tx.setType("WITHDRAW");
        tx.setWallet(wallet);
        tx = txRepo.save(tx);

        logger.info("Withdrawal transaction created: reference={}, status=PENDING", tx.getReference());

    
        Map<String, Object> recipientPayload = new HashMap<>();
        recipientPayload.put("type", "nuban");
        recipientPayload.put("name", request.getAccountNumber());
        recipientPayload.put("account_number", request.getAccountNumber());
        recipientPayload.put("bank_code", request.getBankCode()); // Paystack bank code
        recipientPayload.put("currency", "NGN");

        logger.info("Creating Paystack recipient: {}", recipientPayload);

        Map recipientResponse;
        try {
            recipientResponse = getWebClient().post()
                    .uri("/transferrecipient")
                    .header("Authorization", "Bearer " + paystackSecret)
                    .bodyValue(recipientPayload)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            tx.setStatus("FAILED");
            txRepo.save(tx);
            logger.error("Failed to create Paystack recipient: reference={}, error={}", tx.getReference(), e.getMessage());
            throw new RuntimeException("Failed to create Paystack recipient", e);
        }

        if (recipientResponse == null || !Boolean.TRUE.equals(recipientResponse.get("status"))) {
            tx.setStatus("FAILED");
            txRepo.save(tx);
            logger.error("Paystack recipient creation failed: {}", recipientResponse);
            throw new RuntimeException("Failed to create Paystack recipient");
        }

        Map recipientData = (Map) recipientResponse.get("data");
        String recipientCode = (String) recipientData.get("recipient_code");
        logger.info("Paystack recipient created: {}", recipientCode);

        // 2. Initiate Transfer
        Map<String, Object> transferPayload = new HashMap<>();
        transferPayload.put("source", "balance");
        transferPayload.put("amount", (int) (request.getAmount() * 100)); // convert to kobo
        transferPayload.put("recipient", recipientCode);
        transferPayload.put("reason", "Wallet withdrawal");
        transferPayload.put("reference", tx.getReference());

        logger.info("Initiating Paystack transfer: {}", transferPayload);

        Map transferResponse;
        try {
            transferResponse = getWebClient().post()
                    .uri("/transfer")
                    .header("Authorization", "Bearer " + paystackSecret)
                    .bodyValue(transferPayload)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            tx.setStatus("FAILED");
            txRepo.save(tx);
            logger.error("Paystack transfer failed: reference={}, error={}", tx.getReference(), e.getMessage());
            throw new RuntimeException("Failed to initiate Paystack transfer", e);
        }

        logger.info("Paystack transfer response: {}", transferResponse);

        if (transferResponse != null && Boolean.TRUE.equals(transferResponse.get("status"))) {
            Map data = (Map) transferResponse.get("data");
            tx.setStatus("PROCESSING"); // Paystack may still process asynchronously
            txRepo.save(tx);

            // Deduct from wallet immediately
            wallet.setBalance(wallet.getBalance() - tx.getAmount());
            walletRepo.save(wallet);

            WithdrawResponse withdrawResponse = new WithdrawResponse();
            withdrawResponse.setStatus("PROCESSING");
            withdrawResponse.setMessage("Withdrawal initiated, awaiting Paystack confirmation");
            withdrawResponse.setReference(tx.getReference());
            withdrawResponse.setNewBalance(wallet.getBalance());

            return withdrawResponse;
        } else {
            tx.setStatus("FAILED");
            txRepo.save(tx);
            logger.error("Withdrawal initiation failed: reference={}", tx.getReference());
            throw new RuntimeException("Failed to initiate withdrawal");
        }
    }

    @Override
    public P2PTransferResponse transfer(P2PTransferRequest request) {
        validateAmount(request.getAmount());

        Wallet sender = walletRepo.findByUserId(request.getSenderId())
                .orElseThrow(() -> new RuntimeException("Sender wallet not found"));
        Wallet receiver = walletRepo.findByUserId(request.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Receiver wallet not found"));

        if (sender.getBalance() < request.getAmount()) {
            throw new RuntimeException("Insufficient balance");
        }

        sender.setBalance(sender.getBalance() - request.getAmount());
        receiver.setBalance(receiver.getBalance() + request.getAmount());
        walletRepo.save(sender);
        walletRepo.save(receiver);

        return new P2PTransferResponse(
                "SUCCESS", "Transfer successful",
                sender.getBalance(), receiver.getBalance(),
                generateReference("TRF")
        );
    }

    @Override
    public DepositResponse suiDeposit(DepositRequest request) {
        logger.info("Deposit request received: userId={}, amount={}", request.getUserId(), request.getAmount());
        validateAmount(request.getAmount());

        Wallet wallet = walletRepo.findByUserId(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));


        Transaction tx = new Transaction();
        tx.setReference(generateReference("DEP"));
        tx.setAmount(request.getAmount());
        tx.setStatus("PENDING");
        tx.setType("DEPOSIT");
        tx.setWallet(wallet);
        tx = txRepo.save(tx);

        logger.info("Transaction created: reference={}, status=PENDING", tx.getReference());

        // Initialize Paystack payment
        Map<String, Object> body = new HashMap<>();
        body.put("amount", (int) (request.getAmount() * 100));
        body.put("email", request.getEmail());
        body.put("reference", tx.getReference());
        body.put("callback_url", request.getCallbackUrl());

        logger.info("Initializing Paystack payment: {}", body);

        Map response;
        try {
            response = getWebClient().post()
                    .uri("/transaction/initialize")
                    .header("Authorization", "Bearer " + paystackSecret)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            tx.setStatus("FAILED");
            txRepo.save(tx);
            logger.error("Paystack initialization failed: reference={}, error={}", tx.getReference(), e.getMessage());
            throw new RuntimeException("Failed to initialize Paystack payment", e);
        }

        logger.info("Paystack response: {}", response);

        if (response != null && Boolean.TRUE.equals(response.get("status"))) {
            Map data = (Map) response.get("data");
            String paymentUrl = (String) data.get("authorization_url");

            DepositResponse depositResponse = new DepositResponse();
            depositResponse.setStatus("PENDING");
            depositResponse.setMessage("Payment initialized. Complete payment via Paystack");
            depositResponse.setReference(tx.getReference());
            depositResponse.setPaymentUrl(paymentUrl);
            depositResponse.setNewBalance(wallet.getBalance()); // Balance not yet updated
            logger.info("Deposit initialized: reference={}, paymentUrl={}", tx.getReference(), paymentUrl);
            return depositResponse;
        } else {
            tx.setStatus("FAILED");
            txRepo.save(tx);
            logger.error("Failed to initialize Paystack payment: reference={}", tx.getReference());
            throw new RuntimeException("Failed to initialize Paystack payment");

        }
    }

    @Override
    public TransactionStatusResponse suiCheckTransactionStatus(String reference) {
        Transaction tx = txRepo.findByReference(reference)
                .orElseThrow(() -> new RuntimeException("Transaction not found for reference: " + reference));

        Long userId = tx.getWallet().getUserId();
        Wallet wallet = walletRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found for userId: " + userId));

        Map response;
        try {
            response = getWebClient().get()
                    .uri("/transaction/verify/"+ reference)
                    .header("Authorization", "Bearer " + paystackSecret)
                    .header( "Content-Type","application/json" )
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            tx.setStatus("FAILED");
            txRepo.save(tx);
            logger.error("Paystack verification failed: reference={}, error={}", tx.getReference(), e.getMessage());
            throw new RuntimeException("Failed to verify Paystack payment", e);
        }
        logger.info("Paystack verification response: {}", response);

        if (response != null && Boolean.TRUE.equals(response.get("status"))) {
            tx.setStatus( "SUCCESS" );
            tx = txRepo.save(tx);
            wallet.setBalance( wallet.getBalance() + tx.getAmount() );
            wallet = walletRepo.save( wallet );
        } else {
            tx.setStatus("FAILED");
            txRepo.save(tx);
            logger.error("Failed to verify Paystack payment: reference={}", tx.getReference());
            throw new RuntimeException("Failed to verify Paystack payment");

        }
        TransactionStatusResponse resp = new TransactionStatusResponse();
        resp.setReference(tx.getReference());
        resp.setTransactionStatus(tx.getStatus());
        resp.setWalletBalance(wallet.getBalance());
        return resp;
    }

    @Override
    public BalanceResponse checkBalance(Long userId) {
        Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);
        logger.info("Fetching wallet for userId={}", userId);

        Wallet wallet = walletRepo.findByUserId(userId)
                .orElseThrow(() -> {
                    logger.error("Wallet not found for userId={}", userId);
                    return new RuntimeException("Wallet not found");
                });

        logger.info("Wallet found for userId={}: balance={}", userId, wallet.getBalance());

        BalanceResponse response = new BalanceResponse();
        response.setUserId(userId);
        response.setBalance(wallet.getBalance());
        response.setCurrency("NGN");

        return response;
    }



    public String initiateTransfer(String bankCode, String accountNumber, Double amount) {
        String recipientCode = createRecipient(bankCode, accountNumber);

        Map<String, Object> body = new HashMap<>();
        body.put("source", "balance");
        body.put("reason", "Wallet withdrawal");
        body.put("amount", (int) (amount * 100)); // convert to kobo
        body.put("recipient", recipientCode);

        try {
            Map response = getWebClient().post()
                    .uri("/transfer")
                    .header("Authorization", "Bearer " + paystackSecret)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && Boolean.TRUE.equals(response.get("status"))) {
                return "PENDING";
            } else {
                return "FAILED";
            }
        } catch (Exception e) {
            throw new RuntimeException("Paystack transfer failed: " + e.getMessage(), e);
        }
    }

    private String createRecipient(String bankCode, String accountNumber) {
        Map<String, Object> body = new HashMap<>();
        body.put("type", "nuban");
        body.put("name", "Wallet User");
        body.put("account_number", accountNumber);
        body.put("bank_code", bankCode);
        body.put("currency", "NGN");

        Map response = getWebClient().post()
                .uri("/transferrecipient")
                .header("Authorization", "Bearer " + paystackSecret)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response != null && Boolean.TRUE.equals(response.get("status"))) {
            Map data = (Map) response.get("data");
            return (String) data.get("recipient_code");
        } else {
            throw new RuntimeException("Could not create transfer recipient");
        }
    }


}

