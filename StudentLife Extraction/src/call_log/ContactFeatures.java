package call_log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

import extraction.Features;

public class ContactFeatures implements Features<CallEntry> {

	private HashMap<String, Contact> contacts;	//contacts uniquely identified by number
	private HashMap<Contact, Integer> windowUniqueNums; //store unique numbers and how often they called within current window
	
	@Override
	public CallEntry getEntry(String[] row) {
		return new CallEntry(row);
	}

	public void updateFromLine(CallEntry entry) {
		String number = entry.getCallNumber();
		String name = entry.getCallName();
		Contact toUpdate = contacts.get(number);
		if (toUpdate != null)	//if contact exists, update proper fields
			toUpdate.update(entry);
		else {
			toUpdate = new Contact(number, name);
			contacts.put(number, toUpdate);
		}
		
		Integer numCalls = windowUniqueNums.get(toUpdate);
		if(numCalls != null)	//if contact has already called in this time period, add 1
			numCalls++;
		else
			numCalls = 0;	//otherwise initialize
		
		windowUniqueNums.put(toUpdate, numCalls);
	}

	@Override
	public void updateFromWindow(ArrayBlockingQueue<CallEntry> currentWindow) {		
		for(Map.Entry<Contact, Integer> entry : windowUniqueNums.entrySet()) {
			Contact c = entry.getKey();
			int numConversations = entry.getValue();
			c.addConversationWindow(numConversations);
		}
		windowUniqueNums.clear();	//reset window count in preparation for next window
		
	}

	public void reset() {
		//shouldn't really do anything; nothing changes at the end of a segment, since nothing prints yet
	}

	@Override
	public String[] header() {
		String[] row = new String[8];
		row[0] = "Number";
		row[1] = "Name";
		row[2] = "Num Outgoing;";
		row[3] = "Num Ingoing";
		row[4] = "Num Missed Outgoing;";
		row[5] = "Num Missed Ingoing";
		row[6] = "Number Periods Contacted";
		row[7] = "Total Duration";
		return row;
	}

	public String[] toRow(Long startTime) {
		// TODO Auto-generated method stub
		return null;
	}

}
