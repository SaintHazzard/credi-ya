package co.com.bancolombia.r2dbc.entities.userentity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Table(name = "users")
public class UserEntity {
  

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @NotNull
  @NotBlank
  private String nombres;

  @NotNull
  @NotBlank
  private String apellidos;

  @NotNull
  @NotBlank
  private String correo_electronico;


}
