package com.kruosant.bookwalker.services;

import com.kruosant.bookwalker.domains.Author;
import com.kruosant.bookwalker.domains.Book;
import com.kruosant.bookwalker.dtos.author.AuthorCreateDto;
import com.kruosant.bookwalker.dtos.author.AuthorFullDto;
import com.kruosant.bookwalker.dtos.author.AuthorPatchDto;
import com.kruosant.bookwalker.dtos.author.AuthorPutDto;
import com.kruosant.bookwalker.exceptions.ResourceNotFoundException;
import com.kruosant.bookwalker.mappers.AuthorMapper;
import com.kruosant.bookwalker.repositories.AuthorRepository;
import com.kruosant.bookwalker.repositories.BookRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class AuthorService {
  private final AuthorRepository authorRepo;
  private final BookRepository bookRepo;
  private final BookService bookService;
  private final AuthorMapper mapper;

  public AuthorFullDto getAuthorById(Long id) {
    return mapper.toFullDto(authorRepo.findById(id).orElseThrow(ResourceNotFoundException::new));
  }

  public AuthorFullDto create(AuthorCreateDto dto) {
    return mapper.toFullDto(authorRepo.save(mapper.toAuthor(dto)));
  }

  public List<AuthorFullDto> getAll() {
    return authorRepo.findAll().stream().map(mapper::toFullDto).toList();
  }

  @Transactional
  public void delete(Long id) {
    Author author = authorRepo.findById(id).orElseThrow(ResourceNotFoundException::new);
    new ArrayList<>(author.getBooks()).forEach(book -> {
      book.removeAuthor(author);
      if (book.getAuthors().isEmpty()) {
        bookService.deleteBook(book);
      }
    });
    authorRepo.delete(author);
  }

  @Transactional
  public AuthorFullDto update(Long id, AuthorPatchDto dto) {
    Author author = authorRepo.findById(id).orElseThrow(ResourceNotFoundException::new);
    if (dto.getBio() != null) {
      author.setBio(dto.getBio());
    }
    if (dto.getBooks() != null) {
      new ArrayList<>(author.getBooks()).forEach(b -> b.removeAuthor(author));
      dto.getBooks().stream()
          .map(i -> bookRepo.findById(i).orElseThrow(ResourceNotFoundException::new))
          .forEach(b -> {
            b.addAuthor(author);
            bookRepo.save(b);
          });
    }
    if (dto.getName() != null) {
      author.setName(dto.getName());
    }
    return mapper.toFullDto(authorRepo.save(author));
  }


  public AuthorFullDto update(Long id, AuthorPutDto dto) {
    return update(id, mapper.toPatchDto(dto));
  }

  @Transactional
  public AuthorFullDto addBook(Long bookId, Long authorId) {
    Book book = bookRepo.findById(bookId).orElseThrow(ResourceNotFoundException::new);
    Author author = authorRepo.findById(authorId).orElseThrow(ResourceNotFoundException::new);

    book.addAuthor(author);
    bookRepo.save(book);
    return mapper.toFullDto(author);
  }


  public AuthorFullDto deleteBook(Long bookId, Long authorId) {
    Book book = bookRepo.findById(bookId).orElseThrow(ResourceNotFoundException::new);
    Author author = authorRepo.findById(authorId).orElseThrow(ResourceNotFoundException::new);

    book.removeAuthor(author);
    if (book.getAuthors().isEmpty()) {
      bookService.deleteBook(book);
    } else {
      bookRepo.save(book);
    }
    return mapper.toFullDto(author);
  }
}
