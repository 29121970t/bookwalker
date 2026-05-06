package com.kruosant.bookwalker.domains;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "books")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@NamedEntityGraph(
    name = "Book.full",
    attributeNodes = {
        @NamedAttributeNode("authors"),
        @NamedAttributeNode("genre"),
        @NamedAttributeNode("publishers"),
        @NamedAttributeNode("tags")
    }
)
public class Book {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String title;

  @ManyToMany
  @JoinTable(
      name = "book_authors",
      joinColumns = @JoinColumn(name = "book_id"),
      inverseJoinColumns = @JoinColumn(name = "author_id")
  )
  @Builder.Default
  private Set<Author> authors = new HashSet<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "genre_id")
  private Genre genre;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal price;

  @Column(precision = 10, scale = 2)
  private BigDecimal discountPrice;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private BookFormat format;

  @Column(nullable = false)
  private Integer pages;

  @Column(nullable = false)
  private Integer year;

  private LocalDate publishDate;

  @ManyToMany
  @JoinTable(
      name = "book_publishers",
      joinColumns = @JoinColumn(name = "book_id"),
      inverseJoinColumns = @JoinColumn(name = "publisher_id")
  )
  @Builder.Default
  private Set<Publisher> publishers = new HashSet<>();

  @Column(length = 500)
  private String blurb;

  @Column(length = 2000)
  private String description;

  @Column(length = 6000)
  private String longDescription;

  @ManyToMany
  @JoinTable(
      name = "book_tags",
      joinColumns = @JoinColumn(name = "book_id"),
      inverseJoinColumns = @JoinColumn(name = "tag_id")
  )
  @Builder.Default
  private Set<Tag> tags = new HashSet<>();

  private String coverPath;

  @Builder.Default
  private boolean featured = false;

  @Builder.Default
  private boolean popular = false;

  @Builder.Default
  private boolean newArrival = false;
}
