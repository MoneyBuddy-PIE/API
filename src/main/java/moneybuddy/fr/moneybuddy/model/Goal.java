package moneybuddy.fr.moneybuddy.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "goals")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Goal {
    @Id
    private String id;
    
    private String name;
    private Number amount;
    private String emoji;
    private Number depositStatement;

    @Builder.Default
    private boolean isActive = true;
    @Builder.Default
    private boolean isDone = false;
    @Builder.Default
    private boolean useSavingMoney = false;
    @Builder.Default
    private boolean confirmsUseSavingMoney = false;
    // @Builder.Default
    // private boolean validate = false;
    @Builder.Default
    private Number progression = 0;

    private String subaccountIdParent;
    private String subaccountIdChild;
    private String accountId;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;
}
