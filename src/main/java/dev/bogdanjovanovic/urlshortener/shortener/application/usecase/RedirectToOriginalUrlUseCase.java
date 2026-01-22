package dev.bogdanjovanovic.urlshortener.shortener.application.usecase;

import dev.bogdanjovanovic.urlshortener.common.exception.BadRequestException;
import dev.bogdanjovanovic.urlshortener.shortener.application.CacheService;
import dev.bogdanjovanovic.urlshortener.common.exception.InternalServerErrorException;
import dev.bogdanjovanovic.urlshortener.common.exception.NotFoundException;
import dev.bogdanjovanovic.urlshortener.common.util.HttpResponseUtils;
import dev.bogdanjovanovic.urlshortener.shortener.infrastructure.UrlRepository;
import java.time.Duration;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import redis.clients.jedis.params.SetParams;

@Slf4j
@Service
public class RedirectToOriginalUrlUseCase {

  private final UrlRepository urlRepository;
  private final CacheService cacheService;

  public RedirectToOriginalUrlUseCase(final UrlRepository urlRepository,
      @Qualifier("redisLruService") final CacheService cacheService) {
    this.urlRepository = urlRepository;
    this.cacheService = cacheService;
  }

  public String execute(final String code) {
    final var shortUrlFromCache = cacheService.get(code);
    if (shortUrlFromCache != null) {
      return shortUrlFromCache;
    }

    final var shortUrl = HttpResponseUtils.buildLocationWithPath(code).toString();
    final var url = urlRepository.findByShortUrl(shortUrl).orElseThrow(() -> {
      log.warn("Provided short URL '{}' doesn't exist.", shortUrl);
      return new NotFoundException();
    });

    if (url.hasExpired()) {
      log.info("Short URL has expired");
      throw new BadRequestException();
    }

    final var originalUrl = url.getOriginalUrl();
    final var remainingSeconds = Duration.between(Instant.now(), url.getExpiresAt()).getSeconds();
    final var params = SetParams.setParams().nx().ex(remainingSeconds);
    final var cacheOriginalUrl = cacheService.set(code, originalUrl, params);
    if (cacheOriginalUrl == null) {
      // key collision detected or failed to connect to Redis
      throw new InternalServerErrorException();
    }

    return originalUrl;
  }

}
