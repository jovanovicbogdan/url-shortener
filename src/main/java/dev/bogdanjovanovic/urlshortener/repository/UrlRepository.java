package dev.bogdanjovanovic.urlshortener.repository;

import dev.bogdanjovanovic.urlshortener.model.UrlEntity;
import java.util.Optional;
import org.springframework.data.repository.Repository;

public interface UrlRepository extends Repository<UrlEntity, Long> {

  void save(UrlEntity urlEntity);

  Optional<UrlEntity> findByShortUrl(String shortUrl);

}
