package co.com.bancolombia.r2dbc.entities.userentity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Table("users")
@Data
public class UserEntity {

  @Id
  private String id;

  @NotNull
  @NotBlank
  private String nombres;

  @NotNull
  @NotBlank
  private String apellidos;

  @NotNull
  @NotBlank
  @Column("correo_electronico")
  private String correoElectronico;
}
