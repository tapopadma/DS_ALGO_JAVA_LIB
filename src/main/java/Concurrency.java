import java.util.*;

public class Concurrency{
	static class ReaderWriterDemo{
		class Buffer{
			private String message;
			private boolean canWrite=true;
			public synchronized void write(String message) throws InterruptedException{
				while(!canWrite){
					this.wait();//waits until a reader notifies.
				}
				//enters CS
				canWrite=false;
				this.message=message;
				this.notifyAll();//notfies all (technically readers) and exits CS.
			}
			public synchronized String read() throws InterruptedException{
				while(canWrite){
					this.wait();//waits until a writer notifies.
				}
				//enters CS
				canWrite=true;
				this.notifyAll();//notfies all (technically writers)
				return message;//exits CS.
			}
		}
		class Writer implements Runnable{
			private Buffer buffer;
			public Writer(Buffer b){
				this.buffer=b;
			}
			public void run(){
				String[] messages=new String[]{
					"Hi!","Hope","you","are","well","<eom>"
				};
				for(String message:messages){
					try{
						Thread.sleep(3000);//to appear heavy operation.
						buffer.write(message);	
					}catch(InterruptedException e){
						System.out.println(e.getMessage());
					}
				}
			}
		}
		class Reader implements Runnable{
			private Buffer buffer;
			public Reader(Buffer b){
				this.buffer=b;
			}
			public void run(){
				while(true){
					try{
						String message = buffer.read();
						System.out.println("received: "+message);
						if(message.equals("<eom>"))
							break;	
						Thread.sleep(3000);//to appear heavy operation.
					}catch(InterruptedException e){
						System.out.println(e.getMessage());
					}
				}
			}
		}
		public void start(){
			Buffer buffer=new Buffer();
			Writer writer=new Writer(buffer);
			Reader reader=new Reader(buffer);
			new Thread(writer).start();
			new Thread(reader).start();
		}
	}

	static class DiningPhilosopherDemo{
		class ChopStick{
			public int i;
			public ChopStick(int i){
				this.i=i;
			}
		}
		class Philosopher implements Runnable{
			private int i;
			private ChopStick left;
			private ChopStick right;
			public Philosopher(int i,ChopStick left, ChopStick right){
				this.i=i;
				this.left=left;
				this.right=right;
			}
			public void run(){
				while(true){
					try{
						System.out.println("Philosopher "+i+" thinking.");
						Thread.sleep(1000);
						System.out.println("Philosopher "+i+" hungry.");
						synchronized(left){
							System.out.println("Philosopher "+i+" picking ChopStick "+left.i+" .");
							synchronized(right){
								System.out.println("Philosopher "+i+" picking ChopStick "+right.i+" .");
								Thread.sleep(1000);
								System.out.println("Philosopher "+i+" eating.");
								Thread.sleep(1000);
								System.out.println("Philosopher "+i+" putting down ChopStick "+right.i+" .");
							}
							System.out.println("Philosopher "+i+" putting down ChopStick "+left.i+" .");
						}
					}catch(InterruptedException e){
						System.out.println(e.getMessage());
					}
				}
			}
		}
		public void start(){
			int n=5;
			ChopStick[] c = new ChopStick[n];
			for(int i=0;i<n;++i)c[i]=new ChopStick(i);
			Philosopher[] p=new Philosopher[n];
			p[0]=new Philosopher(0,c[1],c[0]);//Break deadlock.
			for(int i=1;i<n;++i)p[i]=new Philosopher(i,c[i],c[(i+1)%n]);
			for(int i=0;i<n;++i)new Thread(p[i]).start();
		}
	}
	public static void main(String[] args){
		// new ReaderWriterDemo().start();
		new DiningPhilosopherDemo().start();
	}
}