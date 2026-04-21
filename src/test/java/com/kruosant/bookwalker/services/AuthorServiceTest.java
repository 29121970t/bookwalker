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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

  @Mock
  private AuthorRepository authorRepo;
  @Mock
  private BookService bookService;
  @Mock
  private AuthorMapper mapper;
  @Mock
  private BookRepository bookRepo;
  @Mock
  private OrderSearchCache cache;

  @InjectMocks
  private AuthorService service;

  @Test
  void getByIdShouldReturnAuthor() {
    Author author = author(1L);
    when(authorRepo.findById(1L)).thenReturn(Optional.of(author));

    Author result = service.getById(1L);

    assertSame(author, result);
  }

  @Test
  void getFullDtoByIdShouldReturnMappedAuthor() {
    Author author = author(1L);
    AuthorFullDto dto = AuthorFullDto.builder().id(1L).name("Author").build();

    when(authorRepo.findById(1L)).thenReturn(Optional.of(author));
    when(mapper.toFullDto(author)).thenReturn(dto);

    AuthorFullDto result = service.getFullDtoById(1L);

    assertEquals(dto, result);
  }

  @Test
  void createFromCreateDtoShouldSaveAuthor() {
    AuthorCreateDto dto = AuthorCreateDto.builder()
        .name("Name")
        .middleName("Middle")
        .surname("Surname")
        .bio("Bio")
        .build();
    Author author = author(1L);
    AuthorFullDto fullDto = AuthorFullDto.builder().id(1L).name("Name").build();

    when(mapper.toAuthor(dto)).thenReturn(author);
    when(authorRepo.save(author)).thenReturn(author);
    when(mapper.toFullDto(author)).thenReturn(fullDto);

    AuthorFullDto result = service.create(dto);

    verify(cache).invalidate();
    assertEquals(fullDto, result);
  }

  @Test
  void createFromPutDtoShouldAttachBooks() {
    AuthorPutDto dto = AuthorPutDto.builder()
        .name("Name")
        .middleName("Middle")
        .surname("Surname")
        .bio("Bio")
        .books(Set.of(10L, 20L))
        .build();
    Author newAuthor = author(1L);
    Book firstBook = book(10L);
    Book secondBook = book(20L);

    when(bookRepo.findById(10L)).thenReturn(Optional.of(firstBook));
    when(bookRepo.findById(20L)).thenReturn(Optional.of(secondBook));
    when(mapper.toAuthor(dto)).thenReturn(newAuthor);
    when(authorRepo.save(newAuthor)).thenReturn(newAuthor);

    Author result = service.create(dto);

    verify(cache).invalidate();
    assertSame(newAuthor, result);
    assertEquals(Set.of(firstBook, secondBook), result.getBooks());
  }

  @Test
  void createFromPutDtoShouldThrowWhenBookMissing() {
    AuthorPutDto dto = AuthorPutDto.builder()
        .name("Name")
        .middleName("Middle")
        .surname("Surname")
        .bio("Bio")
        .books(Set.of(10L))
        .build();

    when(bookRepo.findById(10L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> service.create(dto));
  }

  @Test
  void getAllFullDtoShouldReturnMappedAuthors() {
    PageRequest pageable = PageRequest.of(0, 20);
    Author first = author(1L);
    Author second = author(2L);
    AuthorFullDto firstDto = AuthorFullDto.builder().id(1L).name("First").build();
    AuthorFullDto secondDto = AuthorFullDto.builder().id(2L).name("Second").build();

    when(authorRepo.findAll(pageable)).thenReturn(new PageImpl<>(List.of(first, second), pageable, 2));
    when(mapper.toFullDto(first)).thenReturn(firstDto);
    when(mapper.toFullDto(second)).thenReturn(secondDto);

    Page<AuthorFullDto> result = service.getAllFullDto(pageable);

    assertEquals(List.of(firstDto, secondDto), result.getContent());
    assertEquals(2, result.getTotalElements());
  }

  @Test
  void deleteShouldRemoveAuthorFromAllBooks() {
    Author author = author(1L);
    Book firstBook = book(10L);
    Book secondBook = book(20L);
    author.setBooks(new HashSet<>(Set.of(firstBook, secondBook)));

    when(authorRepo.findById(1L)).thenReturn(Optional.of(author));

    service.delete(1L);

    verify(cache).invalidate();
    verify(bookService).removeAuthor(firstBook, author);
    verify(bookService).removeAuthor(secondBook, author);
    verify(authorRepo).delete(author);
  }

  @Test
  void patchShouldUpdateAuthorAndBooksWhenProvided() {
    Author author = author(1L);
    Book existingBook = book(10L);
    author.setBooks(new HashSet<>(Set.of(existingBook)));
    AuthorPatchDto dto = AuthorPatchDto.builder().books(Set.of(20L, 30L)).name("Updated").build();
    AuthorFullDto fullDto = AuthorFullDto.builder().id(1L).name("Updated").build();

    when(authorRepo.findById(1L)).thenReturn(Optional.of(author));
    when(authorRepo.save(author)).thenReturn(author);
    when(mapper.toFullDto(author)).thenReturn(fullDto);

    AuthorFullDto result = service.patch(1L, dto);

    verify(cache).invalidate();
    verify(bookService).removeAuthor(existingBook, author);
    verify(mapper).patch(author, dto);
    verify(bookService).addAuthor(20L, 1L);
    verify(bookService).addAuthor(30L, 1L);
    assertEquals(fullDto, result);
  }

  @Test
  void patchShouldSkipAddingBooksWhenNotProvided() {
    Author author = author(1L);
    Book existingBook = book(10L);
    author.setBooks(new HashSet<>(Set.of(existingBook)));
    AuthorPatchDto dto = AuthorPatchDto.builder().name("Updated").build();
    AuthorFullDto fullDto = AuthorFullDto.builder().id(1L).name("Updated").build();

    when(authorRepo.findById(1L)).thenReturn(Optional.of(author));
    when(authorRepo.save(author)).thenReturn(author);
    when(mapper.toFullDto(author)).thenReturn(fullDto);

    AuthorFullDto result = service.patch(1L, dto);

    verify(bookService).removeAuthor(existingBook, author);
    verify(mapper).patch(author, dto);
    assertEquals(fullDto, result);
  }

  @Test
  void putShouldReplaceAuthorDataAndBooks() {
    Author author = author(1L);
    Book existingBook = book(10L);
    author.setBooks(new HashSet<>(Set.of(existingBook)));
    AuthorPutDto dto = AuthorPutDto.builder()
        .name("Updated")
        .middleName("Middle")
        .surname("Surname")
        .bio("Bio")
        .books(Set.of(20L, 30L))
        .build();
    AuthorFullDto fullDto = AuthorFullDto.builder().id(1L).name("Updated").build();

    when(authorRepo.findById(1L)).thenReturn(Optional.of(author));
    when(authorRepo.save(author)).thenReturn(author);
    when(mapper.toFullDto(author)).thenReturn(fullDto);

    AuthorFullDto result = service.put(1L, dto);

    verify(cache).invalidate();
    verify(bookService).removeAuthor(existingBook, author);
    verify(mapper).put(author, dto);
    verify(bookService).addAuthor(20L, 1L);
    verify(bookService).addAuthor(30L, 1L);
    assertEquals(fullDto, result);
  }

  @Test
  void putShouldSkipAddingBooksWhenMissing() {
    Author author = author(1L);
    Book existingBook = book(10L);
    author.setBooks(new HashSet<>(Set.of(existingBook)));
    AuthorPutDto dto = AuthorPutDto.builder()
        .name("Updated")
        .middleName("Middle")
        .surname("Surname")
        .bio("Bio")
        .books(null)
        .build();
    AuthorFullDto fullDto = AuthorFullDto.builder().id(1L).name("Updated").build();

    when(authorRepo.findById(1L)).thenReturn(Optional.of(author));
    when(authorRepo.save(author)).thenReturn(author);
    when(mapper.toFullDto(author)).thenReturn(fullDto);

    AuthorFullDto result = service.put(1L, dto);

    verify(bookService).removeAuthor(existingBook, author);
    verify(mapper).put(author, dto);
    assertEquals(fullDto, result);
  }

  @Test
  void addBookShouldReturnUpdatedAuthor() {
    Author author = author(1L);
    AuthorFullDto fullDto = AuthorFullDto.builder().id(1L).name("Author").build();

    when(authorRepo.findById(1L)).thenReturn(Optional.of(author));
    when(mapper.toFullDto(author)).thenReturn(fullDto);

    AuthorFullDto result = service.addBook(10L, 1L);

    verify(cache).invalidate();
    verify(bookService).addAuthor(10L, 1L);
    assertEquals(fullDto, result);
  }

  @Test
  void deleteBookShouldReturnUpdatedAuthor() {
    Author author = author(1L);
    AuthorFullDto fullDto = AuthorFullDto.builder().id(1L).name("Author").build();

    when(authorRepo.findById(1L)).thenReturn(Optional.of(author));
    when(mapper.toFullDto(author)).thenReturn(fullDto);

    AuthorFullDto result = service.deleteBook(10L, 1L);

    verify(cache).invalidate();
    verify(bookService).removeAuthor(10L, 1L);
    assertEquals(fullDto, result);
  }

  private static Author author(Long id) {
    return Author.builder()
        .id(id)
        .name("Author")
        .middleName("Middle")
        .surname("Surname")
        .bio("Bio")
        .books(new HashSet<>())
        .build();
  }

  private static Book book(Long id) {
    Book book = new Book();
    book.setId(id);
    book.setAuthors(new HashSet<>());
    return book;
  }
}
