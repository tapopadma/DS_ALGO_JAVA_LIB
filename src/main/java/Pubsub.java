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


class Message {
	String payload;
	Message(String payload) {
		this.payload=payload;
	}
}

interface Repository {
	boolean createTopic(String topic);
	boolean publish(String topic, String partition, Message message);
	Message consume(String topic, String partition);
}

class Topic {
	String id;
	final Map<String, List<Message>> messages;
	final Map<String, Integer> offsets;
	
	public Topic(String i) {
		id=i;
		messages = new ConcurrentHashMap<>();
		offsets = new ConcurrentHashMap<>();
	}
	
	public boolean publish(String partition, Message message) {
		messages.compute(partition, (p,m)-> {
			if(m==null) {
				m = new ArrayList<>();
			}
			m.add(message);
			return m;
		});
		return true;
	}
	
	public Message consume(String partition) {
		var result = new Object() {Message message=null;};
		messages.compute(partition, (k,v)-> {
			if(v==null) {
				v = new ArrayList<>();
			}
			offsets.putIfAbsent(partition, 0);
			int offset = offsets.get(partition);
			if(v.size() > offset) {
				result.message = v.get(offset);
				++offset;
				offsets.put(partition, offset);
			}
			return v;
		});
		return result.message;
	}
}

class InmemoryRepository implements Repository {
	
	final Map<String, Topic> topics;
	
	public InmemoryRepository() {
		topics = new ConcurrentHashMap<>();
	}

	@Override
	public boolean createTopic(String topic) {
		var result = new Object() {boolean success = false;};
		topics.compute(topic, (k,v)-> {
			if(v==null) {
				v = new Topic(topic);
				result.success = true;
				return v;
			} else return v;
		});
		return result.success;
	}

	@Override
	public boolean publish(String topic, String partition, Message message) {
		return topics.get(topic).publish(partition,message);
	}

	@Override
	public Message consume(String topic, String partition) {
		return topics.get(topic).consume(partition);
	}
	
}

class PubsubService {
	final Repository repository;
	
	public PubsubService(Repository repository) {
		this.repository=repository;
	}
	
	public boolean createTopic(String topic) {
		return repository.createTopic(topic);
	}
	public boolean publish(String topic, String partition, Message message) {
		return repository.publish(topic,partition,message);
	}
	public Message consume(String topic, String partition) {
		return repository.consume(topic,partition);
	}
}

package design;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.*;
import org.junit.jupiter.api.*;

class DesignTest {
	

	@Test
	public void testPubsub() throws Exception {
		Repository repository = new InmemoryRepository();
		PubsubService service = new PubsubService(repository);
		service.createTopic("topic-1");
		service.publish("topic-1", "1", new Message("hello"));
		assertEquals("hello", service.consume("topic-1", "1").payload);
		assertEquals(null, service.consume("topic-1", "1"));
	}
	
	@Test
	public void testPubsubBulk() throws Exception {
		Repository repository = new InmemoryRepository();
		PubsubService service = new PubsubService(repository);
		String topic = "topic-2";
		service.createTopic(topic);
		List<Thread> requests = new ArrayList<Thread>();
		for(int i=0;i<10;++i) {
			for(int j=0;j<4;++j) {
				String partition = String.valueOf(i)+""+j;
				requests.add(new Thread(()-> service.publish(topic, partition, new Message(partition))));
			}
		}
		Collections.reverse(requests);
		for(Thread r: requests) {
			r.start();
		}
		for(Thread r: requests) {
			r.join();
		}
		for(int i=0;i<10;++i) {
			for(int j=0;j<4;++j) {
				String partition = String.valueOf(i)+""+j;
				System.out.println(service.consume(topic, partition).payload);
			}
		}
	}

}
