package payment.Transactions;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {
    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }
    @PostMapping("/create")
    public Wallet createWallet(@RequestBody Map<String, Long> request) {
        Long userId = request.get("userId");
        return walletService.createWallet(userId);
    }

}
