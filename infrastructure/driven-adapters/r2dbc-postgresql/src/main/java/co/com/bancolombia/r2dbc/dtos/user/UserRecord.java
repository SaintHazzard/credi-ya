package co.com.bancolombia.r2dbc.dtos.user;

public record UserRecord (
    String id,
    String nombres,
    String apellidos,
    String correo_electronico
) {

}
