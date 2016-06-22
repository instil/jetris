package jetris.model;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;

public class PeriodicGameClockTest {

    private final PeriodicGameClock scheduler = new PeriodicGameClock();

    private final GameModel gameContext = new GameModel() {
        {
            start();
        }
    };

    @Test
    public void shouldStartScheduler() throws Exception {
        final AtomicBoolean isInvoked = new AtomicBoolean();
        scheduler.start(gameContext, new Runnable() {
            @Override
            public void run() {
                isInvoked.set(true);
            }
        });
        Thread.sleep(40);
        assertThat(isInvoked.get(), is(true));
    }

}
