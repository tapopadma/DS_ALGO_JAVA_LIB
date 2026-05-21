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



enum VehicleType {
	TWOWHEELER(1),FOURWHEELER(2),LARGE(3);
	int size;
	VehicleType(int s) {
		size=s;
	}
}

class Vehicle {
	int id;
	VehicleType type;
	Vehicle(int i, VehicleType t) {
		id=i;type=t;
	}
}

class Spot {
	int id;
	VehicleType type;
	Spot(int i, VehicleType t) {
		id=i;type=t;
	}
}

class Ticket {
	int id;
	Vehicle vehicle;
	Spot spot;
	int startTime;
	Ticket(int i, Vehicle v, Spot s, int st) {
		id=i;vehicle=v;spot=s;startTime=st;
	}
}

class ParkingLot {
	
	Map<VehicleType, Queue<Spot>> freeSpots;
	Map<Integer, Ticket> tickets;
	int TICKET_ID;
	
	ParkingLot() {
		freeSpots = new HashMap<>();
		freeSpots.putIfAbsent(VehicleType.TWOWHEELER, new ArrayDeque<Spot>());
		freeSpots.putIfAbsent(VehicleType.FOURWHEELER, new ArrayDeque<Spot>());
		freeSpots.putIfAbsent(VehicleType.LARGE, new ArrayDeque<Spot>());
		tickets = new HashMap<Integer, Ticket>();
		TICKET_ID=0;
	}
	
	void addSpot(int id, VehicleType type) {
		freeSpots.get(type).add(new Spot(id,type));
	}
	
	public Ticket park(VehicleType type, int vehicleId) {
		if(freeSpots.get(type).isEmpty()) {
			throw new RuntimeException("parking lot full!!!");
		}
		Spot spot = freeSpots.get(type).poll();
		Ticket ticket = new Ticket(++TICKET_ID,new Vehicle(vehicleId,type),spot,(int)System.currentTimeMillis()/1000);
		tickets.put(TICKET_ID, ticket);
		return ticket;
	}
	
	public boolean checkout(int ticketId) {
		if(!tickets.containsKey(ticketId)) {
			return false;
		}
		Ticket ticket = tickets.get(ticketId);
		Spot spot = ticket.spot;
		freeSpots.get(ticket.vehicle.type).add(spot);
		tickets.remove(ticketId);
		return true;
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
		ParkingLot parkingLot = new ParkingLot();
		parkingLot.addSpot(1, VehicleType.TWOWHEELER);
		parkingLot.addSpot(2, VehicleType.TWOWHEELER);
		parkingLot.addSpot(3, VehicleType.FOURWHEELER);
		parkingLot.addSpot(4, VehicleType.FOURWHEELER);
		parkingLot.addSpot(5, VehicleType.FOURWHEELER);
		parkingLot.addSpot(6, VehicleType.FOURWHEELER);
		parkingLot.addSpot(7, VehicleType.LARGE);
		parkingLot.addSpot(8, VehicleType.LARGE);
		Ticket ticket = parkingLot.park(VehicleType.TWOWHEELER, 1);
		assertTrue(parkingLot.checkout(ticket.id));
		parkingLot.park(VehicleType.TWOWHEELER, 2);
		ticket = parkingLot.park(VehicleType.TWOWHEELER, 3);
		assertThrows(RuntimeException.class, ()-> {
			parkingLot.park(VehicleType.TWOWHEELER, 4);
		});
		parkingLot.checkout(ticket.id);
		parkingLot.park(VehicleType.TWOWHEELER, 4);
	}
	
}
