package moneybuddy.fr.moneybuddy.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "tasks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    @Id
    private String id;
    
    private String description;
    private String category;
    private String reward;
    private String dateLimit;

    @Builder.Default
    private boolean isDone = false;
    @Builder.Default
    private boolean prevalidation = false;
    @Builder.Default
    private boolean refused = false;


    private String subaccountIdParent;
    private String subaccountIdChild;
    private String accountId;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;
}
