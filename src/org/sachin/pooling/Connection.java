package org.sachin.pooling;

public interface Connection {

	void open();
	String read();
	void close();
	
}
