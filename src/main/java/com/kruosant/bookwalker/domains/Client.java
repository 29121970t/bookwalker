package com.kruosant.bookwalker.domains;

import jakarta.persistence.*;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Table(name = "clients")
@Entity
public class Client {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String userName;
  private String password;

  @OneToMany(mappedBy = "client")
  private Set<Order> orders;

}
