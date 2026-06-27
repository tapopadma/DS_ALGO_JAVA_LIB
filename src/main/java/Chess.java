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


interface Repository {
	public boolean move(int teamId, int x, int y, int x1, int y1) throws Exception ;
}

enum Team {
	BLACK(2), WHITE(1);
	int id;
	Team(int id){
		this.id=id;
	}
	static Team of(int i) {
		for(Team t: Team.values()) {
			if(t.id==i)return t;
		}
		return null;
	}
	Team opponent() {
		if(this.id==1)return Team.of(2);
		return Team.of(1);
	}
}

class Cell {
	int x, y;
	
	Cell(int x, int y){
		this.x=x;this.y=y;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Cell))return false;
		if(o==this)return true;
		Cell c = (Cell)o;
		return x==c.x && y==c.y;
	}
	@Override
	public int hashCode() {
		return Objects.hash(x,y);
	}
	@Override
	public String toString() {
		return x+","+y;
	}
	
	public Cell adjacent(List<Integer> diff) {
		return new Cell(x+diff.get(0), y+diff.get(1));
	}
	
	public List<Cell> adjacents(List<List<Integer>> diff) {
		List<Cell> list = new ArrayList<Cell>();
		for(List<Integer> d: diff) {
			list.add(adjacent(d));
		}
		return list;
	}
	
	public List<Cell> continuousAdjacents(List<List<Integer>> diff) {
		List<Cell> list = new ArrayList<Cell>();
		for(List<Integer> d: diff) {
			int xx = x + d.get(0), yy = y + d.get(1);
			while(xx>=0&&xx<8&&yy>=0&&yy<8) {
				list.add(new Cell(xx,yy));
			}
		}
		return list;
	}
}

enum PieceStatus {
	ACTIVE,INACTIVE;
}

enum PieceType {
	KING,QUEEN,BISHOP,KNIGHT,ROOK,PAWN;
}

abstract class Piece {
	Team team;
	PieceStatus status;
	PieceType type;
	Cell currentCell;
	static final Map<PieceType, Integer> points = Map.of(
			PieceType.KING, 10,
			PieceType.QUEEN, 8,
			PieceType.ROOK, 6,
			PieceType.BISHOP, 5,
			PieceType.KNIGHT, 4,
			PieceType.PAWN, 1
	);
	int id;
	static final AtomicInteger ids = new AtomicInteger(); 
	
	public Piece(Team t, PieceType ty, Cell c) {
		team=t;status=PieceStatus.ACTIVE;type=ty;currentCell=c;
		id=ids.incrementAndGet();
	}
	
	public void markInActive() {
		this.status=PieceStatus.INACTIVE;
	}
	
	public abstract List<Cell> reachableCells();
	
	public int points() {
		return this.points.get(type);
	}
}

class King extends Piece {
	public King(Team t, Cell c) {
		super(t,PieceType.KING,c);
	}

	@Override
	public List<Cell> reachableCells() {
		return currentCell.adjacents(List.of(
				List.of(0,1),
				List.of(0,-1),
				List.of(1,0),
				List.of(-1,0),
				List.of(1,1),
				List.of(-1,-1),
				List.of(-1,1),
				List.of(1,-1)
		));
	}

}

class Queen extends Piece {
	public Queen(Team t, Cell c) {
		super(t,PieceType.QUEEN,c);
	}

	@Override
	public List<Cell> reachableCells() {
		return currentCell.continuousAdjacents(List.of(
				List.of(0,1),
				List.of(0,-1),
				List.of(1,0),
				List.of(-1,0),
				List.of(1,1),
				List.of(-1,-1),
				List.of(-1,1),
				List.of(1,-1)));
		
	}

}

class Bishop extends Piece {
	public Bishop(Team t, Cell c) {
		super(t,PieceType.BISHOP,c);
	}

	@Override
	public List<Cell> reachableCells() {
		return currentCell.continuousAdjacents(List.of(
				List.of(1,1),
				List.of(-1,-1),
				List.of(-1,1),
				List.of(1,-1)));
	}
	
}

class Knight extends Piece {
	public Knight(Team t, Cell c) {
		super(t,PieceType.KNIGHT,c);
	}

	@Override
	public List<Cell> reachableCells() {
		return currentCell.adjacents(List.of(
				List.of(1,2),
				List.of(1,-2),
				List.of(-1,2),
				List.of(-1,-2)));
	}

}

class Rook extends Piece {
	public Rook(Team t, Cell c) {
		super(t,PieceType.ROOK,c);
	}

	@Override
	public List<Cell> reachableCells() {
		return currentCell.continuousAdjacents(List.of(
				List.of(0,1),
				List.of(0,-1),
				List.of(1,0),
				List.of(-1,0)));
	}

}

class Pawn extends Piece {
	public Pawn(Team t, Cell c) {
		super(t,PieceType.PAWN,c);
	}

	@Override
	public List<Cell> reachableCells() {
		return currentCell.adjacents(List.of(
				List.of(1,0),
				List.of(2,0)
				));
	}
	
}

class GameStatus {
	enum Status {
		CREATED,RUNNING,STALEMATE, CHECKMATE;
	}
	Status status;
	Team winner;
	GameStatus() {
		status=Status.CREATED;
	}
}

class Board {
	final Map<Cell, Piece> map;
	final Map<Team, Integer> score;
	final GameStatus status;
	
	Board(Map<Cell, Piece> map) {
		this.map=map;
		score = new HashMap<Team, Integer>();
		status= new GameStatus();
	}

	Piece getPieceAt(Cell cell) {
		return map.get(cell);
	}
	
	Piece getKing(Team team) {
		for(Piece piece: map.values()) {
			if(piece.team.equals(team)&&piece.type.equals(PieceType.KING)) return piece;
		}
		return null;
	}
	
	boolean isCheckmate(Piece king) {
		List<Cell> cells = king.reachableCells();
		boolean check=false;
		for(Cell cell: cells) {
			Piece piece = getPieceAt(cell);
			if(piece == null) return false;
			if(!piece.team.equals(king.team))check=true;
		}
		return check;
	}
	
	boolean isStalemate(Piece king) {
		List<Cell> cells = king.reachableCells();
		boolean check=false;
		for(Cell cell: cells) {
			Piece piece = getPieceAt(cell);
			if(piece == null) return false;
			if(!piece.team.equals(king.team))check=true;
		}
		return !check;
	}
	
	void updateStatus(Team team) throws Exception {
		Piece king = getKing(team.opponent());
		if(king==null)throw new Exception("king missing");
		if(isCheckmate(king)) {
			status.status=GameStatus.Status.CHECKMATE;
			status.winner=team;
		}
		if(isStalemate(king)) {
			status.status=GameStatus.Status.STALEMATE;
		}
		status.status=GameStatus.Status.RUNNING;
	}
	
	public boolean move(Team team, Cell source, Cell target)  throws Exception {
		if(status.equals(GameStatus.Status.CHECKMATE)||status.equals(GameStatus.Status.STALEMATE)) return false;
		Piece piece = getPieceAt(source);
		if(piece ==null || !piece.reachableCells().contains(target)) return false;
		Piece targetPiece = getPieceAt(target);
		if(targetPiece!=null && targetPiece.team.equals(piece.team)) return false;
		int points = 0;
		if(targetPiece!=null) {
			targetPiece.markInActive();
			points = targetPiece.points();
		}
		map.put(source, null);
		map.put(target, piece);
		score.put(piece.team, score.getOrDefault(piece.team, 0)+points);
		updateStatus(team);
		return true;
	}
}

class InmemoryRepository implements Repository {

	final Board board;
	
	public InmemoryRepository(Board board) {
		this.board=board;
	}
	
	@Override
	public boolean move(int teamId, int x, int y, int x1, int y1) throws Exception {
		return board.move(Team.of(teamId), new Cell(x, y), new Cell(x1, y1));
	}

}

class ChessService {
	private final Repository repository;
	
	public ChessService(Repository repository) {
		this.repository=repository;
	}
	
	public boolean move(int teamId, int x, int y, int x1, int y1)  throws Exception {
		return repository.move(teamId, x, y, x1, y1);
	}
}

package design;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.*;
import org.junit.jupiter.api.*;

class DesignTest {
	

	Cell cell(int x, int y) {
		return new Cell(x,y);
	}
	
	@Test
	public void testBasic() throws Exception {
		Map<Cell,Piece> map = new HashMap<Cell,Piece>();
		map.put(cell(0,0), new Rook(Team.WHITE, cell(0,0)));
		map.put(cell(0,1), new Bishop(Team.WHITE, cell(0,1)));
		map.put(cell(0,2), new Queen(Team.WHITE, cell(0,2)));
		map.put(cell(0,3), new King(Team.WHITE, cell(0,3)));
		map.put(cell(0,4), new Bishop(Team.WHITE, cell(0,4)));
		map.put(cell(0,5), new Knight(Team.WHITE, cell(0,5)));
		map.put(cell(0,6), new Knight(Team.WHITE, cell(0,6)));
		map.put(cell(0,7), new Rook(Team.WHITE, cell(0,7)));
		map.put(cell(1,0), new Pawn(Team.WHITE, cell(1,0)));
		map.put(cell(1,1), new Pawn(Team.WHITE, cell(1,1)));
		map.put(cell(1,2), new Pawn(Team.WHITE, cell(1,2)));
		map.put(cell(1,3), new Pawn(Team.WHITE, cell(1,3)));
		map.put(cell(1,4), new Pawn(Team.WHITE, cell(1,4)));
		map.put(cell(1,5), new Pawn(Team.WHITE, cell(1,5)));
		map.put(cell(1,6), new Pawn(Team.WHITE, cell(1,6)));
		map.put(cell(1,7), new Pawn(Team.WHITE, cell(1,7)));
		map.put(cell(7,0), new Rook(Team.BLACK, cell(7,0)));
		map.put(cell(7,1), new Bishop(Team.BLACK, cell(7,1)));
		map.put(cell(7,2), new Queen(Team.BLACK, cell(7,2)));
		map.put(cell(7,3), new King(Team.BLACK, cell(7,3)));
		map.put(cell(7,4), new Bishop(Team.BLACK, cell(7,4)));
		map.put(cell(7,5), new Knight(Team.BLACK, cell(7,5)));
		map.put(cell(7,6), new Knight(Team.BLACK, cell(7,6)));
		map.put(cell(7,7), new Rook(Team.BLACK, cell(7,7)));
		map.put(cell(6,0), new Pawn(Team.BLACK, cell(6,0)));
		map.put(cell(6,1), new Pawn(Team.BLACK, cell(6,1)));
		map.put(cell(6,2), new Pawn(Team.BLACK, cell(6,2)));
		map.put(cell(6,3), new Pawn(Team.BLACK, cell(6,3)));
		map.put(cell(6,4), new Pawn(Team.BLACK, cell(6,4)));
		map.put(cell(6,5), new Pawn(Team.BLACK, cell(6,5)));
		map.put(cell(6,6), new Pawn(Team.BLACK, cell(6,6)));
		map.put(cell(6,7), new Pawn(Team.BLACK, cell(6,7)));
		Board board = new Board(map);
		Repository repository = new InmemoryRepository(board);
		ChessService service = new ChessService(repository);
		
		assertTrue(service.move(1, 1, 0, 3, 0));
		
	}

}
