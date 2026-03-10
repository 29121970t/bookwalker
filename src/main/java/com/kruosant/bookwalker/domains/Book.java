package com.kruosant.bookwalker.domains;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;


@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private String name;

  @ManyToMany
  @JoinTable(
      name = "book_author",
      joinColumns = @JoinColumn(name = "book_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "author_id", referencedColumnName = "id"))
  private Set<Author> authors;
  private Long pageCount;
  private LocalDate publishDate;
  @ManyToOne
  private Publisher publisher;
  private Double price;

  public void setAuthors(Set<Author> newAuthors) {
    authors.clear();
    authors.addAll(newAuthors);
    authors.forEach(a -> a.getBooks().add(this));
  }

  public void addAuthor(Author author) {
    authors.add(author);
    author.getBooks().add(this);
  }

  public void deleteAuthor(Author author) {
    authors.remove(author);
    author.getBooks().remove(this);
  }

  public void setPublisher(Publisher newPublisher) {
    publisher = newPublisher;
    publisher.getBooks().add(this);
  }

}
