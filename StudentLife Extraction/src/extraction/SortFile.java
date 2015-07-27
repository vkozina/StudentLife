package extraction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import com.csvreader.CsvReader;

public class SortFile {
	private String writeFile;
	private HSSFWorkbook writeTo;
	private FileOutputStream out;

	public SortFile(File readFile) throws IOException {
		String writeName = readFile.getName().replace(".csv", ".xls");
		this.writeFile = readFile.getAbsolutePath().replace(readFile.getName(), "\\sorted\\" + writeName);
		writeTo = new HSSFWorkbook();
		writeTo.createSheet("Features");
		out = new FileOutputStream(new File(writeFile));
		readLine(readFile);
	}
	
	public String getWriteLoc() {
		return writeFile;
	}
	
	public void readLine(File readFile) throws IOException {
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
	
	public void writeLine(String[] rowToPrint, int toRow) throws IOException {
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
