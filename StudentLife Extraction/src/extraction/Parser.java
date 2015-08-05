package extraction;

import java.lang.Boolean;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import com.csvreader.CsvReader;

//does all the reading of the file, as well as manages information extraction
public class Parser <T extends Entry, E extends Features<T>> {
	private HSSFWorkbook writeTo;
	private FileOutputStream out;
	private int currentWriteRow;
	
	private ArrayBlockingQueue<T> currentWindow;
	private E featureExtractor;
	
	private int step;					//how many lines should be read in before considering a new window
	private long segmentSize;
	
	private int stepCounter;
	private long windowStartTime;
	private long windowCurrentTime;
	
	public Parser(String writeFile, int windowSize, int step, long segmentSize, E extractor) throws IOException, InterruptedException {
		this.segmentSize = segmentSize;
		this.step = step;
		this.currentWriteRow = 0;
		this.stepCounter = 0;
		this.windowStartTime = -1;
		
		writeTo = new HSSFWorkbook();
		writeTo.createSheet("Features");
		out = new FileOutputStream(new File(writeFile));
				   			
	    this.currentWindow = new ArrayBlockingQueue<T>(windowSize);
	    this.featureExtractor = extractor;
	    
	    this.writeLine(featureExtractor.header());
	}

	//will appropriately read CSV and XLS files until the end, extracting data as it goes
	public void read(File readFile) throws IOException, InterruptedException {
		boolean isCSV = readFile.getName().endsWith(".csv");
		FileInputStream file = new FileInputStream(readFile);
		CsvReader CSVreader = null;
		HSSFWorkbook workbook;
		HSSFSheet sheet;
		Iterator<Row> rowIterator = null;
		String [] nextLine = null;
		int numCols = 0;
		
		if(isCSV) {
			CSVreader = new CsvReader(readFile.getAbsolutePath());
			CSVreader.readHeaders();
		}
		else {
			workbook = new HSSFWorkbook(file);
			sheet = workbook.getSheetAt(0);
			rowIterator = sheet.iterator();
			Row r = rowIterator.next();	//headers
			Iterator<Cell> cells = r.cellIterator();
			while(cells.hasNext()) {
				numCols++;
				cells.next();
			}
			nextLine = new String[numCols];
		}
		
		boolean done = false;
		
		while (!done) {
			if(isCSV) {
				CSVreader.readRecord();
				nextLine = CSVreader.getValues();
			}
			else {
				if(rowIterator.hasNext()) {					
					Row r = rowIterator.next();
					Iterator<Cell> cells = r.cellIterator();
					int i = 0;
					while(cells.hasNext()) {
						Cell cell = cells.next();
						cell.setCellType(Cell.CELL_TYPE_STRING);
						nextLine[i] = cell.getStringCellValue();
						i++;
					}
				}
				else {
					for(int i = 0; i < numCols; i++)
						nextLine[i] = "";
				}
			}
			
			if(!this.parseLine(nextLine))
				currentWindow.remove(); 							//nothing to add to the window, still remove oldest entries
			done = currentWindow.isEmpty();							//if there's nothing left in the queue we are done
		}
		
		this.endOfFile();
		writeTo.write(out);
		out.close();
		
		if(isCSV)
			CSVreader.close();	
	}
	
	//create an entry from line, adding it to the window if it exists, updating data as needed
	//returns whether or not the entry created from the line was valid (as determined by the extractor)
	private boolean parseLine(String [] line) throws InterruptedException, IOException {
		T current = (T) featureExtractor.getEntry(line);
	  	//if the entry received should be considered (null if does not contain proper info)
		if(current == null)
			return false;
		
	  	featureExtractor.updateFromLine(current);
    	
	  	//add new element to the window
    	if(!currentWindow.offer(current)) {				//window is full of entries, offer puts if there is space
			currentWindow.remove();						//remove the oldest entry
			currentWindow.put(current);					//replace it with newest data
		}
    	
    	if(windowStartTime == -1)								//initialize start
    		windowStartTime = current.getTime();
    	
    	windowCurrentTime = current.getTime();				//update values to reflect parsed data
    	stepCounter++;
    	
    	//check if new window has been completed
    	if(stepCounter >= step) {
    		this.endOfWindow();
	  	}
    	
    	return true;
	}
	
	private void endOfWindow() throws IOException {
		featureExtractor.updateFromWindow(currentWindow);		//update features in extractor from the window
		stepCounter = 0;
		
		//check if results from this window need to be printed
		if(windowCurrentTime > (segmentSize + windowStartTime)) {	//time we are at has exceeded current segment, need to print
			this.writeLine(featureExtractor.updateFromSegment(windowStartTime));
			featureExtractor.reset();
			windowStartTime = windowCurrentTime;
		}
	}
	
	//prints all information returned by the extractor when the end of the readFile is reached
	private void endOfFile() throws IOException {
		ArrayList<String[]> data = featureExtractor.endData();
		if(data != null)
			for(String[] row : data)
				this.writeLine(row);
	}
	
	public void writeLine(String[] rowToPrint) throws IOException {
		HSSFSheet sheet = writeTo.getSheetAt(0);
		if(rowToPrint != null) {	//if there is something to print
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
}
