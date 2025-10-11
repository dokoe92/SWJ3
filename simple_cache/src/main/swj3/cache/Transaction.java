package swj3.cache;

public interface Transaction extends AutoCloseable {
  Transaction set(String key, Object value);
  Transaction set(String key, Object value, long ttl);
  Transaction del(String key);

  void exec();

  @Override
  default void close() {
    exec();
  }
}
