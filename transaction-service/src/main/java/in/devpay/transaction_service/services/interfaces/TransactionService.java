package in.devpay.transaction_service.services.interfaces;

import in.devpay.transaction_service.entities.Transaction;

import java.util.List;

public interface TransactionService {

    Transaction createTransaction(Transaction transaction);

    List<Transaction> getAllTransaction();
}
