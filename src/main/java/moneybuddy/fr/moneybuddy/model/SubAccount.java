package moneybuddy.fr.moneybuddy.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Document(collection = "subAccounts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubAccount {
    @Id
    private String id;
    private String accountId;

    private String name;
    private SubAccountRole role;
    private boolean isActive;

    private Float money;

    @JsonIgnore
    private String pin;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastConnexion;
    public Float getBalance() {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'getBalance'");
    }
}
