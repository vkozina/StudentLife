package extraction;

import java.io.File;
import java.io.IOException;

import call_log.CallEntry;
import call_log.CallFeatures;
import activity.ActivityEntry;
import activity.ActivityFeatures;
import audio.AudioEntry;
import audio.AudioFeatures;

public class Extraction {

	public static void main(String[] args) throws IOException, InterruptedException {
		//String dataType = "sensing\\audio";
		String dataType = "call_log";
		String path = "C:\\Users\\Valentina\\Documents\\StudentLife_Dataset\\dataset\\" + dataType + "\\";
		int windowSize = 1000;
		int step = 1;
		int segment = 1000;				//segment in seconds

		String readFrom;// = path + dataType + "_u00.csv";
		String writeTo;// = path + "features\\" + dataType + "_u00_features.csv";
		
		File dir = new File(path);
		File[] directoryListing = dir.listFiles();
		new File(path + "features").mkdir();
		if (directoryListing != null) {
			for (File doc : directoryListing) {
				readFrom = doc.getName();
		    	writeTo = path + "features\\"+ readFrom.substring(0, readFrom.lastIndexOf('.')) + "_features.csv";	//TODO do better
		    	
		    	CallFeatures extractor = new CallFeatures();
		    	Parser<CallEntry, CallFeatures> parser = 
		    			new Parser<CallEntry, CallFeatures>(writeTo, windowSize, step, segment, extractor);
		    	
		    	
	/*
		    	AudioFeatures extractor = new AudioFeatures();
		    	Parser<AudioEntry, AudioFeatures> parser = 
		    			new Parser<AudioEntry, AudioFeatures>(writeTo, windowSize, step, segment, extractor);
	

		    	ActivityFeatures extractor = new ActivityFeatures();
				Parser<ActivityEntry, ActivityFeatures> parser = 
						new Parser<ActivityEntry, ActivityFeatures>(writeTo, windowSize, step, segment, extractor);
	 */
				parser.read(doc);
				//comment me to cycle through all the files in the given directory
				break;
			}
		}
	}
}
