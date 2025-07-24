package moneybuddy.fr.moneybuddy.dtos.chapter;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import moneybuddy.fr.moneybuddy.dtos.course.CourseDto;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateChapterRequest {
    @NotBlank(message = "Title is mandatory")
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    private String title;

    @NotBlank(message = "Description is mandatory")
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @PositiveOrZero(message = "Order must be positive or zero")
    private int order;

    @Valid
    @NotEmpty(message = "At least one course is required")
    private List<CourseDto> courses;
}