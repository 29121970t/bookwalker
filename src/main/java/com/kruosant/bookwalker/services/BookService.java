package com.kruosant.bookwalker.services;


import com.kruosant.bookwalker.domains.Author;
import com.kruosant.bookwalker.domains.Book;
import com.kruosant.bookwalker.domains.Publisher;
import com.kruosant.bookwalker.dtos.book.BookCreateDto;
import com.kruosant.bookwalker.dtos.book.BookFullDto;
import com.kruosant.bookwalker.dtos.book.BookPatchDto;
import com.kruosant.bookwalker.dtos.book.BookPutDto;
import com.kruosant.bookwalker.exceptions.BadRequestException;
import com.kruosant.bookwalker.exceptions.ResourceNotFoundException;
import com.kruosant.bookwalker.mappers.BookMapper;
import com.kruosant.bookwalker.repositories.AuthorRepository;
import com.kruosant.bookwalker.repositories.BookRepository;
import com.kruosant.bookwalker.repositories.PublisherRepository;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class BookService {
  private final BookRepository bookRepo;
  private final AuthorRepository authorRepo;
  private final PublisherRepository publisherRepo;
  private final BookMapper mapper;

  public List<BookFullDto> getAllByName(String name) {
    List<Book> books = bookRepo.findAllByName(name);
    if (books.isEmpty()) {
      throw new ResourceNotFoundException();
    }

    return books.stream().map(mapper::toFullDto).toList();
  }

  public BookFullDto getById(long id) {
    Optional<Book> optionalBook = bookRepo.findById(id);
    if (optionalBook.isEmpty()) {
      throw new ResourceNotFoundException();
    }
    return mapper.toFullDto(optionalBook.get());
  }

  public BookFullDto create(BookCreateDto dto) {
    return mapper.toFullDto(bookRepo.save(mapper.toEntity(dto)));
  }

  public List<BookFullDto> getAll() {
    return bookRepo.findAll().stream().map(mapper::toFullDto).toList();
  }

  public void deleteById(Long id) {
    if (!bookRepo.existsById(id)) {
      throw new ResourceNotFoundException();
    }
    bookRepo.deleteById(id);
  }

  public BookFullDto addAuthor(Long bookId, Long authorId) {
    Book book = bookRepo.findById(bookId).orElseThrow(ResourceNotFoundException::new);
    Author author = authorRepo.findById(authorId).orElseThrow(ResourceNotFoundException::new);

    book.addAuthor(author);
    return mapper.toFullDto(bookRepo.save(book));
  }

  public BookFullDto deleteAuthor(Long bookId, Long authorId) {
    Book book = bookRepo.findById(bookId).orElseThrow(ResourceNotFoundException::new);
    Author author = authorRepo.findById(authorId).orElseThrow(ResourceNotFoundException::new);

    book.deleteAuthor(author);
    return mapper.toFullDto(bookRepo.save(book));
  }

  public BookFullDto setPublisher(Long bookId, Long publisherId) {
    Book book = bookRepo.findById(bookId).orElseThrow(ResourceNotFoundException::new);
    Publisher publisher = publisherRepo.findById(publisherId).orElseThrow(ResourceNotFoundException::new);

    book.setPublisher(publisher);
    return mapper.toFullDto(bookRepo.save(book));
  }

  public BookFullDto update(Long id, @NonNull BookPatchDto dto) {
    Book book = bookRepo.findById(id).orElseThrow(ResourceNotFoundException::new);
    if (dto.getAuthors() != null) {
      Set<Author> authors = dto.getAuthors().stream()
          .map(authorId -> authorRepo.findById(authorId)
              .orElseThrow(BadRequestException::new)).collect(Collectors.toCollection(HashSet::new));

      book.setAuthors(authors);
    }
    if (dto.getPublisher() != null) {
      Publisher publisher = publisherRepo.findById(dto.getPublisher())
          .orElseThrow(BadRequestException::new);
      book.setPublisher(publisher);
    }
    if (dto.getName() != null) {
      book.setName(dto.getName());
    }
    if (dto.getPageCount() != null) {
      book.setPageCount(dto.getPageCount());
    }
    if (dto.getPublishDate() != null) {
      book.setPublishDate(dto.getPublishDate());
    }
    if (dto.getPrice() != null) {
      book.setPrice(dto.getPrice());
    }
    return mapper.toFullDto(bookRepo.save(book));
  }

  public BookFullDto update(Long id, @NonNull BookPutDto dto) {
    return update(id, mapper.toPatchDto(dto));
  }
}
