package audio;

import extraction.Entry;

public class AudioEntry implements Entry {
	private long unixSecond;
	private int interference;
	
	public AudioEntry(String[] row) {
		this.unixSecond = Long.parseLong(row[0]);
		this.interference = Integer.parseInt(row[1]);
	}
		
	public long getTime() {
		return unixSecond;
	}
	
	public int getInterference() {
		return interference;
	}
	
}