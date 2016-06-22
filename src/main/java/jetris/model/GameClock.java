package jetris.model;

public interface GameClock {

    void start(GameModel model, Runnable tickTask);

    void stop();

    void pause();

    void resume();
}
