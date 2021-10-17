import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

public class Solution {



    // Note that HackerRank does NOT have a way to create separate classes outside of this file.
    // As a workaround, we recommend creating static inner classes instead.

	static int getState(String state) {
		int ret = 0;
		switch (state) {
		case "HEALTHY":
			ret = 0;
			break;
		case "SICK":
			ret = 1;
			break;
		case "RECOVERING":
			ret = 2;
			break;
		default:
			break;
		}
		return ret;
	}
	
	static String getState(int state) {
		return new String[] {"HEALTHY",
		"SICK",
		"RECOVERING"}[state];
	}
	
    // Complete the traceDisease function below.
    static String[] traceDisease(String[] initialStates) {
    	List<String> res = new ArrayList<>();
    	Map<String, Integer> officeNo = new HashMap<>();
    	Map<String, Integer> memberNo = new HashMap<>();
    	int officeIndex = 0;
    	int n = initialStates.length;
    	int [][] memberToOfficeList = new int[n][];
    	int [] states = new int[n];
    	TreeSet<Integer> sickOffices = new TreeSet<>();
    	String memberList="";
    	for(int i=0;i<n;++i) {
    		String [] data = initialStates[i].split(" ");
    		String member = data[0];
    		memberList += member + " ";
    		memberNo.put(member, i);
    		states[i] = getState(data[1]);
    		int m = data.length;
    		memberToOfficeList[i] = new int[m - 2];
    		for(int j=2;j<m;++j) {
    			String office = data[j];
    			if(!officeNo.containsKey(office)) {
    				officeNo.put(office, officeIndex++);
    			}
    			memberToOfficeList[i][j - 2] = officeNo.get(office);
    		}
    		if(states[i] != 0) {
    			sickOffices.add(memberToOfficeList[i][0]);
    		}
    	}
    	res.add(memberList);
    	String stateStr = "";
    	for(int i=0;i<n;++i) {
    		stateStr += (getState(states[i]) + " ");
    	}
    	res.add(stateStr);stateStr = "";
    	int noOfDays = 0;
    	while(!sickOffices.isEmpty() && res.size() <= 365) {
    		for(int i=0;i<n;++i) {
    			if(states[i] == 0) {
    				if(sickOffices.contains(
    						memberToOfficeList[i][noOfDays%memberToOfficeList[i].length])) {
    					states[i] = 1;
    				}
    			} else {
    				states[i] = (states[i] + 1)%3;
    			}
    		}
    		sickOffices.clear();
    		++noOfDays;
    		for(int i=0;i<n;++i) {
    			if(states[i] != 0) {
    				sickOffices.add(
    						memberToOfficeList[i][noOfDays%memberToOfficeList[i].length]);
    			}
    		}
    		for(int i=0;i<n;++i) {
        		stateStr += (getState(states[i]) + " ");
        	}
        	res.add(stateStr);
        	stateStr = "";
    	}
    	res.add(String.valueOf(noOfDays + 1));
    	String [] result = new String[res.size()];
    	for(int i=0;i<res.size();++i) {
    		result[i] = res.get(i);
    	}
    	return result;
    }

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        int initialStatesCount = scanner.nextInt();
        scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");

        String[] initialStates = new String[initialStatesCount];

        for (int i = 0; i < initialStatesCount; i++) {
            String initialStatesItem = scanner.nextLine();
            initialStates[i] = initialStatesItem;
        }

        String[] res = traceDisease(initialStates);

        for (int i = 0; i < res.length; i++) {
            bufferedWriter.write(res[i]);

            if (i != res.length - 1) {
                bufferedWriter.write("\n");
            }
        }

        bufferedWriter.newLine();

        bufferedWriter.close();

        scanner.close();
    }
}
