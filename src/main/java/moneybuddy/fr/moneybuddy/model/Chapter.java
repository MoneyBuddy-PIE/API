package moneybuddy.fr.moneybuddy.model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import moneybuddy.fr.moneybuddy.model.enums.SubAccountRole;

@Document(collection = "chapters")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Chapter {
    @Id
    private String id;

    private String title;
    private String description;
    private int level;
    private int order;

    private SubAccountRole subAccountRole;
    
    private List<Course> courses;

    private String creatorId;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;
}
