/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.service;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.ressource.CreateRessourceRequest;
import moneybuddy.fr.moneybuddy.dtos.ressource.UpdateRessourceRequest;
import moneybuddy.fr.moneybuddy.exception.CourseNotFoundException;
import moneybuddy.fr.moneybuddy.exception.RessourceNotFoundException;
import moneybuddy.fr.moneybuddy.model.Course;
import moneybuddy.fr.moneybuddy.model.Ressource;
import moneybuddy.fr.moneybuddy.repository.CourseRepository;
import moneybuddy.fr.moneybuddy.repository.RessourceRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RessourceService {
  private final RessourceRepository ressourceRepository;
  private final CourseRepository courseRepository;

  public Ressource getById(String id) {
    return ressourceRepository.findById(id).orElseThrow(() -> new RessourceNotFoundException(id));
  }

  public Course getCourse(String courseId) {
    return courseRepository
        .findById(courseId)
        .orElseThrow(() -> new CourseNotFoundException(courseId));
  }

  public Ressource createRessource(CreateRessourceRequest req) {
    Ressource ressource =
        Ressource.builder()
            .courseId(req.getCourseId())
            .title(req.getTitle())
            .url(req.getUrl())
            .type(req.getType())
            .build();
    ressource = ressourceRepository.save(ressource);

    Course course = getCourse(req.getCourseId());

    course.getRessource().put(ressource.getId(), ressource);
    courseRepository.save(course);

    return ressource;
  }

  public Ressource updateRessource(String ressourceId, UpdateRessourceRequest req) {
    Ressource ressource = getById(ressourceId);

    if (req.getTitle() != null) ressource.setTitle(req.getTitle());
    if (req.getUrl() != null) ressource.setUrl(req.getUrl());
    if (req.getType() != null) ressource.setType(req.getType());

    ressource.setUpdatedAt(LocalDateTime.now());
    ressource = ressourceRepository.save(ressource);
    return ressource;
  }

  public void deleteRessource(String ressourceId) {
    Ressource ressource = getById(ressourceId);
    Course course = getCourse(ressource.getCourseId());

    course.getRessource().remove(ressourceId);
    courseRepository.save(course);

    ressourceRepository.delete(ressource);
  }
}
