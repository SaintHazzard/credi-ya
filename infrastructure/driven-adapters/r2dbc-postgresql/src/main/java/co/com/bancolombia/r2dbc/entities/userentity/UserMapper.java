package co.com.bancolombia.r2dbc.entities.userentity;


import org.mapstruct.Mapper;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.r2dbc.dtos.user.UserDTO;
import co.com.bancolombia.r2dbc.dtos.user.UserRecord;

@Mapper(componentModel = "spring")
public interface UserMapper {

  User toDomain(UserEntity entity);

  User toDomain(UserDTO record);

  UserDTO toDto(User domain);

  UserEntity toEntity(User domain);

  UserRecord toRecord(User domain);

}
