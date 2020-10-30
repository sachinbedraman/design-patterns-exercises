package org.sachin.pooling;

public class ConnectionPoolTest {
	public static void main(String[] args) throws Exception {

		ConnectionPool pool = new ConnectionPool(3, 1, 5);

		
		Connection borrowFromPool = pool.borrowFromPool();		
		pool.borrowFromPool();
		pool.borrowFromPool();
		pool.borrowFromPool();
		pool.borrowFromPool();

		pool.borrowFromPool();

		//pool.returnToPool(borrowFromPool);
		 

		//pool.reducePoolSizeToIdleConnections();
		
	}
}
