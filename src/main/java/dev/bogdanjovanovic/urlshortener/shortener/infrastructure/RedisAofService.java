package dev.bogdanjovanovic.urlshortener.shortener.infrastructure;

import dev.bogdanjovanovic.urlshortener.common.util.RedisUtils;
import dev.bogdanjovanovic.urlshortener.shortener.application.CacheService;
import java.util.List;
import java.util.function.Consumer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import redis.clients.jedis.AbstractTransaction;
import redis.clients.jedis.RedisClient;
import redis.clients.jedis.params.SetParams;

@Service("redisAofService")
public class RedisAofService implements CacheService {

  private final RedisClient redisClient;

  public RedisAofService(@Qualifier("redisAofClient") final RedisClient redisClient) {
    this.redisClient = redisClient;
  }

  @Override
  public String set(final String key, final String value) {
    return RedisUtils.executeWithBackoff(() -> redisClient.set(key, value));
  }

  @Override
  public String set(final String key, final String value, final SetParams params) {
    return RedisUtils.executeWithBackoff(() -> redisClient.set(key, value, params));
  }

  @Override
  public String get(final String key) {
    return RedisUtils.executeWithBackoff(() -> redisClient.get(key));
  }

  @Override
  public Boolean exists(final String key) {
    return RedisUtils.executeWithBackoff(() -> redisClient.exists(key));
  }

  @Override
  public Long incrAndGet(final String key) {
    return RedisUtils.executeWithBackoff(() -> redisClient.incr(key));
  }

  @Override
  public List<Object> multi(final Consumer<AbstractTransaction> transaction) {
    try(AbstractTransaction tx = redisClient.multi()) {
      transaction.accept(tx);
      return tx.exec();
    }
  }

}
