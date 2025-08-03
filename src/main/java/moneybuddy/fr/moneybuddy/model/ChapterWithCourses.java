package moneybuddy.fr.moneybuddy.model;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection = "chapters")
@Data
@EqualsAndHashCode(callSuper = true)
public class ChapterWithCourses extends Chapter{
    
    @DBRef
    private List<Course> courses;
}
