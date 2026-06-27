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


enum RideState {
	CREATED,ACCEPTED,CANCELLED,DECLINED,ARRIVED,PICKED,COMPLETED;
}

interface StateMachine {
	boolean canTransition(RideState oldState, RideState newState);
}

class RideStateMachine implements StateMachine {
	
	final Map<RideState, List<RideState>> stateMap;

	public RideStateMachine() {
		stateMap = new HashMap<RideState, List<RideState>>();
		stateMap.put(RideState.CREATED, List.of(
			RideState.ACCEPTED, RideState.DECLINED, RideState.CANCELLED
			));
		stateMap.put(RideState.ACCEPTED, List.of(
				RideState.CANCELLED,RideState.ARRIVED
				));
		stateMap.put(RideState.ARRIVED, List.of(
				RideState.CANCELLED,RideState.PICKED
				));
		stateMap.put(RideState.PICKED, List.of(
				RideState.COMPLETED
				));
	}
	
	@Override
	public boolean canTransition(RideState oldState, RideState newState) {
		return stateMap.containsKey(oldState) && stateMap.get(oldState).contains(newState);
	}
	
}

class Ride {
	int id;
	RideState state;
	Ride(int i, RideState s) {
		id=i;state=s;
	}
}

interface Repository {
	Ride getRide(int rideId);
	boolean updateRideState(int rideId, RideState newState);
	void addRide(int rideId);
}

class InmemoryRepository implements Repository {

	final StateMachine stateMachine;
	final Map<Integer, Ride> rides;
	
	public InmemoryRepository(StateMachine stateMachine) {
		this.stateMachine=stateMachine;
		rides = new ConcurrentHashMap<Integer, Ride>();
	}
	
	@Override
	public Ride getRide(int rideId) {
		return rides.get(rideId);
	}

	@Override
	public boolean updateRideState(int rideId, RideState newState) {
		boolean[] changed = new boolean[] {false};
		rides.compute(rideId, (k,v)-> {
			if(v!=null && stateMachine.canTransition(v.state, newState)) {
				v.state=newState;
				changed[0]=true;
				return v;
			}
			return v;
		});
		return changed[0];
	}

	@Override
	public void addRide(int rideId) {
		rides.put(rideId, new Ride(rideId, RideState.CREATED));
	}
	
}

class RideManagementService {
	final Repository repository;
	final StateMachine stateMachine;
	
	public RideManagementService(Repository repository, StateMachine stateMachine) {
		this.repository=repository;
		this.stateMachine=stateMachine;
	}
	
	void addRide(int rideId) {
		repository.addRide(rideId);
	}
	
	public boolean changeState(int rideId, RideState newState) {
		Ride ride = repository.getRide(rideId);
		if(stateMachine.canTransition(ride.state, newState)) {
			return repository.updateRideState(rideId,newState);
		} else {
			return false;
		}
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
		StateMachine stateMachine = new RideStateMachine();
		Repository repository = new InmemoryRepository(stateMachine);
		RideManagementService service = new RideManagementService(repository, stateMachine);
		service.addRide(1);
		service.addRide(2);
		service.addRide(3);
		assertTrue(service.changeState(1, RideState.ACCEPTED));
		assertTrue(service.changeState(1, RideState.ARRIVED));
		assertTrue(service.changeState(1, RideState.PICKED));
		assertTrue(service.changeState(1, RideState.COMPLETED));
		assertTrue(service.changeState(2, RideState.DECLINED));
		assertFalse(service.changeState(2, RideState.CANCELLED));
		Thread t1=new Thread(()->service.changeState(3, RideState.ACCEPTED));
		Thread t2=new Thread(()->service.changeState(3, RideState.CANCELLED));
		t1.start();
		t2.start();
		t1.join();
		t2.join();
		assertTrue(repository.getRide(3).state.equals(RideState.CANCELLED));
	}

}
