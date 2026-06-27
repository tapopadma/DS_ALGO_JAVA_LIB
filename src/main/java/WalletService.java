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
	boolean deposit(int userId, int amount);
	boolean withdraw(int userId, int amount);
	Wallet getWallet(int userId);
}

enum TransactionType {
	DEBIT,CREDIT;
}

enum TransactionStatus {
	CREATED,PENDING,TIMEDOUT,DECLINED,APPROVED;
}

class Transaction {
	static final AtomicInteger ids = new AtomicInteger();
	
	int id;
	TransactionType type;
	TransactionStatus status;
	int amount;
	int userId;
	int timestamp;
	
	public Transaction(TransactionType t, TransactionStatus s, int a, int u) {
		id=ids.incrementAndGet();
		type=t;status=s;amount=a;userId=u;
		timestamp=(int)(System.currentTimeMillis()/1000L);
	}
	@Override
	public String toString() {
		return id+" "+type+" "+status+" "+amount+" "+userId+" "+timestamp;
	}
}

class Wallet {
	static final AtomicInteger ids = new AtomicInteger();
	int id;
	int userId;
	int balance;
	List<Transaction> transactions;
	
	public Wallet(int u) {
		userId=u;balance=0;transactions=new ArrayList<Transaction>();
		id=ids.incrementAndGet();
	}
	
	public TransactionStatus deposit(int amount) {
		Transaction transaction = new Transaction(TransactionType.CREDIT, TransactionStatus.APPROVED, amount, userId);
		transactions.add(transaction);
		if(transaction.status.equals(TransactionStatus.APPROVED)) {
			balance += amount;
		}
		return transaction.status;
	}
	public TransactionStatus withdraw(int amount) {
		Transaction transaction = new Transaction(TransactionType.DEBIT, TransactionStatus.APPROVED, amount, userId);
		if(balance>=amount && transaction.status.equals(TransactionStatus.APPROVED)) {
			balance -= amount;
		} else {
			transaction.status=TransactionStatus.DECLINED;
		}
		transactions.add(transaction);
		return transaction.status;
	}
}

class InmemoryRepository implements Repository{
	final Map<Integer, Wallet> wallets;
	
	public InmemoryRepository() {
		wallets = new ConcurrentHashMap<Integer, Wallet>();
	}

	@Override
	public boolean deposit(int userId, int amount) {
		var result = new Object() {boolean success=false;};
		wallets.compute(userId,(k,v)-> {
			if(v==null)v=new Wallet(userId);
			TransactionStatus status = v.deposit(amount);
			result.success = status.equals(TransactionStatus.APPROVED);
			return v;
		});
		return result.success;
	}

	@Override
	public boolean withdraw(int userId, int amount) {
		var result = new Object() {boolean success=false;};
		wallets.compute(userId,(k,v)-> {
			if(v==null)return v;
			TransactionStatus status = v.withdraw(amount);
			result.success = status.equals(TransactionStatus.APPROVED);
			return v;
		});
		return result.success;
	}
	@Override
	public Wallet getWallet(int userId) {
		return wallets.get(userId);
	}
	
}

class WalletService {
	final Repository repository;
	
	public WalletService(Repository repository) {
		this.repository=repository;
	}
	
	public boolean deposit(int userId, int amount) {
		return repository.deposit(userId,amount);
	}
	public boolean withdraw(int userId, int amount) {
		return repository.withdraw(userId,amount);
	}
}

package design;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.*;
import org.junit.jupiter.api.*;

class DesignTest {
	

	@Test
	public void testBasic() throws Exception {
		Repository repository = new InmemoryRepository();
		WalletService service = new WalletService(repository);
		
		assertTrue(service.deposit(1, 10));
		assertTrue(service.withdraw(1, 5));
		assertFalse(service.withdraw(1, 10));
		assertEquals(5, repository.getWallet(1).balance);
		System.out.println(repository.getWallet(1).transactions);
	}

}
