package design;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


class Location {
	int latitude;
	int longitude;
	Location (int l1,int l2) {
		latitude=l1;longitude=l2;
	}
	int distance(Location other) {
		return (int)Math.abs(latitude-other.latitude)+(int)Math.abs(longitude-other.longitude);
	}
}

class Driver {
	public Driver(int driverId, Location location2, int r) {
		id=driverId;location=location2;rating=r;
	}
	int id;
	Location location;
	int rating;
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Driver)) return false;
		if(((Driver)o)==this)return true;
		return id==((Driver)o).id;
	}
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}

interface Repository {
	void updateDriverLocation(int driverId, Location location, int rating);
	List<Driver> findNearByDrivers(Location location, int maxRadius);
}

interface SpatialIndex {
	void addLocation(Location location , Driver driver);
	List<Driver> findNearBy(Location location , int max);
}

class QuadTreeNode {
	int a,b,c,d;
	Map<Integer,Driver> drivers;
	QuadTreeNode[] children;
	QuadTreeNode(int a, int b, int c, int d) {
		this.a=a;
		this.b=b;
		this.c=c;
		this.d=d;
		children = new QuadTreeNode[] {null,null,null,null};
		drivers = new HashMap<>();
	}
}

class WeightedDriver {
	Driver driver;
	int distance;
	WeightedDriver(Driver d, int d1){
		driver=d;distance=d1;
	}
}

class QuadTree implements SpatialIndex {

	QuadTreeNode root;
	
	public QuadTree(int a, int b, int c, int d) {
		root = initialize(root, a,b,c,d);
	}
	
	QuadTreeNode initialize(QuadTreeNode cur, int a, int b, int c, int d) {
		if(a>c||b>d)return cur;
		if(cur == null) cur = new QuadTreeNode(a,b,c,d);
		if(a==c&&b==d) {
			return cur;
		}
		int m1 = (a+c)/2, m2=(b+d)/2;
		cur.children[0]=initialize(cur.children[0],a,b,m1,m2);
		cur.children[1]=initialize(cur.children[1],a,m2+1,m1,d);
		cur.children[2]=initialize(cur.children[2],m1+1,b,c,m2);
		cur.children[3]=initialize(cur.children[3],m1+1,m2+1,c,d);
		return cur;
	}
	
	void addLocation(QuadTreeNode cur, Location location, Driver driver) {
		if(cur==null)return;
		int x = location.latitude, y = location.longitude;
		int a=cur.a, b = cur.b, c = cur.c , d = cur.d;
		if(x > c || x < a || y > d || y < b)return;
		for(int i=0;i<4;++i) {
			addLocation(cur.children[i],location,driver);
		}
		if(b==d&&a==c) {
			cur.drivers.put(driver.id, driver);
		}
	}
	
	@Override
	public void addLocation(Location location, Driver driver) {
		addLocation(root,location,driver);
	}
	
	boolean pointInsideCircle(int a, int b, int x, int y, int r) {
		return (a-x)*(a-x)+(b-y)*(b-y)<r*r;
	}
	
	boolean sideInsideCircle(int a, int b, int c, int d, int x, int y, int r) {
		if(pointInsideCircle(a,b,x,y,r)||pointInsideCircle(c,d,x,y,r))return true;
		return false;
	}
	
	boolean circleIntersectsRectangle(int x, int y, int r, int a, int b, int c, int d) {
		return sideInsideCircle(a,b,a,d,x,y,r)||sideInsideCircle(a,d,c,d,x,y,r)||
				sideInsideCircle(a,b,c,b,x,y,r)||sideInsideCircle(c,b,c,d,x,y,r);
	}
	
	Map<Integer,WeightedDriver> findNearBy(QuadTreeNode cur, Location location, int max) {
		Map<Integer, WeightedDriver> drivers = new HashMap<Integer, WeightedDriver>();
		if(cur==null) return drivers;
		int x = location.latitude, y = location.longitude;
		int a=cur.a, b = cur.b, c = cur.c , d = cur.d;
		if(!circleIntersectsRectangle(x,y,max,a,b,c,d))return drivers;
		for(int i=0;i<4;++i) drivers.putAll(findNearBy(cur.children[i],location,max));
		for(Driver driver: cur.drivers.values()) {
			int distance = driver.location.distance(location);
			if(distance<=max) {
				drivers.put(driver.id, new WeightedDriver(driver,distance));
			}
		}
		return drivers;
	}

	@Override
	public List<Driver> findNearBy(Location location, int max) {
		return findNearBy(root,location,max).values().stream()
				.sorted((w1,w2)->Integer.compare(w1.distance, w2.distance))
				.map(w->w.driver)
				.toList();
	}
	
}

class Cell {
	static final int SIZE=10;
	
	int x; int y;
	Cell(int x, int y) {
		this.x=x;this.y=y;
	}
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Cell)) return false;
		if(((Cell)o)==this)return true;
		return x==((Cell)o).x&&y==((Cell)o).y;
	}
	@Override
	public int hashCode() {
		return Objects.hash(x,y);
	}
	@Override
	public String toString() {
		return x+","+y;
	}
	public static Cell of(Location location) {
		return new Cell(location.latitude/SIZE,location.longitude/SIZE);
	}
}

class GridIndex implements SpatialIndex {
	final Map<Cell, Set<Driver>> drivers;
	
	public GridIndex() {
		drivers = new HashMap<>();
	}
	
	@Override
	public void addLocation(Location location, Driver driver) {
		Cell cell = Cell.of(location);
		drivers.putIfAbsent(cell, new HashSet<>());
		drivers.get(cell).add(driver);
	}
	
	@Override
	public List<Driver> findNearBy(Location location, int max) {
		Cell cell = Cell.of(location);
		return drivers.getOrDefault(cell, new HashSet<>()).stream()
				.filter(d->location.distance(d.location) <= max)
				.sorted((d1,d2)->Integer.compare(location.distance(d1.location), 
						location.distance(d2.location)))
				.toList();
	}
	
}

class InmemoryRepository implements Repository {

	final SpatialIndex index;
	
	public InmemoryRepository(SpatialIndex index) {
		this.index=index;
	}
	
	@Override
	public void updateDriverLocation(int driverId, Location location, int rating) {
		index.addLocation(location, new Driver(driverId, location, rating));
	}

	@Override
	public List<Driver> findNearByDrivers(Location location, int maxRadius) {
		return index.findNearBy(location,maxRadius);
	}
	
}

interface MatchingStrategy {
	List<Driver> rankDrivers(Location pickup, List<Driver> drivers);
}

class ScoreBasedMatchingStrategy implements MatchingStrategy {

	@Override
	public List<Driver> rankDrivers(Location pickup, List<Driver> drivers) {
		return drivers.stream()
				.sorted((d1,d2)->Integer.compare(d2.rating, d1.rating))
				.toList();
	}
	
}

class RideMatchingService {
	static final int MATCHING_RAIDUS = 10;
	
	final Repository repository;
	final MatchingStrategy strategy;
	
	public RideMatchingService(Repository repository, MatchingStrategy strategy) {
		this.repository=repository;
		this.strategy=strategy;
	}
	
	public void updateLocation(int driverId, Location location, int rating) {
		repository.updateDriverLocation(driverId, location,rating);
	}
	
	public List<Driver> findMatchingDrivers(Location pickup) {
		return strategy.rankDrivers(pickup, repository.findNearByDrivers(pickup,MATCHING_RAIDUS));
	}
}

package design;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.*;
import org.junit.jupiter.api.*;

class DesignTest {
	

	@Test
	public void testQuadTree() throws Exception {
		SpatialIndex index = new QuadTree(0, 0, 1000, 1000);
		Repository repository = new InmemoryRepository(index);
		MatchingStrategy strategy = new ScoreBasedMatchingStrategy();
		RideMatchingService service = new RideMatchingService(repository, strategy);
		
		service.updateLocation(1, new Location(1,1),4);
		service.updateLocation(2, new Location(5,2),3);
		
		assertIterableEquals(List.of(1,2), service.findMatchingDrivers(new Location(0,0))
				.stream().map(d->d.id).toList());
		assertIterableEquals(List.of(1,2), service.findMatchingDrivers(new Location(7,3))
				.stream().map(d->d.id).toList());
		
	}
	
	@Test
	public void testGrid() throws Exception {
		SpatialIndex index = new GridIndex();
		Repository repository = new InmemoryRepository(index);
		MatchingStrategy strategy = new ScoreBasedMatchingStrategy();
		RideMatchingService service = new RideMatchingService(repository, strategy);
		
		service.updateLocation(1, new Location(1,1),4);
		service.updateLocation(2, new Location(5,2),3);
		
		assertIterableEquals(List.of(1,2), service.findMatchingDrivers(new Location(0,0))
				.stream().map(d->d.id).toList());
		assertIterableEquals(List.of(1,2), service.findMatchingDrivers(new Location(7,3))
				.stream().map(d->d.id).toList());
		
	}

}
