package moneybuddy.fr.moneybuddy.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "subAccount_course")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubAccountCourse {
    
    @Id
    private String id;

    private String subAccountid;
    private String courseId;
    
    private String score;
    private Boolean isCompleted;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
