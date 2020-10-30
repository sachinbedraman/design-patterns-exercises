package org.test.threading;

public class EvenThread implements Runnable{

	private Printer p;
	int number = 2;

	public EvenThread (Printer p) {
		this.p = p;
	}

	@Override
	public void run() {

		while(true) {
			synchronized (p) {
				
				p.print(number);
				number = number + 2;
				p.notify();
				try {
					p.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
