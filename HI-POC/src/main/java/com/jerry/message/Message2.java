package com.jerry.message;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Message2 implements ByteDataMap{
	Map<String,Object> header,body;
	MessageDefiner definer;
	public Message2(MessageDefiner definer) {
		this.definer=definer;
		this.header = new LinkedHashMap<>();
		this.body = new LinkedHashMap<>();
		Function<String,byte[]> getDefaultFeildValue=definer::getDefaultFeildValue;
		
		Consumer<String> defaultMessagePutAction =feildName-> header.put(feildName, getDefaultFeildValue.andThen(String::new).apply(feildName));
		this.definer.getFeildNameIterator().forEachRemaining(defaultMessagePutAction);
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	private Predicate<String> isHeaderContainKey=candidateKey->header.containsKey(candidateKey);
	private Predicate<String> isBodyContainKey=candidateKey->body.containsKey(candidateKey);
	
	@Override
	public void put(String key, byte[] data) {
		// TODO Auto-generated method stub
		if(isHeaderContainKey.test(key)){
			header.put(key, data);
		}else if(isBodyContainKey.test(key)){
			body.put(key,data);
		}else{
			throw new RuntimeException(String.format("존재하지 않는 key[%s]",key));
		}
			
	}

	@Override
	public byte[] get(String key) {
		byte[] result;
		if(isHeaderContainKey.test(key)){
			result = (byte[]) header.get(key);
		}else if(isBodyContainKey.test(key)){
			result = (byte[]) body.get(key);
		}else{
			throw new RuntimeException(String.format("존재하지 않는 key[%s]",key));
		}
		return null;
	}

	public Object getDefaultMessage() {
		return header;
	}
}
