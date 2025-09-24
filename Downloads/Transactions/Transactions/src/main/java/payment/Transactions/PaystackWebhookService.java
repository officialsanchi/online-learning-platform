package payment.Transactions;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Map;

@Service
public class PaystackWebhookService {
    private static final Logger logger = LoggerFactory.getLogger(PaystackWebhookService.class);

    private final TransactionRepository txRepo;
    private final WalletRepository walletRepo;
    private final String paystackSecret;

    public PaystackWebhookService(TransactionRepository txRepo, WalletRepository walletRepo,
                                  @Value("${paystack.secret.key}") String paystackSecret) {
        this.txRepo = txRepo;
        this.walletRepo = walletRepo;
        this.paystackSecret = paystackSecret;
    }

    @Transactional
    public void handleWebhook(String event, Map<String, Object> data) {
        String reference = (String) data.get("reference");
        Transaction tx = txRepo.findByReference(reference)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        Wallet wallet = tx.getWallet();
        if ("SUCCESS".equals(tx.getStatus())) return;

        switch (event) {
            case "charge.success":
                if ("DEPOSIT".equalsIgnoreCase(tx.getType())) {
                    wallet.setBalance(wallet.getBalance() + tx.getAmount());
                    walletRepo.save(wallet);
                    tx.setStatus("SUCCESS");
                    txRepo.save(tx);
                    logger.info("Deposit successful: reference={}, newBalance={}", reference, wallet.getBalance());
                }
                break;

            case "charge.failed":
                tx.setStatus("FAILED");
                txRepo.save(tx);
                logger.warn("Deposit failed: reference={}", reference);
                break;

            default:
                logger.warn("Unhandled event type: {}", event);
        }
    }

    public boolean verifySignature(String payload, String signature) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(paystackSecret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secretKey);
            byte[] hash = sha256_HMAC.doFinal(payload.getBytes());
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                String h = Integer.toHexString(0xff & b);
                if (h.length() == 1) hex.append('0');
                hex.append(h);
            }
            return hex.toString().equals(signature);
        } catch (Exception e) {
            return false;
        }
    }

    public Map<String, Object> parsePayload(String payload) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(payload, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse webhook payload", e);
        }
    }
}
