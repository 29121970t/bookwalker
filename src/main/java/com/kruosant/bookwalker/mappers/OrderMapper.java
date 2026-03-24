package com.kruosant.bookwalker.mappers;


import com.kruosant.bookwalker.domains.Book;
import com.kruosant.bookwalker.domains.Client;
import com.kruosant.bookwalker.domains.Order;
import com.kruosant.bookwalker.dtos.order.OrderCreateDto;
import com.kruosant.bookwalker.dtos.order.OrderFullDto;
import com.kruosant.bookwalker.dtos.order.OrderPatchDto;
import com.kruosant.bookwalker.dtos.order.OrderPutDto;
import com.kruosant.bookwalker.exceptions.BadRequestException;
import com.kruosant.bookwalker.repositories.BookRepository;
import com.kruosant.bookwalker.repositories.ClientRepository;
import lombok.NonNull;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class OrderMapper {
  @Autowired
  private BookRepository bookRepo;
  @Autowired
  private ClientRepository clientRepo;
  @Autowired
  private ClientMapper clientMapper;

  public abstract Order toOrder(OrderCreateDto dto);

  public abstract OrderFullDto toFullDto(Order order);

  public abstract OrderPatchDto toPatchDto(OrderPutDto dto);

  public Set<Book> map(List<@NonNull Long> value) throws BadRequestException {
    return value.stream().map(id -> bookRepo.findById(id)
        .orElseThrow(BadRequestException::new)).collect(Collectors.toCollection(HashSet::new));
  }

  public Client map(Long client) throws BadRequestException {
    return clientRepo.findById(client).orElseThrow(BadRequestException::new);
  }

}
