package dev.bogdanjovanovic.urlshortener;

import dev.bogdanjovanovic.urlshortener.shortener.infrastructure.RedisUrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@RequiredArgsConstructor
public class UrlShortenerApplication {

  @Value("${url.redis.counter-key}")
  private String counterKey;
  private final ApplicationContext context;

  static void main(String[] args) {
    SpringApplication.run(UrlShortenerApplication.class, args);
  }

  @Bean
  CommandLineRunner runner(final RedisUrlService redisUrlService) {
    return _ -> {
      if (redisUrlService.exists(counterKey)) {
        return;
      }

      final String result = redisUrlService.set(counterKey, "1");
      if (result == null) {
        SpringApplication.exit(context, () -> 1);
      }
    };
  }

}
