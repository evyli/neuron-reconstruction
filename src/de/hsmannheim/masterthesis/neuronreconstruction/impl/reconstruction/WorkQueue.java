/** WorkQueue.java
 *
 * Copyright (C) 2016 Leah Lackner
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package de.hsmannheim.masterthesis.neuronreconstruction.impl.reconstruction;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Class to implement the parallel computation. It uses the java.util.concurrent
 * Classes as underlying implementation, i.e. the ExecutorService instead of
 * implementing the multi-threading with threads directly.
 * 
 * @author Leah Lackner
 */
public class WorkQueue {

	private ExecutorService executorService;
	private Integer tasks;
	private int workerNumber;

	/**
	 * Creates a new worker queue to handle tasks in a threadpool.
	 */
	public WorkQueue() {
		tasks = 0;

		workerNumber = (int) (Runtime.getRuntime().availableProcessors() * 1.4);

		long keepAliveTime = 60;

		BlockingQueue<Runnable> queue = new LinkedBlockingDeque<>();

		executorService = new ThreadPoolExecutor(workerNumber, workerNumber, keepAliveTime, TimeUnit.SECONDS, queue);
	}

	private synchronized void setTasks(int inc) {
		tasks += inc;
		this.notify();
	}

	/**
	 * Adds a new task to the execution queue.
	 * 
	 * @param r
	 */
	public void execute(Runnable r) {
		setTasks(+1);
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				try {
					r.run();
				} finally {
					setTasks(-1);
				}
			}
		});
	}

	/**
	 * @return The number of all tasks in the queue.
	 */
	public int size() {
		return tasks;
	}

	/**
	 * @return The number of all workers threads in the executor.
	 */
	public int getNumberOfWorker() {
		return workerNumber;
	}

	/**
	 * Shutdown the executorservice.
	 */
	public synchronized void shutdown() {
		executorService.shutdown();
	}

	/**
	 * Wait for all tasks to be finished. Similar to Thread.join()
	 */
	public void waitForEndOfWork() {
		synchronized (this) {
			while (tasks != 0) {
				try {
					this.wait();
				} catch (InterruptedException ignored) {
					break;
				}
			}
		}
	}
}
