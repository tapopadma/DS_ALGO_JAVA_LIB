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


class Cell {
	int x, y;
	Cell(int x, int y) {
		this.x=x;this.y=y;
	}
}

class Snake {
	static final int[][] delta = new int[][] {{0,1},{1,0},{0,-1},{-1,0}};
	Deque<Cell> snake;
	int score;
	int direction;// R D L U
	int n,m;
	Set<String> foods;
	boolean running;
	
	Snake(int n, int m, int x, int y) {
		snake = new ArrayDeque<>();
		snake.add(new Cell(x,y));
		score = 0;
		direction = 0;
		this.n=n;this.m=m;
		running=true;
		foods = new HashSet<>();
	}
	
	boolean withinBoundary(int x, int y) {
		return x>=0&&x<n&&y>=0&&y<m;
	}
	
	boolean intersects(int x, int y) {
		List<Cell> list = new ArrayList<>();
		for(Cell c: snake) {
			list.add(c);
		}
		for(int i=0;i<list.size()-1;++i) {
			if(list.get(i).x==x&&list.get(i).y==y)return true;
		}
		return false;
	}
	
	void move() {
		int hx = snake.peekFirst().x, hy = snake.peekFirst().y;
		hx += delta[direction][0];hy += delta[direction][1];
		if(!withinBoundary(hx,hy)||intersects(hx,hy)) {
			running=false;return;
		}
		snake.addFirst(new Cell(hx,hy));
		if(foods.contains(format(hx,hy))) {
			foods.remove(format(hx,hy));
			++score;
		} else snake.pollLast();
	}

	void turn(boolean reverse) {
		if(!running)return;
		direction = (direction + (reverse?-1:1) + delta.length)%delta.length;
	}
	
	void turn() {
		turn(false);
	}
	
	void setFood(int x, int y) {
		if(!running)return;
		foods.add(format(x,y));
	}
	
	String format(int x, int y) {
		return x+","+y;
	}
	
	int getScore() {
		return score;
	}
	
	boolean getStatus() {
		return running;
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
		Snake snake = new Snake(4, 4, 0, 0);
		snake.setFood(0, 1);
		snake.setFood(0, 2);
		snake.setFood(1, 1);
		snake.setFood(2, 2);
		assertTrue(snake.getStatus());
		snake.move();
		snake.turn();
		snake.move();
		assertEquals(2, snake.getScore());
		snake.move();
		snake.turn(true);
		snake.move();
		assertEquals(3, snake.getScore());
		snake.turn(true);
		snake.move();
		snake.move();
		assertEquals(4, snake.getScore());
		assertTrue(snake.getStatus());
		snake.setFood(0, 3);
		snake.turn();
		snake.move();
		assertEquals(5, snake.getScore());
		snake.turn();
		snake.setFood(1, 3);
		snake.move();
		assertEquals(6, snake.getScore());
		snake.turn();
		snake.move();
		assertFalse(snake.getStatus());
	}
	
}
