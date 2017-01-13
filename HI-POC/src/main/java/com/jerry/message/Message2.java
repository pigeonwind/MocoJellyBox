package com.jerry.message;

import java.util.Map;

public class Message2 implements ByteDataMap{
	Map<String,byte[]> header,body;
	MessageDefiner definer;
	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void put(String key, byte[] data) {
		// TODO Auto-generated method stub
	}

	@Override
	public byte[] get(String key) {
		// TODO Auto-generated method stub
		return null;
	}

}
