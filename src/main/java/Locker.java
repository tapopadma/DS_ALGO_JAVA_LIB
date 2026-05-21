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


enum SlotSize {
	SMALL,MEDIUM,LARGE;
}

class SlotAccessCode {
	String accessCode;
	long expiry;
	
	public SlotAccessCode(String a, long e) {
		accessCode=a;expiry=e;
	}
	
	boolean hasExpired() {
		long now = System.currentTimeMillis();
		return now > expiry;
	}
}

class Slot {
	int id;
	SlotSize size;
	SlotAccessCode accessCode;
	Slot(int i, SlotSize s) {
		id=i;size=s;
	}
}

class Locker {
	Map<SlotSize, Queue<Slot>> freeSlots;
	Map<String, Slot> slotsByAccessCodes;
	
	Locker() {
		freeSlots = new HashMap<SlotSize, Queue<Slot>>();
		freeSlots.put(SlotSize.SMALL, new ArrayDeque<Slot>());
		freeSlots.put(SlotSize.MEDIUM, new ArrayDeque<Slot>());
		freeSlots.put(SlotSize.LARGE, new ArrayDeque<Slot>());
		slotsByAccessCodes = new HashMap<>();
	}
	
	void addSlot(int slot, SlotSize size) {
		freeSlots.get(size).add(new Slot(slot,size));
	}
	
	void restoreExpiredSlots() {
		List<String> affected = new ArrayList<>();
		for(String accessCode: slotsByAccessCodes.keySet()) {
			Slot target = slotsByAccessCodes.get(accessCode);
			if(target.accessCode==null||target.accessCode.hasExpired()) {
				target.accessCode = null;
				affected.add(accessCode);
				freeSlots.get(target.size).add(target);
			}
		}
		for(String e: affected) {
			slotsByAccessCodes.remove(e);
		}
	}
	
	public boolean drop(SlotSize size) {
		restoreExpiredSlots();
		if(freeSlots.get(size).isEmpty())return false;
		Slot free = freeSlots.get(size).poll();
		String accessCode = size.name()+"-001";//UUID.randomUUID().toString();
		long expiry = System.currentTimeMillis()+1000*3600*72;
		free.accessCode=new SlotAccessCode(accessCode, expiry);
		slotsByAccessCodes.put(accessCode, free);
		return true;
	}
	public boolean pickUp(String accessCode) {
		if(!slotsByAccessCodes.containsKey(accessCode)) return false;
		Slot targetSlot = slotsByAccessCodes.get(accessCode);
		if(targetSlot.accessCode==null || targetSlot.accessCode.hasExpired()) return false;
		targetSlot.accessCode = null;
		slotsByAccessCodes.remove(accessCode);
		freeSlots.get(targetSlot.size).add(targetSlot);
		return true;
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
		Locker locker = new Locker();
		locker.addSlot(1, SlotSize.SMALL);
		locker.addSlot(2, SlotSize.MEDIUM);
		locker.addSlot(3, SlotSize.LARGE);
		assertTrue(locker.drop(SlotSize.LARGE));
		assertFalse(locker.drop(SlotSize.LARGE));
		assertTrue(locker.pickUp("LARGE-001"));
		assertFalse(locker.pickUp("LARGE-001"));
	}
	
}
