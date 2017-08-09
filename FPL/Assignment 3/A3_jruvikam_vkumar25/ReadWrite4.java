import java.util.Random;
import java.util.concurrent.Semaphore;

public class ReadWrite4 {

	public static void main(String[] args) {

		Database d = new Database();

		Writer w1 = new Writer(d, 1);
		Writer w2 = new Writer(d, 10);
		Writer w3 = new Writer(d, 100);
		Writer w4 = new Writer(d, 1000);
		Writer w5 = new Writer(d, 10000);
		Reader r1 = new Reader(d);
		Reader r2 = new Reader(d);
		Reader r3 = new Reader(d);
		Reader r4 = new Reader(d);
		Reader r5 = new Reader(d);
		Reader r6 = new Reader(d);
		Reader r7 = new Reader(d);
		Reader r8 = new Reader(d);
		Reader r9 = new Reader(d);
		Reader r10 = new Reader(d);

		w1.start();
		r1.start();
		r2.start();
		w2.start();
		r3.start();
		r4.start();
		w3.start();
		r5.start();
		r6.start();
		w4.start();
		r7.start();
		r8.start();
		w5.start();
		r9.start();
		r10.start();

	}
}

class Database {
	int data = 0;
	int r = 0;
	int w = 0;
	int wr = 0;
	int ww = 0;

	Semaphore done_write = new Semaphore(1);
	Semaphore done_read = new Semaphore(1);
	Semaphore request_read = new Semaphore(1);
	Semaphore request_write = new Semaphore(1);
	Semaphore write = new Semaphore(1);
	Semaphore s2 = new Semaphore(0);

	protected boolean allowReader() {
		return ww == 0 && w == 0;
	}

	protected boolean allowWriter() {
		return (r == 0) && (w == 0);
	}

	public void request_read() {
		try {
			request_read.acquire();
			++wr;
			while (!allowReader()) {
				request_read.release();
				s2.acquire();
				request_read.acquire();
			}
			++r;
			--wr;
			request_read.release();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int read() {
		try {
			Thread.sleep(50);
		} catch (Exception e) {
		}
		return data;
	}

	public void done_read() {
		try {
			done_read.acquire();
			--r;
			s2.release(s2.getQueueLength());
			done_read.release();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void request_write() {
		try {
			request_write.acquire();
			++ww;
			while (!allowWriter()) {
				request_write.release();
				s2.acquire();
				request_write.acquire();
			}
			++w;
			--ww;
			request_write.release();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void write(int x) {
		try {
			write.acquire();
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
			}

			data = data + x;
			write.release();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void done_write() {
		try {
			done_write.acquire();
			--w;
			s2.release(s2.getQueueLength());
			done_write.release();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

class Reader extends Thread {
	Database d;

	public Reader(Database d) {
		this.d = d;
	}

	public void run() {

		for (int i = 0; i < 5; i++) {
			try {
				Thread.sleep(new Random().nextInt(750));
			} catch (InterruptedException e) {
			}

			d.request_read();
			System.out.println(d.read());
			d.done_read();

		}
	}
}

class Writer extends Thread {
	Database d;
	int x;

	public Writer(Database d, int x) {
		this.d = d;
		this.x = x;
	}

	public void run() {
		for (int i = 0; i < 5; i++) {
			try {
				Thread.sleep(new Random().nextInt(200));
			} catch (InterruptedException e) {
			}
			d.request_write();
			d.write(x);
			d.done_write();
		}
	}
}
