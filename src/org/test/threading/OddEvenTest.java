package org.test.threading;

public class OddEvenTest {
	public static void main(String[] args) throws InterruptedException {
		Printer p = new Printer();

		Thread odd = new Thread(new OddThread(p));
		odd.start();

		Thread.sleep(1000);
		Thread even = new Thread(new EvenThread(p));
		even.start();

		//odd.join();
		//even.join();
	}
}
