package extraction;

public enum DataType {
	ACTIVITY, AUDIO, CALLS, SMS;
	
	public String toString()
	{
	    switch(this)
	    {
	        case ACTIVITY:
	            return "sensing\\activity";
	            
	        case AUDIO:
	            return "sensing\\activity";
	            
	        case CALLS:
	            return "call_log";
	            
	        case SMS:
	            return "sms";
	    }
	    
	    return "-";
	}
}
