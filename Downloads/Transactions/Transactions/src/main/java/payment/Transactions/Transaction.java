package payment.Transactions;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Transaction() {

    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    private String reference;
    private Double amount;
    private String status; // PENDING, SUCCESS, FAILED
    private String type;   // DEPOSIT, WITHDRAW, TRANSFER

    @ManyToOne
    private Wallet wallet;

    public Transaction(Long id, String reference, Double amount, String status, String type, Wallet wallet) {
        this.id = id;
        this.reference = reference;
        this.amount = amount;
        this.status = status;
        this.type = type;
        this.wallet = wallet;
    }

}
