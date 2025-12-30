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

  public void completeSection(String token, String sectionId, CompleteSection req) {
    Section section = sectionService.getById(sectionId);
    SubAccount subAccount = subAccountService.get(jwtService.extractSubAccountId(token));

    userProgressService.markSectionAsCompleted(subAccount, section, req.getScore());
  }

  public void completeCourse(String token, String chapterId, String courseId) {
    Course course = courseService.getById(courseId);
    SubAccount subAccount = subAccountService.get(jwtService.extractSubAccountId(token));

    userProgressService.markCourseAsCompleted(subAccount, course);

    if (subAccount.getRole() == SubAccountRole.CHILD)
      coinService.updateCoin(subAccount, course.getCoinReward(), true);

    completeChapter(subAccount, chapterId);
  }

  public void completeChapter(SubAccount subAccount, String chapterId) {
    Chapter chapter = chapterService.getTotalChapter(chapterId);

    userProgressService.markChapterAsCompleted(subAccount, chapter);

    if (subAccount.getRole() == SubAccountRole.CHILD)
      coinService.updateCoin(subAccount, chapter.getCoinReward(), true);
  }
}
