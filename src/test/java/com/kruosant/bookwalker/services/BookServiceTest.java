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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

  @Mock
  private BookRepository bookRepo;
  @Mock
  private AuthorRepository authorRepo;
  @Mock
  private PublisherRepository publisherRepo;
  @Mock
  private OrderRepository orderRepository;
  @Mock
  private BookMapper bookMapper;
  @Mock
  private OrderSearchCache cache;

  @Spy
  @InjectMocks
  private BookService service;

  @Test
  void removeAuthorByIdsShouldDelegateToEntityVersion() {
    Book book = book(1L, publisher(1L));
    Author author = author(2L);
    book.setAuthors(new HashSet<>(Set.of(author, author(3L))));
    author.setBooks(new HashSet<>(Set.of(book)));

    when(bookRepo.findById(1L)).thenReturn(Optional.of(book));
    when(authorRepo.findById(2L)).thenReturn(Optional.of(author));

    service.removeAuthor(1L, 2L);

    verify(bookRepo).save(book);
    verify(authorRepo).save(author);
  }

  @Test
  void removeAuthorShouldDeleteBookWhenLastAuthorRemoved() {
    Publisher publisher = publisher(1L);
    Book book = book(1L, publisher);
    Author author = author(2L);
    book.setAuthors(new HashSet<>(Set.of(author)));
    author.setBooks(new HashSet<>(Set.of(book)));
    when(orderRepository.findByBooksContaining(book)).thenReturn(List.of());

    service.removeAuthor(book, author);

    verify(cache, times(2)).invalidate();
    verify(bookRepo).delete(book);
    verify(authorRepo).save(author);
  }

  @Test
  void removeAuthorShouldSaveBookWhenOtherAuthorsRemain() {
    Publisher publisher = publisher(1L);
    Book book = book(1L, publisher);
    Author removed = author(2L);
    Author remaining = author(3L);
    book.setAuthors(new HashSet<>(Set.of(removed, remaining)));
    removed.setBooks(new HashSet<>(Set.of(book)));

    service.removeAuthor(book, removed);

    verify(cache).invalidate();
    verify(bookRepo).save(book);
    verify(authorRepo).save(removed);
  }

  @Test
  void setAuthorsShouldReplaceBookAuthors() {
    Publisher publisher = publisher(1L);
    Book book = book(1L, publisher);
    Author oldAuthor = author(1L);
    Author newAuthor = author(2L);
    oldAuthor.setBooks(new HashSet<>(Set.of(book)));
    book.setAuthors(new HashSet<>(Set.of(oldAuthor)));

    service.setAuthors(book, List.of(newAuthor));

    verify(cache).invalidate();
    assertFalse(oldAuthor.getBooks().contains(book));
    assertEquals(Set.of(newAuthor), book.getAuthors());
  }

  @Test
  void setAuthorsByIdsShouldLoadEntitiesAndReplaceAuthors() {
    Publisher publisher = publisher(1L);
    Book book = book(1L, publisher);
    Author first = author(10L);
    Author second = author(20L);

    when(bookRepo.findById(1L)).thenReturn(Optional.of(book));
    when(authorRepo.findById(10L)).thenReturn(Optional.of(first));
    when(authorRepo.findById(20L)).thenReturn(Optional.of(second));

    service.setAuthors(1L, List.of(10L, 20L));

    verify(cache).invalidate();
    assertEquals(Set.of(first, second), book.getAuthors());
  }

  @Test
  void setPublisherShouldMoveBookBetweenPublishers() {
    Publisher oldPublisher = publisher(1L);
    Publisher newPublisher = publisher(2L);
    Book book = book(1L, oldPublisher);
    oldPublisher.getBooks().add(book);

    service.setPublisher(book, newPublisher);

    assertFalse(oldPublisher.getBooks().contains(book));
    assertEquals(newPublisher, book.getPublisher());
    assertEquals(Set.of(book), newPublisher.getBooks());
  }

  @Test
  void getAllByNameShouldReturnMappedBooks() {
    Book book = book(1L, publisher(1L));
    BookFullDto dto = BookFullDto.builder().id(1L).name("Book").build();

    when(bookRepo.findAllByName("Book")).thenReturn(List.of(book));
    when(bookMapper.toFullDto(List.of(book))).thenReturn(List.of(dto));

    List<BookFullDto> result = service.getAllByName("Book");

    assertEquals(List.of(dto), result);
  }

  @Test
  void getByIdShouldReturnMappedBook() {
    Book book = book(1L, publisher(1L));
    BookFullDto dto = BookFullDto.builder().id(1L).name("Book").build();

    when(bookRepo.findById(1L)).thenReturn(Optional.of(book));
    when(bookMapper.toFullDto(book)).thenReturn(dto);

    BookFullDto result = service.getById(1L);

    assertEquals(dto, result);
  }

  @Test
  void getAllShouldReturnMappedBooks() {
    Book first = book(1L, publisher(1L));
    Book second = book(2L, publisher(1L));
    BookFullDto firstDto = BookFullDto.builder().id(1L).name("First").build();
    BookFullDto secondDto = BookFullDto.builder().id(2L).name("Second").build();

    when(bookRepo.findAll()).thenReturn(List.of(first, second));
    when(bookMapper.toFullDto(first)).thenReturn(firstDto);
    when(bookMapper.toFullDto(second)).thenReturn(secondDto);

    List<BookFullDto> result = service.getAll();

    assertEquals(List.of(firstDto, secondDto), result);
  }

  @Test
  void createShouldSaveBookWhenRelationsExist() {
    Author first = author(1L);
    Author second = author(2L);
    Publisher publisher = publisher(10L);
    BookCreateDto dto = BookCreateDto.builder()
        .authors(Set.of(1L, 2L))
        .name("Book")
        .pageCount(300L)
        .publishDate(LocalDate.of(2020, 1, 1))
        .publisher(10L)
        .price(12.5)
        .build();
    Book savedBook = book(100L, publisher);
    BookFullDto fullDto = BookFullDto.builder().id(100L).name("Book").build();

    when(authorRepo.findAllById(dto.getAuthors())).thenReturn(List.of(first, second));
    when(publisherRepo.findById(10L)).thenReturn(Optional.of(publisher));
    when(bookRepo.save(any(Book.class))).thenReturn(savedBook);
    when(bookMapper.toFullDto(savedBook)).thenReturn(fullDto);

    BookFullDto result = service.create(dto);

    verify(cache).invalidate();
    assertEquals(fullDto, result);
  }

  @Test
  void createShouldThrowWhenAnyRelationMissing() {
    BookCreateDto dto = BookCreateDto.builder()
        .authors(Set.of(1L, 2L))
        .name("Book")
        .pageCount(300L)
        .publishDate(LocalDate.of(2020, 1, 1))
        .publisher(10L)
        .price(12.5)
        .build();

    when(authorRepo.findAllById(dto.getAuthors())).thenReturn(List.of(author(1L)));
    when(publisherRepo.findById(10L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> service.create(dto));
  }

  @Test
  void deleteBookShouldRemoveBookFromOrdersAndDeleteIt() {
    Book book = book(1L, publisher(1L));
    Order firstOrder = new Order();
    firstOrder.setBooks(new HashSet<>(Set.of(book)));
    Order secondOrder = new Order();
    secondOrder.setBooks(new HashSet<>(Set.of(book)));

    when(orderRepository.findByBooksContaining(book)).thenReturn(List.of(firstOrder, secondOrder));

    service.deleteBook(book);

    verify(cache).invalidate();
    assertFalse(firstOrder.getBooks().contains(book));
    assertFalse(secondOrder.getBooks().contains(book));
    verify(bookRepo).delete(book);
  }

  @Test
  void deleteByIdShouldDeleteLoadedBook() {
    Book book = book(1L, publisher(1L));
    when(bookRepo.findById(1L)).thenReturn(Optional.of(book));
    when(orderRepository.findByBooksContaining(book)).thenReturn(List.of());

    service.deleteById(1L);

    verify(bookRepo).delete(book);
  }

  @Test
  void addAuthorShouldLinkBookAndAuthor() {
    Book book = book(1L, publisher(1L));
    Author author = author(2L);
    BookFullDto dto = BookFullDto.builder().id(1L).name("Book").build();

    when(bookRepo.findById(1L)).thenReturn(Optional.of(book));
    when(authorRepo.findById(2L)).thenReturn(Optional.of(author));
    when(bookRepo.save(book)).thenReturn(book);
    when(bookMapper.toFullDto(book)).thenReturn(dto);

    BookFullDto result = service.addAuthor(1L, 2L);

    assertEquals(Set.of(author), book.getAuthors());
    assertEquals(Set.of(book), author.getBooks());
    assertEquals(dto, result);
  }

  @Test
  void deleteAuthorShouldRemoveAuthorAndReturnMappedBook() {
    Publisher publisher = publisher(1L);
    Book book = book(1L, publisher);
    Author author = author(2L);
    Author remaining = author(3L);
    book.setAuthors(new HashSet<>(Set.of(author, remaining)));
    author.setBooks(new HashSet<>(Set.of(book)));
    BookFullDto dto = BookFullDto.builder().id(1L).name("Book").build();

    when(bookRepo.findById(1L)).thenReturn(Optional.of(book));
    when(authorRepo.findById(2L)).thenReturn(Optional.of(author));
    when(bookRepo.save(book)).thenReturn(book);
    when(bookMapper.toFullDto(book)).thenReturn(dto);

    BookFullDto result = service.deleteAuthor(1L, 2L);

    assertEquals(dto, result);
  }

  @Test
  void setPublisherByIdsShouldSaveBookWithNewPublisher() {
    Book book = book(1L, publisher(1L));
    Publisher newPublisher = publisher(2L);
    BookFullDto dto = BookFullDto.builder().id(1L).name("Book").build();

    when(bookRepo.findById(1L)).thenReturn(Optional.of(book));
    when(publisherRepo.findById(2L)).thenReturn(Optional.of(newPublisher));
    when(bookRepo.save(book)).thenReturn(book);
    when(bookMapper.toFullDto(book)).thenReturn(dto);

    BookFullDto result = service.setPublisher(1L, 2L);

    assertEquals(newPublisher, book.getPublisher());
    assertEquals(dto, result);
  }

  @Test
  void patchShouldUpdateOptionalRelationsWhenProvided() {
    Book book = book(1L, publisher(1L));
    BookPatchDto dto = BookPatchDto.builder()
        .name("Updated")
        .publisher(2L)
        .authors(Set.of(3L, 4L))
        .build();
    BookFullDto fullDto = BookFullDto.builder().id(1L).name("Updated").build();

    when(bookRepo.findById(1L)).thenReturn(Optional.of(book));
    doNothing().when(bookMapper).updateBook(book, dto);
    doReturn(BookFullDto.builder().id(1L).build()).when(service).setPublisher(1L, 2L);
    doNothing().when(service).setAuthors(1L, dto.getAuthors());
    when(bookRepo.save(book)).thenReturn(book);
    when(bookMapper.toFullDto(book)).thenReturn(fullDto);

    BookFullDto result = service.patch(1L, dto);

    verify(cache).invalidate();
    verify(service).setPublisher(1L, 2L);
    verify(service).setAuthors(1L, dto.getAuthors());
    assertEquals(fullDto, result);
  }

  @Test
  void patchShouldSkipOptionalRelationsWhenMissing() {
    Book book = book(1L, publisher(1L));
    BookPatchDto dto = BookPatchDto.builder().name("Updated").build();
    BookFullDto fullDto = BookFullDto.builder().id(1L).name("Updated").build();

    when(bookRepo.findById(1L)).thenReturn(Optional.of(book));
    doNothing().when(bookMapper).updateBook(book, dto);
    when(bookRepo.save(book)).thenReturn(book);
    when(bookMapper.toFullDto(book)).thenReturn(fullDto);

    BookFullDto result = service.patch(1L, dto);

    assertEquals(fullDto, result);
  }

  @Test
  void putShouldReplaceOptionalRelationsWhenProvided() {
    Book book = book(1L, publisher(1L));
    BookPutDto dto = BookPutDto.builder()
        .name("Updated")
        .publishDate(LocalDate.of(2020, 1, 1))
        .authors(Set.of(3L, 4L))
        .pageCount(250L)
        .publisher(2L)
        .price(20.0)
        .build();
    BookFullDto fullDto = BookFullDto.builder().id(1L).name("Updated").build();

    when(bookRepo.findById(1L)).thenReturn(Optional.of(book));
    doNothing().when(bookMapper).updateBook(book, dto);
    doReturn(BookFullDto.builder().id(1L).build()).when(service).setPublisher(1L, 2L);
    doNothing().when(service).setAuthors(1L, dto.getAuthors());
    when(bookRepo.save(book)).thenReturn(book);
    when(bookMapper.toFullDto(book)).thenReturn(fullDto);

    BookFullDto result = service.put(1L, dto);

    verify(cache).invalidate();
    verify(service).setPublisher(1L, 2L);
    verify(service).setAuthors(1L, dto.getAuthors());
    assertEquals(fullDto, result);
  }

  @Test
  void putShouldSkipOptionalRelationsWhenMissing() {
    Book book = book(1L, publisher(1L));
    BookPutDto dto = BookPutDto.builder()
        .name("Updated")
        .publishDate(LocalDate.of(2020, 1, 1))
        .authors(null)
        .pageCount(250L)
        .publisher(null)
        .price(20.0)
        .build();
    BookFullDto fullDto = BookFullDto.builder().id(1L).name("Updated").build();

    when(bookRepo.findById(1L)).thenReturn(Optional.of(book));
    doNothing().when(bookMapper).updateBook(book, dto);
    when(bookRepo.save(book)).thenReturn(book);
    when(bookMapper.toFullDto(book)).thenReturn(fullDto);

    BookFullDto result = service.put(1L, dto);

    assertEquals(fullDto, result);
  }

  private static Author author(Long id) {
    Author author = new Author();
    author.setId(id);
    author.setBooks(new HashSet<>());
    return author;
  }

  private static Publisher publisher(long id) {
    Publisher publisher = new Publisher();
    publisher.setId(id);
    publisher.setBooks(new HashSet<>());
    return publisher;
  }

  private static Book book(long id, Publisher publisher) {
    Book book = new Book();
    book.setId(id);
    book.setPublisher(publisher);
    book.setAuthors(new HashSet<>());
    return book;
  }
}
