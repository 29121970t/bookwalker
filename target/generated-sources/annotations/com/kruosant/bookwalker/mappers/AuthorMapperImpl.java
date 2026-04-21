package com.kruosant.bookwalker.mappers;

import com.kruosant.bookwalker.domains.Author;
import com.kruosant.bookwalker.domains.Book;
import com.kruosant.bookwalker.dtos.author.AuthorCreateDto;
import com.kruosant.bookwalker.dtos.author.AuthorFullDto;
import com.kruosant.bookwalker.dtos.author.AuthorPatchDto;
import com.kruosant.bookwalker.dtos.author.AuthorPutDto;
import com.kruosant.bookwalker.dtos.book.BookBasicInfoDto;
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
public class AuthorMapperImpl implements AuthorMapper {

    @Override
    public Author toAuthor(AuthorFullDto dto) {
        if ( dto == null ) {
            return null;
        }

        Author.AuthorBuilder author = Author.builder();

        author.bio( dto.getBio() );
        author.middleName( dto.getMiddleName() );
        author.name( dto.getName() );
        author.surname( dto.getSurname() );

        return author.build();
    }

    @Override
    public Author toAuthor(AuthorCreateDto dto) {
        if ( dto == null ) {
            return null;
        }

        Author.AuthorBuilder author = Author.builder();

        author.bio( dto.getBio() );
        author.middleName( dto.getMiddleName() );
        author.name( dto.getName() );
        author.surname( dto.getSurname() );

        return author.build();
    }

    @Override
    public AuthorFullDto toFullDto(Author dto) {
        if ( dto == null ) {
            return null;
        }

        AuthorFullDto.AuthorFullDtoBuilder authorFullDto = AuthorFullDto.builder();

        authorFullDto.bio( dto.getBio() );
        authorFullDto.books( bookSetToBookBasicInfoDtoList( dto.getBooks() ) );
        authorFullDto.id( dto.getId() );
        authorFullDto.middleName( dto.getMiddleName() );
        authorFullDto.name( dto.getName() );
        authorFullDto.surname( dto.getSurname() );

        return authorFullDto.build();
    }

    @Override
    public AuthorPatchDto toPatchDto(AuthorPutDto dto) {
        if ( dto == null ) {
            return null;
        }

        AuthorPatchDto.AuthorPatchDtoBuilder authorPatchDto = AuthorPatchDto.builder();

        authorPatchDto.bio( dto.getBio() );
        Set<Long> set = dto.getBooks();
        if ( set != null ) {
            authorPatchDto.books( new LinkedHashSet<Long>( set ) );
        }
        authorPatchDto.middleName( dto.getMiddleName() );
        authorPatchDto.name( dto.getName() );
        authorPatchDto.surname( dto.getSurname() );

        return authorPatchDto.build();
    }

    @Override
    public void patch(Author author, AuthorPatchDto dto) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getBio() != null ) {
            author.setBio( dto.getBio() );
        }
        if ( author.getBooks() != null ) {
            Set<Book> set = map( dto.getBooks() );
            if ( set != null ) {
                author.getBooks().clear();
                author.getBooks().addAll( set );
            }
        }
        else {
            Set<Book> set = map( dto.getBooks() );
            if ( set != null ) {
                author.setBooks( set );
            }
        }
        if ( dto.getMiddleName() != null ) {
            author.setMiddleName( dto.getMiddleName() );
        }
        if ( dto.getName() != null ) {
            author.setName( dto.getName() );
        }
        if ( dto.getSurname() != null ) {
            author.setSurname( dto.getSurname() );
        }
    }

    @Override
    public void put(Author author, AuthorPutDto dto) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getBio() != null ) {
            author.setBio( dto.getBio() );
        }
        if ( author.getBooks() != null ) {
            Set<Book> set = map( dto.getBooks() );
            if ( set != null ) {
                author.getBooks().clear();
                author.getBooks().addAll( set );
            }
        }
        else {
            Set<Book> set = map( dto.getBooks() );
            if ( set != null ) {
                author.setBooks( set );
            }
        }
        if ( dto.getMiddleName() != null ) {
            author.setMiddleName( dto.getMiddleName() );
        }
        if ( dto.getName() != null ) {
            author.setName( dto.getName() );
        }
        if ( dto.getSurname() != null ) {
            author.setSurname( dto.getSurname() );
        }
    }

    @Override
    public Author toAuthor(AuthorPutDto dto) {
        if ( dto == null ) {
            return null;
        }

        Author.AuthorBuilder author = Author.builder();

        author.bio( dto.getBio() );
        author.books( map( dto.getBooks() ) );
        author.middleName( dto.getMiddleName() );
        author.name( dto.getName() );
        author.surname( dto.getSurname() );

        return author.build();
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
