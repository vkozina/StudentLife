package extraction;

import java.lang.Boolean;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import com.opencsv.CSVReader;

public class Parser <T extends Entry, E extends Features<T>> {
	private HSSFWorkbook writeTo;
	private FileOutputStream out;
	private int currentWriteRow;
	
	private ArrayBlockingQueue<T> currentWindow;
	private E featureExtractor;
	
	private int step;					//how many lines should be read in before considering a new window
	private long segmentSize;
	
	public Parser(String writeFile, int windowSize, int step, long segmentSize, E extractor) throws IOException, InterruptedException {
		this.segmentSize = segmentSize;
		this.step = step;
		this.currentWriteRow = 0;
		
		writeTo = new HSSFWorkbook();
		writeTo.createSheet("Features");
		out = new FileOutputStream(new File(writeFile));
				   			
	    this.currentWindow = new ArrayBlockingQueue<T>(windowSize);
	    this.featureExtractor = extractor;
	    
	    this.writeLine(featureExtractor.header());
	}
	
	public void read(File readFile) throws IOException, InterruptedException {
		CSVReader reader = new CSVReader(new FileReader(readFile));
		reader.readNext();				//first line is header
		String [] nextLine;
		T current;
		int stepCounter = 0;
		Boolean done = false;
		long startTime = -1;
		long currentTime;
		
		while (!done) {
			//if there are still lines to read
			if ((nextLine = reader.readNext()) != null) {
			  	current = (T) featureExtractor.getEntry(nextLine);
			  	featureExtractor.updateFromLine(current);
	        	
			  	//add new element to the window
	        	if(!currentWindow.offer(current)) {				//window is full of entries, offer puts if there is space
					currentWindow.remove();						//remove the oldest entry
					currentWindow.put(current);					//replace it with newest data
				}
	        	
	        	if(startTime == -1)								//initialize start
	        		startTime = current.getTime();
	        	
	        	currentTime = current.getTime();				//update values to reflect parsed data
	        	stepCounter++;
	        	
	        	//check if new window has been completed
	        	if(stepCounter >= step) {
	        		featureExtractor.updateFromWindow(currentWindow);		//update features from the window
	        		stepCounter = 0;
	        		
	        		//check if results from this window need to be printed
	        		if(currentTime > (segmentSize + startTime)) {	//time we are at has exceeded current segment, need to print
						this.writeLine(featureExtractor.toRow(startTime));
						featureExtractor.reset();
						startTime = currentTime;
					}
	        	}
	        }
			
	        else
	        	currentWindow.remove(); 						//nothing to add to the window, still remove oldest entries
			
			done = currentWindow.isEmpty();						//if there's nothing left in the queue we are done
	        reader.readNext();									//data every other line
		}
		
		writeTo.write(out);
		out.close();
		reader.close();
	}
	
	public void writeLine(String[] rowToPrint) throws IOException {
		HSSFSheet sheet = writeTo.getSheetAt(0);
		Row row = sheet.createRow(currentWriteRow);
		for(int col = 0; col < rowToPrint.length; col ++) {
			Cell cell = row.createCell(col);
			try {
				Double num = Double.parseDouble(rowToPrint[col]);	//if you can make it a double, do
				cell.setCellValue(num);
			} catch(NumberFormatException e) {
				cell.setCellValue(rowToPrint[col]);		//otherwise just print a string
			}
		}
		currentWriteRow++;
	}
}
