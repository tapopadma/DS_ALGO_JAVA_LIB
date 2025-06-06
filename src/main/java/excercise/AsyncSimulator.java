package src.main.java.excercise;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.Executors;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Arrays;
import org.apache.commons.lang3.tuple.Pair;

import static org.junit.Assert.assertEquals;

// threads, threadpool, threadlocal, future, concurrentDS, synchronised, volatile, fork join, wait notify, lock, atomicDs.
public class AsyncSimulator {

    void simulateThreads() {
		int[] balances = new int[]{1000, 0};
		List<Thread> transactions = new ArrayList<>();
		for(int i=0;i<100;++i) {
			transactions.add(new Thread(() -> {
				synchronized(this) { // lock on the reference to the outer class i.e. the simulator object created in main().
					balances[0] -= 10;
					balances[1] += 10;
				}
				try {
					Thread.sleep(5);// 5 ms
				} catch(InterruptedException e) {

				}
			}));
		}

		for(int i=0;i<100;++i) {
			transactions.get(i).start();
			try {
				transactions.get(i).join();// join will ensure the current thread waits for this thread to complete.
			} catch(InterruptedException e){

			}
		}
		assertEquals(0, balances[0]);
		assertEquals(1000, balances[1]);
	}

	// efficient thread management to manage resource at scale. explicit thread creation is expensive. threadpool comes
	// with upper bound on new thread creation, thread reusability, resource efficiency, dynamic pooling of the threads.
	void simulateThreadPool() {
		Object lock = new Object();
		ExecutorService transactionService = Executors.newFixedThreadPool(5);
		int[] balances = new int[] {1000, 0};
		for(int i=0;i<100;++i) {
			int idx = i;
			transactionService.submit(() -> {
				System.out.println("Entering the " + idx + "th transaction");
				synchronized(lock) { // recommended to use a shared object instead of 'this', but 'this' should also work since it's internally the same instance
					// of task runner that executes the task.
					balances[0] -= 10;
					balances[1] += 10;
				}
				System.out.println("Finished the " + idx + "th transaction");// order of transactions mayn't be guaranteed, but atomicity is guaranteed.
				try {
					Thread.sleep(5);// 5 ms
				} catch(InterruptedException e) {

				}
			});
		}
		transactionService.shutdown();// doesn't stop processing the submitted tasks, just stops accepting any new task.
		try {
			transactionService.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);// wait indefinitely until all tasks submitted 
			// before shutdown() was invoked are complete.
		} catch(InterruptedException e){}
		assertEquals(0, balances[0]);
		assertEquals(1000, balances[1]);
	}

	// ThreadLocal is like a hashmap of thread to its context with multiple concerns like synchronisation, recycling etc addressed by default.
	// So it's a clean way to track information in a thread scope.
	// e.g. incoming requests to a webserver can be tracked in terms of its caller/user in a thread-safe way using threadLocal and can be utilised
	// anywhere in the application during the course of the request. threadLocal should be removed right after the thread is successful to avoid 
	// memory leaks due to lack of garbage collection.
	void simulateThreadLocal() {
		ThreadLocal<String> userId = new ThreadLocal<>();
		ExecutorService server = Executors.newFixedThreadPool(2);
		for(String id: Arrays.asList("alice", "bob")) {
			String user = id;
			server.submit(() -> {
				userId.set(user);
				System.out.println("processing the request from " + userId.get());
				try {
					Thread.sleep(5);// 5ms latency
				} catch(InterruptedException e){}
				System.out.println("served the request from " + userId.get());
				userId.remove();
			});
		}
		server.shutdown();
		try {
			server.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
		} catch(InterruptedException e){}
	}

	void simulateFutures() {
		ExecutorService server = Executors.newFixedThreadPool(2);
		int[] responses = new int[] {-1, -1};
		CompletableFuture<Void> request1 = CompletableFuture.supplyAsync(() -> {
			System.out.println("uploading photo");
			try {
				Thread.sleep(1000);
			} catch(InterruptedException e){}
			int id = 432312298;
			System.out.println("uploaded photo: " + id);
			return id;
		}, server) // fork join pool is for recursive cpu-expensive calls, but threadpool is for io bound independent calls such as db calls, calls to other systems.
		.thenApplyAsync(photoId -> { //thenApplyAsync instead of thenAccept to use server threadpool instead of common fork-join pool.
			System.out.println("updated photo metadata: " + photoId);
			return photoId;
		}, server)
		.thenAcceptAsync(id -> responses[0] = id, server);
		Future<?> request2 = server.submit(() -> {
			System.out.println("fetching favorite photo");
			responses[1] = 678923256;
			System.out.println("fetched favorite photo");
		});
		request1.join();// necessary to ensure that the future is executed before shutdown is initiated.
		server.shutdown();
		try {
			server.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
		} catch(InterruptedException e){}
		assertEquals(432312298, responses[0]);
		assertEquals(678923256, responses[1]);
	}

	void simulateConcurrentHashMap() {
		ConcurrentHashMap<String, Pair<Integer, Integer>> mp = new ConcurrentHashMap<>();
		mp.put("balances", Pair.of(1000, 0));
		ExecutorService pool = Executors.newFixedThreadPool(5);
		for(int i=0;i<100;++i) {
			pool.submit(() -> {
				mp.compute("balances", (k, v) ->
                    Pair.of(v.getLeft() - 10, v.getRight() + 10)
                );// normal get + put won't work since get() method doesn't acquire lock.
			});
		}
		pool.shutdown();
		try {
			pool.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
		} catch(InterruptedException e){}
		assertEquals(0, (int)mp.get("balances").getLeft());
		assertEquals(1000, (int)mp.get("balances").getRight());
	}

	synchronized void transfer(int[] balances, int amount) {
		balances[0] -= amount;
		balances[1] += amount;
	}

	void simulateSynchronised() {
		int[] balances = new int[]{1000, 0};
		ExecutorService pool = Executors.newFixedThreadPool(5);
			for(int i=0;i<50;++i) {
				pool.submit(() -> {
					synchronized(this) {
						balances[0] -= 10;
						balances[1] += 10;
					}
				});
			}
			for(int i=0;i<50;++i) {
				pool.submit(() -> {
					transfer(balances, 10);
				});
			}
			pool.shutdown();
			try {
				pool.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
			} catch(InterruptedException e){}
		
		assertEquals(0, balances[0]);
		assertEquals(1000, balances[1]);
	}

	private static int counter = 2000;

	void simulateAtomicAndLock() {
		AtomicInteger a = new AtomicInteger(0);
		Runnable task = () -> {
            for (int i = 0; i < 1000; i++) {
                a.incrementAndGet();
            }
        };
        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        t1.start(); t2.start();
        try {
        	t1.join(); t2.join();
        } catch(InterruptedException e){}
        assertEquals(2000, a.get());

        Lock lock = new ReentrantLock();
        task = () -> {
            for (int i = 0; i < 1000; i++) {
                lock.lock();
                try {
                    counter--;
                } finally {
                    lock.unlock();
                }
            }
        };

        t1 = new Thread(task);
        t2 = new Thread(task);
        t1.start(); t2.start();
        try {
        	t1.join(); t2.join();
        } catch(InterruptedException e){}
		assertEquals(0, counter);
	}

	private static final Object lock = new Object();
    private static String message = null;

	void simulateWaitNotify() {
        // Consumer Thread
        Thread consumer = new Thread(() -> {
            synchronized (lock) {
                while (message == null) {
                    try {
                        System.out.println("Consumer waiting...");
                        lock.wait(); // releases the lock and waits
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                System.out.println("Consumer received message: " + message);
            }
        });

        // Producer Thread
        Thread producer = new Thread(() -> {
            try {
                Thread.sleep(2000); // Simulate some work
            } catch (InterruptedException e) {}

            synchronized (lock) {
                message = "Hello from Producer!";
                lock.notify(); // wakes up the waiting consumer
                System.out.println("Producer sent message");
            }
        });

        consumer.start();
        producer.start();
        try {
        	consumer.join(); producer.join();
        } catch(InterruptedException e){}
    }

	public static void main(String[] args) {
		AsyncSimulator simulator = new AsyncSimulator();
		simulator.simulateThreads();
		simulator.simulateThreadPool();
		simulator.simulateThreadLocal();
		simulator.simulateFutures();
		simulator.simulateConcurrentHashMap();
		simulator.simulateSynchronised();
		simulator.simulateAtomicAndLock();
		simulator.simulateWaitNotify();
	}
}