package com.kruosant.bookwalker.domains;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Table(name = "orders")
@Entity
public class Order {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private Client client;

  private LocalDateTime timeStamp;

  @ManyToMany
  @JoinTable(
      name = "order_book",
      joinColumns = @JoinColumn(name = "client_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "order_id", referencedColumnName = "id")
  )
  private List<Book> book;

}
