package payment.Transactions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


public class WithdrawResponse {
    public WithdrawResponse() {}
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Double getNewBalance() {
        return newBalance;
    }

    public void setNewBalance(Double newBalance) {
        this.newBalance = newBalance;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    private String status;
    private String message;
    private Double newBalance;
    private String reference;

    public WithdrawResponse(String status, String message, Double newBalance, String reference) {
        this.status = status;
        this.message = message;
        this.newBalance = newBalance;
        this.reference = reference;
    }
}
