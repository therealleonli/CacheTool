package cache;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class LFUCache<K, V> {
	private Map<K, Pair<Integer, V>> cache; // key, <frequency, value>
	private Map<Integer, HashSet<K>> frequencies; // frequency, keys
	private int cacheSize;
	private int minf;
	private long ttl;

	public LFUCache(int size, long ttl) {
		if (size <= 0 || ttl <= 0) {
			throw new IllegalArgumentException("Cache size and TTL must be positive.");
		}
		this.cache = new LinkedHashMap<>();
		this.frequencies = new HashMap<>();
		this.cacheSize = size;
		this.minf = 0;
		this.ttl = ttl;
		System.out.println("LFU Cache initialized. Size: " + cacheSize);
	}

	public V get(K key) {
		Pair<Integer, V> cacheEntry = cache.get(key);
		if (cacheEntry == null)
			return null;

		// Remove key in key set of current frequency
		int frequency = cacheEntry.getFrequency();
		Set<K> keys = frequencies.get(frequency);
		keys.remove(key);
		// If key set empty with no remaining keys, remove the empty set
		if (keys.isEmpty()) {
			frequencies.remove(frequency);
			// If entry and key set removed was minf, increase minf to match current
			if (minf == frequency) {
				++minf;
			}
		}

		// Update new frequency in frequencies and cache
		int newFrequency = frequency + 1;
		frequencies.computeIfAbsent(newFrequency, k -> new LinkedHashSet<>()).add(key);
		cacheEntry.setFrequency(newFrequency);

		return cacheEntry.getValue();
	}

	public void put(K key, V value) {
		System.out.println("Put operation. " + key + " : " + value);
		if (key == null || value == null) {
			throw new IllegalArgumentException("Key and value cannot be null.");
		}
		if (cacheSize <= 0)
			return;
		Pair<Integer, V> frequencyAndValue = cache.get(key);
		if (frequencyAndValue != null) {
			// Entry exists, update value and increase frequency
			updateExistingEntry(key, frequencyAndValue, value);
			return;
		}
		if (cacheIsFull()) {
			removeLFUEntry();
		}
		minf = 1; // Set min frequency to 1 for new entry
		insert(key, 1, value);
	}

	private void insert(K key, int frequency, V value) {
		if (key == null || value == null) {
			throw new IllegalArgumentException("Key and value cannot be null.");
		}
		// Insert new entry in cache
		Pair<Integer, V> newEntry = new Pair<>(frequency, value);
		cache.put(key, newEntry);
		// Update frequencies map
		HashSet<K> keys = frequencies.get(frequency);
		if (keys == null) {
			keys = new LinkedHashSet<>();
			frequencies.put(frequency, keys);
		}
		keys.add(key);
	}

	private void updateExistingEntry(K key, Pair<Integer, V> frequencyAndValue, V value) {
		cache.put(key, new Pair<>(frequencyAndValue.getFrequency(), value));
		get(key); // Update frequency, +1
	}

	private void removeLFUEntry() {
		// Cache is full, remove LFU entry
		Set<K> keys = frequencies.get(minf);
		K keyToDelete = keys.iterator().next();
		cache.remove(keyToDelete);
		keys.remove(keyToDelete);
		System.out.println("Removed: " + keyToDelete);
		if (keys.isEmpty()) {
			frequencies.remove(minf);
		}
	}

	private boolean cacheIsFull() {
		return cacheSize == cache.size();
	}

	public void expireEntries() {
		Iterator<Entry<K, Pair<Integer, V>>> iterator = cache.entrySet().iterator();
		System.out.println("\nRunning automatic entries removal - removes entries if expired ...");
		int lowestExpiredFrequency = Integer.MAX_VALUE;
		while (iterator.hasNext()) {
			Entry<K, Pair<Integer, V>> oldestEntry = iterator.next();
			if (!isExpired(oldestEntry)) {
				break;
			}
			lowestExpiredFrequency = processExpiredEntry(iterator, oldestEntry, lowestExpiredFrequency);
		}
		updateMinFrequency(lowestExpiredFrequency);
		printCacheAndFrequenciesEntries();
	}

	private int processExpiredEntry(Iterator<Entry<K, Pair<Integer, V>>> iterator,
			Entry<K, Pair<Integer, V>> oldestEntry, int lowestExpiredFrequency) {
		int frequency = oldestEntry.getValue().getFrequency();
		lowestExpiredFrequency = Math.min(lowestExpiredFrequency, frequency);
		Set<K> keys = frequencies.get(frequency);
		K keyToRemove = oldestEntry.getKey();
		// Remove entry from iterator, cache, frequencies
		iterator.remove();
		cache.remove(keyToRemove);
		keys.remove(keyToRemove);
		if (keys.isEmpty())
			frequencies.remove(frequency);
		if (cache.isEmpty() && frequencies.isEmpty())
			minf = 0;
		System.out.println("Removed expired entry: " + oldestEntry.getKey() + ":" + oldestEntry.getValue().getValue());
		return lowestExpiredFrequency;
	}

	private void updateMinFrequency(int lowestExpiredFrequency) {
		if (lowestExpiredFrequency <= minf) {
			// If last entry with minf removed from LRU, find new minf
			int newLowest = Integer.MAX_VALUE;
			for (Entry<Integer, HashSet<K>> entry : frequencies.entrySet()) {
				int frequency = entry.getKey();
				if (frequency < newLowest) {
					newLowest = frequency;
				}
			}
			minf = newLowest;
		}
	}

	private boolean isExpired(Entry<K, Pair<Integer, V>> oldestEntry) {
		Date now = new Date();
		long elapsedTimeMillis = now.getTime() - oldestEntry.getValue().getCreationTime().getTime();
		return elapsedTimeMillis > ttl;
	}

	public void printCacheEntries() {
		System.out.println("Cache entries:");
		for (Entry<K, Pair<Integer, V>> entry : cache.entrySet()) {
			K key = entry.getKey();
			Pair<Integer, V> pair = entry.getValue();
			System.out.println(key + " : frequency = " + pair.getFrequency() + ", value = " + pair.getValue());
		}
	}

	public void printFrequenciesEntries() {
		System.out.println("Frequencies entries:");
		for (Entry<Integer, HashSet<K>> entry : frequencies.entrySet()) {
			Integer frequency = entry.getKey();
			HashSet<K> keys = entry.getValue();
			System.out.println("Frequency " + frequency + " : " + keys);
		}
	}

	public void printMinf() {
		System.out.println("minf:" + minf);
	}

	public void printCacheAndFrequenciesEntries() {
		printCacheEntries();
		printFrequenciesEntries();
		printMinf();
	}

}