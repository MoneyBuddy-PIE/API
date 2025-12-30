/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.dtos.course;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UpdateCourseRequest {
  private String chapterId;
  private String title;
  private boolean locked;

  @Positive(message = "Readtime has to be > 0")
  private int readTime;

  @PositiveOrZero(message = "Order has to be >= 0")
  private int order;

  private MultipartFile file;
}
