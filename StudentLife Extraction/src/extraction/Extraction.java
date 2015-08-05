package extraction;

import java.io.File;
import java.io.IOException;

import sms.SMSEntry;
import sms.SMSFeatures;
import contacts.ContactFeatures;
import call_log.CallEntry;
import call_log.CallFeatures;
import activity.ActivityEntry;
import activity.ActivityFeatures;
import audio.AudioEntry;
import audio.AudioFeatures;

public class Extraction {

	public static void main(String[] args) throws IOException, InterruptedException {

		DataType type = DataType.CALLS;
		String path = "C:\\Users\\Valentina\\Documents\\StudentLife_Dataset\\dataset\\" + type + "\\";
		int windowSize = 2;
		int step = 1;
		int segment = 1000;				//segment in seconds

		String readFrom;
		String writeTo;
		
		File dir = new File(path);
		File[] directoryListing = dir.listFiles();
		
		//only create sorted directory if necessary
		if(type == DataType.CALLS || type == DataType.SMS) {
			new File(path + "contactFeatures").mkdir();
			new File(path + "sorted").mkdir();
		}

		new File(path + "features").mkdir();
		
		if (directoryListing != null) {
			//cycles through all the files
			for (File doc : directoryListing) {
				readFrom = doc.getName();

				Parser parser;
				
				//determines address of where to write. parser will create file if necessary
				writeTo = path + "features\\"+ readFrom.substring(0, readFrom.lastIndexOf('.')) + "_features.csv";
				
				switch(type) {
					case ACTIVITY: { 
						ActivityFeatures extractor = new ActivityFeatures();
						parser = new Parser<ActivityEntry, ActivityFeatures>(writeTo, windowSize, step, segment, extractor);
						break;
					}
					
					case AUDIO: {
						AudioFeatures extractor = new AudioFeatures();
				    	parser = new Parser<AudioEntry, AudioFeatures>(writeTo, windowSize, step, segment, extractor);
				    	break;
					}
					
					//need to extract contact information for both
					case CALLS:
					case SMS:
					{
						//creates the proper file name (with correct extension) for output doc
						String writeName = doc.getName().replace(".csv", ".xls");
						String writePath = doc.getAbsolutePath().replace(doc.getName(), "\\sorted\\" + writeName);
						File writeFile = new File(writePath);					
						
						int col = type == DataType.CALLS ? 3 : 5;	//choose appropriate col to sort by (3 for calls, 5 for sms)
						SortFile.sortByCol(doc, writeFile, col);
				    	doc = writeFile;	//read from the sorted file from now on
				    	
				    	String contactWrite = path + "contactFeatures\\"+ readFrom.substring(0, readFrom.lastIndexOf('.')) + "_contact_features.csv";
						ContactFeatures contactsExtractor = new ContactFeatures();
				    	Parser<CallEntry, ContactFeatures> contactsParser = 
				    			new Parser<CallEntry, ContactFeatures>(contactWrite, windowSize, step, segment, contactsExtractor);
				    	
				    	contactsParser.read(doc);
			    	
				    	if(type == DataType.CALLS) {
					    	CallFeatures extractor = new CallFeatures(contactsExtractor.getContacts());
					    	parser = new Parser<CallEntry, CallFeatures>(writeTo, windowSize, step, segment, extractor);
						}
				    	else {
				    		SMSFeatures extractor = new SMSFeatures(contactsExtractor.getContacts());
				    		parser = new Parser<SMSEntry, SMSFeatures>(writeTo, windowSize, step, segment, extractor);
				    	}
				    	break;
					}
					
					default: {
						parser = null;	//this line should never be reached
					}
				}
				
				parser.read(doc);
				
				//comment me to cycle through all the files in the given directory
				break;
			}
		}
		
		System.out.println("success");
	}
}
