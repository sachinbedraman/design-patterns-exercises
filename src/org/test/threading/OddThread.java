package org.test.threading;

public class OddThread implements Runnable{

	private Printer p;
	int number = 1;

	public OddThread (Printer p) {
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
