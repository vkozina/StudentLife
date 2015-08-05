package extraction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import com.csvreader.CsvReader;

public class SortFile {
	
	//will sort by the specified column (needs to contain number data)
	public static void sortByCol(File readFile, File writeFile, int col) throws IOException {
		HSSFWorkbook writeTo = new HSSFWorkbook();
		writeTo.createSheet("Features");
		FileOutputStream out = new FileOutputStream(writeFile);
		
		CsvReader readFrom =  new CsvReader(readFile.getAbsolutePath());
		readFrom.readHeaders();
		writeLine(writeTo, readFrom.getHeaders(), 0);
		String[] values = new String[1];

		ArrayList<Long> vals = new ArrayList<Long>();
		while(values.length > 0) {
			vals.add(Long.parseLong(values[col]));
		}
		
		Collections.sort(vals);
		
		while(values.length > 0) {
			readFrom.readRecord();
			values = readFrom.getValues();
			if(values.length > 2 && values[3] != "") {
				long val = Long.parseLong(values[col]);
				writeLine(writeTo, values, vals.indexOf(val));		//first row for headers
			}
		}
		writeTo.write(out);
		out.close();
		
	}
	
	/* sorts directly by id, which is already in order. Only works for call_logs
	public void sort(File readFile, int col) throws IOException {
		CsvReader readFrom =  new CsvReader(readFile.getAbsolutePath());
		readFrom.readHeaders();
		writeLine(readFrom.getHeaders(), 0);
		
		String[] values = new String[1];
		
		while(values.length > 0) {
			readFrom.readRecord();
			values = readFrom.getValues();
			if(values.length > 2 && values[3] != "") {
				int call_id = Integer.parseInt(values[3]);
				writeLine(values, call_id);		//first row for headers
			}
		}
		writeTo.write(out);
		out.close();
	}
	*/
	
	public static void writeLine(HSSFWorkbook writeTo, String[] rowToPrint, int toRow) throws IOException {
		HSSFSheet sheet = writeTo.getSheetAt(0);
		if(rowToPrint != null) {	//if there is something to print
			Row row = sheet.createRow(toRow);
			for(int col = 0; col < rowToPrint.length; col ++) {
				Cell cell = row.createCell(col);
				try {
					Double num = Double.parseDouble(rowToPrint[col]);	//if you can make it a double, do
					cell.setCellValue(num);
				} catch(NumberFormatException e) {
					cell.setCellValue(rowToPrint[col]);		//otherwise just print a string
				}
			}
		}
	}
}
