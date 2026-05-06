package com.kruosant.bookwalker.security;

import com.kruosant.bookwalker.domains.Client;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.List;

@Getter
public class ClientPrincipal implements UserDetails {
  @Serial
  private static final long serialVersionUID = 1L;

  private final transient Client client;

  public ClientPrincipal(Client client) {
    this.client = client;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_" + client.getRole().name()));
  }

  @Override
  public String getPassword() {
    return client.getPassword();
  }

  @Override
  public String getUsername() {
    return client.getEmail();
  }
}
