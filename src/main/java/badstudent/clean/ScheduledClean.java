package badstudent.clean;

import java.text.SimpleDateFormat;

import java.util.Date;

import badstudent.common.Common;

public class ScheduledClean {

    private final Scheduler scheduler = new Scheduler();
    private final SimpleDateFormat dateFormat =
        new SimpleDateFormat("dd MMM yyyy HH:mm:ss.SSS");
    private final int hourOfDay, minute, second;

    public ScheduledClean(int hourOfDay, int minute, int second) {
        this.hourOfDay = hourOfDay;
        this.minute = minute;
        this.second = second;
    }

    public void start() {
        scheduler.schedule(new SchedulerTask() {
            public void run() {
                cleanOldSchedules();
            }
            private void cleanOldSchedules() {
                Common.d("cleaning old schedules at time: " + dateFormat.format(new Date()));
                Clean clean = new Clean();
                clean.cleanSchedules();
            }
        }, new DailyIterator(hourOfDay, minute, second));
        Common.d("ScheduledClean:: clean scheduled at " + hourOfDay + ":" + minute + ":" + second);
    }

}