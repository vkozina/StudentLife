package extraction;

import java.util.concurrent.ArrayBlockingQueue;

public interface Features <E extends Entry>{
	
	public E getEntry(String[] row);
	public void updateFromLine(E entry);
	public void updateFromWindow(ArrayBlockingQueue<E> currentWindow);
	public void reset();
	public String[] header();
	public String[] toRow();

}
