package io.netty.example.common;

public interface ICounter {

	public Double getMovingAvg();

	void add(Double o);
	
	void add();
}
