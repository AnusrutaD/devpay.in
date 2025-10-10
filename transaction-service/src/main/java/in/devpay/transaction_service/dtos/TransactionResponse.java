package in.devpay.transaction_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {

    private Long id;

    private Long senderId;

    private Long receiverId;

    private Double amount;

    private LocalDateTime timestamp;

    private String status;
}
