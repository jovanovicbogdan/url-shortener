package dev.bogdanjovanovic.urlshortener.shortener.application.usecase;

import dev.bogdanjovanovic.urlshortener.common.exception.InternalServerErrorException;
import dev.bogdanjovanovic.urlshortener.common.util.Base62Generator;
import dev.bogdanjovanovic.urlshortener.common.util.HttpResponseUtils;
import dev.bogdanjovanovic.urlshortener.shortener.domain.Url;
import dev.bogdanjovanovic.urlshortener.shortener.infrastructure.UrlRepository;
import java.net.URI;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateHashBasedShortUrlUseCase {

  private final UrlRepository urlRepository;
  private final CreateShortUrlWithAliasUseCase createShortUrlWithAliasUseCase;

  @Transactional
  public URI execute(final int originalUrlHashCode, @Nullable final String alias,
      @Nullable final Instant expiresAt) {
    final var existingUrl = urlRepository.findByOriginalUrlHashCode(originalUrlHashCode);

    if (existingUrl.isPresent() && !existingUrl.get().hasExpired()) {
      return HttpResponseUtils.buildLocationFromUrlPath(existingUrl.get().getShortUrl());
    }

    if (alias != null) {
      return createShortUrlWithAliasUseCase.execute(originalUrlHashCode, alias, expiresAt);
    }

    final var shortUrl = createHashBasedShortUrl(originalUrlHashCode);
    try {
      urlRepository.save(Url.builder()
          .originalUrlHashCode(originalUrlHashCode)
          .shortUrl(shortUrl.toString())
          .expiresAt(expiresAt)
          .build());
    } catch (DuplicateKeyException ex) {
      log.error("Short URL '{}' already exists in the database.", shortUrl);
      throw new InternalServerErrorException();
    }

    return shortUrl;
  }

  private URI createHashBasedShortUrl(final int originalUrlHashCode) {
    final var code = Base62Generator.generate(originalUrlHashCode);
    return HttpResponseUtils.buildLocationWithPath(code);
  }

}
