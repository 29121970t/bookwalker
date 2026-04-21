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
    date = "2026-04-22T00:06:01+0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 26.0.2 (Arch Linux)"
)
@Component
public class ClientMapperImpl implements ClientMapper {

    @Override
    public Client toClient(ClientCreateDto dto) {
        if ( dto == null ) {
            return null;
        }

        Client client = new Client();

        client.setUsername( dto.getUsername() );
        client.setPassword( dto.getPassword() );

        return client;
    }

    @Override
    public ClientFullDto toFullDto(Client client) {
        if ( client == null ) {
            return null;
        }

        ClientFullDto.ClientFullDtoBuilder clientFullDto = ClientFullDto.builder();

        clientFullDto.id( client.getId() );
        clientFullDto.username( client.getUsername() );
        clientFullDto.orders( orderSetToOrderBasicInfoDtoList( client.getOrders() ) );

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
        bookBasicInfoDto.publishDate( book.getPublishDate() );
        bookBasicInfoDto.price( book.getPrice() );

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

        orderBasicInfoDto.id( order.getId() );
        orderBasicInfoDto.date( order.getDate() );
        orderBasicInfoDto.books( bookSetToBookBasicInfoDtoSet( order.getBooks() ) );

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
