package com.kruosant.bookwalker.services;

import com.kruosant.bookwalker.domains.Book;
import com.kruosant.bookwalker.domains.Publisher;
import com.kruosant.bookwalker.dtos.publisher.PublisherCreateDto;
import com.kruosant.bookwalker.dtos.publisher.PublisherFullDto;
import com.kruosant.bookwalker.dtos.publisher.PublisherPatchDto;
import com.kruosant.bookwalker.dtos.publisher.PublisherPutDto;
import com.kruosant.bookwalker.exceptions.ResourceNotFoundException;
import com.kruosant.bookwalker.mappers.PublisherMapper;
import com.kruosant.bookwalker.repositories.BookRepository;
import com.kruosant.bookwalker.repositories.PublisherRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PublisherServiceTest {

  @Mock
  private PublisherRepository publisherRepo;
  @Mock
  private BookService bookService;
  @Mock
  private BookRepository bookRepo;
  @Mock
  private PublisherMapper mapper;

  @InjectMocks
  private PublisherService service;

  @Test
  void getAuthorByIdShouldReturnMappedPublisher() {
    Publisher publisher = publisher(1L, "Pub");
    PublisherFullDto dto = PublisherFullDto.builder().id(1L).name("Pub").build();

    when(publisherRepo.findById(1L)).thenReturn(Optional.of(publisher));
    when(mapper.toFullDto(publisher)).thenReturn(dto);

    PublisherFullDto result = service.getAuthorById(1L);

    assertEquals(dto, result);
  }

  @Test
  void getAuthorByIdShouldThrowWhenMissing() {
    when(publisherRepo.findById(1L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> service.getAuthorById(1L));
  }

  @Test
  void createShouldSaveMappedPublisher() {
    PublisherCreateDto dto = PublisherCreateDto.builder().name("Pub").build();
    Publisher publisher = publisher(1L, "Pub");
    PublisherFullDto fullDto = PublisherFullDto.builder().id(1L).name("Pub").build();

    when(mapper.toAuthor(dto)).thenReturn(publisher);
    when(publisherRepo.save(publisher)).thenReturn(publisher);
    when(mapper.toFullDto(publisher)).thenReturn(fullDto);

    PublisherFullDto result = service.create(dto);

    assertEquals(fullDto, result);
  }

  @Test
  void getAllShouldReturnMappedPublishers() {
    Publisher first = publisher(1L, "Pub1");
    Publisher second = publisher(2L, "Pub2");
    PublisherFullDto firstDto = PublisherFullDto.builder().id(1L).name("Pub1").build();
    PublisherFullDto secondDto = PublisherFullDto.builder().id(2L).name("Pub2").build();

    when(publisherRepo.findAll()).thenReturn(List.of(first, second));
    when(mapper.toFullDto(first)).thenReturn(firstDto);
    when(mapper.toFullDto(second)).thenReturn(secondDto);

    List<PublisherFullDto> result = service.getAll();

    assertEquals(List.of(firstDto, secondDto), result);
  }

  @Test
  void deleteShouldRemovePublisher() {
    Publisher publisher = publisher(1L, "Pub");

    when(publisherRepo.findById(1L)).thenReturn(Optional.of(publisher));

    service.delete(1L);

    verify(publisherRepo).delete(publisher);
  }

  @Test
  void updatePatchShouldApplyNameAndBooks() {
    Publisher publisher = publisher(1L, "Old");
    Book oldBook = book(100L);
    oldBook.setPublisher(publisher);
    publisher.setBooks(new HashSet<>(Set.of(oldBook)));
    Book newBook = book(2L);
    PublisherPatchDto dto = PublisherPatchDto.builder().name("New").books(List.of(2L)).build();
    PublisherFullDto fullDto = PublisherFullDto.builder().id(1L).name("New").build();

    when(publisherRepo.findById(1L)).thenReturn(Optional.of(publisher));
    when(bookRepo.findById(2L)).thenReturn(Optional.of(newBook));
    when(publisherRepo.save(publisher)).thenReturn(publisher);
    when(mapper.toFullDto(publisher)).thenReturn(fullDto);

    PublisherFullDto result = service.update(1L, dto);

    assertNull(oldBook.getPublisher());
    assertEquals("New", publisher.getName());
    verify(bookService).setPublisher(newBook, publisher);
    assertEquals(fullDto, result);
  }

  @Test
  void updatePatchShouldSkipOptionalFieldsWhenMissing() {
    Publisher publisher = publisher(1L, "Old");
    PublisherPatchDto dto = PublisherPatchDto.builder().build();
    PublisherFullDto fullDto = PublisherFullDto.builder().id(1L).name("Old").build();

    when(publisherRepo.findById(1L)).thenReturn(Optional.of(publisher));
    when(publisherRepo.save(publisher)).thenReturn(publisher);
    when(mapper.toFullDto(publisher)).thenReturn(fullDto);

    PublisherFullDto result = service.update(1L, dto);

    assertEquals("Old", publisher.getName());
    verifyNoInteractions(bookService, bookRepo);
    assertEquals(fullDto, result);
  }

  @Test
  void updatePutShouldReplaceBooksAndName() {
    Publisher publisher = publisher(1L, "Old");
    Book oldBook = book(100L);
    oldBook.setPublisher(publisher);
    publisher.setBooks(new HashSet<>(Set.of(oldBook)));
    Book firstNew = book(2L);
    Book secondNew = book(3L);
    PublisherPutDto dto = PublisherPutDto.builder().name("New").books(List.of(2L, 3L)).build();
    PublisherFullDto fullDto = PublisherFullDto.builder().id(1L).name("New").build();

    when(publisherRepo.findById(1L)).thenReturn(Optional.of(publisher));
    when(bookRepo.findById(2L)).thenReturn(Optional.of(firstNew));
    when(bookRepo.findById(3L)).thenReturn(Optional.of(secondNew));
    when(publisherRepo.save(publisher)).thenReturn(publisher);
    when(mapper.toFullDto(publisher)).thenReturn(fullDto);

    PublisherFullDto result = service.update(1L, dto);

    assertNull(oldBook.getPublisher());
    assertEquals("New", publisher.getName());
    verify(bookService).setPublisher(firstNew, publisher);
    verify(bookService).setPublisher(secondNew, publisher);
    assertEquals(fullDto, result);
  }

  @Test
  void addBookShouldAssignPublisherToBook() {
    Book book = book(10L);
    Publisher publisher = publisher(1L, "Pub");
    PublisherFullDto fullDto = PublisherFullDto.builder().id(1L).name("Pub").build();

    when(bookRepo.findById(10L)).thenReturn(Optional.of(book));
    when(publisherRepo.findById(1L)).thenReturn(Optional.of(publisher));
    when(mapper.toFullDto(publisher)).thenReturn(fullDto);

    PublisherFullDto result = service.addBook(10L, 1L);

    assertEquals(publisher, book.getPublisher());
    verify(bookRepo).save(book);
    assertEquals(fullDto, result);
  }

  @Test
  void deleteBookShouldDeleteBookAndReturnPublisher() {
    Publisher publisher = publisher(1L, "Pub");
    PublisherFullDto fullDto = PublisherFullDto.builder().id(1L).name("Pub").build();

    when(publisherRepo.findById(1L)).thenReturn(Optional.of(publisher));
    when(mapper.toFullDto(publisher)).thenReturn(fullDto);

    PublisherFullDto result = service.deleteBook(10L, 1L);

    verify(bookRepo).deleteById(10L);
    assertEquals(fullDto, result);
  }

  private static Publisher publisher(Long id, String name) {
    Publisher publisher = new Publisher();
    publisher.setId(id);
    publisher.setName(name);
    publisher.setBooks(new HashSet<>());
    return publisher;
  }

  private static Book book(Long id) {
    Book book = new Book();
    book.setId(id);
    book.setAuthors(new HashSet<>());
    return book;
  }
}
