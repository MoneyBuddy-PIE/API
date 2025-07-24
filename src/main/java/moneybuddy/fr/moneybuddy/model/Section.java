package moneybuddy.fr.moneybuddy.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Section {    
    private String title;
    private String content;

    private Quiz quiz;
}