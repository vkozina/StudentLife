package call_log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

import extraction.Features;

public class ContactFeatures implements Features<CallEntry> {

	private HashMap<String, Contact> contacts;	//contacts uniquely identified by number
	private HashMap<Contact, Integer> segmentUniqueNums; //store unique numbers and how often they called within current window
	
	public ContactFeatures() {
		this.contacts = new HashMap<String, Contact>();
		this.segmentUniqueNums = new HashMap<Contact, Integer>();
	}
	
	public CallEntry getEntry(String[] row) {
		CallEntry entry = new CallEntry(row);
		if(entry.isValid())
			return entry;
		else
			return null;
	}

	public void updateFromLine(CallEntry entry) {
		String number = entry.getCallNumber();
		String name = entry.getCallName();
		Contact toUpdate = contacts.get(number);
		if (toUpdate == null) {			//contact does not yet exist
			toUpdate = new Contact(number, name);
			contacts.put(number, toUpdate);
		}
		toUpdate.update(entry);		//need to update proper fields
		
		Integer numCalls = segmentUniqueNums.get(toUpdate);
		if(numCalls != null)	//if contact has already called in this time period, add 1
			numCalls++;
		else
			numCalls = 1;		//otherwise initialize
		
		segmentUniqueNums.put(toUpdate, numCalls);
	}

	public void updateFromWindow(ArrayBlockingQueue<CallEntry> currentWindow) {
		//nothing gets calculated by window
	}

	public void reset() {
		segmentUniqueNums.clear();	//reset window count in preparation for next window
	}

	@Override
	public String[] header() {
		String[] row = new String[10];
		row[0] = "Number";
		row[1] = "Name";
		row[2] = "Num Outgoing";
		row[3] = "Num Ingoing";
		row[4] = "Num Missed Outgoing";
		row[5] = "Num Missed Ingoing";
		row[6] = "Total Duration Out";
		row[7] = "Total Duration In";
		row[8] = "Significance";
		row[9] = "Is Significant";
		return row;
	}

	public String[] updateFromSegment(Long startTime) {
		for(Map.Entry<Contact, Integer> entry : segmentUniqueNums.entrySet()) {
			Contact c = entry.getKey();
			int numConversations = entry.getValue();
			c.addConversationWindow(numConversations);
		}
		
		return null;
	}
	
	private double[] calculateRange() {
		double[] min = null;
		double[] max = null;
		
		double[] toReturn = new double[2];
		
		for(Contact contact : contacts.values()) {
			contact.setSignificance();
			double[] current = contact.getSignificance();
			
			for(int i = 0; i < current.length; i++) {
				if(min == null) min = current.clone();
				if(max == null) max = current.clone();
				
				min[i] = Math.min(current[i], min[i]);
				max[i] = Math.max(current[i], max[i]);
			}
				
		}
		
		double minSig = Double.NaN;
		double maxSig = Double.NaN;
		
		double currentSig = 0;
		
		for(Contact contact : contacts.values()) {
			currentSig = contact.calculateSignificance(min, max);
			if (Double.isNaN(minSig) || currentSig < minSig) minSig = currentSig;
			if (Double.isNaN(maxSig) || currentSig > maxSig) maxSig = currentSig;
		}
		toReturn[0] = minSig; toReturn[1] = maxSig;
		return toReturn;
	}

	public ArrayList<String[]> endData() {
		double[] range = this.calculateRange();		//range[0] = min; range[1] = max;
	
		ArrayList<String[]> data = new ArrayList<String[]>();
		
		for(Contact contact : contacts.values()) {
			contact.isSignificant(range[0], range[1]);
			data.add(contact.toRow());
		}
		
		return data;
	}
	
	public HashMap<String, Contact> getContacts() {
		return contacts;
	}

}
