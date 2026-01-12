package dev.bogdanjovanovic.urlshortener.usecase;

import dev.bogdanjovanovic.urlshortener.exception.InternalServerErrorException;
import dev.bogdanjovanovic.urlshortener.model.UrlEntity;
import dev.bogdanjovanovic.urlshortener.repository.UrlRepository;
import dev.bogdanjovanovic.urlshortener.util.Base62Generator;
import dev.bogdanjovanovic.urlshortener.util.HttpUtils;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateShortUrlUseCase {

  private final UrlRepository urlRepository;

  @Transactional
  public URI execute(final String url) {
    final var existingUrl = urlRepository.findByOriginalUrl(url);
    if (existingUrl.isPresent()) {
      return HttpUtils.locationFromUrl(existingUrl.get().getShortUrl());
    }

    final var code = Base62Generator.generate();
    final var shortUrl = HttpUtils.locationWithPath(code);
    try {
      urlRepository.save(UrlEntity.builder()
          .originalUrl(url)
          .shortUrl(shortUrl.toString())
          .build());
    } catch (DuplicateKeyException ex) {
      // collision detected, either throw an error or implement some kind of retry mechanism
      log.error("Short URL '{}' already exists in the database.", shortUrl);
      throw new InternalServerErrorException();
    }

    return shortUrl;
  }

}
