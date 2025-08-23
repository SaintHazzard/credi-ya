package co.com.bancolombia.model.user;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
//import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
  private String id;

  private String names;

  private String lastname;

  private LocalDate birthDate;

  private String address;

  private String phone;

  private String email;

  private BigDecimal salaryBase;

}
