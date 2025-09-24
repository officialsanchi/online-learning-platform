package payment.Transactions;

import lombok.Data;
import lombok.Getter;



public class TransactionStatusResponse {
    private String reference;       // Transaction reference, e.g., DEP-123456
    private String transactionStatus; // PENDING, SUCCESS, FAILED
    private Double walletBalance;     // Wallet balance after transaction

    // Getters and Setters
    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public Double getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(Double walletBalance) {
        this.walletBalance = walletBalance;
    }

    @Override
    public String toString() {
        return "TransactionStatusResponse{" +
                "reference='" + reference + '\'' +
                ", transactionStatus='" + transactionStatus + '\'' +
                ", walletBalance=" + walletBalance +
                '}';
    }
}
