package payment.Transactions;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByUserIdAndCurrency(Long userId, CurrencyType currency);
    Optional<Wallet> findByUserId(Long userId);
}
