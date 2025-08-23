package co.com.bancolombia.r2dbc.dtos.user;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserDTO {
 @Id
  private String id;

  @NotBlank(message = "El nombre es obligatorio")
  private String names;

  @NotBlank(message = "El apellido es obligatorio")
  private String lastname;

  @NotNull(message = "La fecha de nacimiento es obligatoria")
  @Column("birth_date")
  private LocalDate birthDate;

  @NotBlank(message = "La dirección es obligatoria")
  private String address;

  @NotBlank(message = "El teléfono es obligatorio")
  private String phone;

  @NotBlank(message = "El email es obligatorio")
  @Email(message = "Debe ser un email válido")
  @Column("email")
  private String email;

  @NotNull(message = "El salario base es obligatorio")
  @Digits(integer = 10, fraction = 2, message = "Formato de salario inválido")
  @Column("salary_base")
  private BigDecimal salaryBase;
}
