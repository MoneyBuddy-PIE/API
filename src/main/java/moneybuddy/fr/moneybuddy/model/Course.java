package moneybuddy.fr.moneybuddy.model;

import lombok.*;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "courses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    @Id
    private String id;
    private String chapterId;
    private String creator;

    private String title;
    private int readTime;
    private int order;
    
    private List<Section> sections;
    
    private List<Resource> resources;
    
    @Builder.Default
    private boolean locked = true;
}