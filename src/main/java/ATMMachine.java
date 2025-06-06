package src.main.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.JUnitCore;

/**
 * 
 * @author tapopadma
 * 1 - withdraw
 */
public class ATMMachine{
	class ATM{
		Map<Integer, Integer>available;
		public ATM(Map<Integer, Integer>available) {
			this.available=available;
		}
		
		class InsufficientBalanceException extends Exception{
			public InsufficientBalanceException(String message) {
				super(message);
			}
		}
		
		Map<Integer, Integer> withDrawGreedily(int amount) 
				throws InsufficientBalanceException{
			List<Integer> notes=available.keySet().stream()
					.sorted((d1,d2)->Integer.valueOf(d2).compareTo(d1))
					.collect(Collectors.toList());
			if(notes.stream().reduce(amount, 
					(remainder, e)->remainder-e*Math.min(remainder/e, available.get(e)))
			.equals(0)) {
				Map<Integer, Integer> result=new HashMap<>();
				for(int note:notes) {
					int withDraw=Math.min(amount/note, available.get(note));
					amount-=withDraw*note;
					available.put(note, available.get(note)-withDraw);
					result.put(note, withDraw);
				}
				return result;
			}
			throw new InsufficientBalanceException("");
		}
		
		Map<Integer, Integer> withDrawAnyPossibleWay(int amount) 
				throws InsufficientBalanceException{
			throw new InsufficientBalanceException("unimplemented");
		}
		
		public Map<Integer, Integer> withDraw(int amount) 
				throws InsufficientBalanceException{
			Map<Integer, Integer> result=new HashMap<>();
			try {
				result=withDrawGreedily(amount);
			}catch(InsufficientBalanceException e) {
				try {
					result=withDrawAnyPossibleWay(amount);
				}catch(InsufficientBalanceException e1) {
					throw e1;
				}
			}
			return result;
		}
	}

	@Test
	public void withDraw_ifBalanceAvailable_succeeds() 
			throws ATM.InsufficientBalanceException {
		Map<Integer,Integer> available=new HashMap<>();
		available.put(100,1);
		available.put(200,2);
		available.put(500,1);
		available.put(1000,2);
		ATM atm=new ATM(available);
		Map<Integer, Integer>withDraw=atm.withDraw(1200);
		assertEquals(1, withDraw.get(1000).intValue());
		assertEquals(1, withDraw.get(200).intValue());
		withDraw=atm.withDraw(1300);
		assertEquals(1, withDraw.get(1000).intValue());
		assertEquals(1, withDraw.get(200).intValue());
		assertEquals(1, withDraw.get(100).intValue());
	}
	
	@Test
	public void withDraw_ifBalanceUnavailable_throwsException() 
			throws ATM.InsufficientBalanceException {
		Map<Integer,Integer> available=new HashMap<>();
		available.put(100,1);
		available.put(200,2);
		available.put(500,1);
		available.put(1000,2);
		ATM atm=new ATM(available);
		ATM.InsufficientBalanceException exception=
				assertThrows(ATM.InsufficientBalanceException.class, 
				()->atm.withDraw(110));
		assertEquals(exception.getMessage(), "unimplemented");
	}
	
	public static void main(String[] args) {
		JUnitCore.main("src.main.java.ATMMachine");
	}
	
}