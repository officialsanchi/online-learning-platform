package payment.Transactions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


public class P2PTransferResponse {
    private String status;
    private String message;
    private Double senderBalance;
    private Double receiverBalance;
    private String reference;

    // ✅ Add this constructor
    public P2PTransferResponse(String status, String message,
                               Double senderBalance, Double receiverBalance,
                               String reference) {
        this.status = status;
        this.message = message;
        this.senderBalance = senderBalance;
        this.receiverBalance = receiverBalance;
        this.reference = reference;
    }

    // getters and setters...
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Double getSenderBalance() { return senderBalance; }
    public void setSenderBalance(Double senderBalance) { this.senderBalance = senderBalance; }

    public Double getReceiverBalance() { return receiverBalance; }
    public void setReceiverBalance(Double receiverBalance) { this.receiverBalance = receiverBalance; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
}
