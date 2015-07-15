package call_log;

import java.util.ArrayList;

import extraction.Entry;

public class Contact {

	private String call_number;
	private String call_name;
	private int numOutgoing;
	private int numIngoing;
	private int numMissedIn;
	private int numMissedOut;
	private ArrayList<Integer> conversations;	//how often they called within each window
	private long totalDuration;
	
	public Contact(String number, String name) {
		this.call_number = number;
		this.call_name = name;
	}
	
	public void update(CallEntry entry) {
		long duration = entry.getCallDuration();
		switch(entry.getCallType()) {
			case 1: 
				numOutgoing++; 
				break;
			case 2: 
				numIngoing++;
				if (duration == 0)
					numMissedOut++;
			case 3:
		}		numMissedIn++;
		
		totalDuration += duration;
	}
	
	public void addConversationWindow(int numConversations) {
		conversations.add(numConversations);
	}
	
	public Boolean inContacts() {
		return !call_name.equals("");
	}
	
	public Boolean isSignificant() {
		//TODO write specifications for significance
		return true;
	}
	
	
}
