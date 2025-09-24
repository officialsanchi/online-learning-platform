package payment.Transactions;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;


public class DepositRequest {
    private Long userId;
    private Double amount;
    private String paymentMethod;
    private String email;
    private String callbackUrl;

    public DepositRequest() {}

    public DepositRequest(Long userId, Double amount, String paymentMethod, String email, String callbackUrl) {
        this.userId = userId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.email = email;
        this.callbackUrl = callbackUrl;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCallbackUrl() { return callbackUrl; }
    public void setCallbackUrl(String callbackUrl) { this.callbackUrl = callbackUrl; }
}