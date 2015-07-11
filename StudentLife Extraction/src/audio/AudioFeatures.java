package audio;

import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ArrayBlockingQueue;

import extraction.Features;

public class AudioFeatures implements Features<AudioEntry> {
	Set<Integer> audios;
	long[] audioDurations;
	int numChanges;
	long previousTime;
	
	public AudioFeatures() {
		audios = new TreeSet<Integer>();
		audioDurations = new long[4];  //silence, voice, noise, unknown
		numChanges = 0;
		previousTime = -1;
	}
	
	public AudioEntry getEntry(String[] row) {
		return new AudioEntry(row);
	}

	//updates features per line
	public void updateFromLine(AudioEntry entry) {
		AudioEntry e = (AudioEntry) entry;
		if(previousTime == -1)
			previousTime = e.getTime();
		else {
			int currentAudio = e.getInterference();
			long currentTime = e.getTime();
			long timeDifference = currentTime - previousTime;
		
			audioDurations[currentAudio] += timeDifference;
			previousTime = currentTime;
		}
	}
	
	//updates features within a window
	public void updateFromWindow(ArrayBlockingQueue<AudioEntry> currentWindow) {
		int previousAudio = (currentWindow.peek()).getInterference();
		for(AudioEntry e : currentWindow) {
			int currentActivity = e.getInterference();
			audios.add(currentActivity);
			
			if(previousAudio != currentActivity)
				numChanges++;
		}
	}
	
	public void reset() {
		audios = new TreeSet<Integer>();
		audioDurations = new long[4];  //stationary, walking, running, unknown
		numChanges = 0;
	}

	public String[] toRow() {	//create row from features collected
		String[] row = new String[7];
		row[0] = Long.toString(previousTime);
		row[1] = Integer.toString(audios.size());
		row[2] = Integer.toString(numChanges);
		row[3] = Long.toString(audioDurations[0]);
		row[4] = Long.toString(audioDurations[1]);
		row[5] = Long.toString(audioDurations[2]);
		row[6] = Long.toString(audioDurations[3]);
		return row;
	}

	public String[] header() {
		String[] row = new String[7];
		row[0] = "Time";
		row[1] = "Num Audio";
		row[2] = "Num Changes";
		row[3] = "Time Silence";
		row[4] = "Time Voice";
		row[5] = "Time Noise";
		row[6] = "Time Unknown";
		return row;
	}

}
