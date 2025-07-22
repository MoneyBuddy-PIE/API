package moneybuddy.fr.moneybuddy.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import moneybuddy.fr.moneybuddy.model.Question;
import moneybuddy.fr.moneybuddy.model.enums.SubAccountRole;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseRequest {

    @NotBlank(message = "Le titre est obligatoire")
    private String title;

    @NotBlank(message = "Le titre est obligatoire")
    private String description;

    @NotBlank(message = "Le read_time est obligatoire")
    private String read_time;

    @NotNull(message = "Role is mandatory")
    private SubAccountRole subAccountRole;

    private List<Question> questions;
}