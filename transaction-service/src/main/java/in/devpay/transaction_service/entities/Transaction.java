package in.devpay.transaction_service.entities;

import in.devpay.transaction_service.dtos.TransactionResponse;
import in.devpay.transaction_service.entities.enums.TransactionStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sender_name", nullable = false)
    private Long senderId;

    @Column(name = "receiver_name", nullable = false)
    private Long receiverId;

    @Column(nullable = false)
    @Positive(message = "Amount must be positive")
    private Double amount;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private TransactionStatus status;

    @PrePersist
    public void prePersist() {
        if (timestamp == null){
            timestamp = LocalDateTime.now();
        }
        if (status == null){
            status = TransactionStatus.PENDING;
        }
    }

    public TransactionResponse toTransactionResponse(){
        return TransactionResponse.builder()
                .id(this.id)
                .senderId(this.senderId)
                .receiverId(this.receiverId)
                .amount(this.amount)
                .timestamp(this.timestamp)
                .status(this.status.toString())
                .build();
    }
}
