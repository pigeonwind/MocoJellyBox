package com.jerry.message;

public interface ByteDataMap {
	int size();
	void put(String key,byte[] data);
	byte[] get(String key);
}
