package dev.bogdanjovanovic.urlshortener.usecase;

import dev.bogdanjovanovic.urlshortener.exception.InternalServerErrorException;
import dev.bogdanjovanovic.urlshortener.model.UrlEntity;
import dev.bogdanjovanovic.urlshortener.repository.UrlRepository;
import dev.bogdanjovanovic.urlshortener.util.Base62Generator;
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
  public String execute(final String url) {
    final var code = Base62Generator.generate();
    final var shortUrl = "https://shrt.com/" + code;
    try {
      urlRepository.save(UrlEntity.builder()
          .url(url)
          .shortUrl(shortUrl)
          .build());
    } catch (DuplicateKeyException ex) {
      // collision detected, either throw an error or implement
      // some kind of retry mechanism
      log.error("Short URL '{}' already exists in the database.", shortUrl);
      throw new InternalServerErrorException();
    }
    return shortUrl;
  }

}
