package com.kruosant.bookwalker.services;

import com.kruosant.bookwalker.domains.Author;
import com.kruosant.bookwalker.domains.Book;
import com.kruosant.bookwalker.domains.BookFormat;
import com.kruosant.bookwalker.domains.Genre;
import com.kruosant.bookwalker.domains.Publisher;
import com.kruosant.bookwalker.domains.Tag;
import com.kruosant.bookwalker.dtos.book.BookCreateDto;
import com.kruosant.bookwalker.dtos.book.BookFullDto;
import com.kruosant.bookwalker.dtos.book.BookPatchDto;
import com.kruosant.bookwalker.dtos.book.BookPutDto;
import com.kruosant.bookwalker.exceptions.ResourceNotFoundException;
import com.kruosant.bookwalker.mappers.BookMapper;
import com.kruosant.bookwalker.repositories.AuthorRepository;
import com.kruosant.bookwalker.repositories.BookRepository;
import com.kruosant.bookwalker.repositories.GenreRepository;
import com.kruosant.bookwalker.repositories.PublisherRepository;
import com.kruosant.bookwalker.repositories.TagRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
  @Mock
  private BookRepository bookRepository;
  @Mock
  private AuthorRepository authorRepository;
  @Mock
  private GenreRepository genreRepository;
  @Mock
  private PublisherRepository publisherRepository;
  @Mock
  private TagRepository tagRepository;
  @Mock
  private BookMapper bookMapper;
  @Mock
  private StorageService storageService;
  @InjectMocks
  private BookService bookService;

  @Test
  void getAllMapsBooks() {
    Book book = Book.builder().id(1L).title("Book").build();
    BookFullDto dto = BookFullDto.builder().title("Book").build();

    when(bookRepository.findAll()).thenReturn(List.of(book));
    when(bookMapper.toFullDto(book)).thenReturn(dto);

    assertEquals(List.of(dto), bookService.getAll());
  }

  @Test
  void getByIdMapsBook() {
    Book book = Book.builder().id(1L).title("Book").build();
    BookFullDto dto = BookFullDto.builder().title("Book").build();

    when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
    when(bookMapper.toFullDto(book)).thenReturn(dto);

    assertEquals(dto, bookService.getById(1L));
  }

  @Test
  void createBuildsBookRelationsAndFlags() {
    BookCreateDto dto = createDto();
    BookFullDto fullDto = BookFullDto.builder().title("Book").build();

    mockRelations();
    when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(bookMapper.toFullDto(any(Book.class))).thenReturn(fullDto);

    assertEquals(fullDto, bookService.create(dto));
    verify(bookRepository).save(any(Book.class));
  }

  @Test
  void patchOnlyChangesProvidedFields() {
    Book book = Book.builder().id(1L).title("Old").price(BigDecimal.ONE).build();
    BookPatchDto dto = new BookPatchDto();
    dto.setTitle("New");
    dto.setFeatured(true);

    when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
    when(bookRepository.save(book)).thenReturn(book);
    when(bookMapper.toFullDto(book)).thenReturn(BookFullDto.builder().title("New").build());

    bookService.patch(1L, dto);

    assertEquals("New", book.getTitle());
    assertEquals(BigDecimal.ONE, book.getPrice());
    assertEquals(true, book.isFeatured());
  }

  @Test
  void patchChangesEveryProvidedField() {
    Book book = Book.builder().id(1L).title("Old").build();
    BookPatchDto dto = new BookPatchDto();
    dto.setTitle("New");
    dto.setAuthors(Set.of(1L));
    dto.setGenreId(1L);
    dto.setPrice(BigDecimal.valueOf(11));
    dto.setDiscountPrice(BigDecimal.valueOf(9));
    dto.setFormat(BookFormat.PAPERBACK);
    dto.setPages(200);
    dto.setYear(2025);
    dto.setPublishDate(LocalDate.of(2025, 1, 1));
    dto.setPublisherIds(Set.of(1L));
    dto.setBlurb("Blurb");
    dto.setDescription("Description");
    dto.setLongDescription("Long");
    dto.setTagIds(Set.of(1L));
    dto.setFeatured(true);
    dto.setPopular(true);
    dto.setNewArrival(true);

    mockRelations();
    when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
    when(bookRepository.save(book)).thenReturn(book);
    when(bookMapper.toFullDto(book)).thenReturn(BookFullDto.builder().build());

    bookService.patch(1L, dto);

    assertEquals("New", book.getTitle());
    assertEquals(BigDecimal.valueOf(11), book.getPrice());
    assertEquals(BookFormat.PAPERBACK, book.getFormat());
    assertEquals(1, book.getTags().size());
    assertEquals(true, book.isNewArrival());
  }

  @Test
  void patchLeavesNullFieldsUntouched() {
    Book book = Book.builder().id(1L).title("Old").featured(false).popular(false).newArrival(false).build();
    BookPatchDto dto = new BookPatchDto();

    when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
    when(bookRepository.save(book)).thenReturn(book);
    when(bookMapper.toFullDto(book)).thenReturn(BookFullDto.builder().build());

    bookService.patch(1L, dto);

    assertEquals("Old", book.getTitle());
    assertEquals(false, book.isFeatured());
  }

  @Test
  void putReplacesBookFields() {
    Book book = Book.builder().id(1L).title("Old").build();
    BookPutDto dto = new BookPutDto();
    copyCreateFields(dto);

    mockRelations();
    when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
    when(bookRepository.save(book)).thenReturn(book);
    when(bookMapper.toFullDto(book)).thenReturn(BookFullDto.builder().title("Book").build());

    bookService.put(1L, dto);

    assertEquals("Book", book.getTitle());
    assertEquals(BookFormat.HARDCOVER, book.getFormat());
    assertEquals(1, book.getAuthors().size());
    assertEquals(1, book.getPublishers().size());
  }

  @Test
  void uploadCoverStoresPath() {
    Book book = Book.builder().id(1L).build();
    MultipartFile file = org.mockito.Mockito.mock(MultipartFile.class);

    when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
    when(storageService.storeCover(file)).thenReturn("covers/book.jpg");
    when(bookRepository.save(book)).thenReturn(book);
    when(bookMapper.toFullDto(book)).thenReturn(BookFullDto.builder().coverUrl("covers/book.jpg").build());

    bookService.uploadCover(1L, file);

    assertEquals("covers/book.jpg", book.getCoverPath());
  }

  @Test
  void deleteRemovesExistingBook() {
    Book book = Book.builder().id(1L).build();
    when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

    bookService.delete(1L);

    verify(bookRepository).delete(book);
  }

  @Test
  void getByIdThrowsWhenMissing() {
    when(bookRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> bookService.getById(1L));
  }

  @Test
  void createWithoutTagsUsesEmptyTagSet() {
    BookCreateDto dto = createDto();
    dto.setTagIds(null);

    when(authorRepository.findAllById(Set.of(1L))).thenReturn(List.of(Author.builder().id(1L).build()));
    when(genreRepository.findById(1L)).thenReturn(Optional.of(Genre.builder().id(1L).build()));
    when(publisherRepository.findAllById(Set.of(1L))).thenReturn(List.of(Publisher.builder().id(1L).build()));
    when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(bookMapper.toFullDto(any(Book.class))).thenReturn(BookFullDto.builder().build());

    bookService.create(dto);

    verify(bookRepository).save(org.mockito.ArgumentMatchers.argThat(book -> book.getTags().isEmpty()));
  }

  @Test
  void createThrowsWhenGenreMissing() {
    BookCreateDto dto = createDto();
    when(authorRepository.findAllById(Set.of(1L))).thenReturn(List.of(Author.builder().id(1L).build()));
    when(genreRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> bookService.create(dto));
  }

  private void mockRelations() {
    when(authorRepository.findAllById(Set.of(1L))).thenReturn(List.of(Author.builder().id(1L).build()));
    when(genreRepository.findById(1L)).thenReturn(Optional.of(Genre.builder().id(1L).build()));
    when(publisherRepository.findAllById(Set.of(1L))).thenReturn(List.of(Publisher.builder().id(1L).build()));
    when(tagRepository.findAllById(Set.of(1L))).thenReturn(List.of(Tag.builder().id(1L).build()));
  }

  private static BookCreateDto createDto() {
    BookCreateDto dto = new BookCreateDto();
    copyCreateFields(dto);
    return dto;
  }

  private static void copyCreateFields(BookCreateDto dto) {
    dto.setTitle("Book");
    dto.setAuthors(Set.of(1L));
    dto.setGenreId(1L);
    dto.setPrice(BigDecimal.TEN);
    dto.setDiscountPrice(BigDecimal.ONE);
    dto.setFormat(BookFormat.HARDCOVER);
    dto.setPages(100);
    dto.setYear(2026);
    dto.setPublisherIds(Set.of(1L));
    dto.setTagIds(Set.of(1L));
    dto.setFeatured(true);
    dto.setPopular(true);
    dto.setNewArrival(true);
  }
}
