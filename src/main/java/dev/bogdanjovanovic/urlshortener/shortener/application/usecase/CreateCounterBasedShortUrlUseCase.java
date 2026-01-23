package dev.bogdanjovanovic.urlshortener.shortener.application.usecase;

import dev.bogdanjovanovic.urlshortener.common.exception.InternalServerErrorException;
import dev.bogdanjovanovic.urlshortener.common.util.HttpResponseUtils;
import dev.bogdanjovanovic.urlshortener.shortener.application.CacheService;
import dev.bogdanjovanovic.urlshortener.shortener.domain.Url;
import dev.bogdanjovanovic.urlshortener.shortener.infrastructure.UrlRepository;
import java.net.URI;
import java.time.Instant;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sqids.Sqids;

@Slf4j
@Service
public class CreateCounterBasedShortUrlUseCase {

  @Value("${redis.aof.counter-key}")
  private String counterKey;

  private static final Sqids SQIDS = Sqids.builder().build();

  private final UrlRepository urlRepository;
  private final CacheService cacheService;
  private final CreateShortUrlWithAliasUseCase createShortUrlWithAliasUseCase;

  public CreateCounterBasedShortUrlUseCase(final UrlRepository urlRepository,
      @Qualifier("redisAofService") final CacheService cacheService,
      final CreateShortUrlWithAliasUseCase createShortUrlWithAliasUseCase) {
    this.urlRepository = urlRepository;
    this.cacheService = cacheService;
    this.createShortUrlWithAliasUseCase = createShortUrlWithAliasUseCase;
  }

  @Transactional
  public URI execute(final String originalUrl, @Nullable final String alias,
      @Nullable final Instant expiresAt) {
    final var existingUrl = urlRepository.findByOriginalUrl(originalUrl);

    existingUrl.ifPresent(url -> System.out.println(url.hasExpired()));

    if (existingUrl.isPresent() && !existingUrl.get().hasExpired()) {
      return HttpResponseUtils.buildLocationFromUrlPath(existingUrl.get().getShortUrl());
    }

    if (alias != null) {
      return createShortUrlWithAliasUseCase.execute(originalUrl, alias, expiresAt);
    }

    final var next = cacheService.incrAndGet(counterKey);
    if (next == null) {
      throw new InternalServerErrorException();
    }

    final var numbers = String.valueOf(next)
        .chars()
        .mapToLong(Character::getNumericValue)
        .toArray();
    final var code = SQIDS.encode(Arrays.stream(numbers).boxed().toList());
    final var shortUrl = HttpResponseUtils.buildLocationWithPath(code);

    urlRepository.save(Url.builder()
        .originalUrl(originalUrl)
        .shortUrl(shortUrl.toString())
        .expiresAt(expiresAt)
        .build());

    return shortUrl;
  }

}
