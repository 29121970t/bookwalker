package com.kruosant.bookwalker.cashe;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


public record OrderSearchCacheKey(String surname, int pageNumber, int pageSize, Sort sort) {
  public static OrderSearchCacheKey create(String surname, Pageable p) {
    return new OrderSearchCacheKey(surname, p.getPageNumber(), p.getPageSize(), p.getSort());
  }
}