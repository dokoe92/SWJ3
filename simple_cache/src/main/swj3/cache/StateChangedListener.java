package swj3.cache;

@FunctionalInterface
public interface StateChangedListener {
  void changed(EventType type, String key, Object value);
}
