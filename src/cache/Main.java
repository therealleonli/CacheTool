package cache;

import java.util.concurrent.TimeUnit;

public class Main {

	public static void main(String[] args) {
		// Test-cases for the cache is defined and called in the main() function for
		// demonstration purposes
		basicOperationsTest();

		evictionTest();

		expirationTest();

	}

	private static void basicOperationsTest() {
		System.out.println("Basic Operations Test:");
		LFUCache<String, Integer> LFUCache = createAndPopulateCache();
		LFUCache.printCacheAndPriorityQueueEntries();
		System.out.println("Update value");
		LFUCache.put("Key3", 5);
		LFUCache.printCacheAndPriorityQueueEntries();
		LFUCache.cancelExpirationTask();
		System.out.println();
	}

	private static void evictionTest() {
		System.out.println("Evicts LFU Test: ");
		LFUCache<String, Integer> LFUCache = createAndPopulateCache();
		LFUCache.put("Key3", 6);
		LFUCache.put("Key2", 7);
		LFUCache.put("Key2", 9);
		LFUCache.put("Key5", 1);
		LFUCache.put("Key5", 8);
		LFUCache.get("Key2");
		LFUCache.printCacheAndPriorityQueueEntries();
		LFUCache.put("Key6", 9);
		LFUCache.printCacheAndPriorityQueueEntries();
		LFUCache.cancelExpirationTask();
		System.out.println();
	}

	private static void expirationTest() {
		System.out.println("Expiration Test: ");
		System.out.println("Scheduled Executor Service running periodically...");
		LFUCache<String, Integer> LFUCache = createAndPopulateCache();
		sleepSeconds(3);
		LFUCache.put("Key5", 5);
		LFUCache.put("Key6", 6);
		sleepSeconds(3);
		LFUCache.put("Key7", 7);
		sleepSeconds(10);
		System.out.println("Finished");
		LFUCache.cancelExpirationTask();
		System.out.println();
	}

	private static void sleepSeconds(int seconds) {
		System.out.println("Sleeping for " + seconds + " seconds...");
		try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static LFUCache<String, Integer> createAndPopulateCache() {
		LFUCache<String, Integer> LFUCache = new LFUCache<>(4);
		LFUCache.put("Key1", 1);
		LFUCache.put("Key2", 2);
		LFUCache.put("Key3", 3);
		LFUCache.put("Key4", 4);
		return LFUCache;
	}
}
