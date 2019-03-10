package cz.somrlik;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Supplier;


public class EditableTimerTask {

    private Runnable task;
    private Supplier<Long> period;
    private Long oldPeriod;
    private Timer theTimer = new Timer();

    public EditableTimerTask(Runnable task, Supplier<Long> period) {
        super();
        this.task = task;
        this.period = period;
    }

    private void adjustTiming() {
        Long p = period.get();
        if (oldPeriod == null || !oldPeriod.equals(p)) {
            theTimer.cancel();
            theTimer = new Timer();
            theTimer.schedule(createTask(), p, p);
            oldPeriod = p;
        }
    }

    private TimerTask createTask() {
        return new TimerTask() {
            @Override
            public void run() {
                task.run();
                adjustTiming();
            }
        };
    }

    public synchronized void startExecution() {
        adjustTiming();
    }

    public synchronized void stopExecution() {
        theTimer.cancel();
        oldPeriod = 0L;
    }

}
