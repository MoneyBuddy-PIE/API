package moneybuddy.fr.moneybuddy.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import moneybuddy.fr.moneybuddy.model.Question;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseRequest {

    @NotBlank(message = "Le titre est obligatoire")
    private String title;

    @NotBlank(message = "Le titre est obligatoire")
    private String description;

    @NotBlank(message = "Le cours doit contenir des questions")
    private List<Question> questions;
}