package dev.bogdanjovanovic.urlshortener;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class UrlShortenerApplication {

  static void main(String[] args) {
    SpringApplication.run(UrlShortenerApplication.class, args);
  }

}
