package moneybuddy.fr.moneybuddy.dtos.course;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import moneybuddy.fr.moneybuddy.dtos.resource.ResourceDto;
import moneybuddy.fr.moneybuddy.dtos.section.SectionDto;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDto {
    @NotBlank(message = "Course title is mandatory")
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    private String title;
    
    private String description;
    
    @PositiveOrZero
    private int order;

    @Positive
    private int readTime;
    
    @Valid
    @NotEmpty(message = "At least one section is required")
    private List<SectionDto> sections;
    
    @Valid
    private List<ResourceDto> resources;
}