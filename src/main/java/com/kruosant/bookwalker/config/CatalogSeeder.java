package com.kruosant.bookwalker.config;

import com.kruosant.bookwalker.domains.Author;
import com.kruosant.bookwalker.domains.Book;
import com.kruosant.bookwalker.domains.BookFormat;
import com.kruosant.bookwalker.domains.Client;
import com.kruosant.bookwalker.domains.Genre;
import com.kruosant.bookwalker.domains.Publisher;
import com.kruosant.bookwalker.domains.Tag;
import com.kruosant.bookwalker.domains.UserRole;
import com.kruosant.bookwalker.domains.UserStatus;
import com.kruosant.bookwalker.repositories.AuthorRepository;
import com.kruosant.bookwalker.repositories.BookRepository;
import com.kruosant.bookwalker.repositories.ClientRepository;
import com.kruosant.bookwalker.repositories.GenreRepository;
import com.kruosant.bookwalker.repositories.PublisherRepository;
import com.kruosant.bookwalker.repositories.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class CatalogSeeder implements CommandLineRunner {
  private final ClientRepository clientRepository;
  private final GenreRepository genreRepository;
  private final AuthorRepository authorRepository;
  private final PublisherRepository publisherRepository;
  private final TagRepository tagRepository;
  private final BookRepository bookRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void run(String... args) {
    seedUsers();
    seedCatalog();
  }

  private void seedUsers() {
    if (!clientRepository.existsByEmailIgnoreCase("admin@bookwalker.app")) {
      clientRepository.save(Client.builder()
          .name("Bookwalker Admin")
          .email("admin@bookwalker.app")
          .password(passwordEncoder.encode("admin123"))
          .city("Minsk")
          .role(UserRole.ADMIN)
          .status(UserStatus.ACTIVE)
          .joinedAt(LocalDate.now())
          .build());
    }
  }

  private void seedCatalog() {
    if (bookRepository.count() > 0) {
      return;
    }

    Genre fiction = genreRepository.save(Genre.builder()
        .name("Fiction")
        .description("Character-driven contemporary fiction.")
        .build());

    Author mattHaig = authorRepository.save(Author.builder()
        .name("Matt Haig")
        .bio("British novelist and journalist.")
        .country("United Kingdom")
        .website("https://matthaig.com")
        .build());

    Publisher canongate = publisherRepository.save(Publisher.builder()
        .name("Canongate")
        .description("Independent publisher from Edinburgh.")
        .country("United Kingdom")
        .website("https://canongate.co.uk")
        .build());

    Tag bestSeller = tagRepository.save(Tag.builder()
        .name("Best Seller")
        .description("Popular with readers.")
        .color("#0f766e")
        .featured(true)
        .build());
    Tag reflective = tagRepository.save(Tag.builder()
        .name("Reflective")
        .description("Thoughtful and emotional reads.")
        .color("#1d4ed8")
        .featured(false)
        .build());

    bookRepository.save(Book.builder()
        .title("The Midnight Library")
        .authors(Set.of(mattHaig))
        .genre(fiction)
        .price(new BigDecimal("18.99"))
        .discountPrice(new BigDecimal("24.99"))
        .format(BookFormat.HARDCOVER)
        .pages(304)
        .year(2020)
        .publishDate(LocalDate.of(2020, 8, 13))
        .publishers(Set.of(canongate))
        .blurb("A life between regrets, second chances, and infinite shelves.")
        .description("Between life and death, Nora discovers a library filled with alternate versions of her life.")
        .longDescription("A luminous novel about regret, possibility, and the quiet courage required to choose a life on purpose.")
        .tags(Set.of(bestSeller, reflective))
        .featured(true)
        .popular(true)
        .newArrival(false)
        .build());
  }
}
