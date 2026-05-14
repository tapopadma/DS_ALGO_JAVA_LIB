package design;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

interface Throttler {
	boolean throttle(int user);
}

class SlidingWindowThrottler implements Throttler {
	static final int WINDOW_SIZE_MSEC=1000;
	static final int CAPACITY=2;
	
	Map<Integer, Deque<Long>> requests;
	
	public SlidingWindowThrottler() {
		requests = new HashMap<Integer, Deque<Long>>();
	}
	
	@Override
	public boolean throttle(int user) {
		requests.putIfAbsent(user, new ArrayDeque<>());
		long now = System.currentTimeMillis();
		Deque<Long> log = requests.get(user);
		while(!log.isEmpty() && log.peekFirst()<now-WINDOW_SIZE_MSEC) {
			log.pollFirst();
		}
		if(log.size()>=CAPACITY) {
			return true;
		}
		log.add(now);
		requests.put(user, log);
		return false;
	}
	
}

class TokenBucket {
	long lastRefillTime;
	int tokens;
	public TokenBucket(long l, int t) {
		lastRefillTime=l;
		tokens=t;
	}
}

class TokenBucketThrottler implements Throttler {
	static final int REFILL_RATE_SEC=1;
	static final int CAPACITY=2;
	
	Map<Integer, TokenBucket> buckets;
	
	public TokenBucketThrottler() {
		buckets = new HashMap<>();
	}

	@Override
	public boolean throttle(int user) {
		long now = System.currentTimeMillis();
		buckets.putIfAbsent(user, new TokenBucket(now, CAPACITY));
		TokenBucket bucket = buckets.get(user);
		bucket.tokens += (int)(1.0*(now-bucket.lastRefillTime)*REFILL_RATE_SEC/1000.0);
		bucket.tokens = Math.min(bucket.tokens, CAPACITY);
		bucket.lastRefillTime=now;
		boolean throttled = true;
		if(bucket.tokens > 0) {
			bucket.tokens--;
			throttled = false;
		}
		buckets.put(user, bucket);
		return throttled;
	}
	
}

class ThrottlerFactory {
	
	static final String SLIDING_WINDOW = "SlidingWindow";
	static final String TOKEN_BUCKET = "TokenBucket";
	
	SlidingWindowThrottler slidingWindowThrottler;
	TokenBucketThrottler tokenBucketThrottler;
	
	public ThrottlerFactory() {
		slidingWindowThrottler = new SlidingWindowThrottler();
		tokenBucketThrottler = new TokenBucketThrottler();
	}
	
	public Throttler buildThrottler(ThrottlerConfig config) {
		String throttler = config!=null?config.throttler:"";
		switch(throttler) {
		case SLIDING_WINDOW:
			return slidingWindowThrottler;
		case TOKEN_BUCKET:
			return tokenBucketThrottler;
		default:
			return tokenBucketThrottler;
		}
	}
}

class ThrottlerConfig {
	String throttler;
	ThrottlerConfig(String throttler) {
		this.throttler=throttler;
	}
}

class RateLimiter {
	Map<String, ThrottlerConfig> configs;
	ThrottlerFactory factory;
	
	RateLimiter() {
		configs = new HashMap<>();
		factory = new ThrottlerFactory();
	}
	
	public void addConfig(String endpoint, ThrottlerConfig config) {
		configs.put(endpoint, config);
	}
	
	public boolean allow(int userId, String endpoint) {
		endpoint=endpoint==null?"":endpoint;
		ThrottlerConfig config = configs.getOrDefault(endpoint,null);
		return !factory.buildThrottler(config).throttle(userId);
	}
}

package design;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.*;
import org.junit.jupiter.api.*;

class DesignTest {
	
	
	@Test
	public void testDefault() throws Exception {
		RateLimiter limiter = new RateLimiter();
		assertTrue(limiter.allow(1, null));
		assertTrue(limiter.allow(1, null));
		assertFalse(limiter.allow(1, null));
		assertTrue(limiter.allow(2, null));
	}
	
	@Test
	public void testSlidingWindow() throws Exception {
		RateLimiter limiter = new RateLimiter();
		limiter.addConfig("ListLocation", new ThrottlerConfig(ThrottlerFactory.SLIDING_WINDOW));
		assertTrue(limiter.allow(1, "ListLocation"));
		assertTrue(limiter.allow(1, "ListLocation"));
		assertFalse(limiter.allow(1, "ListLocation"));
	}
	
	@Test
	public void testTokenBucket() throws Exception {
		RateLimiter limiter = new RateLimiter();
		limiter.addConfig("ListPrincipal", new ThrottlerConfig(ThrottlerFactory.TOKEN_BUCKET));
		assertTrue(limiter.allow(1, "ListPrincipal"));
		assertTrue(limiter.allow(1, "ListPrincipal"));
		assertFalse(limiter.allow(1, "ListPrincipal"));
	}
	
}
