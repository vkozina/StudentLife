package activity;

import extraction.Entry;

public class ActivityEntry implements Entry {
	private long unixSecond;
	private int activity;
	
	public ActivityEntry(String[] row) {
		if(row.length == 0 || row[0].equals("")) {
			this.unixSecond = -1;
			this.activity = -1;
		}
		else {
			this.unixSecond = Long.parseLong(row[0]);
			this.activity = Integer.parseInt(row[1]);
		}
	}
	
	public Boolean isValid() {
		//if it does not contain a time it is not a valid entry
		return unixSecond != -1;
	}
		
	public long getTime() {
		return unixSecond;
	}
	
	public int getActivity() {
		return activity;
	}
	
}
