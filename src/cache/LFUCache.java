package cache;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class LFUCache<K, V> {
	int cacheSize;
	Map<K, V> cache;
	PriorityQueue<Pair<K>> minHeap;
	private final ExecutorService executor = Executors.newCachedThreadPool();
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private final long expirationTimeMillis = TimeUnit.SECONDS.toMillis(5);

	public LFUCache(int size) {
		this.cacheSize = size;
		this.cache = new HashMap<>();
		this.minHeap = new PriorityQueue<>((a, b) -> a.frequency - b.frequency);
		// Schedule method, init delay, interval, unit
		scheduler.scheduleAtFixedRate(this::expireEntries, expirationTimeMillis, expirationTimeMillis,
				TimeUnit.MILLISECONDS);
		System.out.println("LFU Cache initialized wiht size: " + cacheSize);
	}

	public synchronized void cancelExpirationTask() {
		scheduler.shutdown();
	}

	private synchronized void expireEntries() {
		// Remove expired entry in map
		synchronized (cache) {
			minHeap.forEach(pair -> {
				if (isEntryExpired(pair)) {
					cache.remove(pair.getKey());
				}
			});
		}
		// Remove expired entry in pq
		synchronized (minHeap) {
			minHeap.removeIf(pair -> {
				boolean expired = isEntryExpired(pair);
				return expired;
			});
			System.out.println("Expired cache entries removed. Remaining entries: "
					+ (cache.isEmpty() ? "Cache is empty." : cache.entrySet()));
		}
	}

	private boolean isEntryExpired(Pair<K> pair) {
		return (System.currentTimeMillis() - pair.getCreationTime()) > expirationTimeMillis;
	}

	public synchronized void insert(K key, V value) {
		if (cache.size() == cacheSize) {
			evictEntry();
		}
		cache.put(key, value);
		Pair<K> newPair = new Pair<>(key, 1);
		minHeap.offer(newPair);
		System.out.println("Cache block inserted: " + key + " , " + value);
	}

	public synchronized void evictEntry() {
		if (!minHeap.isEmpty()) {
			Pair<K> minHeapPair = minHeap.poll();
			K keyToRemove = minHeapPair.getKey();
			V valueToRemoved = cache.get(keyToRemove);
			cache.remove(keyToRemove);
			System.out.println("Cache block removed:" + keyToRemove + " , " + valueToRemoved);
		} else {
			System.out.println("Cache is empty, no entries in minHeap to evict.");
		}
	}

	public synchronized void put(K key, V value) {
		if (key == null) {
			throw new IllegalArgumentException("Key cannot be null");
		}
		if (!cache.containsKey(key)) {
			insert(key, value);
		} else {
			update(key, value);
		}
	}

	public synchronized void update(K key, V value) {
		if (key == null) {
			throw new IllegalArgumentException("Key cannot be null");
		}

		if (cache.containsKey(key)) {
			// Update map
			cache.put(key, value);

			// Update pq
			Pair<K> updatedPair = null;
			for (Pair<K> pair : minHeap) {
				if (pair.getKey().equals(key)) {
					pair.update();
					updatedPair = pair;
					break;
				}
			}
			minHeap.remove(updatedPair);
			minHeap.offer(updatedPair);
			System.out.println("Cache block updated key: " + key + ", value: " + value + ", freq: "
					+ updatedPair.getFrequency() + ", create: " + updatedPair.getCreationTime());
		}
	}

	public void printCacheEntries() {
		System.out.println("Cache entries:");
		for (Map.Entry<K, V> entry : cache.entrySet()) {
			System.out.println(entry.getKey() + " : " + entry.getValue());
		}
	}

	public void printPriorityQueueEntries() {
		System.out.println("Priority queue entries:");
		for (Pair<K> pair : minHeap) {
			System.out.println(pair.getKey() + " : freq" + pair.getFrequency());
		}
	}

	public void printCacheAndPriorityQueueEntries() {
		printCacheEntries();
		printPriorityQueueEntries();
	}

	public V get(K key) {
		V value = cache.get(key);	// Allows value to be null
		executor.submit(() -> {
			synchronized (this) {
				try {
					updateMinHeap(key);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		return value;
	}

	public synchronized void updateMinHeap(K key) {
		for (Pair<K> pair : minHeap) {
			if (pair.getKey().equals(key)) {
				pair.update();
				minHeap.remove(pair);
				minHeap.offer(pair);
				break;
			}
		}
	}

}
