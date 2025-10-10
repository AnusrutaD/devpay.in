package in.devpay.transaction_service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import in.devpay.transaction_service.entities.Transaction;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class KafkaEventProducer {
    private static final String TOPIC = "txt-initiated";

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    @Autowired
    public KafkaEventProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;

        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public void sendTransactionEvent(String key, String message){
        System.out.println("Sending to Kafka -> Topic: " + TOPIC + ", key: " + key + ", Message: " + message);

        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(TOPIC, key, message);

        future.thenAccept(result ->{
            RecordMetadata metadata = result.getRecordMetadata();
            System.out.println("Kafka Message sent Successfully! Topic: " + metadata.topic() + ", Partition: " + metadata.partition());
        }).exceptionally(ex -> {
            System.err.println("Failed to send kafka message: " + ex.getMessage());
            return null;
        });
    }

    public void sendTransactionEvent(String key, Transaction transaction){
        try {
            String message = objectMapper.writeValueAsString(transaction);
            sendTransactionEvent(key, message);
        }
        catch (JsonProcessingException e){
            System.err.println("Error in serializing transaction: " + e.getMessage());
        }
    }


}
