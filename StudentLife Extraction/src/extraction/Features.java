package extraction;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

public interface Features <E extends Entry>{
	
	public E getEntry(String[] row);
	public void updateFromLine(E entry);
	public void updateFromWindow(ArrayBlockingQueue<E> currentWindow);
	public String[] updateFromSegment(Long startTime);				//returns the line that should be printed at end of segment
	public void reset();
	public String[] header();
	public ArrayList<String[]> endData();				//returns anything that needs to be printed at the end of the file

}
