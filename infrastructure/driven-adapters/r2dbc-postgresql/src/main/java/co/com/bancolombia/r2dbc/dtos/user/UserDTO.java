package co.com.bancolombia.r2dbc.dtos.user;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
  @Digits(integer = 15, fraction = 0, message = "Formato de salario inválido")
  @Max(value = 15000000, message = "El salario base no puede ser mayor a 15000000")
  @Min(value = 0, message = "El salario base no puede ser menor a 0")
  private BigDecimal salaryBase;
}
