package payment.Transactions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



import java.util.Map;

@RestController
@RequestMapping("/api/transactions")

public class TransactionController {
    private final PaystackWebhookService webhookService;
    private final TransactionService transactionService;
    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    public TransactionController(PaystackWebhookService webhookService, TransactionService transactionService ) {
        this.webhookService = webhookService;
        this.transactionService = transactionService;
    }

    @PostMapping("/deposit")
    public DepositResponse deposit(@RequestBody DepositRequest request) {
        return transactionService.deposit(request);
    }

//    @PostMapping("/webhook")
//    public ResponseEntity<String> paystackWebhook(@RequestBody String payload,
//                                                  @RequestHeader("x-paystack-signature") String signature) {
//        if (!transactionService.verifySignatu(payload, signature)) {
//            logger.warn("Invalid webhook signature");
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid signature");
//        }
//
//        Map<String, Object> eventPayload = transactionService.parsePayload(payload);
//        webhookService.handleWebhook(
//                (String) eventPayload.get("event"),
//                (Map<String, Object>) eventPayload.get("data")
//        );
//
//        logger.info("Webhook processed successfully");
//        return ResponseEntity.ok("Webhook processed");
//    }

    @PostMapping("sui/deposit")
    public DepositResponse suiDeposit(@RequestBody DepositRequest request) {
        return transactionService.suiDeposit(request);
    }

    @GetMapping("/balance/{userId}")
    public BalanceResponse checkBalance(@PathVariable Long userId) {
        logger.info("Check balance request for userId={}", userId);
        return transactionService.checkBalance(userId);
    }

    @PostMapping("/withdraw")
    public WithdrawResponse fiatWithdraw(@RequestBody WithdrawRequest request) {
        return transactionService.withdraw(request);
    }

    @PostMapping("/p2p")
    public P2PTransferResponse fiatP2PTransfer(@RequestBody P2PTransferRequest request) {
        return transactionService.transfer(request);
    }

    @GetMapping("/verify/transaction")
    public TransactionStatusResponse checkTransactionStatus(@RequestParam(value="reference") String reference){
        return transactionService.checkDepositTransactionStatus(reference);
    }
    @GetMapping("/verify/withdrawal/transaction")
    public TransactionStatusResponse checkWithdrawalTransactionStatus(@RequestParam(value="reference") String reference){
        return transactionService.checkWithdrawTransactionStatus(reference);
    }
}
