package jetris.model;

class DeterministicGameClock implements GameClock {

    private Runnable runnable;
    private boolean isPaused;

    @Override
    public void start(GameModel state, Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public void stop() {
        runnable = null;
    }

    @Override
    public void pause() {
        isPaused = true;
    }

    @Override
    public void resume() {
        isPaused = false;
    }

    public void fire() {
        if (!isPaused && (runnable != null)) {
            runnable.run();
        }
    }

    public boolean isStarted() {
        return runnable != null;
    }

    public boolean isPaused() {
        return isPaused;
    }
}
