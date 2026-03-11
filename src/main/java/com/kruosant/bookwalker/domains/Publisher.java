package com.kruosant.bookwalker.domains;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Table(name = "publishers")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@NamedEntityGraph(
    name = "Publisher.books",
    attributeNodes = {
        @NamedAttributeNode("books"),
    }
)
public class Publisher {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private String name;

  @OneToMany(
      cascade = CascadeType.ALL,
      mappedBy = "publisher", orphanRemoval = true,
      fetch = FetchType.EAGER
  )
  private Set<Book> books = HashSet.newHashSet(0);

  public void addBook(Book book) {
    books.add(book);
    book.setPublisher(this);
  }

  public void removeBook(Book book) {
    books.remove(book);
    book.setPublisher(null);
  }
}
