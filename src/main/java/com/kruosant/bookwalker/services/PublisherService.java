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
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class PublisherService {
  private final PublisherRepository publisherRepo;
  private final BookService bookService;
  private final BookRepository bookRepo;
  private final PublisherMapper mapper;

  @Transactional(readOnly = true)
  public PublisherFullDto getAuthorById(Long id) {
    return mapper.toFullDto(publisherRepo.findById(id).orElseThrow(ResourceNotFoundException::new));
  }

  @Transactional(readOnly = true)
  public PublisherFullDto create(PublisherCreateDto dto) {
    return mapper.toFullDto(publisherRepo.save(mapper.toAuthor(dto)));
  }

  @Transactional(readOnly = true)
  public List<PublisherFullDto> getAll() {
    return publisherRepo.findAll().stream().map(mapper::toFullDto).toList();
  }

  @Transactional
  public void delete(Long id) {
    Publisher publisher = publisherRepo.findById(id).orElseThrow(ResourceNotFoundException::new);

    publisherRepo.delete(publisher);
  }

  @Transactional
  public PublisherFullDto update(Long id, PublisherPatchDto dto) {
    Publisher publisher = publisherRepo.findById(id).orElseThrow(ResourceNotFoundException::new);

    if (dto.getBooks() != null) {

      new ArrayList<>(publisher.getBooks()).forEach(b -> b.setPublisher(null));
      dto.getBooks().stream()
          .map(bookId -> bookRepo.findById(bookId).orElseThrow(ResourceNotFoundException::new))
          .forEach(book -> bookService.setPublisher(book, publisher));
    }
    if (dto.getName() != null) {
      publisher.setName(dto.getName());
    }
    return mapper.toFullDto(publisherRepo.save(publisher));
  }

  @Transactional
  public PublisherFullDto update(Long id, PublisherPutDto dto) {
    Publisher publisher = publisherRepo.findById(id).orElseThrow(ResourceNotFoundException::new);

    new ArrayList<>(publisher.getBooks()).forEach(b -> b.setPublisher(null));
    dto.getBooks().stream()
        .map(bookId -> bookRepo.findById(bookId).orElseThrow(ResourceNotFoundException::new))
        .forEach(book -> bookService.setPublisher(book, publisher));

    publisher.setName(dto.getName());

    return mapper.toFullDto(publisherRepo.save(publisher));
  }

  @Transactional
  public PublisherFullDto addBook(Long bookId, Long publisherId) {
    Book book = bookRepo.findById(bookId).orElseThrow(ResourceNotFoundException::new);
    Publisher publisher = publisherRepo.findById(publisherId)
        .orElseThrow(ResourceNotFoundException::new);

    book.setPublisher(publisher);
    bookRepo.save(book);

    return mapper.toFullDto(publisher);
  }

  @Transactional
  public PublisherFullDto deleteBook(Long bookId, Long publisherId) {
    bookRepo.deleteById(bookId);
    return mapper.toFullDto(publisherRepo.findById(publisherId)
        .orElseThrow(ResourceNotFoundException::new));
  }
}
