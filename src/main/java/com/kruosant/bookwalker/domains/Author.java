package com.kruosant.bookwalker.domains;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Table(name = "authors")
@Entity
@AllArgsConstructor
@Getter
@Setter
@Builder
@NoArgsConstructor
@NamedEntityGraph(
    name = "Author.books",
    attributeNodes = {
        @NamedAttributeNode("books"),
    }
)
public class Author {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private String name;
  private String bio;

  @ManyToMany(mappedBy = "authors", fetch = FetchType.EAGER)
  private Set<Book> books = new HashSet<>();

  public void addBook(Book book) {
    book.addAuthor(this);
  }

  public void removeBook(Book book) {
    book.removeAuthor(this);
  }

  public void removeAllBooks() {
    new ArrayList<>(books).forEach(book -> book.removeAuthor(this));
  }
}
