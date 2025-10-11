package swj3.cache;

public interface Cache {
  Object get(String key);
  void set(String key, Object value);
  void set(String key, Object value, long ttl);
  void del(String key);

  void addStateChangedListener(StateChangedListener listener);
  void removeStateChangedListener(StateChangedListener listener);

}
