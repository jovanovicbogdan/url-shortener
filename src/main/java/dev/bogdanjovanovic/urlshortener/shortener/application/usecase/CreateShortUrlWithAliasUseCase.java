package dev.bogdanjovanovic.urlshortener.shortener.application.usecase;

import dev.bogdanjovanovic.urlshortener.common.exception.ConflictException;
import dev.bogdanjovanovic.urlshortener.common.util.HttpResponseUtils;
import dev.bogdanjovanovic.urlshortener.shortener.domain.Url;
import dev.bogdanjovanovic.urlshortener.shortener.infrastructure.UrlRepository;
import java.net.URI;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateShortUrlWithAliasUseCase {

  private final UrlRepository urlRepository;

  public URI execute(final String originalUrl, final String alias,
      @Nullable final Instant expiresAt) {
    final var existingAliasUrl = urlRepository.findByAlias(alias);
    if (existingAliasUrl.isPresent()) {
      throw new ConflictException();
    }
    final var shortUrl = HttpResponseUtils.buildLocationWithPath(alias);
    urlRepository.save(Url.builder()
        .originalUrl(originalUrl)
        .shortUrl(shortUrl.toString())
        .alias(alias)
        .expiresAt(expiresAt)
        .build());
    return shortUrl;
  }

}
