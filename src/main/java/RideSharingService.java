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


class Rider {
	int id;
	public Rider(int i){
		id=i;
	}
}

enum DriverStatus {
	AVAILABLE, ASSIGNED;
}

class Driver {
	int id;
	DriverStatus status;
	Location location;
	Driver(int i, DriverStatus s, Location l){
		id=i;status=s;location=l;
	}
}

class Location {
	int longitude;
	int latitude;
	Location(int l1, int l2) {
		longitude=l1;latitude=l2;
	}
	int distance(Location l) {
		return (int)Math.abs(latitude-l.latitude)+(int)Math.abs(longitude-l.longitude);
	}
}

class Ride {
	int id;
	Rider rider;
	Driver driver;
	Location pickup;
	Location drop;
	Ride(int i, Rider r, Driver d, Location l1, Location l2) {
		id=i;rider=r;driver=d;pickup=l1;drop=l2;
	}
}

interface Repository {
	void addDriver(Driver driver);
	void addRider(Rider rider);
	Map<Integer, Driver> getAllDrivers();
	Rider getRider(int riderId);
	int getNewId();
	void addRide(Ride ride);
	Ride getRide(int riderId);
}

class InmemoryRepository implements Repository {
	final Map<Integer, Driver> drivers;
	final AtomicInteger id;
	final Map<Integer, Ride> rides;
	final Map<Integer, Rider> riders;
	
	public InmemoryRepository() {
		drivers = new ConcurrentHashMap<Integer, Driver>();
		riders = new ConcurrentHashMap<Integer, Rider>();
		id = new AtomicInteger();
		rides = new ConcurrentHashMap<Integer, Ride>();
	}
	
	@Override
	public void addDriver(Driver driver) {
		drivers.put(driver.id, driver);
	}
	@Override
	public Map<Integer, Driver> getAllDrivers() {
		return drivers;
	}
	@Override
	public Rider getRider(int riderId) {
		return riders.get(riderId);
	}
	@Override
	public Ride getRide(int riderId) {
		return rides.getOrDefault(riderId,null);
	}

	@Override
	public int getNewId() {
		return id.incrementAndGet();
	}

	@Override
	public void addRide(Ride ride) {
		rides.put(ride.rider.id, ride);
	}

	@Override
	public void addRider(Rider rider) {
		riders.put(rider.id, rider);
	}

}

interface RideMatchingStrategy {
	Ride findMatch(Rider rider, Location pickup, Location drop);
}

class DistanceStrategy implements RideMatchingStrategy {
	static final int MATCH_DISTANCE = 10;
	
	final Repository repository;
	
	public DistanceStrategy(Repository repository) {
		this.repository=repository;
	}
	
	@Override
	public Ride findMatch(Rider rider, Location pickup, Location drop) {
		Map<Integer, Driver> drivers = repository.getAllDrivers();
		Ride[] ride = new Ride[] {null};
		for(int driverId: drivers.keySet()) {
			drivers.compute(driverId, (k,v)-> {
				if(v!=null&&v.status.equals(DriverStatus.AVAILABLE)&&v.location.distance(pickup)<=MATCH_DISTANCE) {
					v.status=DriverStatus.ASSIGNED;
					ride[0] = new Ride(repository.getNewId(),rider,v,pickup,drop);
					repository.addRide(ride[0]);
					return v;
				} else return v;
			});
			if(ride[0]!=null)return ride[0];
		}
		return null;
	}
}

class RideMatchingService {
	final Repository repository;
	final RideMatchingStrategy strategy;
	
	public RideMatchingService(Repository repository, RideMatchingStrategy strategy) {
		this.repository=repository;
		this.strategy=strategy;
	}
	
	void addDriver(int driverId, Location location) {
		repository.addDriver(new Driver(driverId,DriverStatus.AVAILABLE,location));
	}
	
	void addRider(int riderId) {
		repository.addRider(new Rider(riderId));
	}
	
	public Ride requestRide(int riderId, Location pickup, Location drop) {
		Rider rider = repository.getRider(riderId);
		return strategy.findMatch(rider, pickup, drop);
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
		Repository repository = new InmemoryRepository();
		RideMatchingStrategy strategy = new DistanceStrategy(repository);
		RideMatchingService service = new RideMatchingService(repository,strategy);
		service.addDriver(1, new Location(1,1));
		service.addDriver(2, new Location(2,2));
		service.addDriver(3, new Location(3,3));
		service.addDriver(4, new Location(10,10));
		service.addDriver(5, new Location(5,5));
		service.addDriver(6, new Location(6,6));
		List<Location> pickups = List.of(
				new Location(0,0),
				new Location(2,1),
				new Location(3,5),
				new Location(10,20),
				new Location(0,1),
				new Location(2,3),
				new Location(4,3),
				new Location(1,2),
				new Location(20,40));
		for(int i=0;i<pickups.size();++i)service.addRider(i);
		
		
		List<Thread> requests = new ArrayList<>();
		Location defaultDrop=new Location(5,5);
		for(int i=0;i< pickups.size();++i) {
			Location l = pickups.get(i);
			int rider=i;
			requests.add(new Thread(()->service.requestRide(rider, l,defaultDrop)));
		}
		for(Thread r: requests) {
			r.start();
		}
		for(Thread r: requests) {
			r.join();
		}
		Set<Integer> set = new HashSet<>();
		int unassigned=0;
		for(int i=0;i<pickups.size();++i) {
			Ride ride = repository.getRide(i);
			if(ride!=null) {
				set.add(ride.driver.id);
			} else ++unassigned; 
		}
		assertEquals(3, unassigned);
		assertEquals(6, set.size());
	}

}
