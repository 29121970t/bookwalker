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
    date = "2026-04-22T00:06:01+0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 26.0.2 (Arch Linux)"
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

        publisherFullDto.id( dto.getId() );
        publisherFullDto.name( dto.getName() );
        publisherFullDto.books( bookSetToBookBasicInfoDtoList( dto.getBooks() ) );

        return publisherFullDto.build();
    }

    @Override
    public PublisherPatchDto toPatchDto(PublisherPutDto dto) {
        if ( dto == null ) {
            return null;
        }

        PublisherPatchDto.PublisherPatchDtoBuilder publisherPatchDto = PublisherPatchDto.builder();

        publisherPatchDto.name( dto.getName() );
        List<Long> list = dto.getBooks();
        if ( list != null ) {
            publisherPatchDto.books( new ArrayList<Long>( list ) );
        }

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
