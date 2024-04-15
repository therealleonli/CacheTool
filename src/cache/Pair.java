package cache;

public class Pair<K> {
	K key;
	int frequency;
	long lastAccessTime;
	private long creationTime;

	public Pair(K key, int frequency) {
		this.key = key;
		this.frequency = frequency;
		this.lastAccessTime = System.currentTimeMillis();
		this.creationTime = System.currentTimeMillis();
	}

	public void update() {
		this.frequency += 1;
		this.lastAccessTime = System.currentTimeMillis();
	}

	public K getKey() {
		return key;
	}

	public void setKey(K key) {
		this.key = key;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public long getLastAccessTime() {
		return lastAccessTime;
	}

	public void setLastAccessTime(long lastAccessTime) {
		this.lastAccessTime = lastAccessTime;
	}

	public long getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}

}
