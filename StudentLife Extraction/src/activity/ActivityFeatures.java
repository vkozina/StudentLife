package activity;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ArrayBlockingQueue;

import audio.AudioEntry;
import extraction.Features;

public class ActivityFeatures implements Features<ActivityEntry> {
	Set<Integer> activities;
	long[] activityDurations;
	int numChanges;
	long previousTime;
	
	public ActivityFeatures() {
		activities = new TreeSet<Integer>();
		activityDurations = new long[4];  //stationary, walking, running, unknown
		numChanges = 0;
		previousTime = -1;
	}
	
	public ActivityEntry getEntry(String[] row) {
		ActivityEntry entry = new ActivityEntry(row);
		if(entry.isValid())
			return entry;
		else
			return null;
	}

	//updates features per line
	public void updateFromLine(ActivityEntry entry) {
		ActivityEntry e = (ActivityEntry) entry;
		if(previousTime == -1)
			previousTime = e.getTime();
		else {
			int currentActivity = e.getActivity();
			long currentTime = e.getTime();
			long timeDifference = currentTime - previousTime;
			
			activityDurations[currentActivity] += timeDifference;
			previousTime = currentTime;
		}
	}
	
	//updates features within a window
	public void updateFromWindow(ArrayBlockingQueue<ActivityEntry> currentWindow) {
		int previousActivity = (currentWindow.peek()).getActivity();
		for(ActivityEntry e : currentWindow) {
			int currentActivity = e.getActivity();
			activities.add(currentActivity);
			
			if(previousActivity != currentActivity)
				numChanges++;
		}
	}
	
	public void reset() {
		activities = new TreeSet<Integer>();
		activityDurations = new long[4];  //stationary, walking, running, unknown
		numChanges = 0;
	}

	public String[] updateFromSegment(Long startTime) {		//create row from features collected
		String[] row = new String[7];
		row[0] = Long.toString(startTime);
		row[1] = Integer.toString(activities.size());
		row[2] = Integer.toString(numChanges);
		row[3] = Long.toString(activityDurations[0]);
		row[4] = Long.toString(activityDurations[1]);
		row[5] = Long.toString(activityDurations[2]);
		row[6] = Long.toString(activityDurations[3]);
		return row;
	}

	public String[] header() {
		String[] row = new String[7];
		row[0] = "Time";
		row[1] = "Num Activities";
		row[2] = "Num Changes";
		row[3] = "Time Resting";
		row[4] = "Time Walking";
		row[5] = "Time Running";
		row[6] = "Time Unknown";
		return row;
	}

	public ArrayList<String[]> endData() {
		return null;
	}

}
