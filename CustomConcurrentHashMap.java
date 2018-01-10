package com.custom.classes;

import java.util.concurrent.locks.ReentrantLock;


@SuppressWarnings({ "unchecked", "rawtypes", "serial", "hiding" })
public class CustomConcurrentHashMap<K, V> {

	final static Integer DEFAULT_CONCURRENCY_LEVEL = 16;
	int size = 0;
	Segment[] segments;

	class Segment<K, V> extends ReentrantLock {
		HashEntry table[];

		public HashEntry[] getTable() {
			return table;
		}

		public void setTable(HashEntry[] table) {
			this.table = table;
		}

	}

	class HashEntry<K, V> {
		K key;
		V value;
		int hash;
		HashEntry<K, V> next;

		public HashEntry(K key, V value, int hash) {
			super();
			this.key = key;
			this.value = value;
			this.hash = hash;
			this.next = null;
		}

		public K getKey() {
			return key;
		}

		public void setKey(K key) {
			this.key = key;
		}

		public V getValue() {
			return value;
		}

		public void setValue(V value) {
			this.value = value;
		}

		public int getHash() {
			return hash;
		}

		public void setHash(int hash) {
			this.hash = hash;
		}

		public HashEntry<K, V> getNext() {
			return next;
		}

		public void setNext(HashEntry<K, V> next) {
			this.next = next;
		}

	}

	public CustomConcurrentHashMap() {
		this.segments = new Segment[DEFAULT_CONCURRENCY_LEVEL];
		initailzeSegementArray();
	}

	public void initailzeSegementArray() {
		for (int i = 0; i < this.segments.length; i++) {
			HashEntry[] table = new HashEntry[DEFAULT_CONCURRENCY_LEVEL];
			this.segments[i] = new Segment<K, V>();
			this.segments[i].setTable(table);
		}
	}

	public CustomConcurrentHashMap(int concurrencyLevel) {
		int segementArraySize = getSegmentArraySize(concurrencyLevel);
		this.segments = new Segment[segementArraySize];
		initailzeSegementArray();
	}

	public int getSegmentArraySize(int concurrencyLevel) {
		int segementArraySize = 1;
		while (segementArraySize <= concurrencyLevel) {
			segementArraySize = segementArraySize * 2;
		}
		return segementArraySize;
	}

	public V put(K key, V value) {
		if (key == null || value == null) {
			throw new NullPointerException();
		}
		// get the segement location in array of segments
		int hash = Math.abs(key.hashCode() % segments.length);
		Segment segement = segments[hash];
		return put(key, value, segement);
	}

	public V put(K key, V value, Segment segement) {
		segement.lock();
		try {
			int hash = Math.abs(key.hashCode() % segement.table.length);
			HashEntry entry = new HashEntry(key, value, hash);
			// If bucket is not having any key
			if (segement.table[hash] == null) {
				segement.table[hash] = entry;
			} else {
				HashEntry temp = segement.table[hash];
				while (temp.getNext() != null) {
					// If already Key is present
					if (temp.getHash() == hash
							&& ((temp.getKey().equals(key)) || temp.getKey() == key)) {
						V oldValue = (V) temp.getValue();
						temp.setValue(value);
						return oldValue;
					}
					temp = temp.getNext();
				}
				temp.setNext(entry);

			}
			size++;
			return value;
		} finally {
			segement.unlock();
		}
	}

	public V putIfAbsent(K key , V value) {
		if(this.get(key) == null ) {
			return put(key,value);
		} 
		return this.get(key);
	}
	
	public V get(K key) {
		if (key == null) {
			throw new NullPointerException();
		}
		int hash = Math.abs(key.hashCode() % segments.length);
		Segment segment = segments[hash];
		return get(key, segment);
	}

	public V get(K key, Segment segment) {
		int hash = Math.abs(key.hashCode() % segment.table.length);
		HashEntry<K, V> temp = segment.table[hash];
		while (temp != null) {
			// If already Key is present
			if (temp.getHash() == hash
					&& (temp.getKey().equals(key) || temp.getKey() == key)) {
				return (V) temp.getValue();
			}
			temp = temp.getNext();
		}
		return null;
	}

	public String toString() {
		StringBuffer s = new StringBuffer();
		int count =0;
		s.append("{");
		for (int i = 0; i < segments.length; i++) {
			Segment currentSegment = segments[i];
			for (int j = 0; j < currentSegment.table.length; j++) {
				for (HashEntry<K, V> temp = currentSegment.table[j]; temp != null; temp = temp.next) {
					if (temp != null) {
						s = s.append( temp.getKey()
								+ "=" + temp.getValue());
						if(++count != this.size) {
						  s.append(",");
						}
					}
				}
			}
		}
		s.append("}");
		return s.toString();
	}
}
