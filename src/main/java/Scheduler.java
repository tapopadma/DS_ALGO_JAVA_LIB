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


class Interval {
	int start;
	int end;
	Interval(int s,int e){
		start=s;end=e;
	}
}

class Schedule {
	int user;
	TreeSet<Interval> events;
	Schedule(int user) {
		this.user=user;
		events = new TreeSet<>((e1,e2)->Integer.compare(e1.start, e2.start));
	}
	
	boolean intersects(int start, int end) {
		Interval target = events.floor(new Interval(end-1,0));
		return target!=null&&target.start<end&&target.end>start;
	}
	
	void addEvent(int start, int end) {
		events.add(new Interval(start, end));
	}
}

class Scheduler {
	
	ConcurrentHashMap<Integer, Schedule> schedules;
	ConcurrentHashMap<Integer, Lock> locks;
	
	Scheduler() {
		schedules = new ConcurrentHashMap<>();
		locks = new ConcurrentHashMap<Integer, Lock>();
	}
	
	boolean schedule(int user, int start, int end, List<Integer> invitees) {
		List<Integer> users = new ArrayList<>();
		users.add(user);
		for(int u: invitees)users.add(u);
		Collections.sort(users);
		for(int u: users) {
			locks.putIfAbsent(u, new ReentrantLock());
			locks.get(u).lock();
		}
		boolean[] possible = new boolean[] {true};
		for(int u: users) {
			schedules.compute(u, (k,v)->{
				if(v==null) v=new Schedule(u);
				if(v.intersects(start, end)) {
					possible[0] = false;
				}
				return v;
			});
			if(!possible[0])break;
		}
		if(possible[0]) {
			for(int u: users) {
				schedules.get(u).addEvent(start, end);
			}
		}
		for(int u: users) {
			locks.get(u).unlock();
		}
		return possible[0];
	}
	
	List<String> fetchSchedule(int u) {
		return schedules.get(u).events.stream().map(e->e.start+","+e.end).toList();
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
		Scheduler scheduler = new Scheduler();
		List<Thread> schedules = new ArrayList<>();
		schedules.add(new Thread(()-> {
			scheduler.schedule(1, 1, 5, List.of(2,3));
		}));
		schedules.add(new Thread(()-> {
			scheduler.schedule(2, 2, 5, List.of(3));
		}));
		for(Thread s: schedules) {
			s.start();
		}
		for(Thread s: schedules) {
			s.join();
		}
		System.out.println(scheduler.fetchSchedule(1));
		System.out.println(scheduler.fetchSchedule(2));
		System.out.println(scheduler.fetchSchedule(3));
		schedules = new ArrayList<>();
		schedules.add(new Thread(()-> {
			scheduler.schedule(3, 5, 6, List.of(1));
		}));
		schedules.add(new Thread(()-> {
			scheduler.schedule(2, 6, 7, List.of(3));
		}));
		schedules.add(new Thread(()-> {
			scheduler.schedule(1, 6, 8, List.of(2));
		}));
		for(Thread s: schedules) {
			s.start();
		}
		for(Thread s: schedules) {
			s.join();
		}
		System.out.println(scheduler.fetchSchedule(1));
		System.out.println(scheduler.fetchSchedule(2));
		System.out.println(scheduler.fetchSchedule(3));
	}
	
}
