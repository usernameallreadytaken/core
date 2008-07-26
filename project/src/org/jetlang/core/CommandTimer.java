package org.jetlang.core;

import java.util.Timer;
import java.util.TimerTask;

public class CommandTimer implements RunnableScheduler, Stopable {
    private final Timer _timer = new Timer(true);
    private final ICommandQueue _queue;

    public CommandTimer(ICommandQueue queue) {
        _queue = queue;
    }

    public TimerControl schedule(final Runnable comm, long timeTillEnqueueInMs) {
        if (timeTillEnqueueInMs <= 0) {
            PendingCommand pending = new PendingCommand(comm);
            _queue.queue(pending);
            return pending;
        } else {
            TimerTask task = new TimerTask() {
                public void run() {
                    _queue.queue(comm);
                }
            };
            _timer.schedule(task, timeTillEnqueueInMs);
            return new TimerTaskControl(task);
        }
    }

    public TimerControl scheduleOnInterval(final Runnable comm, long firstInMs, long intervalInMs) {

        TimerTask task = new TimerTask() {
            public void run() {
                _queue.queue(comm);
            }
        };
        _timer.schedule(task, firstInMs, intervalInMs);
        return new TimerTaskControl(task);
    }

    public void stop() {
        _timer.cancel();
    }
}

class TimerTaskControl implements TimerControl {
    private TimerTask _task;

    public TimerTaskControl(TimerTask task) {
        _task = task;
    }

    /// <summary>
    /// Cancels scheduled timer.
    /// </summary>
    public void cancel() {
        _task.cancel();
    }
}