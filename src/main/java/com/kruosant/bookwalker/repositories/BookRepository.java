package com.kruosant.bookwalker.repositories;

import com.kruosant.bookwalker.domains.Book;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Repository
public class BookRepository {
  List<Book> books = new ArrayList<>();

  {
    books.add(Book.builder()
        .id(1)
        .name("Real time rendering")
        .author("Erick Heine")
        .pageCount(1000)
        .publishDate(Date.valueOf(LocalDate.parse("2018-06-25")))
        .publisher("A K Peters/CRC Press")
        .build());

    books.add(Book.builder()
        .id(2)
        .name("Clean Code: A Handbook of Agile Software Craftsmanship")
        .author("Robert C. Martin")
        .pageCount(464)
        .publishDate(Date.valueOf(LocalDate.parse("2008-08-01")))
        .publisher("Prentice Hall")
        .build());

    books.add(Book.builder()
        .id(3)
        .name("Design Patterns: Elements of Reusable Object-Oriented Software")
        .author("Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides")
        .pageCount(395)
        .publishDate(Date.valueOf(LocalDate.parse("1994-10-21")))
        .publisher("Addison-Wesley Professional")
        .build());

    books.add(Book.builder()
        .id(4)
        .name("The Pragmatic Programmer: Your Journey to Mastery")
        .author("David Thomas, Andrew Hunt")
        .pageCount(352)
        .publishDate(Date.valueOf(LocalDate.parse("2019-07-30")))
        .publisher("Addison-Wesley Professional")
        .build());

    books.add(Book.builder()
        .id(5)
        .name("Introduction to Algorithms")
        .author("Thomas H. Cormen, Charles E. Leiserson, Ronald L. Rivest, Clifford Stein")
        .pageCount(1312)
        .publishDate(Date.valueOf(LocalDate.parse("2009-07-31")))
        .publisher("MIT Press")
        .build());

    books.add(Book.builder()
        .id(6)
        .name("The Mythical Man-Month: Essays on Software Engineering")
        .author("Frederick P. Brooks Jr.")
        .pageCount(336)
        .publishDate(Date.valueOf(LocalDate.parse("1995-08-12")))
        .publisher("Addison-Wesley Professional")
        .build());
  }

  public Optional<Book> findFirstById(long id) {
    return books.stream().filter((Book b) -> b.getId() == id).findFirst();
  }

  public List<Book> findByName(String name) {
    return books.stream().filter(b -> b.getName().equals(name)).toList();

  }

  public Book save(Book book) {
    if (book.getId() == 0) {
      book.setId((long) books.size() + 1);
    }
    books.add(book);
    return book;
  }

  public List<Book> getAll() {
    return books;
  }
}