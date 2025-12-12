/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.service;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.userProgress.UserProgressMakeCourseAsComplete;
import moneybuddy.fr.moneybuddy.model.UserProgress;
import moneybuddy.fr.moneybuddy.repository.UserProgressRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProgressService {

  private final UserProgressRepository userProgressRepository;
  private final JwtService jwtService;

  public void markCourseAsCompleted(String token, UserProgressMakeCourseAsComplete req) {
    String subAccountId = jwtService.extractSubAccountId(token);

    UserProgress.CourseProgress courseProgress =
        UserProgress.CourseProgress.builder()
            .completed(req.getSubAccountScore() >= req.getQuizScore())
            .unlocked(req.getSubAccountScore() >= req.getQuizScore())
            .quizScore(req.getSubAccountScore())
            .completedAt(LocalDateTime.now())
            .build();

    System.out.println(courseProgress);

    UserProgress userProgress =
        UserProgress.builder().subAccountId(subAccountId).createdAt(LocalDateTime.now()).build();

    userProgressRepository.save(userProgress);
  }
}
