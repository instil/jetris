package jetris.model;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import jetris.model.GameModel.GameState;
import jetris.util.DeterministicScheduler;

public class GameControllerTest {

    private final DeterministicScheduler scheduler = new DeterministicScheduler();

    private final GameModelListener modelListener = mock(GameModelListener.class);

    private final GameController gameController = new GameController(modelListener, scheduler);

    @Test
    public void shouldNotInformModelListenerAfterMoveLeftWhenGameNotInPlay() throws Exception {
        moveLeft().thenVerifyNoInteractionOnModelListener();
    }

    @Test
    public void shouldNotInformModelListenerAfterMoveRightWhenGameNotInPlay() throws Exception {
        moveRight().thenVerifyNoInteractionOnModelListener();
    }

    @Test
    public void shouldNotInformModelListenerAfterMoveDownWhenGameNotInPlay() throws Exception {
        moveDown().thenVerifyNoInteractionOnModelListener();
    }

    @Test
    public void shouldInformModelListenerOfActiveStateWhenPlayStarts() throws Exception {
        startGame().thenVerifyLastPublishedStateIs(activeState());
        assertThat(scheduler.isStarted(), is(true));
        assertThat(scheduler.isPaused(), is(false));
    }

    @Test
    public void shouldPauseSchedulerAndInformModelListenerOfPausedStateWhenGamePaused() throws Exception {
        startGame().pauseGame().thenVerifyLastPublishedStateIs(pausedState());
        assertThat(scheduler.isStarted(), is(true));
        assertThat(scheduler.isPaused(), is(true));
    }

    @Test
    public void shouldIgnoreMovesWhenGamePaused() throws Exception {
        startGame().pauseGame().moveLeft().moveRight().thenVerifyLastPublishedStateIs(pausedState());
    }

    @Test
    public void shouldRestartSchedulerAndPublishActiveModelWhenGameResumed() throws Exception {
        startGame().pauseGame().resumeGame().thenVerifyLastPublishedStateIs(activeState());
        assertThat(scheduler.isStarted(), is(true));
        assertThat(scheduler.isPaused(), is(false));
    }

    @Test
    public void shouldStopSchedulerAndPublishEndedModelWhenGameEnded() throws Exception {
        startGame().endGame().thenVerifyLastPublishedStateIs(endState());
        assertThat(scheduler.isStarted(), is(false));
    }

    @Test
    public void shouldPublishGameModelWhenSchedulerFires() throws Exception {
        startGame().fireScheduler().thenVerifyLastPublishedStateIs(activeState());
    }

    @Test
    public void shouldActivateTetrominoWhenGameStarts() throws Exception {
        startGame().fireScheduler().thenVerifyGameHas(aTetromino());
    }

    @Test
    public void shouldInformModelListenerAfterMoveLeftWhenGameInPlay() throws Exception {
        startGame().moveLeft().thenVerifyGameHas(firstTetrominoAtX(2));
        startGame().moveLeft().thenVerifyGameHas(firstTetrominoAtY(0));
    }

    @Test
    public void shouldInformModelListenerAfterMoveRightWhenGameInPlay() throws Exception {
        startGame().moveRight().thenVerifyGameHas(firstTetrominoAtX(4));
        startGame().moveRight().thenVerifyGameHas(firstTetrominoAtY(0));
    }

    @Test
    public void shouldInformModelListenerAfterMoveDownWhenGameInPlay() throws Exception {
        startGame().moveDown().thenVerifyGameHas(firstTetrominoAtY(1));
    }

    @Test
    public void shouldNotInformModelListenerAfterRotateLeftWhenGameNotInPlay() throws Exception {
        rotateLeft().thenVerifyNoInteractionOnModelListener();
    }

    @Test
    public void shouldNotInformModelListenerAfterRotateRightWhenGameNotInPlay() throws Exception {
        rotateRight().thenVerifyNoInteractionOnModelListener();
    }

    @Test
    public void shouldInformModelListenerAfterRotateLeftWhenGameInPlay() throws Exception {
        startGame().rotateLeft().thenVerifyModelListenerInformed();
    }

    @Test
    public void shouldInformModelListenerAfterRotateRightWhenGameInPlay() throws Exception {
        startGame().rotateRight().thenVerifyModelListenerInformed();
    }

    @Test
    public void shouldInformModelListenerWhenActiveTetrominoDropped() throws Exception {
        startGame().dropDown().thenVerifyGameHas(aTetrominoAtY(16));
    }

    @Test
    public void shouldStopSchedulerWhenGameEndsAfterDrop() throws Exception {
        startGame().dropUntilGamesEnds().thenVerifyLastPublishedStateIs(endState());
        assertThat(scheduler.isStarted(), is(false));
    }

    @Test
    public void shouldStopSchedulerWhenGameEndsAfterMoveDown() throws Exception {
        startGame().moveDownUntilGamesEnds().thenVerifyLastPublishedStateIs(endState());
        assertThat(scheduler.isStarted(), is(false));
    }

    private GameControllerTest fireScheduler() {
        scheduler.fire();
        return this;
    }

    private GameControllerTest thenVerifyLastPublishedStateIs(GameState state) {
        // Mockito doesn't handle case where same object is passed to method multiple
        // times and its model changes.  Only the last published model can be verified.
        ArgumentCaptor<GameModel> capturedModel = ArgumentCaptor.forClass(GameModel.class);
        verify(modelListener, atLeastOnce()).modelChanged(capturedModel.capture());
        assertThat(capturedModel.getValue().state(), is(state));
        return this;
    }

    private GameControllerTest thenVerifyNoInteractionOnModelListener() {
        verifyNoMoreInteractions(modelListener);
        return this;
    }

    private void thenVerifyModelListenerInformed() {
        verify(modelListener, atLeastOnce()).modelChanged(isA(GameModel.class));
    }

    private GameControllerTest thenVerifyGameHas(Matcher<List<Block>> blocks) {
        // Mockito doesn't handle case where same object is passed to method multiple
        // times and its Model changes.  Only the last known Model can be verified.
        ArgumentCaptor<GameModel> capturedGameModel = ArgumentCaptor.forClass(GameModel.class);
        verify(modelListener, atLeastOnce()).modelChanged(capturedGameModel.capture());
        assertThat(capturedGameModel.getValue().blocks(), is(blocks));
        return this;
    }

    private GameControllerTest startGame() {
        gameController.startGame();
        return this;
    }

    private GameControllerTest endGame() {
        gameController.endGame();
        return this;
    }

    private GameControllerTest pauseGame() {
        gameController.pauseGame();
        return this;
    }

    private GameControllerTest resumeGame() {
        gameController.resumeGame();
        return this;
    }

    private GameControllerTest moveLeft() {
        gameController.moveLeft();
        return this;
    }

    private GameControllerTest moveRight() {
        gameController.moveRight();
        return this;
    }

    private GameControllerTest moveDown() {
        gameController.moveDown();
        return this;
    }

    private GameControllerTest dropDown() {
        gameController.dropDown();
        return this;
    }

    private GameControllerTest rotateLeft() {
        gameController.rotateLeft();
        return this;
    }

    private GameControllerTest rotateRight() {
        gameController.rotateRight();
        return this;
    }

    private GameControllerTest dropUntilGamesEnds() {
        final AtomicBoolean isActive = new AtomicBoolean(true);
        setInactiveWhenGameEnds(isActive);
        while (isActive.get()) {
            gameController.dropDown();
        }
        return this;
    }

    private GameControllerTest moveDownUntilGamesEnds() {
        final AtomicBoolean isActive = new AtomicBoolean(true);
        setInactiveWhenGameEnds(isActive);
        while (isActive.get()) {
            gameController.moveDown();
        }
        return this;
    }

    private void setInactiveWhenGameEnds(final AtomicBoolean isActive) {
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                isActive.set(((GameModel) invocation.getArguments()[0]).state() != GameState.ENDED);
                return null;
            }
        }).when(modelListener).modelChanged(isA(GameModel.class));
    }

    private GameState activeState() {
        return GameState.ACTIVE;
    }

    private GameState pausedState() {
        return GameState.PAUSED;
    }

    private GameState endState() {
        return GameState.ENDED;
    }

    private Matcher<List<Block>> aTetromino() {
        return new BaseMatcher<List<Block>>() {
            @SuppressWarnings("unchecked") @Override
            public boolean matches(Object item) {
                // All tetrominos have 4 blocks
                List<Block> blocks = (List<Block>) item;
                return blocks.size() == 4;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Board did not contain a single tetromino");
            }
        };
    }

    private Matcher<List<Block>> firstTetrominoAtX(final int x) {
        return new BaseMatcher<List<Block>>() {
            @SuppressWarnings("unchecked") @Override
            public boolean matches(Object item) {
                int firstX = Integer.MAX_VALUE;
                for (Block block : (List<Block>) item) {
                    firstX = (block.x() < firstX) ? block.x() : firstX;
                }
                return firstX == x;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Board did not contain a tetromino block at x=" + x);
            }
        };
    }

    private Matcher<List<Block>> firstTetrominoAtY(final int y) {
        return new BaseMatcher<List<Block>>() {
            @SuppressWarnings("unchecked") @Override
            public boolean matches(Object item) {
                int firstY = Integer.MAX_VALUE;
                for (Block block : (List<Block>) item) {
                    firstY = (block.y() < firstY) ? block.y() : firstY;
                }
                return firstY == y;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Board did not contain a tetromino block at y=" + y);
            }
        };
    }

    private Matcher<List<Block>> aTetrominoAtY(final int y) {
        return new BaseMatcher<List<Block>>() {
            @SuppressWarnings("unchecked") @Override
            public boolean matches(Object item) {
                for (Block block : (List<Block>) item) {
                    if (block.y() == y) return true;
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Board did not contain a tetromino block at y=" + y);
            }
        };
    }




}
