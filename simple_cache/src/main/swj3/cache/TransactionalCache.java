package swj3.cache;

public interface TransactionalCache extends Cache{
  Transaction beginTransaction();
}
