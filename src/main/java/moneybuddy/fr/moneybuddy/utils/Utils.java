/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.utils;

import java.util.UUID;

import moneybuddy.fr.moneybuddy.dtos.ResponseDto;
import moneybuddy.fr.moneybuddy.model.enums.CompletedCourse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class Utils {

  public Pageable pagination(int page, int size, String sortBy, String sortDir) {
    Sort sort =
        sortDir.equalsIgnoreCase("desc")
            ? Sort.by(sortBy).descending()
            : Sort.by(sortBy).ascending();
    Pageable pageable = PageRequest.of(page, size, sort);
    return pageable;
  }

  public String generateKey(String format) {
    return String.format(format + "%s", UUID.randomUUID());
  }

  public ResponseDto ResponseMessage(CompletedCourse completedCourse) {
    String message = "";
    HttpStatus status = HttpStatus.BAD_REQUEST;
    switch (completedCourse) {
      case LOW_SCORE:
        message = "Score pas suffisant. ";
        status = HttpStatus.BAD_REQUEST;
        break;
      case COMPLETED:
        message = "Completed";
        status = HttpStatus.OK;
        break;
      case ALREADY_COMPLETED:
        message = "Already completed";
        status = HttpStatus.CONFLICT;
        break;
      case NOT_ENOUGH_SECTION_COMPLETED:
        message = "Not all sections completed";
        status = HttpStatus.BAD_REQUEST;
        break;
      default:
        message = "Error";
        status = HttpStatus.BAD_REQUEST;
        break;
    }

    return ResponseDto.builder().message(message).status(status).build();
  }
}
