package payment.Transactions;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.HashMap;
import java.util.Map;






import java.util.HashMap;
import java.util.Map;

    @Service
    public class PaystackService {

        private final WebClient webClient;

        @Value("${paystack.secret.key}")
        private String paystackSecret;

        public PaystackService(WebClient.Builder builder) {
            this.webClient = builder.baseUrl("https://api.paystack.co").build();
        }

        public String initiateTransfer(String bankCode, String accountNumber, Double amount) {
            String recipientCode = createRecipient(bankCode, accountNumber);

            Map<String, Object> body = new HashMap<>();
            body.put("source", "balance");
            body.put("reason", "Wallet withdrawal");
            body.put("amount", (int) (amount * 100));
            body.put("recipient", recipientCode);

            Map response = webClient.post()
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
        }

        private String createRecipient(String bankCode, String accountNumber) {
            Map<String, Object> body = new HashMap<>();
            body.put("type", "nuban");
            body.put("name", "Wallet User");
            body.put("account_number", accountNumber);
            body.put("bank_code", bankCode);
            body.put("currency", "NGN");

            Map response = webClient.post()
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

        public boolean verifySignature(String jsonPayload, String signature) {
            try {
                Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
                SecretKeySpec secret_key = new SecretKeySpec(paystackSecret.getBytes(), "HmacSHA256");
                sha256_HMAC.init(secret_key);

                byte[] hash = sha256_HMAC.doFinal(jsonPayload.getBytes());
                String expected = bytesToHex(hash);

                return expected.equals(signature);
            } catch (Exception e) {
                return false;
            }
        }

        private String bytesToHex(byte[] hash) {
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        }

        }

