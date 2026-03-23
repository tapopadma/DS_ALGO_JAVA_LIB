package src.main.java.excercise;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.Executors;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Arrays;
import org.apache.commons.lang3.tuple.Pair;

import static org.junit.Assert.assertEquals;

public class PatternSimulator {

	static class Bank {

		private static volatile Bank bank;

		static Bank newInstance(int[] count) {
			if(bank!=null)return bank;// optimisation
			synchronized (Bank.class) {// thread safety
				if(bank == null) {
					bank = new Bank();
					++count[0];
				}
				return bank;
			}
		}

		static class BasicRecord {
			private final String headquarters;
			private final List<String> branches;
			private final List<String> customers;
			private final List<Integer> loans;

			BasicRecord(Builder builder) {
				this.headquarters=builder.headquarters;
				this.branches=builder.branches;
				this.customers=builder.customers;
				this.loans=builder.loans;
			}

			static class Builder {
				String headquarters;
				List<String> branches;
				List<String> customers;
				List<Integer> loans;
				Builder(String headquarters) {
					this.headquarters=headquarters;
				}
				Builder branches(List<String> branches) {
					this.branches=branches;
					return this;
				}
				Builder customers(List<String> customers) {
					this.customers=customers;
					return this;
				}
				Builder loans(List<Integer> loans) {
					this.loans=loans;
					return this;
				}
				BasicRecord build() {
					return new BasicRecord(this);
				}
			}
		}

		interface PaymentHandler {
			void pay();
		}

		static class StripePaymentHandler implements PaymentHandler {
			@Override
			public void pay() {}
		}

		static class VisaPaymentHandler implements PaymentHandler {
			@Override
			public void pay() {}
		}

		interface PaymentWebHook {
			void pushConfirmation(boolean ack);
		}

		static class StripeWebHook implements PaymentWebHook {
			@Override
			public void pushConfirmation(boolean ack) {}
		}

		static class VisaWebHook implements PaymentWebHook {
			@Override
			public void pushConfirmation(boolean ack) {}
		}

		static class PaymentWebHookFactory {
			static PaymentWebHook getPaymentWebHook(String platform) {
				switch(platform) {
				case "stripe":
					return new StripeWebHook();
				case "visa":
					return new VisaWebHook();
				default:
					return null;
				}
			}
		}

		interface PaymentAbstractFactory {
			PaymentHandler paymentHandler(); // open api for making payment via gateway.
			PaymentWebHook paymentWebHook(); // the webhook that pushes confirmation to the client only after successful pay.
		}

		static class StripePaymentAbstractFactory implements PaymentAbstractFactory {
			public PaymentHandler paymentHandler() {
				return new StripePaymentHandler();
			}
			public PaymentWebHook paymentWebHook() {
				return new StripeWebHook();
			}
		}

		static class VisaPaymentAbstractFactory implements PaymentAbstractFactory {
			public PaymentHandler paymentHandler() {
				return new VisaPaymentHandler();
			}
			public PaymentWebHook paymentWebHook() {
				return new VisaWebHook();
			}
		}

		static class PaymentAbstractFactoryService {
			PaymentHandler paymentHandler;
			PaymentWebHook paymentWebHook;
			PaymentAbstractFactoryService(PaymentAbstractFactory factory) {
				this.paymentHandler=factory.paymentHandler();
				this.paymentWebHook=factory.paymentWebHook();
			}
			void processPayment() {
				paymentHandler.pay();
				paymentWebHook.pushConfirmation(true);
			}
		}

		interface LoanPaymentStrategy {
			void configure();
		}

		static class EMILoanStrategy implements LoanPaymentStrategy {
			public void configure() {}
		}
		static class LumpsumLoanStrategy implements LoanPaymentStrategy {
			public void configure() {}
		}
		static class LoanRegister {
			private LoanPaymentStrategy strategy;
			public void setStrategy(LoanPaymentStrategy s) {
				tihs.strategy=s;
			}
			public void register(){
				this.strategy.configure();
			}
		}
		static class StatementManager { //subject
			Set<User> users;
			StatementManager() {
				users = new HashSet<>();
			}
			public void subscribe(User user) {
				users.add(user);
			}
			public void unsubscribe(User user) {
				users.remove(user);
			}
			public void release() {
				for(User u: users)u.update("current month");
			}
		}
		static class User { //object
			String name;
			StatementManager(String name) {
				this.name=name;
			}
			public void update(String message) {				
			}
		}
		interface CassandraTransactionWriter {
			boolean committ();
		}		
		static class DiskLogWriter implements CassandraTransactionWriter {
			public boolean committ() {
			}
		}
		static abstract class AbstractCassandraTxWriter implements CassandraTransactionWriter {
			CassandraTransactionWriter writer;
			AbstractCassandraTxWriter(CassandraTransactionWriter writer){
				this.writer=writer;
			}
		}
		static class MemtableWriter extends AbstractCassandraTxWriter {
			MemtableWriter(CassandraTransactionWriter writer) {
				super(writer);
			}
			boolean committ() {
				super().writer.committ();
				write();
			}
		}
		interface PaymentProcessor {
		    void pay(int amount);
		}
		class OldPaymentSystem {
		    public void makePayment(int amount) {
		    }
		}
		class PaymentAdapter implements PaymentProcessor {
		    private OldPaymentSystem oldSystem;
		    public PaymentAdapter(OldPaymentSystem oldSystem) {
		        this.oldSystem = oldSystem;
		    }
		    public void pay(int amount) {
		        oldSystem.makePayment(amount);
		    }
		}
		class BankServer {
			BankDB db;
			BankCache cache;
			BankMessageQueue mq;
			BankServer(BankDB db, BankCache cache, BankMessageQueue mq) {
				this.db=db;this.cache=cache;this.mq=mq;
			}
			void start() {
				db.start();
				cache.start();
				mq.listen();
			}
		}
		abstract class Logger {
		    protected Logger next;
		    public void setNext(Logger next) {
		        this.next = next;
		    }
		    public void log(String level, String message) {
		        if (canHandle(level)) {
		            write(message);
		        } else if (next != null) {
		            next.log(level, message);
		        }
		    }
		    protected abstract boolean canHandle(String level);
		    protected abstract void write(String message);
		}
		class InfoLogger extends Logger {
		    protected boolean canHandle(String level) {
		        return "INFO".equals(level);
		    }
		    protected void write(String message) {
		        System.out.println("INFO: " + message);
		    }
		}
		class DebugLogger extends Logger {
		    protected boolean canHandle(String level) {
		        return "DEBUG".equals(level);
		    }
		    protected void write(String message) {
		        System.out.println("DEBUG: " + message);
		    }
		}
		class ErrorLogger extends Logger {
		    protected boolean canHandle(String level) {
		        return "ERROR".equals(level);
		    }
		    protected void write(String message) {
		        System.out.println("ERROR: " + message);
		    }
		}
		abstract class AccountManager {
			void createAccount(){

			}
			abstract void configureTax();
		}
		class NRIAccountManager extends AccountManager {
			@Override
			void configureTax() {
				setupEPPass();
			}
		}
		class SavingsAccountManager extends AccountManager {
			@Override
			void configureTax() {
				linkAadhar();
			}
		}
	}

    void simulateSingleton() { // instantiating exactly once to avoid redundancy & inconsistency.
    	int[] count = new int[]{0};
    	ExecutorService pool = Executors.newFixedThreadPool(5);
    	for(int i=0;i<50;++i) {
    		pool.submit(()->Bank.newInstance(count));
    	}
    	pool.shutdown();
    	try {
			pool.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
		} catch(InterruptedException e){}
		assertEquals(1, count[0]);
	}

	void simulateFactory() { // choose an implementation based on input param.
		assertEquals(Bank.PaymentWebHookFactory.getPaymentWebHook("stripe") instanceof Bank.StripeWebHook, true);
		assertEquals(Bank.PaymentWebHookFactory.getPaymentWebHook("visa") instanceof Bank.VisaWebHook, true);
	}

	void simulateAbstractFactory() { // generic class using multiple interfaces can inject any type of available
		new Bank.PaymentAbstractFactoryService(new Bank.StripePaymentAbstractFactory()).processPayment();
	}

	void simulateBuilder() { // able to instantiate a class with params selectively using its builder.
		assertEquals(new Bank.BasicRecord.Builder("california").branches(Arrays.asList("seattle","newjersey")).build() instanceof Bank.BasicRecord, true);
	}

	void simulateStrategy() { // simply either of the 2 implementations can be injected to be used in another class.
		Bank.LoanRegister reg = new Bank.LoanRegister();
		reg.setStrategy(new Bank.EMILoanStrategy());
		reg.register();
		reg.setStrategy(new Bank.LumpsumLoanStrategy());
		reg.register();
	}

	void simulateObserver() { // pubsub
		Bank.StatementManager sub = new Bank.StatementManager();
		sub.subscribe(new User("a"));
		sub.subscribe(new User("b"));
		sub.release();
	}

	void simulateDecorator() { // when impls of intf have heirarchy, i.e. child impls can use parent impls in their code.
		CassandraTransactionWriter logWriter = new DiskLogWriter();
		CassandraTransactionWriter writer = new MemtableWriter(logWriter);
		writer.committ();
	}

	void simulateAdapter() { // when impl of an intf can just use another impl in different format.
		OldPaymentSystem oldSystem = new OldPaymentSystem();
		PaymentAdapter adapter = new PaymentAdapter(oldSystem);
		adapter.pay();
	}

	void simulateFacade() { // just a central class within which multiple dependencies are injected.
		BankServer server = new BankServer();
		server.start();
	}

	void simulateChainOfResponsibility() { // adding conditional edges between impls to create a flow chart.
		Logger info = new InfoLogger();
        Logger debug = new DebugLogger();
        Logger error = new ErrorLogger();
        info.setNext(debug);
        debug.setNext(error);
        info.log("INFO", "This is info message");
        info.log("DEBUG", "Debugging details");
        info.log("ERROR", "Something went wrong");
	}

	void simulateTemplate() { // basically the abstract class and its child class pattern
		AccountManager nri = new NRIAccountManager();nri.createAccount();nri.configureTax();
		AccountManager local = new SavingsAccountManager();local.createAccount();local.configureTax();
	}

	public static void main(String[] args) {
		PatternSimulator simulator = new PatternSimulator();
		simulator.simulateSingleton();
		simulator.simulateFactory();
		simulator.simulateAbstractFactory();
		simulator.simulateBuilder();
		simulator.simulateStrategy();
		simulator.simulateObserver();
		simulator.simulateDecorator();
		simulator.simulateAdapter();
		simulator.simulateFacade();
		simulator.simulateChainOfResponsibility();
		simulator.simulateState();
		simulator.simulateCommand();
		simulator.simulateTemplate();
	}
}