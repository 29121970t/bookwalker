package com.kruosant.bookwalker.domains;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Table(name = "orders")
@Entity
@Setter
@Getter
@NamedEntityGraph(
    name = "Order.full",
    attributeNodes = {
        @NamedAttributeNode("client"),
        @NamedAttributeNode(value = "books", subgraph = "books-details")
    },
    subgraphs = {
        @NamedSubgraph(
            name = "books-details",
            attributeNodes = {
                @NamedAttributeNode("authors"),
                @NamedAttributeNode("publisher")
            }
        )
    }
)
public class Order {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "client_id")
  private Client client;

  private LocalDateTime date;

  @ManyToMany
  @JoinTable(
      name = "order_book",
      joinColumns = @JoinColumn(name = "order_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "book_id", referencedColumnName = "id")
  )
  private Set<Book> books = new HashSet<>();

}
