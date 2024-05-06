package cache;

import java.util.Date;
import java.util.Objects;

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

	@Override
	public int hashCode() {
		return Objects.hash(creationTime, frequency, value);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pair other = (Pair) obj;
		return Objects.equals(creationTime, other.creationTime) && Objects.equals(frequency, other.frequency)
				&& Objects.equals(value, other.value);
	}

}