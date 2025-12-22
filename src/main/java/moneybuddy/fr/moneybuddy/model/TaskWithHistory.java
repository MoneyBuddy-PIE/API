/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.model;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tasks")
@Data
@EqualsAndHashCode(callSuper = true)
public class TaskWithHistory extends Task {

  @DBRef private List<TaskHistory> taskHistory;
}
