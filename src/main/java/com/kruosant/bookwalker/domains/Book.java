package com.kruosant.bookwalker.domains;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;


@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@NamedEntityGraph(
    name = "Book.authors.publisher",
    attributeNodes = {
        @NamedAttributeNode("authors"),
        @NamedAttributeNode("publisher")
    }
)
public class Book {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private String name;

  @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinTable(
      name = "book_author",
      joinColumns = @JoinColumn(name = "book_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "author_id", referencedColumnName = "id"))
  private Set<Author> authors;
  private Long pageCount;
  private LocalDate publishDate;

  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  private Publisher publisher;
  private Double price;

  public void setAuthors(Collection<Author> newAuthors) {
    authors.clear();
    authors.addAll(newAuthors);
    authors.forEach(a -> a.getBooks().add(this));
  }

  public void addAuthor(Author author) {
    authors.add(author);
    author.getBooks().add(this);
  }

  public void removeAuthor(Author author) {
    authors.remove(author);
    author.getBooks().remove(this);
  }

  public void setPublisher(Publisher newPublisher) {
    if (publisher != null) {
      publisher.getBooks().remove(this);
    }
    publisher = newPublisher;
    if (newPublisher != null) {
      newPublisher.getBooks().add(this);
    }
  }

}
