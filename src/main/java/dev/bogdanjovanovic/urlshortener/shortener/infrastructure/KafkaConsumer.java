package dev.bogdanjovanovic.urlshortener.shortener.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaConsumer {

  @KafkaListener(id = "myId", groupId = "myGroupId", topics = "topic1")
  public void listen(
      final String value,
      @Header(name = KafkaHeaders.RECEIVED_KEY, required = false) final String key
  ) {
    log.info("Received a new event from 'topic1': {}:{}", key, value);
  }

}
