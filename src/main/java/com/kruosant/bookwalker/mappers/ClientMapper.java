package com.kruosant.bookwalker.mappers;


import com.kruosant.bookwalker.domains.Client;

import com.kruosant.bookwalker.dtos.client.*;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClientMapper {
  Client toClient(ClientCreateDto dto);

  ClientFullDto toFullDto(Client client);

  ClientPatchDto toPatchDto(ClientPutDto dto);
}
