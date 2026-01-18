package dev.bogdanjovanovic.urlshortener.common.util;

import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;

@Slf4j
public class RedisUtils {

  private static final int MAX_ATTEMPTS = 5;

  public static <R> R executeWithBackoff(final Supplier<R> jedisCommand) {
    for (int i = 0; i < MAX_ATTEMPTS; i++) {
      try {
        return jedisCommand.get();
      } catch (JedisConnectionException jce) {
        log.warn(jce.getMessage());
        try {
          Thread.sleep(500L * i);
        } catch (InterruptedException ie) {
          Thread.currentThread().interrupt();
        }
      } catch (JedisException je) {
        log.error("Unexpected error occurred when trying to execute Redis command: {}",
            je.getMessage());
        throw je;
      }
    }
    log.error("Failed to execute redis command after {} attempts", MAX_ATTEMPTS);
    return null;
  }

}
