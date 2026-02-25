package com.kruosant.bookwalker.domains;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Date;


@Entity
public class Book {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private String name;
  private String author;
  private long pageCount;
  private Date publishDate;
  private String publisher;

  // No-argument constructor
  public Book() {
  }

  // All-arguments constructor
  public Book(long id, String name, String author, long pageCount,
              Date publishDate, String publisher) {
    this.id = id;
    this.name = name;
    this.author = author;
    this.pageCount = pageCount;
    this.publishDate = publishDate;
    this.publisher = publisher;
  }

  // Getters and Setters
  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public long getPageCount() {
    return pageCount;
  }

  public void setPageCount(long pageCount) {
    this.pageCount = pageCount;
  }

  public Date getPublishDate() {
    return publishDate;
  }

  public void setPublishDate(Date publishDate) {
    this.publishDate = publishDate;
  }

  public String getPublisher() {
    return publisher;
  }

  public void setPublisher(String publisher) {
    this.publisher = publisher;
  }

  // Builder pattern
  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private long id;
    private String name;
    private String author;
    private long pageCount;
    private Date publishDate;
    private String publisher;

    Builder() {
    }

    public Builder id(long id) {
      this.id = id;
      return this;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder author(String author) {
      this.author = author;
      return this;
    }

    public Builder pageCount(long pageCount) {
      this.pageCount = pageCount;
      return this;
    }

    public Builder publishDate(Date publishDate) {
      this.publishDate = publishDate;
      return this;
    }

    public Builder publisher(String publisher) {
      this.publisher = publisher;
      return this;
    }

    public Book build() {
      return new Book(id, name, author, pageCount, publishDate, publisher);
    }
  }
}
