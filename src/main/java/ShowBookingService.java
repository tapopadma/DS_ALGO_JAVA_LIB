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


class Movie {
	int id;
	String title;
	Movie(int i, String t) {
		id=i;title=t;
	}
	@Override
	public String toString() {
		return id+" "+title;
	}
}

class Theater {
	int id;
	Theater(int i) {
		id=i;
	}
	@Override
	public String toString() {
		return id+"";
	}
}

class ShowTime {
	int start;
	int end;
	ShowTime(int s, int e) {
		start=s;end=e;
	}
	@Override
	public String toString() {
		return start+" "+end;
	}
}

enum SeatStatus {
	FREE,BOOKED;
}

class Show {
	int id;
	Theater theater;
	Movie movie;
	ShowTime showtime;
	final Map<String, SeatStatus> seatmap;
	final Map<String, Lock> locks;
	static final AtomicInteger idg = new AtomicInteger();
	
	@Override
	public String toString() {
		return id+" ";
	}
	
	public Show(Theater t, Movie m, ShowTime s) {
		seatmap = new ConcurrentHashMap<String, SeatStatus>();
		seatmap.put("A1", SeatStatus.FREE);
		seatmap.put("B1", SeatStatus.FREE);
		seatmap.put("B2", SeatStatus.FREE);
		seatmap.put("C1", SeatStatus.FREE);
		seatmap.put("C2", SeatStatus.FREE);
		seatmap.put("C3", SeatStatus.FREE);
		locks = new ConcurrentHashMap<String, Lock>();
		id=idg.incrementAndGet();theater=t;movie=m;showtime=s;
	}
	
	Reservation book(List<String> seats, int userId) {
		Collections.sort(seats);
		for(String seat: seats) {
			locks.putIfAbsent(seat, new ReentrantLock());
			locks.get(seat).lock();
		}
		Reservation reservation = null;
		try {
			boolean ok = true;
			for(String seat: seats) {
				if(seatmap.get(seat).equals(SeatStatus.BOOKED)) {
					ok=false;break;
				}
			}
			if(ok) {
				for(String seat: seats) {
					seatmap.put(seat, SeatStatus.BOOKED);
				}
				reservation = new Reservation(idg.incrementAndGet(),this,seats,BookingStatus.BOOKED,userId);
			}
		} finally {
			Collections.reverse(seats);
			for(String seat: seats) {
				locks.get(seat).unlock();
			}
		}
		return reservation;
	}
	
	void cancel(List<String> seats, int userId) {
		Collections.sort(seats);
		for(String seat: seats) {
			locks.putIfAbsent(seat, new ReentrantLock());
			locks.get(seat).lock();
		}
		try {
			for(String seat: seats) {
				seatmap.put(seat, SeatStatus.FREE);
			}
		} finally {
			Collections.reverse(seats);
			for(String seat: seats) {
				locks.get(seat).unlock();
			}
		}
	}
}

enum BookingStatus {
	BOOKED,CANCELLED;
}

class Reservation {
	int id;
	Show show;
	List<String> seats;
	BookingStatus status;
	int bookedBy;
	Reservation(int i, Show s, List<String >st, BookingStatus sta, int b) {
		id=i;show=s;seats=st;status=sta;bookedBy=b;
	}
	@Override
	public String toString() {
		return id+" ";
	}
}

interface Repository {
	Map<Integer, Movie> listMovies();
	List<Theater> listTheatersByMovie(int movieId);
	List<Show> listShowsByTheater(int theaterId);
	Reservation bookSeats(int userId, int theaterId, List<String> seats);
	boolean cancelBooking(int reservationId, int userId);
	void addShow(int movieId, String title, int theaterId, int start, int end);
}

class InmemoryRepository implements Repository {
	final Map<Integer, Movie> movies;
	final Map<Integer, Show> shows;
	final Map<Integer, Theater> theaters;
	final Map<Integer, List<Theater>> theatersByMovie;
	final Map<Integer, List<Show>> showsByTheater;
	final Map<Integer, Reservation> reservations;
	
	public InmemoryRepository() {
		theatersByMovie = new ConcurrentHashMap<Integer, List<Theater>>();
		showsByTheater=  new ConcurrentHashMap<Integer, List<Show>>();
		movies = new ConcurrentHashMap<Integer, Movie>();
		shows = new ConcurrentHashMap<Integer, Show>();
		reservations = new ConcurrentHashMap<Integer, Reservation>();
		theaters = new ConcurrentHashMap<Integer, Theater>();
	}
	
	public void addShow(int movieId, String title, int theaterId, int start, int end) {
		Movie movie = new Movie(movieId, title);
		movies.putIfAbsent(movieId, movie);
		Theater theater = new Theater(theaterId);
		theaters.putIfAbsent(theater.id, theater);
		Show show = new Show(theater, movie, new ShowTime(start, end));
		shows.putIfAbsent(show.id, show);
		theatersByMovie.putIfAbsent(movie.id, new ArrayList<>());
		theatersByMovie.get(movie.id).add(theater);
		showsByTheater.putIfAbsent(theater.id, new ArrayList<>());
		showsByTheater.get(theater.id).add(show);
	}

	@Override
	public Map<Integer, Movie> listMovies() {
		return movies;
	}

	@Override
	public List<Theater> listTheatersByMovie(int movieId) {
		return theatersByMovie.get(movieId);
	}

	@Override
	public List<Show> listShowsByTheater(int theaterId) {
		return showsByTheater.get(theaterId);
	}

	@Override
	public Reservation bookSeats(int userId, int showId, List<String> seats) {
		Reservation reservation = shows.get(showId).book(seats, userId);
		if(reservation!=null) reservations.put(reservation.id, reservation);
		return reservation;
	}

	@Override
	public boolean cancelBooking(int reservationId, int userId) {
		if(!reservations.containsKey(reservationId))return false;
		Reservation reservation = reservations.get(reservationId);
		Show show = reservation.show;
		show.cancel(reservation.seats, userId);
		reservations.remove(reservation.id);
		return true;
	}
	
}


class ShowBookingService {
	final Repository repository;
	
	public ShowBookingService(Repository repository) {
		this.repository=repository;
	}
	
	void addShow(int movieId, String title, int theaterId, int start, int end) {
		repository.addShow(movieId, title, theaterId, start, end);
	}
	
	public List<Movie> displayMovies(String keyword) {
		return repository.listMovies().values().stream().filter(m->m.title.contains(keyword)).toList();
	}
	
	public List<Theater> displayTheaters(int movieId) {
		return repository.listTheatersByMovie(movieId);
	}
	
	public List<Show> displayShows(int theaterId) {
		return repository.listShowsByTheater(theaterId);
	}
	
	public Reservation bookSeats(int userId, int showId, List<String> seats) {
		return repository.bookSeats(userId,showId,seats);
	}
	
	public boolean cancelBooking(int reservationId, int userId) {
		return repository.cancelBooking(reservationId,userId);
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
		ShowBookingService service = new ShowBookingService(repository);
		service.addShow(1, "ddlj", 1, 0, 1);
		service.addShow(2, "krish", 1, 1, 2);
		service.addShow(3, "kkkg", 1, 2, 3);
		
		System.out.println(service.displayMovies("k"));
		System.out.println(service.displayTheaters(2));
		System.out.println(service.displayShows(1));
		
		Set<Integer> reservations = new HashSet<Integer>();
		List<Thread> requests = new ArrayList<>();
		requests.add(new Thread(()->{
			Reservation r = service.bookSeats(1, 1, new ArrayList<>(List.of("A1","B1")));
			if(r!=null) reservations.add(r.id);
		}));
		requests.add(new Thread(()->{
			Reservation r = service.bookSeats(2, 1, new ArrayList<>(List.of("B1","C1")));
			if(r!=null) reservations.add(r.id);
		}));
		requests.add(new Thread(()->{
			Reservation r = service.bookSeats(3, 2, new ArrayList<>(List.of("B1","C1")));
			if(r!=null) reservations.add(r.id);
		}));
		for(Thread r: requests) {
			r.start();
		}
		for(Thread r: requests) {
			r.join();
		}
		assertEquals(2, reservations.size());
	}

}
