package com.kruosant.bookwalker.mappers;

import com.kruosant.bookwalker.domains.Author;
import com.kruosant.bookwalker.domains.Book;
import com.kruosant.bookwalker.domains.Publisher;
import com.kruosant.bookwalker.dtos.author.AuthorBasicInfoDto;
import com.kruosant.bookwalker.dtos.book.BookBasicInfoDto;
import com.kruosant.bookwalker.dtos.book.BookFullDto;
import com.kruosant.bookwalker.dtos.book.BookPatchDto;
import com.kruosant.bookwalker.dtos.book.BookPutDto;
import com.kruosant.bookwalker.dtos.publisher.PublisherBasicInfoDto;
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
public class BookMapperImpl implements BookMapper {

    @Override
    public BookFullDto toFullDto(Book book) {
        if ( book == null ) {
            return null;
        }

        BookFullDto.BookFullDtoBuilder bookFullDto = BookFullDto.builder();

        bookFullDto.id( book.getId() );
        bookFullDto.name( book.getName() );
        bookFullDto.authors( authorSetToAuthorBasicInfoDtoSet( book.getAuthors() ) );
        bookFullDto.pageCount( book.getPageCount() );
        bookFullDto.publishDate( book.getPublishDate() );
        bookFullDto.publisher( publisherToPublisherBasicInfoDto( book.getPublisher() ) );
        bookFullDto.price( book.getPrice() );

        return bookFullDto.build();
    }

    @Override
    public List<BookFullDto> toFullDto(List<Book> book) {
        if ( book == null ) {
            return null;
        }

        List<BookFullDto> list = new ArrayList<BookFullDto>( book.size() );
        for ( Book book1 : book ) {
            list.add( toFullDto( book1 ) );
        }

        return list;
    }

    @Override
    public Set<BookFullDto> toFullDto(Set<Book> book) {
        if ( book == null ) {
            return null;
        }

        Set<BookFullDto> set = LinkedHashSet.newLinkedHashSet( book.size() );
        for ( Book book1 : book ) {
            set.add( toFullDto( book1 ) );
        }

        return set;
    }

    @Override
    public BookBasicInfoDto toBasicInfoDto(Book book) {
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

    @Override
    public List<BookBasicInfoDto> toBasicInfoDto(List<Book> book) {
        if ( book == null ) {
            return null;
        }

        List<BookBasicInfoDto> list = new ArrayList<BookBasicInfoDto>( book.size() );
        for ( Book book1 : book ) {
            list.add( toBasicInfoDto( book1 ) );
        }

        return list;
    }

    @Override
    public Set<BookBasicInfoDto> toBasicInfoDto(Set<Book> book) {
        if ( book == null ) {
            return null;
        }

        Set<BookBasicInfoDto> set = LinkedHashSet.newLinkedHashSet( book.size() );
        for ( Book book1 : book ) {
            set.add( toBasicInfoDto( book1 ) );
        }

        return set;
    }

    @Override
    public BookPatchDto toPatchDto(BookPutDto dto) {
        if ( dto == null ) {
            return null;
        }

        BookPatchDto.BookPatchDtoBuilder bookPatchDto = BookPatchDto.builder();

        bookPatchDto.name( dto.getName() );
        Set<Long> set = dto.getAuthors();
        if ( set != null ) {
            bookPatchDto.authors( new LinkedHashSet<Long>( set ) );
        }
        bookPatchDto.pageCount( dto.getPageCount() );
        bookPatchDto.publishDate( dto.getPublishDate() );
        bookPatchDto.publisher( dto.getPublisher() );
        bookPatchDto.price( dto.getPrice() );

        return bookPatchDto.build();
    }

    @Override
    public void updateBook(Book book, BookPatchDto dto) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getName() != null ) {
            book.setName( dto.getName() );
        }
        if ( dto.getPageCount() != null ) {
            book.setPageCount( dto.getPageCount() );
        }
        if ( dto.getPublishDate() != null ) {
            book.setPublishDate( dto.getPublishDate() );
        }
        if ( dto.getPrice() != null ) {
            book.setPrice( dto.getPrice() );
        }
    }

    @Override
    public void updateBook(Book book, BookPutDto dto) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getName() != null ) {
            book.setName( dto.getName() );
        }
        if ( dto.getPageCount() != null ) {
            book.setPageCount( dto.getPageCount() );
        }
        if ( dto.getPublishDate() != null ) {
            book.setPublishDate( dto.getPublishDate() );
        }
        if ( dto.getPrice() != null ) {
            book.setPrice( dto.getPrice() );
        }
    }

    protected AuthorBasicInfoDto authorToAuthorBasicInfoDto(Author author) {
        if ( author == null ) {
            return null;
        }

        AuthorBasicInfoDto.AuthorBasicInfoDtoBuilder authorBasicInfoDto = AuthorBasicInfoDto.builder();

        authorBasicInfoDto.id( author.getId() );
        authorBasicInfoDto.name( author.getName() );
        authorBasicInfoDto.middleName( author.getMiddleName() );
        authorBasicInfoDto.surname( author.getSurname() );
        authorBasicInfoDto.bio( author.getBio() );

        return authorBasicInfoDto.build();
    }

    protected Set<AuthorBasicInfoDto> authorSetToAuthorBasicInfoDtoSet(Set<Author> set) {
        if ( set == null ) {
            return null;
        }

        Set<AuthorBasicInfoDto> set1 = LinkedHashSet.newLinkedHashSet( set.size() );
        for ( Author author : set ) {
            set1.add( authorToAuthorBasicInfoDto( author ) );
        }

        return set1;
    }

    protected PublisherBasicInfoDto publisherToPublisherBasicInfoDto(Publisher publisher) {
        if ( publisher == null ) {
            return null;
        }

        PublisherBasicInfoDto.PublisherBasicInfoDtoBuilder publisherBasicInfoDto = PublisherBasicInfoDto.builder();

        publisherBasicInfoDto.id( publisher.getId() );
        publisherBasicInfoDto.name( publisher.getName() );

        return publisherBasicInfoDto.build();
    }
}
