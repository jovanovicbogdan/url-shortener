package dev.bogdanjovanovic.urlshortener.shortener.infrastructure;

import dev.bogdanjovanovic.urlshortener.shortener.domain.Url;
import java.util.Optional;
import org.springframework.data.repository.Repository;

public interface UrlRepository extends Repository<Url, Long> {

  void save(Url url);

  Optional<Url> findByShortUrl(String shortUrl);

  Optional<Url> findByOriginalUrl(String originalUrl);

  Optional<Url> findByAlias(String alias);

}
