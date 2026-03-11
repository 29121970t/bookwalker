package com.kruosant.bookwalker.domains;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Table(name = "orders")
@Entity
@Setter
@Getter
public class Order {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Setter
  @ManyToOne(optional = false)
  @JoinColumn(name = "client_id")
  private Client client;

  private LocalDateTime timeStamp;

  @ManyToMany
  @JoinTable(
      name = "order_book",
      joinColumns = @JoinColumn(name = "order_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "book_id", referencedColumnName = "id")
  )
  private Set<Book> books = new HashSet<>();

  public void addBook(Book book) {
    books.add(book);
  }

  public void removeBook(Book book) {
    books.remove(book);
  }

}
