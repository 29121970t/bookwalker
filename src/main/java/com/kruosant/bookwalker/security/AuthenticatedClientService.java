package com.kruosant.bookwalker.security;

import com.kruosant.bookwalker.domains.Client;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticatedClientService {
  public Client getCurrentClient() {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return ((ClientPrincipal) principal).getClient();
  }
}
