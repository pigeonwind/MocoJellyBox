package com.jerry.util;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class JSONCollector implements Collector<Object, Object, Object> {
	/**
	 * A:Object
	 */
	@Override
	public Supplier<Object> supplier() {
		// TODO Auto-generated method stub
		
		return null;
	}
	/**
	 * A:Object
	 * T:Object
	 */
	@Override
	public BiConsumer<Object, Object> accumulator() {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * A:Object
	 */
	@Override
	public BinaryOperator<Object> combiner() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * A: Object
	 * R: Object
	 */
	@Override
	public Function<Object,Object> finisher() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<java.util.stream.Collector.Characteristics> characteristics() {
		// TODO Auto-generated method stub
		return null;
	}

}
