package payment.Transactions;

import java.util.Map;

public interface TransactionService {
    DepositResponse deposit(DepositRequest request);
    WithdrawResponse withdraw(WithdrawRequest request);
    P2PTransferResponse transfer(P2PTransferRequest request);
    DepositResponse suiDeposit(DepositRequest request);
    TransactionStatusResponse suiCheckTransactionStatus(String reference);

    BalanceResponse checkBalance(Long userId);

    Map<String, Object> parsePayload(String payload);
    TransactionStatusResponse checkWithdrawTransactionStatus(String reference);


    TransactionStatusResponse checkDepositTransactionStatus(String reference);
}
