package in.devpay.transaction_service.controller;

import in.devpay.transaction_service.dtos.TransactionResponse;
import in.devpay.transaction_service.dtos.TransferRequest;
import in.devpay.transaction_service.entities.Transaction;
import in.devpay.transaction_service.services.interfaces.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@RestController
@RequestMapping("/api/transactions/")
public class TransactionController {
    private final TransactionService service;

    @PostMapping("/create")
    public ResponseEntity<TransactionResponse> createTransaction(@RequestBody TransferRequest request){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createTransaction(request.toTransaction()).toTransactionResponse());
    }

    @GetMapping("/all")
    public ResponseEntity<List<TransactionResponse>> getAllTransactions(){
        return ResponseEntity.status(HttpStatus.OK)
                .body(service.getAllTransaction()
                        .stream()
                        .map(Transaction::toTransactionResponse)
                        .toList()
                );
    }
}
