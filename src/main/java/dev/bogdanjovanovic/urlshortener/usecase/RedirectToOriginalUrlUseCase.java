package dev.bogdanjovanovic.urlshortener.usecase;

import dev.bogdanjovanovic.urlshortener.exception.NotFoundException;
import dev.bogdanjovanovic.urlshortener.repository.UrlRepository;
import dev.bogdanjovanovic.urlshortener.util.HttpUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedirectToOriginalUrlUseCase {

  private final UrlRepository urlRepository;

  public String execute(final String code) {
    final var shortUrl = HttpUtils.urlFromCurrentRequest(code).toString();
    final var url = urlRepository.findByShortUrl(shortUrl).orElseThrow(
        () -> {
          log.warn("Provided short URL '{}' doesn't exist.", shortUrl);
          return new NotFoundException();
        });
    return url.getOriginalUrl();
  }

}
