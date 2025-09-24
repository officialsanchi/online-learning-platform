package payment.Transactions;

import lombok.Builder;
import lombok.Data;


public class WithdrawRequest {
    public WithdrawRequest(){}
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public WithdrawRequest(Long userId, Double amount, String bankCode, String accountNumber) {
        this.userId = userId;
        this.amount = amount;
        this.bankCode = bankCode;
        this.accountNumber = accountNumber;
    }

    private Long userId;       // who is withdrawing
    private Double amount;     // how much
    private String bankCode;   // optional if using Paystack
    private String accountNumber;
}
