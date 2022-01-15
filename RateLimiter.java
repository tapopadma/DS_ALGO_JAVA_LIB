import static org.junit.Assert.*;

import java.util.*;
import java.util.stream.*;
import org.apache.commons.lang3.tuple.*;
import org.junit.*;
import org.junit.runner.*;


public class RateLimiter {
	class Request{
		long userId;
		long machineId;
		long requestTime;
	}
	class Server {
		Map<Long,Queue<Long>> requestsByUser;
		Map<Long, Integer> thresholdMap;
		int windowSize;
		public Server(Map<Long, Integer> thresholdMap, int windowSize){
			requestsByUser=new HashMap<>();
			this.windowSize=windowSize;
			this.thresholdMap=thresholdMap;
		}
		public Request buildRequest(long userId
		, long machineId, long requestTime){
			Request r = new Request();
			r.userId=userId;
			r.machineId=machineId;
			r.requestTime=requestTime;
			return r;
		}
		public synchronized boolean accept(Request request){
			long currentRequestTime = request.requestTime;
			Queue<Long> requests = new LinkedList<>();
			long key=request.userId;
			if(requestsByUser.containsKey(key)){
				requests=requestsByUser.get(key);
				while(!requests.isEmpty() 
					&& requests.peek()+windowSize <= currentRequestTime){
					requests.poll();
				}
			}else{
				requests=new LinkedList<>();
			}
			int maxAllowedCount=thresholdMap.get(key);
			if(requests.size()>=maxAllowedCount){
				return false;
			}else{
				requests.add(currentRequestTime);
				requestsByUser.put(key, requests);
				return true;
			}
		}
	}

	class Client extends Thread{
		Request request;
		Server server;
		boolean response;
		public Client(Request request, Server server){
			this.request=request;
			this.server=server;
			this.response=false;
		}
		@Override
		public void run(){
			this.response=server.accept(request);
		}
	}

	@Test
	public void accept_forBoundedRequests_acceptsAll() throws Exception{
		int maxAllowedCount=3;
		int windowSize = 5;
		long userId=1;
		Map<Long, Integer>thresholdMap=new HashMap<>();
		thresholdMap.put(userId, maxAllowedCount);
		Server server=new Server(thresholdMap, windowSize);
		List<Client> requests=Arrays.asList(
			new Client(server.buildRequest(userId, 1,0),server),
			new Client(server.buildRequest(userId, 2,0),server),
			new Client(server.buildRequest(userId, 3,0),server)
			);
		requests.stream().forEach(r -> r.start());
		requests.stream().forEach(r -> {
			try{
				r.join();
			}catch(InterruptedException e){
				System.out.println(e.getMessage());
			}
		});
		assertTrue(requests.stream().noneMatch(r->!r.response));

		requests=Arrays.asList(
			new Client(server.buildRequest(userId, 1,6),server),
			new Client(server.buildRequest(userId, 2,6),server),
			new Client(server.buildRequest(userId, 3,6),server)
			);
		requests.stream().forEach(r -> r.start());
		requests.stream().forEach(r -> {
			try{
				r.join();
			}catch(InterruptedException e){
				System.out.println(e.getMessage());
			}
		});
		assertTrue(requests.stream().noneMatch(r->!r.response));
	}

	@Test
	public void accept_rejectsUnboundedRequests() throws Exception{
		int windowSize = 1;
		Map<Long, Integer>thresholdMap=new HashMap<>();
		thresholdMap.put(1L, 2);thresholdMap.put(2L, 3);thresholdMap.put(3L, 1);
		Server server=new Server(thresholdMap, windowSize);
		List<Request> throttledRequests=new ArrayList<>();
		List<Client> requests=Arrays.asList(
			new Client(server.buildRequest(2, 1,0),server)
			);
		requests.stream().forEach(r -> r.start());
		requests.stream().forEach(r -> {
			try{
				r.join();
			}catch(InterruptedException e){
				System.out.println(e.getMessage());
			}
		});
		throttledRequests.addAll(requests.stream().filter(r->!r.response)
			.map(r->r.request)
			.collect(Collectors.toList()));
		requests=Arrays.asList(
			new Client(server.buildRequest(2, 1,1),server),
			new Client(server.buildRequest(2, 1,1),server),
			new Client(server.buildRequest(2, 1,1),server),
			new Client(server.buildRequest(1, 1,1),server),
			new Client(server.buildRequest(1, 1,1),server),
			new Client(server.buildRequest(1, 1,1),server),
			new Client(server.buildRequest(1, 1,1),server),
			new Client(server.buildRequest(3, 1,1),server),
			new Client(server.buildRequest(3, 1,1),server)
			);
		requests.stream().forEach(r -> r.start());
		requests.stream().forEach(r -> {
			try{
				r.join();
			}catch(InterruptedException e){
				System.out.println(e.getMessage());
			}
		});
		throttledRequests.addAll(requests.stream().filter(r->!r.response)
			.map(r->r.request)
			.collect(Collectors.toList()));
		List<Request>expected=Arrays.asList(
			server.buildRequest(1, 1,1),
			server.buildRequest(1, 1,1),
			server.buildRequest(3, 1,1)
			);
		assertEquals(throttledRequests.size(),expected.size());
		for(int i=0;i<expected.size();++i){
			assertEquals(throttledRequests.get(i).userId,
				expected.get(i).userId);
			assertEquals(throttledRequests.get(i).requestTime,
				expected.get(i).requestTime);
		}
	}

	public static void main(String[] args){
		JUnitCore.main("RateLimiter");
	}
}