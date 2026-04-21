package com.kruosant.bookwalker.services;

import com.kruosant.bookwalker.cashe.OrderSearchCache;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
@AllArgsConstructor
public class AuthorService {
  private final AuthorRepository authorRepo;
  private final BookService bookService;
  private final AuthorMapper mapper;
  private final BookRepository bookRepo;
  private final OrderSearchCache cache;

  @Transactional(readOnly = true)
  public Author getById(Long id) {
    return authorRepo.findById(id).orElseThrow(ResourceNotFoundException::new);
  }

  @Transactional(readOnly = true)
  public AuthorFullDto getFullDtoById(Long id) {
    return mapper.toFullDto(authorRepo.findById(id).orElseThrow(ResourceNotFoundException::new));
  }

  @Transactional
  public AuthorFullDto create(AuthorCreateDto dto) {
    cache.invalidate();
    Author author = mapper.toAuthor(dto);
    return mapper.toFullDto(authorRepo.save(author));
  }

  @Transactional
  public Author create(AuthorPutDto dto) {
    cache.invalidate();
    List<Book> books = dto.getBooks().stream().map(id -> bookRepo.findById(id).orElseThrow(ResourceNotFoundException::new)).toList();
    Author newAuthor =  mapper.toAuthor(dto);
    newAuthor.getBooks().addAll(books);
    return authorRepo.save(newAuthor);
  }

  @Transactional(readOnly = true)
  public Page<AuthorFullDto> getAllFullDto(Pageable pageable) {
    return authorRepo.findAll(pageable).map(mapper::toFullDto);
  }

  @Transactional
  public void delete(Long id) {
    cache.invalidate();
    Author author = authorRepo.findById(id).orElseThrow(ResourceNotFoundException::new);
    new HashSet<>(author.getBooks()).forEach(book -> bookService.removeAuthor(book, author));
    authorRepo.delete(author);
  }

  @Transactional
  public AuthorFullDto patch(Long id, AuthorPatchDto dto) {
    cache.invalidate();
    Author author = authorRepo.findById(id).orElseThrow(ResourceNotFoundException::new);
    new ArrayList<>(author.getBooks()).forEach(book -> bookService.removeAuthor(book, author));
    mapper.patch(author, dto);
    if (dto.getBooks() != null) {
      dto.getBooks().forEach(bookId -> bookService.addAuthor(bookId, id));
    }
    return mapper.toFullDto(authorRepo.save(author));
  }

  @Transactional
  public AuthorFullDto put(Long id, AuthorPutDto dto) {
    cache.invalidate();
    Author author = authorRepo.findById(id).orElseThrow(ResourceNotFoundException::new);
    new ArrayList<>(author.getBooks()).forEach(book -> bookService.removeAuthor(book, author));
    mapper.put(author, dto);
    if (dto.getBooks() != null) {
      dto.getBooks().forEach(bookId -> bookService.addAuthor(bookId, id));
    }
    return mapper.toFullDto(authorRepo.save(author));
  }

  @Transactional
  public AuthorFullDto addBook(Long bookId, Long authorId) {
    cache.invalidate();
    bookService.addAuthor(bookId, authorId);
    Author author = authorRepo.findById(authorId).orElseThrow(ResourceNotFoundException::new);
    return mapper.toFullDto(author);
  }

  @Transactional
  public AuthorFullDto deleteBook(Long bookId, Long authorId) {
    cache.invalidate();
    bookService.removeAuthor(bookId, authorId);
    Author author = authorRepo.findById(authorId).orElseThrow(ResourceNotFoundException::new);
    return mapper.toFullDto(author);
  }
}
