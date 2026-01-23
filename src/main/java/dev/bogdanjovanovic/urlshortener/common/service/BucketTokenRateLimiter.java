package dev.bogdanjovanovic.urlshortener.common.service;

import dev.bogdanjovanovic.urlshortener.shortener.application.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BucketTokenRateLimiter {

  private static final int BUCKET_CAPACITY = 60;
  private static final double REFILL_RATE = 1.0;
  private final CacheService cacheService;

  public BucketTokenRateLimiter(@Qualifier("redisAofService") final CacheService cacheService) {
    this.cacheService = cacheService;
  }

  public boolean isAllowed(@NonNull final String ip) {
    final var tokenCountKey = String.format("token_count:%s", ip);
    final var lastRefillTimeKey = String.format("last_refill_time:%s", ip);

    final var res = cacheService.multi((tx) -> {
      tx.get(tokenCountKey);
      tx.get(lastRefillTimeKey);
    });

    final var now = System.currentTimeMillis();

    var tokenCount =
        res.get(0) == null ? BUCKET_CAPACITY : Long.parseLong((String) res.get(0));
    final var lastRefillTime = res.get(1) == null ? now : Long.parseLong((String) res.get(1));

    final var elapsedTimeSecs = (now - lastRefillTime) / 1000.0;
    // refill 1 request per second
    final var toRefill = (int) (elapsedTimeSecs * REFILL_RATE);
    tokenCount = Math.min(BUCKET_CAPACITY, tokenCount + toRefill);

    final var isAllowed = tokenCount > 0;
    if (isAllowed) {
      tokenCount--;
    }

    final var availableTokens = tokenCount;
    cacheService.multi((tx) -> {
      tx.set(tokenCountKey, String.valueOf(availableTokens));
      tx.set(lastRefillTimeKey, String.valueOf(now));
    });

    return isAllowed;
  }

}
