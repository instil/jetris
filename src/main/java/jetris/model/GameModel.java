package jetris.model;

import java.util.List;

import com.google.common.collect.ImmutableList;


public class GameModel extends GameObject {

    /**
     * State of play:
     * <pre>
     *   INITIAL-->ACTIVE<-----
     *               ^         |
     *               |         |
     *               v         v
     *             PAUSED--->ENDED
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

    /**
     * Moves active tetromino to the left
     * @return <tt>true</tt> if the state of the game has change (i.e. the
     *         tetromino was successfully moved), <tt>false</tt> otherwise.
     */
    synchronized boolean moveActiveTetrominoLeft() {
        return (state == GameState.ACTIVE) && game.moveActiveTetrominoLeft();
    }

    /**
     * Moves active tetromino to the right
     * @return <tt>true</tt> if the state of the game has change (i.e. the
     *         tetromino was successfully moved), <tt>false</tt> otherwise.
     */
    synchronized boolean moveActiveTetrominoRight() {
        return (state == GameState.ACTIVE) && game.moveActiveTetrominoRight();
    }

    /**
     * Moves active tetromino to the down
     * @return <tt>true</tt> if the state of the game has change (i.e. the
     *         tetromino was successfully moved or the game ended), <tt>false</tt>
     *         otherwise.
     */
    synchronized boolean moveActiveTetrominoDown() {
        if (state == GameState.ACTIVE) {
            if (!game.moveActiveTetrominoDown() && !game.activateNextTetromino()) {
                state = GameState.ENDED;
            }
            return true;
        }
        return false;
    }

    /**
     * Drops active tetromino to the bottom of the well
     * @return <tt>true</tt> if the state of the game has change (i.e. the
     *         tetromino was successfully dropped or the game ended),
     *         <tt>false</tt> otherwise.
     */
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

    /**
     * Rotates the active tetromino anti-clockwise, to the left.
     * @return <tt>true</tt> if the state of the game has change (i.e. the
     *         tetromino was successfully rotated), <tt>false</tt> otherwise.
     */
    synchronized boolean rotateActiveTetrominoLeft() {
        return (state == GameState.ACTIVE) && game.rotateActiveTetrominoLeft();
    }

    /**
     * Rotates the active tetromino clockwise, to the right.
     * @return <tt>true</tt> if the state of the game has change (i.e. the
     *         tetromino was successfully rotated), <tt>false</tt> otherwise.
     */
    synchronized boolean rotateActiveTetrominoRight() {
        return (state == GameState.ACTIVE) && game.rotateActiveTetrominoRight();
    }
}
