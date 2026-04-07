package com.kruosant.bookwalker.mappers;


import com.kruosant.bookwalker.domains.Client;
import com.kruosant.bookwalker.dtos.client.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClientMapper {
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "orders", ignore = true)
  Client toClient(ClientCreateDto dto);

  ClientFullDto toFullDto(Client client);

  ClientBasicInfoDto toBasicInfoDto(Client client);

  ClientPatchDto toPatchDto(ClientPutDto dto);
}
