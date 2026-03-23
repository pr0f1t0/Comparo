package com.pr0f1t.comparo.adminservice.mapper;

import com.pr0f1t.comparo.adminservice.dto.StatsResponse;
import com.pr0f1t.comparo.adminservice.dto.UserResponse;
import com.pr0f1t.comparo.adminservice.entity.PlatformStats;
import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdminMapper {

    StatsResponse toDto(PlatformStats stats);

    UserResponse toDto(UserRepresentation user);

}
