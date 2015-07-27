package audio;

import extraction.Entry;

public class AudioEntry implements Entry {
	private long unixSecond;
	private int interference;
	
	public AudioEntry(String[] row) {
		if(row.length == 0 || row[0].equals("")) {
			this.unixSecond = -1;
			this.interference = -1;
		}
		else {
			this.unixSecond = Long.parseLong(row[0]);
			this.interference = Integer.parseInt(row[1]);
		}
	}
	
	public Boolean isValid() {
		//if it does not contain a time it is not a valid entry
		return unixSecond != -1;
	}
		
	public long getTime() {
		return unixSecond;
	}
	
	public int getInterference() {
		return interference;
	}
	
}