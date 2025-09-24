package payment.Transactions;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;


public class DepositResponse {
    private String status;
    private String message;
    private Double newBalance;
    private String reference;
    private String paymentUrl;

    public DepositResponse() {}

    public DepositResponse(String status, String message, Double newBalance, String reference, String paymentUrl) {
        this.status = status;
        this.message = message;
        this.newBalance = newBalance;
        this.reference = reference;
        this.paymentUrl = paymentUrl;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Double getNewBalance() { return newBalance; }
    public void setNewBalance(Double newBalance) { this.newBalance = newBalance; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public String getPaymentUrl() { return paymentUrl; }
    public void setPaymentUrl(String paymentUrl) { this.paymentUrl = paymentUrl; }
}
