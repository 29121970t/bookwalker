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
    date = "2026-04-22T00:06:01+0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 26.0.2 (Arch Linux)"
)
@Component
public class AuthorMapperImpl implements AuthorMapper {

    @Override
    public Author toAuthor(AuthorFullDto dto) {
        if ( dto == null ) {
            return null;
        }

        Author.AuthorBuilder author = Author.builder();

        author.name( dto.getName() );
        author.middleName( dto.getMiddleName() );
        author.surname( dto.getSurname() );
        author.bio( dto.getBio() );

        return author.build();
    }

    @Override
    public Author toAuthor(AuthorCreateDto dto) {
        if ( dto == null ) {
            return null;
        }

        Author.AuthorBuilder author = Author.builder();

        author.name( dto.getName() );
        author.middleName( dto.getMiddleName() );
        author.surname( dto.getSurname() );
        author.bio( dto.getBio() );

        return author.build();
    }

    @Override
    public AuthorFullDto toFullDto(Author dto) {
        if ( dto == null ) {
            return null;
        }

        AuthorFullDto.AuthorFullDtoBuilder authorFullDto = AuthorFullDto.builder();

        authorFullDto.id( dto.getId() );
        authorFullDto.name( dto.getName() );
        authorFullDto.middleName( dto.getMiddleName() );
        authorFullDto.surname( dto.getSurname() );
        authorFullDto.bio( dto.getBio() );
        authorFullDto.books( bookSetToBookBasicInfoDtoList( dto.getBooks() ) );

        return authorFullDto.build();
    }

    @Override
    public AuthorPatchDto toPatchDto(AuthorPutDto dto) {
        if ( dto == null ) {
            return null;
        }

        AuthorPatchDto.AuthorPatchDtoBuilder authorPatchDto = AuthorPatchDto.builder();

        authorPatchDto.name( dto.getName() );
        authorPatchDto.middleName( dto.getMiddleName() );
        authorPatchDto.surname( dto.getSurname() );
        authorPatchDto.bio( dto.getBio() );
        Set<Long> set = dto.getBooks();
        if ( set != null ) {
            authorPatchDto.books( new LinkedHashSet<Long>( set ) );
        }

        return authorPatchDto.build();
    }

    @Override
    public void patch(Author author, AuthorPatchDto dto) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getName() != null ) {
            author.setName( dto.getName() );
        }
        if ( dto.getMiddleName() != null ) {
            author.setMiddleName( dto.getMiddleName() );
        }
        if ( dto.getSurname() != null ) {
            author.setSurname( dto.getSurname() );
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
    }

    @Override
    public void put(Author author, AuthorPutDto dto) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getName() != null ) {
            author.setName( dto.getName() );
        }
        if ( dto.getMiddleName() != null ) {
            author.setMiddleName( dto.getMiddleName() );
        }
        if ( dto.getSurname() != null ) {
            author.setSurname( dto.getSurname() );
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
    }

    @Override
    public Author toAuthor(AuthorPutDto dto) {
        if ( dto == null ) {
            return null;
        }

        Author.AuthorBuilder author = Author.builder();

        author.name( dto.getName() );
        author.middleName( dto.getMiddleName() );
        author.surname( dto.getSurname() );
        author.bio( dto.getBio() );
        author.books( map( dto.getBooks() ) );

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
