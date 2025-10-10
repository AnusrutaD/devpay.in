package in.devpay.transaction_service.dtos;


import in.devpay.transaction_service.entities.Transaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferRequest {

    private Long senderId;

    private Long receiverId;

    private Double amount;

    public Transaction toTransaction(){
        return Transaction.builder()
                .senderId(this.senderId)
                .receiverId(this.receiverId)
                .amount(this.amount)
                .build();
    }
}
