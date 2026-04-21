package com.kruosant.bookwalker.mappers;

import com.kruosant.bookwalker.domains.Book;
import com.kruosant.bookwalker.domains.Publisher;
import com.kruosant.bookwalker.dtos.book.BookBasicInfoDto;
import com.kruosant.bookwalker.dtos.publisher.PublisherCreateDto;
import com.kruosant.bookwalker.dtos.publisher.PublisherFullDto;
import com.kruosant.bookwalker.dtos.publisher.PublisherPatchDto;
import com.kruosant.bookwalker.dtos.publisher.PublisherPutDto;
import java.util.ArrayList;
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
public class PublisherMapperImpl implements PublisherMapper {

    @Override
    public Publisher toAuthor(PublisherFullDto dto) {
        if ( dto == null ) {
            return null;
        }

        Publisher publisher = new Publisher();

        publisher.setName( dto.getName() );

        return publisher;
    }

    @Override
    public Publisher toAuthor(PublisherCreateDto dto) {
        if ( dto == null ) {
            return null;
        }

        Publisher publisher = new Publisher();

        publisher.setName( dto.getName() );

        return publisher;
    }

    @Override
    public PublisherFullDto toFullDto(Publisher dto) {
        if ( dto == null ) {
            return null;
        }

        PublisherFullDto.PublisherFullDtoBuilder publisherFullDto = PublisherFullDto.builder();

        publisherFullDto.books( bookSetToBookBasicInfoDtoList( dto.getBooks() ) );
        publisherFullDto.id( dto.getId() );
        publisherFullDto.name( dto.getName() );

        return publisherFullDto.build();
    }

    @Override
    public PublisherPatchDto toPatchDto(PublisherPutDto dto) {
        if ( dto == null ) {
            return null;
        }

        PublisherPatchDto.PublisherPatchDtoBuilder publisherPatchDto = PublisherPatchDto.builder();

        List<Long> list = dto.getBooks();
        if ( list != null ) {
            publisherPatchDto.books( new ArrayList<Long>( list ) );
        }
        publisherPatchDto.name( dto.getName() );

        return publisherPatchDto.build();
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
