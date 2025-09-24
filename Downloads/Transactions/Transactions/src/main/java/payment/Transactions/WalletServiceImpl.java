package payment.Transactions;

import org.springframework.stereotype.Service;

@Service
public class WalletServiceImpl implements WalletService{
    private final WalletRepository walletRepo;

    public WalletServiceImpl(WalletRepository walletRepo) {
        this.walletRepo = walletRepo;
    }

    @Override
    public Wallet createWallet(Long userId) {
        Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setBalance(0.0);
        wallet.setCurrency(CurrencyType.FIAT); // <-- set a default enum value
       return walletRepo.save(wallet);


//        Wallet wallet = new Wallet();
//        wallet.setUserId(userId);
//        // Do NOT set currency here, let DB handle it
//        return walletRepo.save(wallet);
//        Wallet wallet = new Wallet();
//        wallet.setUserId(userId);
//        wallet.setBalance(0.0);
//        wallet.setCurrency("NGN");
//        return walletRepo.save(wallet);
    }
}
