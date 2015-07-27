package extraction;

import java.io.File;
import java.io.IOException;

import call_log.CallEntry;
import call_log.CallFeatures;
import call_log.ContactFeatures;
import activity.ActivityEntry;
import activity.ActivityFeatures;
import audio.AudioEntry;
import audio.AudioFeatures;

public class Extraction {

	public static void main(String[] args) throws IOException, InterruptedException {
		//TODO break up into helper functions
		
//		String dataType = "sensing\\activity";
		String dataType = "call_log";
		String path = "C:\\Users\\Valentina\\Documents\\StudentLife_Dataset\\dataset\\" + dataType + "\\";
		int windowSize = 2;
		int step = 1;
		int segment = 1000;				//segment in seconds

		String readFrom;// = path + dataType + "_u00.csv";
		String writeTo;// = path + "features\\" + dataType + "_u00_features.csv";
		
		File dir = new File(path);
		File[] directoryListing = dir.listFiles();
		new File(path + "contactFeatures").mkdir();
		new File(path + "callFeatures").mkdir();
		new File(path + "sorted").mkdir();
		if (directoryListing != null) {
			for (File doc : directoryListing) {
				readFrom = doc.getName();
		    	//writeTo = path + "features\\"+ readFrom.substring(0, readFrom.lastIndexOf('.')) + "_features.csv";	//TODO do better
		    	
		    	/*
		    	AudioFeatures extractor = new AudioFeatures();
		    	Parser<AudioEntry, AudioFeatures> parser = 
		    			new Parser<AudioEntry, AudioFeatures>(writeTo, windowSize, step, segment, extractor);
	 			*/
		    	
		    	
		    	/*
		    	ActivityFeatures extractor = new ActivityFeatures();
				Parser<ActivityEntry, ActivityFeatures> parser = 
						new Parser<ActivityEntry, ActivityFeatures>(writeTo, windowSize, step, segment, extractor);
		    	*/
				
				writeTo = path + "contactFeatures\\"+ readFrom.substring(0, readFrom.lastIndexOf('.')) + "_features.csv";	//TODO do better
		    	
		    	SortFile s = new SortFile(doc);
		    	doc = new File(s.getWriteLoc());
		    	ContactFeatures extractor = new ContactFeatures();
		    	Parser<CallEntry, ContactFeatures> parser = 
		    			new Parser<CallEntry, ContactFeatures>(writeTo, windowSize, step, segment, extractor);
		    	
		    	parser.read(doc);
		    	
		    	writeTo = path + "callFeatures\\"+ readFrom.substring(0, readFrom.lastIndexOf('.')) + "_features.csv";	//TODO do better
		    	
		    	CallFeatures callExtractor = new CallFeatures(extractor.getContacts());
		    	Parser<CallEntry, CallFeatures> callParser = 
		    			new Parser<CallEntry, CallFeatures>(writeTo, windowSize, step, segment, callExtractor);
		    	
				callParser.read(doc);
				
				//comment me to cycle through all the files in the given directory
				break;
			}
		}
		
		System.out.println("success");
	}
}
