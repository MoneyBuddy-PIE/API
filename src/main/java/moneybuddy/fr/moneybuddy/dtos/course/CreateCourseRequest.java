package moneybuddy.fr.moneybuddy.dtos.course;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import moneybuddy.fr.moneybuddy.model.Resource;
import moneybuddy.fr.moneybuddy.model.Section;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCourseRequest {
    
    @NotBlank(message = "ChapterId is mandatory")
    private String chapterId;

    @NotBlank(message = "Title is mandatory")
    private String title;

    @Positive(message = "Readtime has to be > 0")
    private int readTime;

    @PositiveOrZero(message = "Order has to be >= 0")
    private int order;

    @NotNull(message = "Image is mandatory")
    private MultipartFile file;

    @Valid
    @NotEmpty(message = "Resources is mandatory => Array of {title: string, url: string, type: RessourceType}")
    private List<Resource> resources;

    @Valid
    @NotEmpty(message = "Section is mandatory")
    private List<Section> sections;
}
