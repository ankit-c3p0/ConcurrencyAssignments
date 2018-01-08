package concurrent;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomThreadPool {
	
	private static AtomicInteger poolCount = new AtomicInteger(0);
	private LinkedBlockingQueue<Runnable> runnables;
	private AtomicBoolean execute;
	private List<CustomThread> threads;
	

	private class ThreadPoolException extends RuntimeException {

		public ThreadPoolException(Throwable cause) {
			super(cause);
		}
	}
 	
	private class CustomThread extends Thread {

		private AtomicBoolean isExecute;
		private LinkedBlockingQueue<Runnable> runnableTasks;
		
		public CustomThread(String name, AtomicBoolean isExecute, LinkedBlockingQueue<Runnable> runnableTask) {
			super(name);
			this.isExecute = isExecute;
			this.runnableTasks = runnableTask;
		}
		
		@Override
		public void run() {
			
			try {
				while( isExecute.get() || runnableTasks.isEmpty()) {
					
					Runnable runnable;
					while((runnable = runnableTasks.poll()) != null) {
						runnable.run();
					}
					Thread.sleep(1);
				}
			} catch (RuntimeException | InterruptedException e) {
				throw new ThreadPoolException(e);
			}
		}
	}

	
	private CustomThreadPool(int threadCount) {
		
		poolCount.incrementAndGet();
		this.runnables = new LinkedBlockingQueue<>();
		this.execute = new AtomicBoolean(true);
		for(int i = 0; i < threadCount; i++) {
			CustomThread _thread =  new CustomThread("CustomThreadPool"+poolCount.get()+"Thread"+i, this.execute, this.runnables);
			_thread.start();
			this.threads.add(_thread);
		}
	}

	public void execute(Runnable runnable) {
		
		if(this.execute.get()) {
			runnables.add(runnable);
		} else {
			throw new IllegalStateException();
		}
	}
	
	public void awaitTermination(long timeout) throws TimeoutException {
		
		if(this.execute.get()) {
			throw new IllegalThreadStateException();
		}
		long startTIme = System.currentTimeMillis();
		while(System.currentTimeMillis() - startTIme <= timeout) {
			boolean flag = true;
			for(Thread _thread: threads) {
				if(_thread.isAlive()) {
					flag = true;
					break;
				}
			}
			if(flag) {
				return;
			}
			try {
				Thread.sleep(1);
			} catch(Exception e) {
				throw new ThreadPoolException(e);
			}
		}
		throw new TimeoutException();
	}

	public void awaitTermination() throws TimeoutException {
		
		if(this.execute.get()) {
			throw new IllegalThreadStateException();
		}
		long startTIme = System.currentTimeMillis();
		while(true) {
			boolean flag = true;
			for(Thread _thread: threads) {
				if(_thread.isAlive()) {
					flag = true;
					break;
				}
			}
			if(flag) {
				return;
			}
			try {
				Thread.sleep(1);
			} catch(Exception e) {
				throw new ThreadPoolException(e);
			}
		}
	}

	
	public static CustomThreadPool getInstance() {
        return getInstance(Runtime.getRuntime().availableProcessors());
    }

    public static CustomThreadPool getInstance(int threadCount) {
        return new CustomThreadPool(threadCount);
    }
    
	public void terminate() {
		runnables.clear();
		stop();
	}
	public void stop() {
		execute.set(false);
	}
	
}
