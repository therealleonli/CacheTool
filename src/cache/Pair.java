package cache;

import java.util.Date;

public class Pair<K, V> {
	K frequency;
	V value;
	private Date creationTime;

	public Pair(K frequency, V value) {
		this.frequency = frequency;
		this.value = value;
		this.creationTime = new Date();
	}

	public K getFrequency() {
		return frequency;
	}

	public void setFrequency(K frequency) {
		this.frequency = frequency;
	}

	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
	}
	
	public Date getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}

	
}