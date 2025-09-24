package payment.Transactions;

import lombok.Builder;
import lombok.Data;


public class BalanceResponse {
    public BalanceResponse() {}
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    private Long userId;
    private Double balance;
    private String currency;

    public BalanceResponse(Long userId, Double balance, String currency) {
        this.userId = userId;
        this.balance = balance;
        this.currency = currency;
    }
}
