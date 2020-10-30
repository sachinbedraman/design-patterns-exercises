package org.sachin.pooling;

public class ConnectionProxy implements Connection {

	private Connection connection;
	
	public  ConnectionProxy(Connection connection) {
		this.connection = connection;
	}
	
	@Override
	public void open() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String read() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}
	
	public void closeConnection() {
		
	}

}
