package dev.bogdanjovanovic.urlshortener.common.config;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.ConnectionPoolConfig;
import redis.clients.jedis.RedisClient;

@Configuration
public class RedisConfig {

  @Value("${redis.lru.port}")
  private Integer redisLruPort;
  @Value("${redis.aof.port}")
  private Integer redisAofPort;

  @Bean("redisLruClient")
  public RedisClient redisLruClient() {
    return RedisClient.builder()
        .hostAndPort("localhost", redisLruPort)
        .poolConfig(defaultPoolConfig())
        .build();
  }

  @Bean("redisAofClient")
  public RedisClient redisAofClient() {
    return RedisClient.builder()
        .hostAndPort("localhost", redisAofPort)
        .poolConfig(defaultPoolConfig())
        .build();
  }

  private ConnectionPoolConfig defaultPoolConfig() {
    final var poolConfig = new ConnectionPoolConfig();

    // maximum active connections in the pool
    poolConfig.setMaxTotal(8);

    // maximum idle connections in the pool
    poolConfig.setMaxIdle(8);

    // minimum idle connections in the pool
    poolConfig.setMinIdle(0);

    // enables waiting for a connection to become available
    poolConfig.setBlockWhenExhausted(true);

    // the maximum number of seconds to wait for a connection to become available
    poolConfig.setMaxWait(Duration.ofSeconds(1));

    // enables sending a PING command periodically while the connection is idle
    poolConfig.setTestWhileIdle(true);

    // controls the period between checks for idle connections in the pool
    poolConfig.setTimeBetweenEvictionRuns(Duration.ofSeconds(1));

    return poolConfig;
  }

}
