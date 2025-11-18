package moneybuddy.fr.moneybuddy.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.*;
import moneybuddy.fr.moneybuddy.model.enums.DepositType;

@Document(collection = "goals-transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositDetails {  
    
    @Id
    private String id;
    private String goalId;
    private String childId;
    private String parentId;
    private String accountId;

    private DepositType type;
    private Number amount;
    private Number previousAmount;
    private Number newAmount;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

}