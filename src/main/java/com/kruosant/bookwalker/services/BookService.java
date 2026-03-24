package com.kruosant.bookwalker.services;


import com.kruosant.bookwalker.cashe.OrderSearchCache;
import com.kruosant.bookwalker.domains.Author;
import com.kruosant.bookwalker.domains.Book;
import com.kruosant.bookwalker.domains.Order;
import com.kruosant.bookwalker.domains.Publisher;
import com.kruosant.bookwalker.dtos.book.BookCreateDto;
import com.kruosant.bookwalker.dtos.book.BookFullDto;
import com.kruosant.bookwalker.dtos.book.BookPatchDto;
import com.kruosant.bookwalker.dtos.book.BookPutDto;
import com.kruosant.bookwalker.exceptions.ResourceNotFoundException;
import com.kruosant.bookwalker.mappers.BookMapper;
import com.kruosant.bookwalker.repositories.AuthorRepository;
import com.kruosant.bookwalker.repositories.BookRepository;
import com.kruosant.bookwalker.repositories.OrderRepository;
import com.kruosant.bookwalker.repositories.PublisherRepository;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class BookService {
  private final BookRepository bookRepo;
  private final AuthorRepository authorRepo;
  private final PublisherRepository publisherRepo;
  private final OrderRepository orderRepository;
  private final BookMapper bookMapper;
  private final OrderSearchCache cache;


  public void removeAuthor(Long bookId, Long authorId) {
    Book book = bookRepo.findById(bookId).orElseThrow(ResourceNotFoundException::new);
    Author author = authorRepo.findById(authorId).orElseThrow(ResourceNotFoundException::new);
    removeAuthor(book, author);
  }

  public void removeAuthor(Book book, Author author) {
    cache.invalidate();
    book.getAuthors().remove(author);
    author.getBooks().remove(book);
    if (book.getAuthors().isEmpty()) {
      deleteBook(book);
    } else {
      bookRepo.save(book);
    }
    authorRepo.save(author);
  }


  public void setAuthors(Book book, Collection<Author> newAuthors) {
    cache.invalidate();
    Collection<Author> bookAuthors = book.getAuthors();
    bookAuthors.forEach(author -> author.getBooks().remove(book));
    bookAuthors.clear();
    bookAuthors.addAll(newAuthors);
  }

  public void setAuthors(Long bookId, Collection<Long> authorIds) {
    Book book = bookRepo.findById(bookId).orElseThrow(ResourceNotFoundException::new);
    List<Author> newAuthors = authorIds.stream()
        .map(id -> authorRepo.findById(id).orElseThrow(ResourceNotFoundException::new))
        .collect(Collectors.toCollection(ArrayList::new));
    setAuthors(book, newAuthors);
  }

  public void setPublisher(Book book, Publisher publisher) {
    book.getPublisher().getBooks().remove(book);
    book.setPublisher(publisher);
    publisher.getBooks().add(book);
  }

  @Transactional(readOnly = true)
  public List<BookFullDto> getAllByName(String name) {
    List<Book> books = bookRepo.findAllByName(name);
    return bookMapper.toFullDto(books);
  }

  @Transactional(readOnly = true)
  public BookFullDto getById(long id) {
    Book book = bookRepo.findById(id).orElseThrow(ResourceNotFoundException::new);
    return bookMapper.toFullDto(book);
  }

  @Transactional(readOnly = true)
  public List<BookFullDto> getAll() {
    return bookRepo.findAll().stream().map(bookMapper::toFullDto).toList();
  }

  @Transactional
  public BookFullDto create(BookCreateDto dto) {
    cache.invalidate();
    List<Author> authors = authorRepo.findAllById(dto.getAuthors());
    Optional<Publisher> publisherOpt = publisherRepo.findById(dto.getPublisher());

    if (authors.size() != dto.getAuthors().size() || publisherOpt.isEmpty()) {
      throw new ResourceNotFoundException();
    }

    Book newBook = new Book(
        0,
        dto.getName(),
        new HashSet<>(authors),
        dto.getPageCount(),
        dto.getPublishDate(),
        publisherOpt.get(),
        dto.getPrice()
    );
    return bookMapper.toFullDto(bookRepo.save(newBook));
  }


  public void deleteBook(Book book) {
    cache.invalidate();
    List<Order> orders = orderRepository.findByBooksContaining(book);
    for (Order order : orders) {
      order.getBooks().remove(book);
    }
    bookRepo.delete(book);
  }

  @Transactional
  public void deleteById(Long id) {
    Book book = bookRepo.findById(id).orElseThrow(ResourceNotFoundException::new);
    deleteBook(book);
  }

  @Transactional
  public BookFullDto addAuthor(Long bookId, Long authorId) {
    Book book = bookRepo.findById(bookId).orElseThrow(ResourceNotFoundException::new);
    Author author = authorRepo.findById(authorId).orElseThrow(ResourceNotFoundException::new);
    book.getAuthors().add(author);
    author.getBooks().add(book);
    return bookMapper.toFullDto(bookRepo.save(book));
  }

  @Transactional
  public BookFullDto deleteAuthor(Long bookId, Long authorId) {
    Book book = bookRepo.findById(bookId).orElseThrow(ResourceNotFoundException::new);
    Author author = authorRepo.findById(authorId).orElseThrow(ResourceNotFoundException::new);
    removeAuthor(book, author);
    return bookMapper.toFullDto(bookRepo.save(book));
  }

  public BookFullDto setPublisher(Long bookId, Long publisherId) {
    Book book = bookRepo.findById(bookId).orElseThrow(ResourceNotFoundException::new);
    Publisher publisher = publisherRepo.findById(publisherId).orElseThrow(ResourceNotFoundException::new);
    setPublisher(book, publisher);
    return bookMapper.toFullDto(bookRepo.save(book));
  }

  @Transactional
  public BookFullDto patch(Long id, @NonNull BookPatchDto dto) {
    cache.invalidate();
    Book book = bookRepo.findById(id).orElseThrow(ResourceNotFoundException::new);
    bookMapper.updateBook(book, dto);
    if (dto.getPublisher() != null) {
      setPublisher(id, dto.getPublisher());
    }
    if (dto.getAuthors() != null) {
      setAuthors(id, dto.getAuthors());
    }
    return bookMapper.toFullDto(bookRepo.save(book));
  }

  @Transactional
  public BookFullDto put(Long id, @NonNull BookPutDto dto) {
    cache.invalidate();
    Book book = bookRepo.findById(id).orElseThrow(ResourceNotFoundException::new);
    bookMapper.updateBook(book, dto);
    if (dto.getPublisher() != null) {
      setPublisher(id, dto.getPublisher());
    }
    if (dto.getAuthors() != null) {
      setAuthors(id, dto.getAuthors());
    }
    return bookMapper.toFullDto(bookRepo.save(book));
  }
}
