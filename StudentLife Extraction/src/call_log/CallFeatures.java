package call_log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import audio.AudioEntry;
import extraction.Features;

public class CallFeatures implements Features<CallEntry> {
	private HashMap<String, ArrayList<Integer>> uniqueNumbers; //store unique numbers and how often they called within each window
	private HashMap<String, Integer> windowUniqueNums; //store unique numbers and how often they called within current window
	private int numCalls;
	private int uniqueCalls;
	private long totalCallDuration;
	private int inContacts;
	private int[] type; //empty, ingoing, outgoing, missed
	
	public CallFeatures() {
		uniqueNumbers = new HashMap<String, ArrayList<Integer>>();
		windowUniqueNums = new HashMap<String, Integer>();
		reset();		
	}
	
	public CallEntry getEntry(String[] row) {
		return new CallEntry(row);
	}

	public void updateFromLine(CallEntry entry) {
		numCalls++;
		if(!windowUniqueNums.containsKey(entry.getCallNumber()))
			uniqueCalls++;
		totalCallDuration += entry.getCallDuration();
		if(entry.isContact())
			inContacts++;
		type[entry.getCallType()]++;
	}

	public void updateFromWindow(ArrayBlockingQueue<CallEntry> currentWindow) {
		for(Map.Entry<String, Integer> entry : windowUniqueNums.entrySet()) {
			ArrayList<Integer> runningList = uniqueNumbers.get(entry.getKey());
			if(runningList == null)		//this number appears for the first time in this segment
				uniqueNumbers.put(entry.getKey(), new ArrayList<Integer>());
			else	//otherwise, create other entry in current list to mark this segment
				runningList.add(entry.getValue());
		}
		windowUniqueNums.clear();	//reset window count in preparation for next window
	}

	public void reset() {
		numCalls = 0; uniqueCalls = 0; totalCallDuration = 0; inContacts = 0;
		type = new int[4];
	}

	public String[] header() {
		String[] row = new String[8];
		row[0] = "Time";
		row[1] = "Num Calls";
		row[2] = "Num Unique Calls";
		row[3] = "Total Call Duration";
		row[4] = "Num Calls From Contact List";
		row[5] = "Num Ingoing";
		row[6] = "Num Outgoing";
		row[7] = "Num Missed";
		return row;
	}

	public String[] toRow(Long startTime) {
		String[] row = new String[8];
		row[0] = Long.toString(startTime);
		row[1] = Integer.toString(numCalls);
		row[2] = Integer.toString(uniqueCalls);
		row[3] = Long.toString(totalCallDuration);
		row[4] = Integer.toString(inContacts);
		row[5] = Integer.toString(type[1]);
		row[6] = Integer.toString(type[2]);
		row[7] = Integer.toString(type[3]);
		return row;
	}
}
