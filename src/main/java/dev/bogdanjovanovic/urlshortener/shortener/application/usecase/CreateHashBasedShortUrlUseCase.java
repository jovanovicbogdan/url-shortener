package dev.bogdanjovanovic.urlshortener.shortener.application.usecase;

import dev.bogdanjovanovic.urlshortener.common.exception.InternalServerErrorException;
import dev.bogdanjovanovic.urlshortener.common.util.Base62Generator;
import dev.bogdanjovanovic.urlshortener.common.util.HttpResponseUtils;
import dev.bogdanjovanovic.urlshortener.shortener.domain.Url;
import dev.bogdanjovanovic.urlshortener.shortener.infrastructure.UrlRepository;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.MurmurHash3;
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
  public URI execute(final String originalUrl, @Nullable final String alias,
      @Nullable final Instant expiresAt) {
    final var existingUrl = urlRepository.findByOriginalUrl(originalUrl);

    if (existingUrl.isPresent() && !existingUrl.get().hasExpired()) {
      return HttpResponseUtils.buildLocationFromUrlPath(existingUrl.get().getShortUrl());
    }

    if (alias != null) {
      return createShortUrlWithAliasUseCase.execute(originalUrl, alias, expiresAt);
    }

    final var shortUrl = createHashBasedShortUrl(originalUrl);
    try {
      urlRepository.save(Url.builder()
          .originalUrl(originalUrl)
          .shortUrl(shortUrl.toString())
          .expiresAt(expiresAt)
          .build());
    } catch (DuplicateKeyException ex) {
      log.error("Short URL '{}' already exists in the database.", shortUrl);
      throw new InternalServerErrorException();
    }

    return shortUrl;
  }

  private URI createHashBasedShortUrl(final String originalUrl) {
    final var bytes = originalUrl.getBytes(StandardCharsets.UTF_8);
    final var hash = MurmurHash3.hash32x86(bytes, 0, bytes.length, MurmurHash3.DEFAULT_SEED);
    final var code = Base62Generator.generate(hash);
    return HttpResponseUtils.buildLocationWithPath(code);
  }

}
