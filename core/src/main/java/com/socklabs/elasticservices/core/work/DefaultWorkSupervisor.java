package com.socklabs.elasticservices.core.work;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.socklabs.elasticservices.core.collection.Pair;
import org.springframework.beans.factory.DisposableBean;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DefaultWorkSupervisor implements WorkSupervisor, DisposableBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultWorkSupervisor.class);

	private final ExecutorService workThreadPool;

	private final List<Work> preStartWork;

	private final Map<String, Pair<Work, Future>> activeWork;

	private final AtomicBoolean hasStarted = new AtomicBoolean(false);

	private final AtomicBoolean hasShutdown = new AtomicBoolean(false);

	// NKG: A read/write lock is used to wrap the adding of work, start and shutdown methods.
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	public DefaultWorkSupervisor(final int workThreadPoolSize) {
		this.workThreadPool = Executors.newFixedThreadPool(workThreadPoolSize);
		this.preStartWork = Lists.newLinkedList();
		this.activeWork = Maps.newHashMap();
	}

	public void addWork(final Work work) {
		try {
			LOGGER.info("Read lock");
			lock.readLock().lock();
			if (hasShutdown.get()) {
				// NKG: Prevent any work from being added once the work supervisor has been asked to shutdown.
				return;
			}
			if (!hasStarted.get()) {
				preStartWork.add(work);
			} else {
				try {
					LOGGER.info("Write lock");
					lock.readLock().unlock();
					lock.writeLock().lock();
					final Future workFuture = workThreadPool.submit(new WorkRunnable(work));
					activeWork.put(work.getId(), Pair.create(work, workFuture));
				} finally {
					LOGGER.info("Write unlock");
					lock.readLock().lock();
					lock.writeLock().unlock();
				}
			}
		} finally {
			LOGGER.info("Read unlock");
			lock.readLock().unlock();
		}
	}

	@Override
	public void start() {
		try {
			lock.writeLock().lock();
			if (hasStarted.compareAndSet(false, true)) {
				for (final Work work : preStartWork) {
					workThreadPool.submit(new WorkRunnable(work));
				}
			}
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public void stop() {
		try {
			lock.writeLock().lock();
			for (final Map.Entry<String, Pair<Work, Future>> entry : activeWork.entrySet()) {
				final Work work = entry.getValue().getA();
				work.stop();
			}
			if (hasShutdown.compareAndSet(false, true)) {
				workThreadPool.shutdown();
			}
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public void destroy() throws Exception {
		stop();
	}

	private static class WorkRunnable implements Runnable {

		private final Work work;

		private WorkRunnable(final Work work) {
			this.work = work;
		}

		@Override
		public void run() {
			LOGGER.info("Starting work {}", work);
			work.run();
		}
	}

}
