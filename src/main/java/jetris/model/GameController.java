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

    private final GameClock clock;

    public GameController(GameModelListener modelListener, GameClock clock) {
        this.modelListener = modelListener;
        this.clock = clock;
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
                clock.stop();
            }
        }
    }

    public void dropDown() {
        if (model.dropActiveTetrominoDown()) {
            updateModelListener();
            if (model.state() == GameState.ENDED) {
                clock.stop();
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
        clock.start(model, () -> handleTick());
    }

    private void handleTick() {
        if (!model.moveActiveTetrominoDown()) {
            clock.stop();
        }
        updateModelListener();
    }

    public void endGame() {
        model.end();
        updateModelListener();
        clock.stop();
    }

    public void pauseGame() {
        model.pause();
        updateModelListener();
        clock.pause();
    }

    public void resumeGame() {
        model.resume();
        updateModelListener();
        clock.resume();
    }

    private void updateModelListener() {
        modelListener.modelChanged(model);
    }
}
