/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.dtos.course;

import java.util.Map;

import lombok.Builder;
import lombok.Data;
import moneybuddy.fr.moneybuddy.dtos.section.SectionWithProgress;

@Data
@Builder
public class CourseProgressData {
  private boolean completed;
  private int completedCoursesCount;
  private int totalCoursesCount;
  private int progressPercentage;
  private Map<String, SectionWithProgress> sections;
}
