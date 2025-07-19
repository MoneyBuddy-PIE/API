package moneybuddy.fr.moneybuddy.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import moneybuddy.fr.moneybuddy.model.enums.TransactionType;

@Document(collection = "transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    private String id;
    private String childId;
    private String parentId;
    private TransactionType type;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String amount;
    private String oldAmount;
    private String newAmount;
    private String description;
}
