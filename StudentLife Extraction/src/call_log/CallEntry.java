package call_log;

import extraction.Entry;

public class CallEntry implements Entry {
	private String id;
	private String device;
	private long unix_second;
	private int call_id;
	private long call_date;
	private int call_duration;
	private String call_name;
	private String call_number;
	private String call_number_type;
	private int call_type; //1, 2 or 3
	
	public CallEntry(String[] row) {
		this.id = row[0];
		this.device = row[1];
		this.unix_second = safeParseLong(row[2]);
		this.call_id = safeParseInt(row[3]);
		this.call_date = safeParseLong(row[4]);
		this.call_duration = safeParseInt(row[5]);
		this.call_name = extractHash(row[6]);
		this.call_number = extractHash(row[7]);
		//TODO row 8 has CALLS_numberlabel, which appears to be blank. check if that's true/what to do
		this.call_number_type = extractHash(row[9]);
		this.call_type = safeParseInt(row[10]);
	}
	
	//get just the hash without all the extra chars
	private String extractHash(String s) {
		return s.equals("") ? s : s.split("\"")[3];
	}
	
	private int safeParseInt(String s) {
		return s.equals("") ? 0 : Integer.parseInt(s);
	}
	
	private Long safeParseLong(String s) {
		return s.equals("") ? 0 : Long.parseLong(s);
	}
	
	
	public int getCallType() {
		return call_type;
	}
	
	public String getCallNumber() {
		return call_number;
	}
	
	public int getCallDuration() {
		return call_duration;
	}
	
	public Boolean isContact() {
		return !call_name.equals("");
	}
	
	public long getTime() {
		return unix_second;
	}

}