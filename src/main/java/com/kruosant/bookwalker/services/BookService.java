package com.kruosant.bookwalker.services;

import com.kruosant.bookwalker.domains.Author;
import com.kruosant.bookwalker.domains.Book;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BookService {
  private final BookRepository bookRepository;
  private final AuthorRepository authorRepository;
  private final GenreRepository genreRepository;
  private final PublisherRepository publisherRepository;
  private final TagRepository tagRepository;
  private final BookMapper bookMapper;
  private final StorageService storageService;

  @Transactional(readOnly = true)
  public List<BookFullDto> getAll() {
    return bookRepository.findAll().stream().map(bookMapper::toFullDto).toList();
  }

  @Transactional(readOnly = true)
  public BookFullDto getById(Long id) {
    return bookMapper.toFullDto(getEntity(id));
  }

  @Transactional
  public BookFullDto create(BookCreateDto dto) {
    Book book = Book.builder().build();
    applyCreateOrPut(book, dto);
    return bookMapper.toFullDto(bookRepository.save(book));
  }

  @Transactional
  public BookFullDto patch(Long id, BookPatchDto dto) {
    Book book = getEntity(id);
    patchCatalogFields(book, dto);
    patchDescriptionFields(book, dto);
    patchVisibilityFlags(book, dto);
    return bookMapper.toFullDto(bookRepository.save(book));
  }

  private void patchCatalogFields(Book book, BookPatchDto dto) {
    if (dto.getTitle() != null) book.setTitle(dto.getTitle());
    if (dto.getAuthors() != null) book.setAuthors(fetchAuthors(dto.getAuthors()));
    if (dto.getGenreId() != null) book.setGenre(fetchGenre(dto.getGenreId()));
    if (dto.getPrice() != null) book.setPrice(dto.getPrice());
    if (dto.getDiscountPrice() != null) book.setDiscountPrice(dto.getDiscountPrice());
    if (dto.getFormat() != null) book.setFormat(dto.getFormat());
    if (dto.getPages() != null) book.setPages(dto.getPages());
    if (dto.getYear() != null) book.setYear(dto.getYear());
    if (dto.getPublishDate() != null) book.setPublishDate(dto.getPublishDate());
    if (dto.getPublisherIds() != null) book.setPublishers(fetchPublishers(dto.getPublisherIds()));
  }

  private void patchDescriptionFields(Book book, BookPatchDto dto) {
    if (dto.getBlurb() != null) book.setBlurb(dto.getBlurb());
    if (dto.getDescription() != null) book.setDescription(dto.getDescription());
    if (dto.getLongDescription() != null) book.setLongDescription(dto.getLongDescription());
    if (dto.getTagIds() != null) book.setTags(fetchTags(dto.getTagIds()));
  }

  private void patchVisibilityFlags(Book book, BookPatchDto dto) {
    if (dto.getFeatured() != null) book.setFeatured(dto.getFeatured());
    if (dto.getPopular() != null) book.setPopular(dto.getPopular());
    if (dto.getNewArrival() != null) book.setNewArrival(dto.getNewArrival());
  }

  @Transactional
  public BookFullDto put(Long id, BookPutDto dto) {
    Book book = getEntity(id);
    applyCreateOrPut(book, dto);
    return bookMapper.toFullDto(bookRepository.save(book));
  }

  @Transactional
  public BookFullDto uploadCover(Long id, MultipartFile file) {
    Book book = getEntity(id);
    book.setCoverPath(storageService.storeCover(file));
    return bookMapper.toFullDto(bookRepository.save(book));
  }

  @Transactional
  public void delete(Long id) {
    bookRepository.delete(getEntity(id));
  }

  private void applyCreateOrPut(Book book, BookCreateDto dto) {
    book.setTitle(dto.getTitle());
    book.setAuthors(fetchAuthors(dto.getAuthors()));
    book.setGenre(fetchGenre(dto.getGenreId()));
    book.setPrice(dto.getPrice());
    book.setDiscountPrice(dto.getDiscountPrice());
    book.setFormat(dto.getFormat());
    book.setPages(dto.getPages());
    book.setYear(dto.getYear());
    book.setPublishDate(dto.getPublishDate());
    book.setPublishers(fetchPublishers(dto.getPublisherIds()));
    book.setBlurb(dto.getBlurb());
    book.setDescription(dto.getDescription());
    book.setLongDescription(dto.getLongDescription());
    book.setTags(dto.getTagIds() == null ? new HashSet<>() : fetchTags(dto.getTagIds()));
    book.setFeatured(dto.isFeatured());
    book.setPopular(dto.isPopular());
    book.setNewArrival(dto.isNewArrival());
  }

  private Set<Author> fetchAuthors(Set<Long> ids) {
    return new HashSet<>(authorRepository.findAllById(ids));
  }

  private Genre fetchGenre(Long id) {
    return genreRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
  }

  private Set<Publisher> fetchPublishers(Set<Long> ids) {
    return new HashSet<>(publisherRepository.findAllById(ids));
  }

  private Set<Tag> fetchTags(Set<Long> ids) {
    return new HashSet<>(tagRepository.findAllById(ids));
  }

  private Book getEntity(Long id) {
    return bookRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
  }
}
