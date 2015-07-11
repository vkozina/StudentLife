package call_log;

import java.util.concurrent.ArrayBlockingQueue;

import extraction.Features;

public class CallFeatures implements Features<CallEntry>{

	public CallEntry getEntry(String[] row) {
		// TODO Auto-generated method stub
		return null;
	}

	public void updateFromLine(CallEntry entry) {
		// TODO Auto-generated method stub
		
	}

	public void updateFromWindow(ArrayBlockingQueue<CallEntry> currentWindow) {
		// TODO Auto-generated method stub
		
	}

	public void reset() {
		// TODO Auto-generated method stub
		
	}

	public String[] header() {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] toRow() {
		// TODO Auto-generated method stub
		return null;
	}

}
