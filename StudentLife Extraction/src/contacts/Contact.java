package call_log;

import java.util.ArrayList;
import java.util.Arrays;

import extraction.Entry;

public class Contact {

	private String call_number;
	private String call_name;
	private int numOutgoing;
	private int numIngoing;
	private int numMissedIn;
	private int numMissedOut;
	private ArrayList<Integer> conversations;	//how often they called within each window
	private long totalDurationIn;
	private long totalDurationOut;
	private double[] sig;
	/*	totalCalls, totalDuration, periodsContacted, avgCallsPerPeriod, avgDurationPerPeriod, proportionMissedIn;
	 *  proportionMissedOut, minInPeriod, maxInPeriod, median;
	 */
	
	private double adjustedSig;
	private boolean isSig;
	
	public Contact(String number, String name) {
		this.call_number = number;
		this.call_name = name;
		this.conversations = new ArrayList<Integer>();
		this.sig = new double[10];
	}
	
	public void update(CallEntry entry) {
		long duration = entry.getCallDuration();
		switch(entry.getCallType()) {
			case 1: 
				numIngoing++;
				totalDurationIn+=duration;
				break;
			case 2: 
				if (duration == 0)
					numMissedOut++;
				else {
					numOutgoing++;
					totalDurationOut+=duration;
				}
			
				break;
			case 3:
				numMissedIn++;
				break;
		}
	}
	
	public void addConversationWindow(int numConversations) {
		conversations.add(numConversations);
	}
	
	public Boolean inContacts() {
		return !call_name.equals("");
	}

	public String[] toRow() {
		String[] row = new String[10];
		row[0] = call_number;
		row[1] = call_name;
		row[2] = Integer.toString(numOutgoing);
		row[3] = Integer.toString(numIngoing);
		row[4] = Integer.toString(numMissedOut);
		row[5] = Integer.toString(numMissedIn);
		row[6] = Long.toString(totalDurationOut);
		row[7] = Long.toString(totalDurationIn);
		row[8] = Double.toString(adjustedSig);
		row[9] = Boolean.toString(isSig);
		return row;
	}

	
	public void setSignificance() {
		int totalCalls = numOutgoing + numIngoing;
		long totalDuration = totalDurationIn + totalDurationOut;
		int periodsContacted = conversations.size();
		double avgCallsPerPeriod = periodsContacted == 0 ? 0 : totalCalls / (double) periodsContacted;
		double avgDurationPerPeriod = periodsContacted == 0 ? 0 : totalDuration / (double) periodsContacted;
		int totalIn = numIngoing + numMissedIn;
		double proportionMissedIn = totalIn == 0 ? 0 : numMissedIn / (double) (totalIn);
		int totalOut = numOutgoing + numMissedOut;
		double proportionMissedOut = totalOut == 0 ? 0 : numMissedOut / (double) (totalOut);
		
		int[] convos = new int[periodsContacted];
	
		for(int i = 0; i < convos.length; i++) {
			convos[i] = conversations.get(i);
		}
			
		Arrays.sort(convos);
	
		int minInPeriod = convos[0];
		int maxInPeriod = convos[convos.length - 1];
		
		double median;
		if (convos.length % 2 == 0)
		    median = ((double)convos[convos.length/2] + (double)convos[convos.length/2 - 1])/2;
		else
		    median = (double) convos[convos.length/2];
		
		sig[0] = totalCalls;
		sig[1] = totalDuration;
		sig[2] = periodsContacted;
		sig[3] = avgCallsPerPeriod;
		sig[4] = avgDurationPerPeriod;
		sig[5] = proportionMissedIn;
		sig[6] = proportionMissedOut;
		sig[7] = minInPeriod;
		sig[8] = maxInPeriod;
		sig[9] = median;		
	}	
	
	public double[] getSignificance() {
		return sig;
	}
	
	public double calculateSignificance(double[] min, double[] max) {
		/*
		 * proportion of missed to their respective picked up (inverse)
		 * rate incoming/outgoing (proportional)
		 * total duration (proportional)
		 * total analysis of statistics for 'conversations'
		 * avg calls/time period (proportional)
		 * max num calls in time period (proportional)
		 * min num calls in time period (inverse)
		 * num of time periods contacted (proportional)
		 */

		double[] adjSig = new double[10];
		
		for(int i = 0; i < sig.length; i++) {
			if(min[i] == max[i])
				adjSig[i] = 0.5;
			else
				adjSig[i] = (sig[i] - min[i]) / (double)(max[i] - min[i]);
		}
		
		/*totalCalls, totalDuration, periodsContacted, avgCallsPerPeriod, avgDurationPerPeriod, proportionMissedIn;
		 *  proportionMissedOut, minInPeriod, maxInPeriod, median; */
		
		double proportional = adjSig[0] + adjSig[1] + adjSig[2] + adjSig[3] + adjSig[4] + adjSig[8] + adjSig[9];
		double inverse = adjSig[5] + adjSig[6] + adjSig[7];
		adjustedSig = proportional - inverse;
		
		return adjustedSig;
	}
	
	public void isSignificant(double min, double max) {
		double calutionSig = (adjustedSig - min) / (double)(max - min);
		isSig = (calutionSig >= 0.5);
	}
	
	public boolean isSig() {
		return isSig;
	}
}
