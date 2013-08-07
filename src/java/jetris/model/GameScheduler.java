package jetris.model;

public interface GameScheduler {

    void start(GameModel context, Runnable runnable);

    void stop();

    void pause();

    void resume();
}
