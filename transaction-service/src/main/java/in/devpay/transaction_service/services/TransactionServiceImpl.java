package in.devpay.transaction_service.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.devpay.transaction_service.entities.Transaction;
import in.devpay.transaction_service.entities.enums.TransactionStatus;
import in.devpay.transaction_service.kafka.KafkaEventProducer;
import in.devpay.transaction_service.repositorirs.TransactionRepository;
import in.devpay.transaction_service.services.interfaces.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository repository;

    private final ObjectMapper objectMapper;

    private final KafkaEventProducer kafkaEventProducer;

    @Override
    public Transaction createTransaction(Transaction transaction) {

        transaction.setTimestamp(LocalDateTime.now());
        transaction.setStatus(TransactionStatus.SUCCESS);

        Transaction saved = repository.save(transaction);
        System.out.println("Saved Transaction from DB: " + saved);

        try {
            String eventPayload = objectMapper.writeValueAsString(saved);
            String key = String.valueOf(saved.getId());
            kafkaEventProducer.sendTransactionEvent(key, eventPayload);
            System.out.println("Kafka Message Sent");
        }
        catch (Exception e){
            System.err.println("Failed to send kafka event: " + e.getMessage());
            e.printStackTrace();
        }
        return saved;
    }

    @Override
    public List<Transaction> getAllTransaction() {
        return repository.findAll();
    }
}
