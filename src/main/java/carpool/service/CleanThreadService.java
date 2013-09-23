package carpool.service;

import carpool.clean.ScheduledClean;
import carpool.common.DebugLog;

public class CleanThreadService extends Thread {
    // This method is called when the thread runs
    public void run() {
		ScheduledClean scheduledClean = new ScheduledClean(04, 00, 00);
		scheduledClean.start();
		DebugLog.d("ScheduledClean thread starting, cleaning at 4:30AM every day");
    }
}