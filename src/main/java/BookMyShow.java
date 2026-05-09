package design;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Movie {
	int id;
	String name;
	String genre;
	int rating;
	public Movie(int id, String name, String gener, int rating) {
		this.id=id;
		this.name=name;
		this.genre=genre;
		this.rating=rating;
	}
}

class Show {
	int id;
	ConcurrentHashMap<String, Boolean> seatmap;
	ConcurrentHashMap<String, Lock> locks;
	int movie;
	int start;
	int end;
	int theater;
	int hall;
	public Show(int id, int movie, int start, int end, int theater, int hall) {
		this.id=id;
		this.movie=movie;
		this.start=start;
		this.end=end;
		seatmap = new ConcurrentHashMap<String, Boolean>();
		seatmap.put("A1", false);
		seatmap.put("A2", false);
		seatmap.put("B1", false);
		seatmap.put("B2", false);
		seatmap.put("C1", false);
		seatmap.put("C2", false);
		seatmap.put("C3", false);
		seatmap.put("C4", false);
		this.theater=theater;
		this.hall=hall;
		locks = new ConcurrentHashMap<String, Lock>();
	}
	@Override
	public String toString() {
		return id+" "+movie+" "+" "+start+" "+end+" "+ theater+ " "+hall+" "+seatmap.toString();
	}
	public boolean book(List<String> seats) {
		Collections.sort(seats);// sorted order to avoid deadlock
		for(String s: seats) {
			Lock lock = locks.computeIfAbsent(s, k-> new ReentrantLock());
			lock.lock();//blocks here if this lock is already acquired
		}
		boolean success=true;
		for(String s: seats) {
			boolean[] altered = new boolean[] {true};
			seatmap.compute(s, (k,v)->{
				if(v) {
					altered[0]=false;
				}
				return true;
			});
			if(!altered[0]) {
				success=false;break;
			}
		}
		for(int i=seats.size()-1;i>=0;--i) { // consistent reverse order to avoid deadlock
			Lock lock = locks.get(seats.get(i));
			lock.unlock();
		}
		return success;
	}
}

class ShowManager {
	Map<Integer, Show> shows;
	Map<Integer, Movie> movies;
	
	public ShowManager() {
		shows = new HashMap<>();
		movies = new HashMap<>();
	}
	public void addMovie(int movie, String name, String genre, int rating) {
		movies.put(movie, new Movie(movie,name,genre,rating));
	}
	public void addShow(int showId, int movie, int start, int end, int theater, int hall) {
		shows.put(showId, new Show(showId, movie, start, end, theater, hall));
	}
	
	public List<Show> search(String word) {
		return shows.values().stream()
				.filter(s->movies.get(s.movie).name.contains(word))
				.toList();
	}
	
	public boolean book(int show, List<String> seats) {
		return shows.get(show).book(seats);
	}
}

package design;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import org.junit.jupiter.api.*;

class DesignTest {
	
	private ShowManager buildShowManager() {
		ShowManager manager = new ShowManager();
		manager.addMovie(1, "a b", "scifi", 4);
		manager.addMovie(2, "b c", "horror", 5);
		manager.addMovie(3, "c d", "drama", 3);
		manager.addShow(1, 1, 10, 20, 1, 1);
		manager.addShow(2, 1, 20, 30, 1, 3);
		manager.addShow(3, 2, 10, 20, 2, 1);
		manager.addShow(4, 2, 20, 30, 2, 3);
		return manager;
	}
	
	@Test
	public void testSingleUser() throws Exception {
		ShowManager manager = buildShowManager(); 
		assertIterableEquals(List.of(1,2,3,4), manager.search("b").stream().map(e->e.id).toList());
		assertIterableEquals(List.of(1,2), manager.search("a").stream().map(e->e.id).toList());
	}
	
	@Test
	public void testMultiUser() throws Exception {
		ShowManager manager = buildShowManager();
		Thread request1 = new Thread(()-> {
			manager.book(1, new ArrayList<>(List.of("A1","A2")));
		});
		Thread request2 = new Thread(()-> {
			manager.book(1, new ArrayList<>(List.of("A1","A2","B1","B2","C1","C2","C3")));
		});
		Thread request3 = new Thread(()-> {
			manager.book(1, new ArrayList<>(List.of("B1","C1","C2","C3","C4")));
		});
		request1.start();request2.start();request3.start();
		request1.join();request2.join();request3.join();
		System.out.println(manager.search("a b"));
	}

}
