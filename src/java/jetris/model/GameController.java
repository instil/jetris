package jetris.model;

import jetris.model.GameModel.GameState;

/**
 * Controller that arbitrates the flow of messages between
 * the model and the view.  Messages can flow in both
 * directions.  Messages from the model to the view are
 * sent via the {@link GameModelListener model listener}.
 */
public class GameController {

    private final GameModel model;

    private final GameModelListener modelListener;

    private final GameScheduler scheduler;

    private final Runnable scheduleTask = new Runnable() {
        @Override
        public void run() {
            updateGame();
        }
    };

    public GameController(GameModelListener modelListener, GameScheduler scheduler) {
        this.modelListener = modelListener;
        this.scheduler = scheduler;
        model = new GameModel();
    }

    public void moveLeft() {
        if (model.moveActiveTetrominoLeft()) {
            updateModelListener();
        }
    }

    public void moveRight() {
        if (model.moveActiveTetrominoRight()) {
            updateModelListener();
        }
    }

    public void moveDown() {
        if (model.moveActiveTetrominoDown()) {
            updateModelListener();
            if (model.state() == GameState.ENDED) {
                scheduler.stop();
            }
        }
    }

    public void dropDown() {
        if (model.dropActiveTetrominoDown()) {
            updateModelListener();
            if (model.state() == GameState.ENDED) {
                scheduler.stop();
            }
        }
    }

    public void rotateLeft() {
        if (model.rotateActiveTetrominoLeft()) {
            updateModelListener();
        }
    }

    public void rotateRight() {
        if (model.rotateActiveTetrominoRight()) {
            updateModelListener();
        }
    }

    public void startGame() {
        model.start();
        updateModelListener();
        scheduler.start(model, scheduleTask);
    }

    public void endGame() {
        model.end();
        updateModelListener();
        scheduler.stop();
    }

    public void pauseGame() {
        model.pause();
        updateModelListener();
        scheduler.pause();
    }

    public void resumeGame() {
        model.resume();
        updateModelListener();
        scheduler.resume();
    }

    private void updateGame() {
        if (!model.moveActiveTetrominoDown()) {
            scheduler.stop();
        }
        updateModelListener();
    }

    private void updateModelListener() {
        modelListener.modelChanged(model);
    }
}
