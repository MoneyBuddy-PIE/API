/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.service;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.section.CompleteSection;
import moneybuddy.fr.moneybuddy.model.Chapter;
import moneybuddy.fr.moneybuddy.model.Course;
import moneybuddy.fr.moneybuddy.model.Section;
import moneybuddy.fr.moneybuddy.model.SubAccount;
import moneybuddy.fr.moneybuddy.model.enums.CompletedCourse;
import moneybuddy.fr.moneybuddy.model.enums.SubAccountRole;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProgressOrchestratorService {

  private final UserProgressService userProgressService;
  private final ChapterService chapterService;
  private final CourseService courseService;
  private final SectionService sectionService;
  private final SubAccountService subAccountService;
  private final CoinService coinService;
  private final JwtService jwtService;

  public CompletedCourse completeSection(String token, String sectionId, CompleteSection req) {
    Section section = sectionService.getById(sectionId);
    SubAccount subAccount = subAccountService.getById(jwtService.extractSubAccountId(token));

    return userProgressService.markSectionAsCompleted(subAccount, section, req.getScore());
  }

  public CompletedCourse completeCourse(String token, String courseId) {
    Course course = courseService.getById(courseId);
    SubAccount subAccount = subAccountService.getById(jwtService.extractSubAccountId(token));

    CompletedCourse status = userProgressService.markCourseAsCompleted(subAccount, course);

    if (status == CompletedCourse.COMPLETED) {
      if (subAccount.getRole() == SubAccountRole.CHILD)
        coinService.updateCoinForCourseOrChapter(
            subAccount,
            course.getCoinReward(),
            String.format("Cours %s complété !", course.getTitle()));

      completeChapter(subAccount, course.getChapterId());
    }

    return status;
  }

  public void completeChapter(SubAccount subAccount, String chapterId) {
    Chapter chapter = chapterService.getTotalChapter(chapterId);

    userProgressService.markChapterAsCompleted(subAccount, chapter);

    if (subAccount.getRole() == SubAccountRole.CHILD)
      coinService.updateCoinForCourseOrChapter(
          subAccount,
          chapter.getCoinReward(),
          String.format("Chapitre %s complété !", chapter.getTitle()));
  }
}
