package com.kruosant.bookwalker.domains;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;


@Entity
@Table(name = "Books")
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
  private String author;
  private long pageCount;
  private Date publishDate;
  private String publisher;

}
