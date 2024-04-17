package cache;

public class Main {

	public static void main(String[] args) {
		// Test-cases for the cache in the main() for demonstration purposes
		testBasicOperations();
		testMaxSizeEviction();
		testExpiration();
	}

	private static void testBasicOperations() {
		System.out.println("Test Basic Operations:");
		LFUCache<String, Integer> LFUCache = createAndPopulateCache();
		LFUCache.printCacheAndFrequenciesEntries();
		LFUCache.put("Key3", 5);
		System.out.println("Update Key: " + "Key3");
		LFUCache.printCacheAndFrequenciesEntries();
		sleepSeconds(5);
		System.out.println("\n\n");
	}

	private static void testMaxSizeEviction() {
		System.out.println("Test LFU Evictions: ");
		LFUCache<String, Integer> LFUCache = createAndPopulateCache();
		LFUCache.printCacheAndFrequenciesEntries();
		LFUCache.get("Key1");
		LFUCache.get("Key1");
		LFUCache.get("Key2");
		LFUCache.get("Key4");
		LFUCache.get("Key4");
		System.out.println("Get operation. Key1, Key1, Key2, Key4, Key4");
		System.out.println("\nAdds and updates entries + frequencies. Passes max size.");
		LFUCache.put("Key3", 6);
		LFUCache.put("Key2", 7);
		LFUCache.put("Key2", 9);
		LFUCache.printCacheAndFrequenciesEntries();
		LFUCache.put("Key5", 1);
		LFUCache.printCacheAndFrequenciesEntries();
		LFUCache.get("Key2");
		System.out.println("Get operation. Key2");
		LFUCache.printCacheAndFrequenciesEntries();
		System.out.println("\n\n");
	}

	private static void testExpiration() {
		System.out.println("Test Expiration: ");
		System.out.println("Scheduled Executor Service running periodically...");
		LFUCache<String, Integer> LFUCache = new LFUCache<>(4, 3000);
		LFUCache.put("Key1", 1);
		LFUCache.put("Key2", 2);
		sleepSeconds(2);
		LFUCache.put("Key5", 5);
		LFUCache.put("Key6", 6);
		LFUCache.expireEntries();
		System.out.println("Get operation. Key5, Key5, Key6");
		LFUCache.get("Key5");
		LFUCache.get("Key5");
		LFUCache.get("Key6");
		sleepSeconds(2);
		LFUCache.expireEntries();
		LFUCache.put("Key7", 7);
		System.out.println("Get operation. Key7, Key7, Key7");
		LFUCache.get("Key7");
		LFUCache.get("Key7");
		LFUCache.get("Key7");
		sleepSeconds(2);
		LFUCache.expireEntries();
		System.out.println("Finished");
		System.out.println("\n\n");
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
		LFUCache<String, Integer> LFUCache = new LFUCache<>(4, 3000);
		LFUCache.put("Key1", 1);
		LFUCache.put("Key2", 2);
		LFUCache.put("Key3", 3);
		LFUCache.put("Key4", 4);
		return LFUCache;
	}
}
