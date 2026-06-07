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


enum VehicleSize {
	SMALL,MEDIUM,LARGE;
}

class Spot {
	int id;
	VehicleSize sizeCompatible;
	Spot(int i, VehicleSize v) {
		id=i;sizeCompatible=v;
	}
}

class Ticket {
	static final Map<VehicleSize, Double> RATES = Map.of(
			VehicleSize.SMALL, 0.003,
			VehicleSize.MEDIUM, 0.01,
			VehicleSize.LARGE, 0.05);
	int id;
	int vehicle;
	Spot spot;
	int parkedAt;
	double rate;
	boolean closed;
	Ticket(int i, int vehicle, Spot spot, int now) {
		id=i;
		this.vehicle=vehicle;
		this.spot=spot;
		this.parkedAt=now;
		this.rate=RATES.get(spot.sizeCompatible);
		closed=false;
	}
	public int estimateFare() {
		int now = (int)(System.currentTimeMillis()/1000L);
		int elapsed = now - parkedAt;
		double total = 1.0*elapsed * rate;
		return (int)total;
	}
}

class ParkingLotManager {
	ConcurrentHashMap<VehicleSize, Queue<Spot>> spots;
	ConcurrentHashMap<Integer, Ticket> tickets;
	int TICKET_ID;
	public ParkingLotManager() {
		TICKET_ID=0;
		spots = new ConcurrentHashMap<>();
		spots.putIfAbsent(VehicleSize.SMALL, new ArrayDeque<Spot>());
		spots.putIfAbsent(VehicleSize.MEDIUM, new ArrayDeque<Spot>());
		spots.putIfAbsent(VehicleSize.LARGE, new ArrayDeque<Spot>());
		tickets = new ConcurrentHashMap<>();
	}
	void addSpot(int spotId, VehicleSize size) {
		spots.compute(size, (k,v)-> {
			v.add(new Spot(spotId, size));
			return v;
		});
	}
	
	public Ticket checkIn(VehicleSize size, int vehicleId) {
		Ticket[] result = new Ticket[] {null};
		spots.compute(size, (k,v)-> {
			if(v.isEmpty()) {
				throw new RuntimeException("Parking Lot Full");
			} else {
				Spot spot = v.poll();
				int now = (int)(System.currentTimeMillis()/1000L);
				Ticket ticket = new Ticket(++TICKET_ID,vehicleId, spot,now);
				tickets.put(ticket.id, ticket);
				result[0]=ticket;
			}
			return v;
		});
		return result[0];
	}
	
	public int checkOut(int ticketId) {
		int[] result = new int[] {-1};
		tickets.compute(ticketId, (k,v)-> {
			if(v==null||v.closed) {
				throw new RuntimeException("Invalid ticket");
			}
			result[0] = v.estimateFare();
			v.closed=true;
			spots.get(v.spot.sizeCompatible).add(v.spot);
			return v;
		});
		return result[0];
	}
}

package design;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.*;
import org.junit.jupiter.api.*;

class DesignTest {
	
	
	ParkingLotManager buildManager() {
		ParkingLotManager manager = new ParkingLotManager();
		manager.addSpot(2, VehicleSize.SMALL);
		manager.addSpot(3, VehicleSize.SMALL);
		manager.addSpot(1, VehicleSize.MEDIUM);
		manager.addSpot(4, VehicleSize.MEDIUM);
		manager.addSpot(5, VehicleSize.LARGE);
		return manager;
	}
	
	@Test
	public void testDefault() throws Exception {
		ParkingLotManager manager = buildManager();
		int t1 = manager.checkIn(VehicleSize.SMALL, 1).id;
		int t2 = manager.checkIn(VehicleSize.SMALL, 2).id;
		assertEquals(t1,1);
		assertEquals(t2,2);
		assertThrows(RuntimeException.class, ()->manager.checkIn(VehicleSize.SMALL, 3));
		manager.checkOut(t2);
		int t3 = manager.checkIn(VehicleSize.SMALL, 3).id;
		assertEquals(t3,3);
		assertThrows(RuntimeException.class, ()->manager.checkIn(VehicleSize.SMALL, 4));
	}
	
	@Test
	public void testBulkConcurrent() throws Exception {
		ParkingLotManager manager = buildManager();
		List<Thread> requests = new ArrayList<>();
		for(int i=0;i<2;++i) {
			int vehicleId=i+1;
			requests.add(new Thread(()-> {
				manager.checkIn(VehicleSize.SMALL, vehicleId);
			}));
		}
		for(int i=0;i<2;++i) {
			int vehicleId=i+3;
			requests.add(new Thread(()-> {
				manager.checkIn(VehicleSize.MEDIUM, vehicleId);
			}));
		}
		requests.add(new Thread(()-> {
			manager.checkIn(VehicleSize.LARGE, 5);
		}));
		for(Thread request: requests) {
			request.start();
		}
		for(Thread request: requests) {
			request.join();
		}
		assertThrows(RuntimeException.class, ()->manager.checkIn(VehicleSize.SMALL, 6));
		assertThrows(RuntimeException.class, ()->manager.checkIn(VehicleSize.MEDIUM, 7));
		assertThrows(RuntimeException.class, ()->manager.checkIn(VehicleSize.LARGE, 8));
	}
	
}
