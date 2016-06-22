package jetris.model;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class PeriodicGameClock implements GameClock {

    private ScheduledExecutorService executor;
    private Runnable task;
    private GameModel model;
    private volatile long time;
    private volatile long initialDelay;

    @Override
    public void start(GameModel context, Runnable tickTask) {
        this.model = context;
        this.task = tickTask;
        this.initialDelay = 0;
        startClock();
    }

    @Override
    public void stop() {
        executor.shutdownNow();
    }

    @Override
    public void pause() {
        executor.shutdownNow();
        initialDelay = period() - (System.currentTimeMillis() - time);
        assert initialDelay >= 0;
    }

    @Override
    public void resume() {
        startClock();
    }

    private static long now() {
        return System.currentTimeMillis();
    }

    private void startClock() {
        this.time = now();
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                executeTask();
            }
        }, initialDelay, period(), TimeUnit.MILLISECONDS);
    }

    private void executeTask() {
        task.run();
        time = now();
    }

    private long period() {
        // interval between pulses reduced by 70ms per level
        return 1080 - (80 * model.level());
    }
}
