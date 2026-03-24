package com.kruosant.bookwalker.cashe;

import com.kruosant.bookwalker.domains.Order;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor
@Component
public class OrderSearchCache {
  Logger logger = LoggerFactory.getLogger(OrderSearchCache.class);
  private final ConcurrentHashMap<OrderSearchCacheKey, Page<Order>> map = new ConcurrentHashMap<>();

  public void save(OrderSearchCacheKey key, Page<Order> page) {
    map.put(key, page);
  }

  public Optional<Page<Order>> get(OrderSearchCacheKey key) {
    if (map.containsKey(key)) {
      logger.info("Cache hit!");
      return Optional.of(map.get(key));
    } else {
      logger.info("Cache miss");
      return Optional.empty();
    }
  }

  public void invalidate() {
    map.clear();
    logger.info("Cache cleared");
  }
}
