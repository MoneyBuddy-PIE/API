package moneybuddy.fr.moneybuddy.model;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    private String title;
    private String description;
    private int order;
    private int readTime;
    
    private List<Section> sections;
    
    private List<Resource> resources;
    
    @Builder.Default
    private boolean isLocked = true;
}