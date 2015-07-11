package extraction;

import java.io.File;
import java.io.IOException;

import activity.ActivityEntry;
import activity.ActivityFeatures;
import audio.AudioEntry;
import audio.AudioFeatures;

public class Extraction {

	public static void main(String[] args) throws IOException, InterruptedException {
		String dataType = "audio";
		String path = "C:\\Users\\Valentina\\Documents\\StudentLife_Dataset\\dataset\\sensing\\" + dataType + "\\";
		int windowSize = 2;
		int step = 1;
		int segment = 300;				//segment in seconds

		String readFrom;// = path + dataType + "_u00.csv";
		String writeTo;// = path + "features\\" + dataType + "_u00_features.csv";
		
		File dir = new File(path);
		File[] directoryListing = dir.listFiles();
		new File(path + "features").mkdir();
		if (directoryListing != null) {
			for (File doc : directoryListing) {
				readFrom = doc.getName();
		    	writeTo = path + "features\\"+ readFrom.substring(0, readFrom.lastIndexOf('.')) + "_features.csv";	//TODO do better
	
		    	AudioFeatures extractor = new AudioFeatures();
		    	Parser<AudioEntry, AudioFeatures> parser = 
		    			new Parser<AudioEntry, AudioFeatures>(writeTo, windowSize, step, segment, extractor);
	/*

		    	ActivityFeatures extractor = new ActivityFeatures();
				Parser<ActivityEntry, ActivityFeatures> parser = 
						new Parser<ActivityEntry, ActivityFeatures>(writeTo, windowSize, step, segment, extractor);
	 */
				parser.read(doc);
				
				//comment me to cycle through all the files in the given directory
				//break;
			}
		}
	}
}
