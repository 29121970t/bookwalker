package com.kruosant.bookwalker.mappers;

import com.kruosant.bookwalker.domains.Book;
import com.kruosant.bookwalker.domains.Order;
import com.kruosant.bookwalker.dtos.book.BookBasicInfoDto;
import com.kruosant.bookwalker.dtos.order.OrderCreateDto;
import com.kruosant.bookwalker.dtos.order.OrderFullDto;
import com.kruosant.bookwalker.dtos.order.OrderPatchDto;
import com.kruosant.bookwalker.dtos.order.OrderPutDto;
import com.kruosant.bookwalker.exceptions.BadRequestException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-22T00:21:17+0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class OrderMapperImpl extends OrderMapper {

    @Override
    public Order toOrder(OrderCreateDto dto) {
        if ( dto == null ) {
            return null;
        }

        Order order = new Order();

        try {
            order.setClient( map( dto.getClient() ) );
        }
        catch ( BadRequestException e ) {
            throw new RuntimeException( e );
        }
        order.setDate( dto.getDate() );
        try {
            order.setBooks( map( dto.getBooks() ) );
        }
        catch ( BadRequestException e ) {
            throw new RuntimeException( e );
        }

        return order;
    }

    @Override
    public OrderFullDto toFullDto(Order order) {
        if ( order == null ) {
            return null;
        }

        OrderFullDto.OrderFullDtoBuilder orderFullDto = OrderFullDto.builder();

        orderFullDto.id( order.getId() );
        orderFullDto.client( map( order.getClient() ) );
        orderFullDto.date( order.getDate() );
        orderFullDto.books( bookSetToBookBasicInfoDtoList( order.getBooks() ) );

        return orderFullDto.build();
    }

    @Override
    public OrderPatchDto toPatchDto(OrderPutDto dto) {
        if ( dto == null ) {
            return null;
        }

        OrderPatchDto.OrderPatchDtoBuilder orderPatchDto = OrderPatchDto.builder();

        List<Long> list = dto.getBooks();
        if ( list != null ) {
            orderPatchDto.books( new ArrayList<Long>( list ) );
        }

        return orderPatchDto.build();
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

    protected List<BookBasicInfoDto> bookSetToBookBasicInfoDtoList(Set<Book> set) {
        if ( set == null ) {
            return null;
        }

        List<BookBasicInfoDto> list = new ArrayList<BookBasicInfoDto>( set.size() );
        for ( Book book : set ) {
            list.add( bookToBookBasicInfoDto( book ) );
        }

        return list;
    }
}
