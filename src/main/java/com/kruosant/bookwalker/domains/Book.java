package com.kruosant.bookwalker.domains;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;


@Entity
@Table(name = "books")
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
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

  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinTable(
      name = "book_author",
      joinColumns = @JoinColumn(name = "book_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "author_id", referencedColumnName = "id")
  )
  private Set<Author> authors;
  private Long pageCount;
  private LocalDate publishDate;

  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  private Publisher publisher;
  private Double price;


}
