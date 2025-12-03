package com.riwi.H4.infrastructure.mapper;

import com.riwi.H4.domain.model.User;
import com.riwi.H4.infrastructure.entity.UserEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * User Mapper
 *
 * Maps between UserEntity (infrastructure) and User (domain) using MapStruct.
 *
 * HU5 - TASK 3: Security JWT
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "role", target = "role")
    @Mapping(source = "enabled", target = "enabled")
    @Mapping(source = "createdAt", target = "createdAt")
    User toDomain(UserEntity entity);

    @InheritInverseConfiguration
    UserEntity toEntity(User domain);
}


