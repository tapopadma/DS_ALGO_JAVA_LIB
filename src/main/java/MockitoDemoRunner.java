import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.junit.Assert.*;

import java.util.*;
import org.junit.runner.*;
import org.junit.*;
import org.mockito.*;
import org.mockito.junit.*;

public class MockitoDemoRunner {
	//Mockito.verify: verify how many times a method of an object was invoked.
	//
	//Mockito.when: apply precondition on a method invokation using a object.
	//
	//ArgumentMatchers.eq, any: if it's applied to atleast 1 param in the precondition
	// then it's applied to all the param of that precondition.
	//
	//rule: used for initialising mockito in one of the 3 ways:
	// 1 - @RunWith(MockitoJUnitRunner.class)
	// 2 - MockitoAnnotations.initMocks(this)
	// 3 - @Rule public MockitoRule initRule = MockitoJUnit.rule();
	//
	//@Mock: alternative to Type mockedObject=Mockito.mock(Type.class);
    //
	//@Spy: unless specified using when, this invokes the real method. Just
	//opposite to @Mock.
	//
	//@InjectMocks: enables auto injection of mock dependencies into the object.

	@Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

	class B{
		public String print(){
			return "hey there";
		}
		public int gcd(int x, int y){
			return y==0?x:gcd(y,x%y);
		}
	}

	class A{
		B b;
		public String print(){
			return b.gcd(5,10)==5?b.print():"oops";
		}
	}

	@InjectMocks A a=new A();
	@Mock B b;
	@Spy B bSpy=new B();

	@Test
	public void test_mockitoFunctions(){
		when(b.print()).thenReturn("hi dude");
		when(b.gcd(eq(5),anyInt())).thenReturn(5);
		assertEquals(a.print(), "hi dude");
		verify(b,times(1)).print();
		verify(b,times(1)).gcd(5,10);
		assertEquals(bSpy.print(), "hey there");
		when(b.gcd(anyInt(),anyInt())).thenReturn(25);
		assertEquals(a.print(), "oops");
		when(bSpy.gcd(-1,-1)).thenThrow(new RuntimeException("NA"));
		RuntimeException ex=assertThrows(RuntimeException.class,()->bSpy.gcd(-1,-1));
		assertEquals(ex.getMessage(),"NA");
	}

	public static void main(String[] args){
		JUnitCore.main("MockitoDemoRunner");
	}
}