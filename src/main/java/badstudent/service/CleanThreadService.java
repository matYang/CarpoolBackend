package badstudent.service;

import badstudent.clean.ScheduledClean;
import badstudent.common.Common;

public class CleanThreadService extends Thread {
    // This method is called when the thread runs
    public void run() {
		ScheduledClean scheduledClean = new ScheduledClean(04, 00, 00);
		scheduledClean.start();
		Common.d("ScheduledClean thread starting, cleaning at 4:30AM every day");
    }
}