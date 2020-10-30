package org.sachin.pooling;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionPool {

	private int initialSize = 1;
	private int idleSize = 5;
	private int maxSize = 10;
	private long waitTimeout = 3000;

	private AtomicInteger usedConnectionCount = new AtomicInteger();
	private AtomicInteger currentPoolsize = new AtomicInteger();

	// Object that represents a lock to synchronize locking implementation
	private Object lock = new Object();

	private Queue<ConnectionProxy> idleConnectionQueue = new LinkedBlockingDeque<>();
	private Queue<ConnectionProxy> usedConnectionQueue = new LinkedBlockingDeque<>();

	private Map<String, Integer> metricsMap = new LinkedHashMap<String, Integer>();

	public ConnectionPool(int initialSize, int idleSize, int maxSize) throws Exception {

		if(initialSize > maxSize) throw new Exception("Initialsize cannot be greater than maxSize");

		this.maxSize = maxSize;
		this.initialSize = initialSize; 
		this.idleSize = idleSize;

		initialize();
	}

	private void initialize() {

		synchronized (lock) {

			for(int i = 0 ; i < initialSize ; i++) {
				idleConnectionQueue.add(createConnection());		
			}

			metricsMap.put("initialSize", initialSize);
			metricsMap.put("idleSize", idleSize);
			metricsMap.put("maxSize", maxSize);
			metricsMap.put("currentPoolsize", currentPoolsize.get());
			metricsMap.put("usedConnectionCount", usedConnectionCount.get());
		}
		
		printMetrics();

	}

	public Connection borrowFromPool() {
		
		long borrowRequestTime = System.currentTimeMillis();

		while(usedConnectionCount.get() == maxSize) {
			System.out.println(Thread.currentThread().getName() + " waiting for connection");
			long currentTime = System.currentTimeMillis();
			
			if((currentTime - borrowRequestTime) > waitTimeout) {
				System.out.println(Thread.currentThread().getName() + " request timed out");
				return null;
			}
		}

		
		CountDownLatch latch = new CountDownLatch(1);
		
		
		ConnectionProxy connection = null;

		synchronized (lock) {
			connection = idleConnectionQueue.poll();

			// Idle queue is currently empty
			// Create a connection 
			if(connection == null) {
				connection = createConnection();
			}

			usedConnectionQueue.add(connection);

			usedConnectionCount.incrementAndGet();

			updateMetrics();

			printMetrics();
		}

		return connection;
	}

	public void returnToPool(Connection connection) {
		synchronized (lock) {

			boolean remove = usedConnectionQueue.remove(connection);

			if(remove) {
				idleConnectionQueue.add((ConnectionProxy)connection);

				// Reduce the used connection count
				usedConnectionCount.decrementAndGet();
				currentPoolsize.decrementAndGet();

				updateMetrics();
			}

			printMetrics();
		}
	}

	public void reducePoolSizeToIdleConnections() {
		synchronized (lock) {
			if(usedConnectionCount.get() > idleSize) {
				// Nothing to be done here as connections are currently in use
			} else {
				// Idle count = total connection - currently used - idle
				int connectionToBeRemoved = currentPoolsize.get() - usedConnectionQueue.size() - idleSize;
				
				System.out.println("connectionToBeRemoved " + connectionToBeRemoved);
				while(connectionToBeRemoved > 0) {
					idleConnectionQueue.remove();
					currentPoolsize.decrementAndGet();
					connectionToBeRemoved--;
				}
				
				updateMetrics();
			}
		}
		
		printMetrics();
	}
	
	private void updateMetrics() {
		metricsMap.put("currentPoolsize", currentPoolsize.get());
		metricsMap.put("usedConnectionCount", usedConnectionCount.get());
	}

	private ConnectionProxy createConnection() {
		currentPoolsize.incrementAndGet();
		return new ConnectionProxy(new ConnectionImpl());
	}

	private void printMetrics() {
		System.out.println(metricsMap);
	}
}
