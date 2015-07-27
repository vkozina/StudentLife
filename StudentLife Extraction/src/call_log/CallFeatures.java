package call_log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

import audio.AudioEntry;
import extraction.Features;

public class CallFeatures implements Features<CallEntry> {
	private int numCalls;
	private HashSet<String> uniqueCalls;	//keeps track of unique numbers to have called in window
	private long totalCallDuration;
	private int inContacts;
	private int[] type; //empty, ingoing, outgoing, missed
	private int numSignificant;
	private HashMap<String, Contact> contacts;	//contacts uniquely identified by number
	
	public CallFeatures(HashMap<String, Contact> contacts) {
		uniqueCalls = new HashSet<String>();
		this.contacts = contacts;
		reset();		
	}
	
	public CallEntry getEntry(String[] row) {
		//only return call entry if it contains all neccesary information
		CallEntry entry = new CallEntry(row);
		if(entry.isValid())
			return entry;
		else
			return null;
	}

	public void updateFromLine(CallEntry entry) {
		numCalls++;
		uniqueCalls.add(entry.getCallNumber());
		totalCallDuration += entry.getCallDuration();
		if(entry.isContact())
			inContacts++;
		type[entry.getCallType()]++;
		Contact contact = contacts.get(entry.getCallNumber());
		if(contact != null && contact.isSig())
			this.numSignificant++;
	}

	public void updateFromWindow(ArrayBlockingQueue<CallEntry> currentWindow) {
		//TODO figure out what should be based on window
	}

	public void reset() {
		numCalls = 0; uniqueCalls.clear(); totalCallDuration = 0; inContacts = 0; numSignificant = 0;
		type = new int[4];
	}

	public String[] header() {
		String[] row = new String[9];
		row[0] = "Time";
		row[1] = "Num Calls";
		row[2] = "Num Unique Calls";
		row[3] = "Total Call Duration";
		row[4] = "Num Calls From Contact List";
		row[5] = "Num Ingoing";
		row[6] = "Num Outgoing";
		row[7] = "Num Missed";
		row[8] = "Num Significant";
		return row;
	}

	public String[] updateFromSegment(Long startTime) {
		String[] row = new String[9];
		row[0] = Long.toString(startTime);
		row[1] = Integer.toString(numCalls);
		row[2] = Integer.toString(uniqueCalls.size());
		row[3] = Long.toString(totalCallDuration);
		row[4] = Integer.toString(inContacts);
		row[5] = Integer.toString(type[1]);
		row[6] = Integer.toString(type[2]);
		row[7] = Integer.toString(type[3]);
		row[8] = Integer.toString(numSignificant);
		return row;
	}

	public ArrayList<String[]> endData() {
		return null;
	}
}
