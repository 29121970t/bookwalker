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
  private final AuthorMapper mapper;

  public AuthorFullDto getAuthorById(Long id) {
    return mapper.toFullDto(authorRepo.findById(id).orElseThrow(ResourceNotFoundException::new));
  }

  public List<AuthorFullDto> getAll() {
    return authorRepo.findAll().stream().map(mapper::toFullDto).toList();
  }

  public AuthorFullDto create(AuthorCreateDto dto) {
    return mapper.toFullDto(authorRepo.save(mapper.toAuthor(dto)));
  }

  @Transactional
  public void delete(Long id) {
    Author author = authorRepo.findById(id).orElseThrow(ResourceNotFoundException::new);

    new ArrayList<>(author.getBooks()).forEach(book -> {
      book.removeAuthor(author);
      if (book.getAuthors().isEmpty()) {
        bookRepo.delete(book);
      } else {
        bookRepo.save(book);
      }
    });

    authorRepo.delete(author);
  }

  @Transactional
  public AuthorFullDto update(Long id, AuthorPatchDto dto) {
    Author author = authorRepo.findById(id).orElseThrow(ResourceNotFoundException::new);

    if (dto.getName() != null) {
      author.setName(dto.getName());
    }
    if (dto.getBio() != null) {
      author.setBio(dto.getBio());
    }

    if (dto.getBooks() != null) {

      new ArrayList<>(author.getBooks()).forEach(book -> book.removeAuthor(author));

      dto.getBooks().stream()
          .map(bookId -> bookRepo.findById(bookId).orElseThrow(ResourceNotFoundException::new))
          .forEach(book -> book.addAuthor(author));
    }

    return mapper.toFullDto(authorRepo.save(author));
  }

  @Transactional
  public AuthorFullDto update(Long id, AuthorPutDto dto) {
    return update(id, mapper.toPatchDto(dto));
  }

  @Transactional
  public AuthorFullDto addBook(Long authorId, Long bookId) {
    Author author = authorRepo.findById(authorId).orElseThrow(ResourceNotFoundException::new);
    Book book = bookRepo.findById(bookId).orElseThrow(ResourceNotFoundException::new);

    book.addAuthor(author);
    bookRepo.save(book);

    return mapper.toFullDto(author);
  }

  @Transactional
  public AuthorFullDto deleteBook(Long authorId, Long bookId) {
    Author author = authorRepo.findById(authorId).orElseThrow(ResourceNotFoundException::new);
    Book book = bookRepo.findById(bookId).orElseThrow(ResourceNotFoundException::new);

    book.removeAuthor(author);
    if (book.getAuthors().isEmpty()) {
      bookRepo.delete(book);
    } else {
      bookRepo.save(book);
    }

    return mapper.toFullDto(author);
  }
}