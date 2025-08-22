package co.com.bancolombia.r2dbc.dtos.user;

import lombok.Data;

@Data
public class UserDTO {
  private String id;

  private String nombres;


  private String apellidos;


  private String correo_electronico;
}
