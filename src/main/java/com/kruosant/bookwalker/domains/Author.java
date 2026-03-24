package com.kruosant.bookwalker.domains;

import jakarta.persistence.*;
import lombok.*;

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
  private String name = "";
  private String middleName = "";
  private String surname = "";
  private String bio = "";

  @ManyToMany(mappedBy = "authors", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @Builder.Default
  private Set<Book> books = new HashSet<>();

}
