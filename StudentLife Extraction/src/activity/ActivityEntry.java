package activity;

import extraction.Entry;

public class ActivityEntry implements Entry {
	private long unixSecond;
	private int activity;
	
	public ActivityEntry(String[] row) {
		this.unixSecond = Long.parseLong(row[0]);
		this.activity = Integer.parseInt(row[1]);
	}
		
	public long getTime() {
		return unixSecond;
	}
	
	public int getActivity() {
		return activity;
	}
	
}
