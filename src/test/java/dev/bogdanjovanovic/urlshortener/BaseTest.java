package dev.bogdanjovanovic.urlshortener;

import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.MountableFile;

@Testcontainers
@AutoConfigureRestTestClient
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class BaseTest {

  @Container
  @ServiceConnection
  static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(
      "postgres:18.1-alpine");
  @Container
  @ServiceConnection
  static KafkaContainer kafkaContainer = new KafkaContainer("apache/kafka:4.1.1");
  static RedisContainer redisLruContainer = new RedisContainer("redis:8.4.0-alpine");
  static RedisContainer redisAofContainer = new RedisContainer("redis:8.4.0-alpine");
  @Autowired
  protected RestTestClient client;

  @BeforeAll
  static void setup() {
    final var redisConfigDir = System.getProperty("user.dir") + "/redis";
    redisLruContainer
        .withExposedPorts(6379)
        .withCopyFileToContainer(
            MountableFile.forHostPath(redisConfigDir + "/redis-lru.conf"),
            "/usr/local/etc/redis/"
        )
        .withCommand("redis-server", "/usr/local/etc/redis/redis-lru.conf")
        .start();
    redisAofContainer
        .withExposedPorts(6380)
        .withCopyFileToContainer(
            MountableFile.forHostPath(redisConfigDir + "/redis-aof.conf"),
            "/usr/local/etc/redis/"
        )
        .withCommand("redis-server", "/usr/local/etc/redis/redis-aof.conf")
        .start();
  }

  @AfterAll
  static void teardown() {
    redisLruContainer.stop();
    redisAofContainer.stop();
  }

  @DynamicPropertySource
  static void redisDynamicProperties(final DynamicPropertyRegistry registry) {
    registry.add("redis.lru.port", () -> redisLruContainer.getMappedPort(6379));
    registry.add("redis.aof.port", () -> redisAofContainer.getMappedPort(6380));
  }

}
