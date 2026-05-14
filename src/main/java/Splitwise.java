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

class Transaction {
	int from,to,amount;
	
	Transaction(int f,int t, int a) {
		from=f;to=t;amount=a;
	}
}

class Debt {
	int from, to, amount;
	
	Debt(int f, int t, int a) {
		from=f;to=t;amount=a;
	}
	
	@Override
	public String toString() {
		return from+" "+to+": "+amount;
	}
}

class SplitWise {
	
	List<Transaction> expenses;
	
	SplitWise() {
		expenses = new ArrayList<>();
	}
	
	void addExpense(int from, int to, int amount) {
		expenses.add(new Transaction(from,to,amount));
	}
	
	List<Debt> fetchSummary() {
		Map<Integer, Integer> gave = new HashMap<>();
		for(Transaction transaction: expenses) {
			gave.put(transaction.from, gave.getOrDefault(transaction.from, 0)+transaction.amount);
			gave.put(transaction.to, gave.getOrDefault(transaction.to, 0)-transaction.amount);
		}
		TreeSet<int[]> debits = new TreeSet<>((x,y)->x[0]==y[0]?
				Integer.compare(x[1],y[1]):
				Integer.compare(y[0], x[0]));
		TreeSet<int[]> credits = new TreeSet<>((x,y)->x[0]==y[0]?
				Integer.compare(x[1],y[1]):
				Integer.compare(y[0], x[0]));
		for(int u: gave.keySet()) {
			if(gave.get(u)>0) {
				debits.add(new int[] {gave.get(u),u});
			} else if(gave.get(u)<0) {
				credits.add(new int[] {-gave.get(u),u});
			}
		}
		List<Debt> owes = new ArrayList<>();
		while(!debits.isEmpty()||!credits.isEmpty()) {
			int[] debit = debits.removeFirst();
			int[] credit = credits.removeFirst();
			if(debit[0]==credit[0]) {
				owes.add(new Debt(credit[1], debit[1], debit[0]));
			} else if(debit[0]>credit[0]) {
				debit[0]-=credit[0];
				owes.add(new Debt(credit[1], debit[1], credit[0]));
				debits.add(debit);
			} else {
				credit[0]-=debit[0];
				owes.add(new Debt(credit[1], debit[1], debit[0]));
				credits.add(credit);
			}
		}
		return owes;
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
		SplitWise splitwise = new SplitWise();
		splitwise.addExpense(1, 2, 5);
		splitwise.addExpense(2, 3, 5);
		splitwise.addExpense(3, 1, 5);
		System.out.println(splitwise.fetchSummary());
		splitwise = new SplitWise();
		splitwise.addExpense(1, 2, 5);
		splitwise.addExpense(2, 3, 15);
		System.out.println(splitwise.fetchSummary());
	}
	
}
