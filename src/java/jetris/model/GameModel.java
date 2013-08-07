package jetris.model;

import java.util.List;

import com.google.common.collect.ImmutableList;


public class GameModel extends GameObject {

    /**
     * State of play:
     * <pre>
     *   INITIALÐÐ>ACTIVE<ÐÐÐÐÐ
     *               ^         |
     *               |         |
     *               v         v
     *             PAUSEDÐÐÐ>ENDED
     * </pre>
     */
    public enum GameState {
        INITIAL,
        ACTIVE,
        PAUSED,
        ENDED
    }

    private GameState state = GameState.INITIAL;

    private Game game;

    public GameState state() {
        return state;
    }

    public ImmutableList<Block> blocks() {
        return game.blocks();
    }

    public int level() {
        return game.level();
    }

    public int lines() {
        return game.lines();
    }

    public int score() {
        return game.score();
    }

    public List<Block> nextTetromino() {
        return game.nextTetromino();
    }

    synchronized void start() {
        state = GameState.ACTIVE;
        game = new Game(new RandomTetrominoFactory());
    }

    synchronized void end() {
        state = GameState.ENDED;
    }

    synchronized void pause() {
        state = GameState.PAUSED;
    }

    synchronized void resume() {
        state = GameState.ACTIVE;
    }

    synchronized boolean moveActiveTetrominoLeft() {
        return (state == GameState.ACTIVE) && game.moveActiveTetrominoLeft();
    }

    synchronized boolean moveActiveTetrominoRight() {
        return (state == GameState.ACTIVE) && game.moveActiveTetrominoRight();
    }

    synchronized boolean moveActiveTetrominoDown() {
        if (state == GameState.ACTIVE) {
            if (!game.moveActiveTetrominoDown() && !game.activateNextTetromino()) {
                state = GameState.ENDED;
            }
            return true;
        }
        return false;
    }

    synchronized boolean dropActiveTetrominoDown() {
        if (state == GameState.ACTIVE) {
            game.dropActiveTetrominoDown();
            if (!game.activateNextTetromino()) {
                state = GameState.ENDED;
            }
            return true;
        }
        return false;
    }

    synchronized boolean rotateActiveTetrominoLeft() {
        return (state == GameState.ACTIVE) && game.rotateActiveTetrominoLeft();
    }

    synchronized boolean rotateActiveTetrominoRight() {
        return (state == GameState.ACTIVE) && game.rotateActiveTetrominoRight();
    }
}
