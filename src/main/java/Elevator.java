import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.junit.Test;
import org.junit.runner.JUnitCore;

/**
 * 
 * @author tapopadma
 *
 * 1 - request -> (floor, up/down)
 * 2 - request -> (floor)
 * 3 - is_full()
 */
public class Elevator{
	static final int CAPACITY=5; 
	static final int FLOOR_COUNT=10;
	enum Direction{
		UP(1),DOWN(-1);
		int step;
		Direction(int step){
			this.step=step;
		}
		public Direction reversed() {
			return this.equals(UP)?DOWN:UP;
		}
	}
	class Request{
		int timestamp;
		int floor;
		Direction direction;
		int requestedFloor;
		boolean complete;
		int requestId;
		public Request(int f, Direction d, int r, int requestId) {
//			this.timestamp=t;
			this.floor=f;
			this.direction=d;
			this.requestedFloor=r;
			this.complete=false;
			this.requestId=requestId;
		}
	}
	class Server extends Thread{
		int capacity;
		int currentFloor;
		Direction direction;
		Map<Direction, TreeSet<Request>>requestsPending;
		TreeSet<Request>requestsProcessing;
		int floorCount;
		
		public Server() {
			this.capacity=CAPACITY;
			this.currentFloor=0;
			this.direction=Direction.UP;
			this.requestsPending=new HashMap<>();
			requestsProcessing=new TreeSet<Request>(
					(r1, r2)->Integer.valueOf(r1.requestedFloor)
					.equals(r2.requestedFloor)?
							Integer.valueOf(r1.requestId)
							.compareTo(r2.requestId)
							:Integer.valueOf(r1.floor)
							.compareTo(r2.floor));
			requestsPending.put(Direction.UP, new TreeSet<Request>(
					(r1, r2)->Integer.valueOf(r1.floor).equals(r2.floor)?
							Integer.valueOf(r1.requestId).compareTo(r2.requestId)
							:Integer.valueOf(r1.floor).compareTo(r2.floor)));
			requestsPending.put(Direction.DOWN, new TreeSet<Request>(
					(r1, r2)->Integer.valueOf(r1.floor).equals(r2.floor)?
							Integer.valueOf(r1.requestId).compareTo(r2.requestId)
							:Integer.valueOf(r1.floor).compareTo(r2.floor)));
			this.floorCount=FLOOR_COUNT;
		}
		
		synchronized void processRequests() throws InterruptedException {
			if(requestsPending.values().stream()
					.mapToInt(s->s.size()).sum()==0
					&&requestsProcessing.isEmpty()) {
				return;
			}
			while(!requestsProcessing.isEmpty()
					&&(direction.equals(Direction.UP)?
							requestsProcessing.first().requestedFloor:
								requestsProcessing.last().requestedFloor)
						==currentFloor) {
				Request request=(direction.equals(Direction.UP)?
						requestsProcessing.pollFirst():
							requestsProcessing.pollLast());
				request.complete=true;clientsComplete[request.requestId]=true;
			}
			int available=capacity-requestsProcessing.size();
			List<Request>newRequests=requestsPending.get(direction)
					.stream().filter(r->direction.equals(Direction.UP)
							?r.floor>=currentFloor:r.floor<=currentFloor)
					.limit(available)
					.toList();
			if(requestsProcessing.isEmpty()
					&&newRequests.isEmpty()
					&&requestsPending.values().stream()
					.flatMap(l->l.stream())
					.noneMatch(r->direction.equals(Direction.UP)
							?r.floor>currentFloor
							:r.floor<currentFloor)) {
				direction=direction.reversed();
				return;
			}
			requestsProcessing.addAll(newRequests);
			newRequests.stream()
			.forEach(r->requestsPending
					.get(direction).remove(r));
			Thread.sleep(1000);
			System.out.print("Lift moved from floor "+currentFloor);
			currentFloor+=direction.step;
			System.out.println("->"+currentFloor);
		}
		
		public synchronized void sendRequest(Request request) {
			requestsPending.get(request.direction).add(request);
		}
		
		@Override
		public void run() {
			while(!serverTurnOff) {
				try {
					processRequests();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	class Client extends Thread{
		Request request;
		public Client(Request r) {
			this.request=r;
		}
		@Override
		public void run() {
			server.sendRequest(request);
			while(!clientsComplete[request.requestId]);
			System.out.println("client "+request.requestId+" exits.");
		}
		
		public boolean arrivedCorrectly() {
			return request.complete;
		}
	}
	
	Server server = new Server();
	volatile boolean[] clientsComplete=new boolean[] {false,false,false,
			false,false,false
	};
	volatile boolean serverTurnOff=false;
	
	@Test
	public void server_dropsAllClientsCorrectly() throws InterruptedException {
		server.start();
		Client[] clients = new Client[] {
				new Client(new Request(2, Direction.UP, 5, 0)),
				new Client(new Request(4, Direction.UP, 5, 1)),
				new Client(new Request(1, Direction.DOWN, 0, 2)),
				new Client(new Request(3, Direction.UP, 4, 3)),
				new Client(new Request(4, Direction.UP, 6, 4)),
				new Client(new Request(7, Direction.DOWN, 0, 5))
		};
		Thread.sleep(2000);
		clients[0].start();
		clients[1].start();Thread.sleep(2000);
		clients[2].start();Thread.sleep(1000);
		clients[3].start();
		clients[4].start();clients[5].start();
		Arrays.asList(clients).stream().forEach(c->{
			try {
				c.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		serverTurnOff=true;
		assertTrue(Arrays.asList(clients).stream()
				.noneMatch(c->!c.arrivedCorrectly()));
	}
	
	@Test
	public void server_handlesOneClientCorrectly() throws InterruptedException {
		serverTurnOff=false;clientsComplete[0]=false;
		server.start();
		Client client = new Client(new Request(4, Direction.DOWN, 1, 0));
		Thread.sleep(2000);
		client.start();
		try {
			client.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		serverTurnOff=true;
		assertTrue(client.arrivedCorrectly());
	}
	
	@Test
	public void server_handlesDayOffsCorrectly() throws InterruptedException {
		serverTurnOff=false;
		server.start();
		Thread.sleep(2000);
		serverTurnOff=true;
		assertEquals(server.currentFloor,0);
	}
	
	public static void main(String[] args) {
		JUnitCore.main("Elevator");
	}
	
}