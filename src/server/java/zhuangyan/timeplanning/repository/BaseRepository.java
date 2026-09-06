package zhuangyan.timeplanning.repository;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;

@Repository
public class BaseRepository<T> {
    private final Map<Long, T> objects = new ConcurrentHashMap<>();
    private final Map<Long, Long> objectOwners = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public T save(T obj, long userId, Function<T, Long> idExtractor) {
        Long id = idExtractor.apply(obj);
        lock.writeLock().lock();
        try {
            objects.put(id, obj);
            objectOwners.put(id, userId);
            return obj;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<T> save(List<T> obj, long userId, Function<T, Long> idExtractor) {
        List<T> result = new ArrayList<>();
        for (var o : obj) {
            save(o, userId, idExtractor);
            result.add(o);
        }
        return result;
    }

    public List<T> findByUserId(long userId) {
        lock.readLock().lock();
        try {
            return objectOwners.entrySet().stream()
                    .filter(e -> e.getValue() == userId)
                    .map(e -> objects.get(e.getKey()))
                    .toList();
        } finally {
            lock.readLock().unlock();
        }
    }

    public T deleteById(long id) {
        lock.writeLock().lock();
        try {
            T deletedObj = objects.remove(id);
            objectOwners.remove(id);
            return deletedObj;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void deleteByUserId(long userId) {
        lock.writeLock().lock();
        try {
            objectOwners.entrySet().stream()
                    .filter(e -> e.getValue() == userId)
                    .mapToLong(e -> e.getKey())
                    .forEach(id -> {
                        objects.remove(id);
                        objectOwners.remove(id);
                    });
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Optional<Long> getOwnerId(long id) {
        lock.readLock().lock();
        try {
            return Optional.ofNullable(objectOwners.get(id));
        } finally {
            lock.readLock().unlock();
        }
    }

    public long getNextId() {
        return nextId.getAndIncrement();
    }
}
