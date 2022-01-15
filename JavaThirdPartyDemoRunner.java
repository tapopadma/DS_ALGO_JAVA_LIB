import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

import java.util.*;
import java.util.stream.*;
import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.junit.*;
import org.json.simple.*;
import org.json.simple.parser.*;
import org.apache.commons.lang3.tuple.*;
import org.apache.commons.lang3.time.*;

public class JavaThirdPartyDemoRunner {

	@Test
	public void test_jsonSimple_functions() throws ParseException{
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("user_id", 123);
		jsonObject.put("email", "abc");
		jsonObject.put("name", "xyz");
		JSONArray ranks = new JSONArray();
		ranks.add(1);ranks.add(2);ranks.add(3);
		jsonObject.put("ranks", ranks);
		assertEquals(jsonObject.toJSONString(), 
			"{\"ranks\":[1,2,3],\"user_id\":123,\"name\":\"xyz\",\"email\":\"abc\"}");
		JSONParser parser = new JSONParser();
		JSONObject result = (JSONObject)parser.parse(
			"{\"ranks\":[1,2,3],\"user_id\":123,\"name\":\"xyz\",\"email\":\"abc\"}");
		assertEquals(result.get("user_id"),123L);
		assertEquals(result.get("email"),"abc");
		assertEquals(result.get("name"),"xyz");
		List<Long> rankList = new ArrayList<>();
		for(Object r: (JSONArray)result.get("ranks")){
			rankList.add((long)r);
		}
		assertArrayEquals(rankList.toArray(new Long[0]), new Long[]{1L,2L,3L});
	}

	@Test
	public void test_apacheFunctions() throws Exception{
		Pair<String,Integer> p= Pair.of("mango",9);
		assertEquals(p.getKey(),"mango");
		assertEquals(p.getValue().intValue(),9);
		Map<Pair<String,Integer>,Integer>mm=new HashMap<>();
		mm.put(Pair.of("opp",23),33);
		mm.put(Pair.of("opp",23),32);
		assertEquals(mm.get(Pair.of("opp",23)).intValue(),32);
	}
	
	public static void main(String[] args){
		JUnitCore.main("JavaThirdPartyDemoRunner");
	}
}