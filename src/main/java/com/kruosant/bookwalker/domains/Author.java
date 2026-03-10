package com.kruosant.bookwalker.domains;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Table(name = "authors")
@Entity
@AllArgsConstructor
@Getter
@Setter
@Builder
@NoArgsConstructor
public class Author {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private String name;
  private String bio;

  @ManyToMany(cascade = CascadeType.ALL, mappedBy = "authors")
  private Set<Book> books;
}
