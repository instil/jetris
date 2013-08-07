package jetris.model;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import jetris.model.GameModel.GameState;

public class GameModelTest {

    private final GameModel model = new GameModel();

    @Test
    public void shouldMoveGameStateToActiveOnStart() throws Exception {
        model.start();
        assertThat(model.state(), is(GameState.ACTIVE));
    }

    @Test
    public void shouldEndGameWhenNoMoreTetrominosCanBeAdded() throws Exception {
        model.start();
        assertThat(model.state(), is(GameState.ACTIVE));
        while (model.moveActiveTetrominoDown()) {
            // empty
        }
        assertThat(model.state(), is(GameState.ENDED));
    }

    @Test
    public void shouldUpdateBoardAsTetrominosAreMovedDown() throws Exception {
        model.start();
        assertThat(model.blocks().size(), is(4));
        while (model.moveActiveTetrominoDown()) {
            // empty
        }
        // at least 8 rows of 4 blocks will have been added
        assertThat(model.blocks().size(), greaterThan(8 * 4));
    }

    @Test
    public void shouldUpdateBoardAsTetrominosAreDropped() throws Exception {
        model.start();
        assertThat(model.blocks().size(), is(4));
        assertThat(model.state(), is(GameState.ACTIVE));
        while (model.dropActiveTetrominoDown()) {
            // empty
        }
        // at least 8 rows of 4 blocks will have been added
        assertThat(model.state(), is(GameState.ENDED));
        assertThat(model.blocks().size(), greaterThan(8 * 4));
    }

    @Test
    public void shouldNotMoveActiveTetrominoWhenGameHasEnded() throws Exception {
        model.start();
        model.end();
        assertThat(model.moveActiveTetrominoDown(), is(false));
        assertThat(model.moveActiveTetrominoLeft(), is(false));
        assertThat(model.moveActiveTetrominoRight(), is(false));
        assertThat(model.dropActiveTetrominoDown(), is(false));
    }

    @Test
    public void shouldNotMoveActiveTetrominoWhenGameIsPaused() throws Exception {
        model.start();
        model.pause();
        assertThat(model.moveActiveTetrominoDown(), is(false));
        assertThat(model.moveActiveTetrominoLeft(), is(false));
        assertThat(model.moveActiveTetrominoRight(), is(false));
        assertThat(model.dropActiveTetrominoDown(), is(false));
    }

    @Test
    public void shouldNotMoveActiveTetrominoWhenGameHasNotStarted() throws Exception {
        assertThat(model.moveActiveTetrominoDown(), is(false));
        assertThat(model.moveActiveTetrominoLeft(), is(false));
        assertThat(model.moveActiveTetrominoRight(), is(false));
        assertThat(model.dropActiveTetrominoDown(), is(false));
    }

    @Test
    public void shouldMoveActiveTetrominoWhenGameIsStarted() throws Exception {
        model.start();
        assertThat(model.moveActiveTetrominoDown(), is(true));
        assertThat(model.moveActiveTetrominoLeft(), is(true));
        assertThat(model.moveActiveTetrominoRight(), is(true));
        assertThat(model.dropActiveTetrominoDown(), is(true));
    }

    @Test
    public void shouldNotMoveActiveTetrominoBeyondLeftBoundary() throws Exception {
        model.start();
        int moves = 0;
        while (model.moveActiveTetrominoLeft()) {
            moves++;
        }
        assertThat(moves, is(3));
        assertThat(model.moveActiveTetrominoLeft(), is(false));
    }

    @Test
    public void shouldNotMoveActiveTetrominoBeyondRightBoundary() throws Exception {
        model.start();
        int moves = 0;
        while (model.moveActiveTetrominoRight()) {
            moves++;
        }
        assertThat(moves, greaterThanOrEqualTo(3));
        assertThat(model.moveActiveTetrominoRight(), is(false));
    }

    @Test
    public void shouldHaveNextTetrominoAfterGameStarts() throws Exception {
        model.start();
        assertThat(model.nextTetromino().size(), is(4));
    }

    @Test
    public void shouldUpdateStatsInformationAfterGameStarts() throws Exception {
        model.start();
        assertThat(model.score(), is(0));
        assertThat(model.lines(), is(0));
        model.dropActiveTetrominoDown();
        assertThat(model.score(), greaterThanOrEqualTo(16));
    }
}
