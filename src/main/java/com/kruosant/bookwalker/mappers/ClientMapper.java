package com.kruosant.bookwalker.mappers;


import com.kruosant.bookwalker.domains.Client;
import com.kruosant.bookwalker.dtos.client.ClientBasicInfoDto;
import com.kruosant.bookwalker.dtos.client.ClientCreateDto;
import com.kruosant.bookwalker.dtos.client.ClientFullDto;
import com.kruosant.bookwalker.dtos.client.ClientPatchDto;
import com.kruosant.bookwalker.dtos.client.ClientPutDto;
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
