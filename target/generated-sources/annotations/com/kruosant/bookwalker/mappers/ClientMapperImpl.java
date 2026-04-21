package com.kruosant.bookwalker.mappers;

import com.kruosant.bookwalker.domains.Book;
import com.kruosant.bookwalker.domains.Client;
import com.kruosant.bookwalker.domains.Order;
import com.kruosant.bookwalker.dtos.book.BookBasicInfoDto;
import com.kruosant.bookwalker.dtos.client.ClientBasicInfoDto;
import com.kruosant.bookwalker.dtos.client.ClientCreateDto;
import com.kruosant.bookwalker.dtos.client.ClientFullDto;
import com.kruosant.bookwalker.dtos.client.ClientPatchDto;
import com.kruosant.bookwalker.dtos.client.ClientPutDto;
import com.kruosant.bookwalker.dtos.order.OrderBasicInfoDto;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-21T23:04:11+0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class ClientMapperImpl implements ClientMapper {

    @Override
    public Client toClient(ClientCreateDto dto) {
        if ( dto == null ) {
            return null;
        }

        Client client = new Client();

        client.setPassword( dto.getPassword() );
        client.setUsername( dto.getUsername() );

        return client;
    }

    @Override
    public ClientFullDto toFullDto(Client client) {
        if ( client == null ) {
            return null;
        }

        ClientFullDto.ClientFullDtoBuilder clientFullDto = ClientFullDto.builder();

        clientFullDto.id( client.getId() );
        clientFullDto.orders( orderSetToOrderBasicInfoDtoList( client.getOrders() ) );
        clientFullDto.username( client.getUsername() );

        return clientFullDto.build();
    }

    @Override
    public ClientBasicInfoDto toBasicInfoDto(Client client) {
        if ( client == null ) {
            return null;
        }

        ClientBasicInfoDto.ClientBasicInfoDtoBuilder clientBasicInfoDto = ClientBasicInfoDto.builder();

        clientBasicInfoDto.id( client.getId() );
        clientBasicInfoDto.username( client.getUsername() );

        return clientBasicInfoDto.build();
    }

    @Override
    public ClientPatchDto toPatchDto(ClientPutDto dto) {
        if ( dto == null ) {
            return null;
        }

        ClientPatchDto.ClientPatchDtoBuilder clientPatchDto = ClientPatchDto.builder();

        clientPatchDto.username( dto.getUsername() );

        return clientPatchDto.build();
    }

    protected BookBasicInfoDto bookToBookBasicInfoDto(Book book) {
        if ( book == null ) {
            return null;
        }

        BookBasicInfoDto.BookBasicInfoDtoBuilder bookBasicInfoDto = BookBasicInfoDto.builder();

        bookBasicInfoDto.id( book.getId() );
        bookBasicInfoDto.name( book.getName() );
        bookBasicInfoDto.pageCount( book.getPageCount() );
        bookBasicInfoDto.price( book.getPrice() );
        bookBasicInfoDto.publishDate( book.getPublishDate() );

        return bookBasicInfoDto.build();
    }

    protected Set<BookBasicInfoDto> bookSetToBookBasicInfoDtoSet(Set<Book> set) {
        if ( set == null ) {
            return null;
        }

        Set<BookBasicInfoDto> set1 = LinkedHashSet.newLinkedHashSet( set.size() );
        for ( Book book : set ) {
            set1.add( bookToBookBasicInfoDto( book ) );
        }

        return set1;
    }

    protected OrderBasicInfoDto orderToOrderBasicInfoDto(Order order) {
        if ( order == null ) {
            return null;
        }

        OrderBasicInfoDto.OrderBasicInfoDtoBuilder orderBasicInfoDto = OrderBasicInfoDto.builder();

        orderBasicInfoDto.books( bookSetToBookBasicInfoDtoSet( order.getBooks() ) );
        orderBasicInfoDto.date( order.getDate() );
        orderBasicInfoDto.id( order.getId() );

        return orderBasicInfoDto.build();
    }

    protected List<OrderBasicInfoDto> orderSetToOrderBasicInfoDtoList(Set<Order> set) {
        if ( set == null ) {
            return null;
        }

        List<OrderBasicInfoDto> list = new ArrayList<OrderBasicInfoDto>( set.size() );
        for ( Order order : set ) {
            list.add( orderToOrderBasicInfoDto( order ) );
        }

        return list;
    }
}
