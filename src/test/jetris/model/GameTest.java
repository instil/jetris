package jetris.model;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import jetris.model.Tetromino.Shape;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class GameTest {

    private Game game = gameThatCreates(Shape.I, Shape.I, Shape.O);

    @Test
    public void shouldContainSingleTetrominoOnCreation() throws Exception {
        // all tetrominos have 4 blocks
        assertThat(game.blocks().size(), is(4));
    }

    @Test
    public void shouldPositionFirstTetrominoAtTopOfBoard() throws Exception {
        assertThat(leftMostBlock().y(), is(0));
        assertThat(leftMostBlock().x(), is(3));
        assertThat(rightMostBlock().x(), is(6));
    }

    @Test
    public void shouldBeAbleToMoveFirstTetrominoRight() throws Exception {
        moveActiveTetrominoRightBy(1);
        assertThat(leftMostBlock().x(), is(4));
    }

    @Test
    public void shouldBeAbleToMoveFirstTetrominoLeft() throws Exception {
        moveActiveTetrominoLeftBy(1);
        assertThat(leftMostBlock().x(), is(2));
    }

    @Test
    public void shouldBeAbleToMoveTetrominoLeftMultiplePlaces() throws Exception {
        moveActiveTetrominoLeftBy(2);
        assertThat(leftMostBlock().x(), is(1));
    }

    @Test
    public void shouldBeAbleToMoveTetrominoRightMultiplePlaces() throws Exception {
        moveActiveTetrominoRightBy(2);
        assertThat(leftMostBlock().x(), is(5));
    }

    @Test
    public void shouldNotMoveTetrominoLeftWhenAtLeftEdgeOfBoard() throws Exception {
        moveActiveTetrominoLeftBy(3);
        assertThat(game.moveActiveTetrominoLeft(), is(false));
        assertThat(leftMostBlock().x(), is(0));
    }

    @Test
    public void shouldNotMoveTetrominoRightWhenAtRightEdgeOfBoard() throws Exception {
        moveActiveTetrominoRightBy(5);
        assertThat(game.moveActiveTetrominoRight(), is(false));
        assertThat(leftMostBlock().x(), is(6));
    }

    @Test
    public void shouldMoveTetrominoRightFromLeftEdgeOfBoard() throws Exception {
        moveActiveTetrominoLeftBy(3);
        assertThat(game.moveActiveTetrominoRight(), is(true));
        assertThat(leftMostBlock().x(), is(1));
    }

    @Test
    public void shouldMoveTetrominoLeftFromRightEdgeOfBoard() throws Exception {
        moveActiveTetrominoRightBy(5);
        assertThat(game.moveActiveTetrominoLeft(), is(true));
        assertThat(leftMostBlock().x(), is(5));
    }

    @Test
    public void shouldMoveTetrominoDownOnePlace() throws Exception {
        moveActiveTetrominoDownBy(1);
        assertThat(leftMostBlock().y(), is(1));
    }

    @Test
    public void shouldDropTetrominoToBottomOfBoard() throws Exception {
        game.dropActiveTetrominoDown();
        assertThat(leftMostBlock().y(), is(16));
    }

    @Test
    public void shouldMoveTetrominoDownMultiplePlaces() throws Exception {
        moveActiveTetrominoDownBy(4);
        assertThat(game.moveActiveTetrominoDown(), is(true));
        assertThat(leftMostBlock().y(), is(5));
    }

    @Test
    public void shouldNotMoveTetrominoDownWhenAtBottom() throws Exception {
        moveActiveTetrominoDownBy(16);
        assertThat(leftMostBlock().y(), is(16));
        assertThat(game.moveActiveTetrominoDown(), is(false));
    }

    @Test
    public void shouldNotMoveTetrominoDownOverExistingBlocks() throws Exception {
        game = gameThatCreates(Shape.I);
        for (int maxRows = 16; maxRows >= 0; maxRows--) {
            assertThat(moveActiveTetrominoDownBy(maxRows), is(true));
            assertThat(game.moveActiveTetrominoDown(), is(false));
            activateNextTetromino();
        }
    }

    @Test
    public void shouldRemoveCompletedRows() throws Exception {
        // Row made up of 2 'I's and 1 'O'.
        // BEFORE: |          |
        //         |        ее|
        //         |ееееееееее|
        completeLine();

        // Add another I shape to force removal of completed row
        // AFTER:  |          |
        //         |          |
        //         |ееее    ее|
        activateNextTetromino();
        assertThat(game.blocks().size(), is(6));
        assertThat(game.lines(), is(1));
    }

    @Test
    public void shouldRemoveMultipleCompletedRows() throws Exception {
        // COMPLETED ROW   : |        ее|
        //                   |ееееееееее|  <-- removed after assertion
        completeLine();
        assertThat(game.blocks().size(), is(12));

        // Add next I-Shape
        activateNextTetromino();
        assertThat(game.blocks().size(), is(6));

        // COMPLETED ROW  : |        ее|
        //                  |        ее|
        //                  |ееееееееее| <-- removed before assertion
        completeLine();
        assertThat(game.blocks().size(), is(4));


        // Add another I shape to force removal of completed rows
        // AFTER ACTIVATION   :  |   ееее   |
        //                            ...
        //                       |          |
        //                       |        ее|
        //                       |        ее|
        activateNextTetromino();
        assertThat(game.lines(), is(2));
        assertThat(game.blocks().size(), is(8));
    }

    @Test
    public void shouldScoreEachTetrominoByNumberOfRowsDropped() throws Exception {
        // I-shape to left, drop to row 17
        assertThat(game.score(), is(0));
        moveActiveTetrominoLeftBy(3);
        moveActiveTetrominoDownBy(16);
        activateNextTetromino();
        assertThat(game.score(), is(17));

        // I-shape to left on top of previous one, row 16
        moveActiveTetrominoLeftBy(3);
        moveActiveTetrominoDownBy(15);
        activateNextTetromino();
        assertThat(game.score(), is(33));

        // O-shape to left on top of previous shapes, row 14
        moveActiveTetrominoLeftBy(3);
        moveActiveTetrominoDownBy(14);
        activateNextTetromino();
        assertThat(game.score(), is(47));
    }

    @Test
    public void shouldScoreSingleCompletedRowAndDroppedShapes() throws Exception {
        // SCORE:  |          |
        //         |        ее|
        //         |ееееееееее|
        // score = 17 + 17 + 16 + 100
        completeLine();
        activateNextTetromino();
        assertThat(game.score(), is(150));
    }

    @Test
    public void shouldScoreDoubleCompletedRow() throws Exception {
        // SCORE:  |          |
        //         |ееееееееее|
        //         |ееееееееее|
        // score = 17 + 17 + 16 + 16 + 16 + 200
        game = gameThatCreates(Shape.I, Shape.I, Shape.I, Shape.I, Shape.O);
        completeLines(2);
        assertThat(game.score(), is(282));
    }

    @Test
    public void shouldScoreQuadrupleCompletedRow() throws Exception {
        // SCORE:  |ееееееееее|
        //         |ееееееееее|
        //         |ееееееееее|
        //         |ееееееееее|
        // score = (14 * 10) + 800
        game = gameThatCreates(Shape.I);
        rotateAndMoveLeftBy(4);
        rotateAndMoveLeftBy(3);
        rotateAndMoveLeftBy(2);
        rotateAndMoveLeftBy(1);
        rotateAndMoveLeftBy(0);
        rotateAndMoveRightBy(1);
        rotateAndMoveRightBy(2);
        rotateAndMoveRightBy(3);
        rotateAndMoveRightBy(4);
        rotateAndMoveRightBy(5);
        assertThat(game.score(), is(940));
    }

    @Test
    public void shouldRotateIShapeLeft() throws Exception {
        // INITIAL SHAPE: ееее
        game = gameThatCreates(Shape.I);
        assertThat(game.blocks(), areAt(new int[][] {{3, 0}, {4, 0}, {5, 0}, {6, 0}}));

        game.rotateActiveTetrominoLeft();
        assertThat(game.blocks(), areAt(new int[][] {{4, -1}, {4, 0}, {4, 1}, {4, 2}}));

        game.rotateActiveTetrominoLeft();
        assertThat(game.blocks(), areAt(new int[][] {{3, 0}, {4, 0}, {5, 0}, {6, 0}}));

        game.rotateActiveTetrominoLeft();
        assertThat(game.blocks(), areAt(new int[][] {{4, -1}, {4, 0}, {4, 1}, {4, 2}}));

        game.rotateActiveTetrominoLeft();
        assertThat(game.blocks(), areAt(new int[][] {{3, 0}, {4, 0}, {5, 0}, {6, 0}}));
    }

    @Test
    public void shouldRotateIShapeRight() throws Exception {
        // INITIAL SHAPE: ееее
        game = gameThatCreates(Shape.I);
        game.rotateActiveTetrominoRight();
        assertThat(game.blocks(), areAt(new int[][] {{4, -1}, {4, 0}, {4, 1}, {4, 2}}));

        game.rotateActiveTetrominoRight();
        assertThat(game.blocks(), areAt(new int[][] {{3, 0}, {4, 0}, {5, 0}, {6, 0}}));

        game.rotateActiveTetrominoRight();
        assertThat(game.blocks(), areAt(new int[][] {{4, -1}, {4, 0}, {4, 1}, {4, 2}}));

        game.rotateActiveTetrominoRight();
        assertThat(game.blocks(), areAt(new int[][] {{3, 0}, {4, 0}, {5, 0}, {6, 0}}));
    }

    @Test
    public void shouldRotateOShapeLeftAndRight() throws Exception {
        // INITIAL SHAPE: ее
        //                ее
        game = gameThatCreates(Shape.O);
        assertThat(game.blocks(), areAt(new int[][] {{3, 0}, {4, 0}, {3, 1}, {4, 1}}));

        game.rotateActiveTetrominoRight();
        assertThat(game.blocks(), areAt(new int[][] {{3, 0}, {4, 0}, {3, 1}, {4, 1}}));

        game.rotateActiveTetrominoLeft();
        assertThat(game.blocks(), areAt(new int[][] {{3, 0}, {4, 0}, {3, 1}, {4, 1}}));
    }

    @Test
    public void shouldRotateLShapeLeft() throws Exception {
        // INITIAL SHAPE:   е
        //                еее
        game = gameThatCreates(Shape.L);
        assertThat(game.blocks(), areAt(new int[][] {{5, 0}, {3, 1}, {4, 1}, {5, 1}}));

        game.rotateActiveTetrominoLeft();
        assertThat(game.blocks(), areAt(new int[][] {{3, 0}, {4, 2}, {4, 1}, {4, 0}}));

        game.rotateActiveTetrominoLeft();
        assertThat(game.blocks(), areAt(new int[][] {{3, 2}, {5, 1}, {4, 1}, {3, 1}}));

        game.rotateActiveTetrominoLeft();
        assertThat(game.blocks(), areAt(new int[][] {{5, 2}, {4, 0}, {4, 1}, {4, 2}}));

        game.rotateActiveTetrominoLeft();
        assertThat(game.blocks(), areAt(new int[][] {{5, 0}, {3, 1}, {4, 1}, {5, 1}}));
    }

    @Test
    public void shouldRotateLShapeRight() throws Exception {
        // INITIAL SHAPE:   е
        //                еее
        game = gameThatCreates(Shape.L);
        game.rotateActiveTetrominoRight();
        assertThat(game.blocks(), areAt(new int[][] {{5, 2}, {4, 0}, {4, 1}, {4, 2}}));

        game.rotateActiveTetrominoRight();
        assertThat(game.blocks(), areAt(new int[][] {{3, 2}, {5, 1}, {4, 1}, {3, 1}}));

        game.rotateActiveTetrominoRight();
        assertThat(game.blocks(), areAt(new int[][] {{3, 0}, {4, 2}, {4, 1}, {4, 0}}));

        game.rotateActiveTetrominoRight();
        assertThat(game.blocks(), areAt(new int[][] {{5, 0}, {3, 1}, {4, 1}, {5, 1}}));
    }

    @Test
    public void shouldRotateJShapeLeft() throws Exception {
        // INITIAL SHAPE: е
        //                еее
        game = gameThatCreates(Shape.J);
        assertThat(game.blocks(), areAt(new int[][] {{3, 0}, {3, 1}, {4, 1}, {5, 1}}));

        game.rotateActiveTetrominoLeft();
        assertThat(game.blocks(), areAt(new int[][] {{3, 2}, {4, 2}, {4, 1}, {4, 0}}));

        game.rotateActiveTetrominoLeft();
        assertThat(game.blocks(), areAt(new int[][] {{5, 2}, {5, 1}, {4, 1}, {3, 1}}));

        game.rotateActiveTetrominoLeft();
        assertThat(game.blocks(), areAt(new int[][] {{5, 0}, {4, 0}, {4, 1}, {4, 2}}));

        game.rotateActiveTetrominoLeft();
        assertThat(game.blocks(), areAt(new int[][] {{3, 0}, {3, 1}, {4, 1}, {5, 1}}));
    }

    @Test
    public void shouldRotateJShapeRight() throws Exception {
        // INITIAL SHAPE: е
        //                еее
        game = gameThatCreates(Shape.J);
        game.rotateActiveTetrominoRight();
        assertThat(game.blocks(), areAt(new int[][] {{5, 0}, {4, 0}, {4, 1}, {4, 2}}));

        game.rotateActiveTetrominoRight();
        assertThat(game.blocks(), areAt(new int[][] {{5, 2}, {5, 1}, {4, 1}, {3, 1}}));

        game.rotateActiveTetrominoRight();
        assertThat(game.blocks(), areAt(new int[][] {{3, 2}, {4, 2}, {4, 1}, {4, 0}}));

        game.rotateActiveTetrominoRight();
        assertThat(game.blocks(), areAt(new int[][] {{3, 0}, {3, 1}, {4, 1}, {5, 1}}));
    }

    @Test
    public void shouldRotateSShapeLeft() throws Exception {
        // INITIAL SHAPE:  ее
        //                ее
        game = gameThatCreates(Shape.S);
        assertThat(game.blocks(), areAt(new int[][] {{4, 0}, {5, 0}, {3, 1}, {4, 1}}));

        game.rotateActiveTetrominoLeft();
        assertThat(game.blocks(), areAt(new int[][] {{4, 0}, {5, 1}, {5, 2}, {4, 1}}));

        game.rotateActiveTetrominoLeft();
        assertThat(game.blocks(), areAt(new int[][] {{4, 0}, {5, 0}, {3, 1}, {4, 1}}));

        game.rotateActiveTetrominoLeft();
        assertThat(game.blocks(), areAt(new int[][] {{4, 0}, {5, 1}, {5, 2}, {4, 1}}));
    }

    @Test
    public void shouldRotateSShapeRight() throws Exception {
        // INITIAL SHAPE:  ее
        //                ее
        game = gameThatCreates(Shape.S);
        game.rotateActiveTetrominoRight();
        assertThat(game.blocks(), areAt(new int[][] {{4, 0}, {5, 1}, {5, 2}, {4, 1}}));

        game.rotateActiveTetrominoRight();
        assertThat(game.blocks(), areAt(new int[][] {{4, 0}, {5, 0}, {3, 1}, {4, 1}}));

        game.rotateActiveTetrominoRight();
        assertThat(game.blocks(), areAt(new int[][] {{4, 0}, {5, 1}, {5, 2}, {4, 1}}));
    }


    @Test
    public void shouldRotateZShapeLeft() throws Exception {
        // INITIAL SHAPE: ее
        //                 ее
        game = gameThatCreates(Shape.Z);
        assertThat(game.blocks(), areAt(new int[][] {{3, 0}, {4, 0}, {4, 1}, {5, 1}}));

        game.rotateActiveTetrominoLeft();
        assertThat(game.blocks(), areAt(new int[][] {{3, 1}, {4, 0}, {4, 1}, {3, 2}}));

        game.rotateActiveTetrominoLeft();
        assertThat(game.blocks(), areAt(new int[][] {{3, 0}, {4, 0}, {4, 1}, {5, 1}}));

        game.rotateActiveTetrominoLeft();
        assertThat(game.blocks(), areAt(new int[][] {{3, 1}, {4, 0}, {4, 1}, {3, 2}}));
    }

    @Test
    public void shouldRotateZShapeRight() throws Exception {
        // INITIAL SHAPE: ее
        //                 ее
        game = gameThatCreates(Shape.Z);
        game.rotateActiveTetrominoRight();
        assertThat(game.blocks(), areAt(new int[][] {{3, 1}, {4, 0}, {4, 1}, {3, 2}}));

        game.rotateActiveTetrominoRight();
        assertThat(game.blocks(), areAt(new int[][] {{3, 0}, {4, 0}, {4, 1}, {5, 1}}));

        game.rotateActiveTetrominoRight();
        assertThat(game.blocks(), areAt(new int[][] {{3, 1}, {4, 0}, {4, 1}, {3, 2}}));
    }

    @Test
    public void shouldRotateTShapeLeft() throws Exception {
        // INITIAL SHAPE:  е
        //                еее
        game = gameThatCreates(Shape.T);
        assertThat(game.blocks(), areAt(new int[][] {{4, 0}, {3, 1}, {4, 1}, {5, 1}}));

        game.rotateActiveTetrominoLeft();
        assertThat(game.blocks(), areAt(new int[][] {{3, 1}, {4, 2}, {4, 1}, {4, 0}}));

        game.rotateActiveTetrominoLeft();
        assertThat(game.blocks(), areAt(new int[][] {{4, 2}, {5, 1}, {4, 1}, {3, 1}}));

        game.rotateActiveTetrominoLeft();
        assertThat(game.blocks(), areAt(new int[][] {{5, 1}, {4, 0}, {4, 1}, {4, 2}}));
    }

    @Test
    public void shouldRotateTShapeRight() throws Exception {
        // INITIAL SHAPE:  е
        //                еее
        game = gameThatCreates(Shape.T);
        game.rotateActiveTetrominoRight();
        assertThat(game.blocks(), areAt(new int[][] {{5, 1}, {4, 0}, {4, 1}, {4, 2}}));

        game.rotateActiveTetrominoRight();
        assertThat(game.blocks(), areAt(new int[][] {{4, 2}, {5, 1}, {4, 1}, {3, 1}}));

        game.rotateActiveTetrominoRight();
        assertThat(game.blocks(), areAt(new int[][] {{3, 1}, {4, 2}, {4, 1}, {4, 0}}));
    }

    @Test
    public void shouldHaveNextTetrominoAfterGameStarts() throws Exception {
        game = gameThatCreates(Shape.O, Shape.I, Shape.S);
        assertThat(game.blocks(), areAt(new int[][] {{3, 0}, {4, 0}, {3, 1}, {4, 1}}));

        assertThat(game.nextTetromino(), areAt(new int[][] {{0, 0}, {1, 0}, {2, 0}, {3, 0}}));
        game.activateNextTetromino();

        assertThat(game.nextTetromino(), areAt(new int[][] {{1, 0}, {2, 0}, {0, 1}, {1, 1}}));
    }

    @Test
    public void shouldLevelUpAfterTenRows() throws Exception {
        game = gameThatCreates(Shape.I, Shape.I, Shape.I, Shape.I, Shape.O);
        assertThat(game.level(), is(0));
        completeLines(8);
        assertThat(game.level(), is(0));

        completeLines(2);
        assertThat(game.level(), is(1));
        assertThat(game.lines(), is(10));
    }

    @Test
    public void shouldLevelUpEveryTenRows() throws Exception {
        game = gameThatCreates(Shape.I, Shape.I, Shape.I, Shape.I, Shape.O);
        assertThat(game.level(), is(0));
        for (int i = 0; i < 10; i++) {
            completeLines(10);
            assertThat(game.level(), is(i + 1));
        }
    }

    @Test
    public void shouldMoveBlocksDownWhenRowsCompleted() throws Exception {
        game = gameThatCreates(Shape.Z, Shape.I, Shape.I, Shape.I, Shape.I);
        // After first shape:
        //      |         е|
        //      |        ее|
        //      |        е |
        game.rotateActiveTetrominoRight();
        moveActiveTetrominoRightBy(5);
        dropAndActivateNextTetromino();

        //
        // followed by 4 Is:
        //      |         е|
        //      |ееееееееее|  <-- removed
        //      |еееееееее |
        //
        //      |         е|
        //      |еееееееее |
        moveActiveTetrominoLeftBy(3);
        dropAndActivateNextTetromino();
        moveActiveTetrominoRightBy(1);
        dropAndActivateNextTetromino();
        moveActiveTetrominoLeftBy(3);
        dropAndActivateNextTetromino();
        moveActiveTetrominoRightBy(1);
        dropAndActivateNextTetromino();
        assertThat(game.lines(), is(1));
        assertThat(game.blocks().size(), is(14));
    }

    private void completeLines(int lines) {
        // Adds complete pairs of lines
        // initially 4 Is:
        //      |ееееееее  |
        //      |ееееееее  |
        //
        // followed by a single O:
        //      |ееееееееее|
        //      |ееееееееее|
        for (int i = 0; i < lines / 2; i++) {
            moveActiveTetrominoLeftBy(3);
            dropAndActivateNextTetromino();
            moveActiveTetrominoRightBy(1);
            dropAndActivateNextTetromino();
            moveActiveTetrominoLeftBy(3);
            dropAndActivateNextTetromino();
            moveActiveTetrominoRightBy(1);
            dropAndActivateNextTetromino();
            moveActiveTetrominoRightBy(5);
            dropAndActivateNextTetromino();
        }
    }

    private void dropAndActivateNextTetromino() {
        moveActiveTetrominoDownBy(16);
        activateNextTetromino();
    }

    private void completeLine() {
        // I-shape to left
        moveActiveTetrominoLeftBy(3);
        moveActiveTetrominoDownBy(16);

        // I-shape to right of first shape
        activateNextTetromino();
        moveActiveTetrominoRightBy(1);
        moveActiveTetrominoDownBy(16);

        // O-shape to right of I-shapes
        activateNextTetromino();
        moveActiveTetrominoRightBy(5);
        moveActiveTetrominoDownBy(16);
    }

    private Matcher<ImmutableList<Block>> areAt(final int[][] points) {
        return new BaseMatcher<ImmutableList<Block>>() {

            @SuppressWarnings("unchecked") @Override
            public boolean matches(Object item) {
                ImmutableList<Block> blocks = (ImmutableList<Block>) item;
                for (int i = 0; i < points.length; i++) {
                    if ((blocks.get(i).x() != points[i][0]) ||
                        (blocks.get(i).y() != points[i][1])) {
                        return false;
                    }
                }
                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Invalid rotation");
            }
        };
    }

    /**
     * Creates games that will use only the supplied shapes
     * in the order given.
     */
    private Game gameThatCreates(final Shape...shapes) {
        return new Game(new TetrominoFactory() {

            private int index;

            @Override
            public Tetromino create() {
                index = index % shapes.length;
                return new Tetromino(shapes[index++]);
            }
        });
    }

    private boolean moveActiveTetrominoDownBy(int count) {
        for (int i = 0; i < count; i++) {
            if (!game.moveActiveTetrominoDown()) {
                return false;
            }
        }
        return true;
    }

    private void moveActiveTetrominoRightBy(int count) {
        for (int i = 0; i < count; i++) {
            game.moveActiveTetrominoRight();
        }
    }

    private void moveActiveTetrominoLeftBy(int count) {
        for (int i = 0; i < count; i++) {
            game.moveActiveTetrominoLeft();
        }
    }

    private void rotateAndMoveLeftBy(int count) {
        game.rotateActiveTetrominoLeft();
        moveActiveTetrominoLeftBy(count);
        dropAndActivateNextTetromino();
    }

    private void rotateAndMoveRightBy(int count) {
        game.rotateActiveTetrominoLeft();
        moveActiveTetrominoRightBy(count);
        dropAndActivateNextTetromino();
    }

    private void activateNextTetromino() {
        game.activateNextTetromino();
    }

    private Block leftMostBlock() {
        return game.blocks().get(0);
    }

    private Block rightMostBlock() {
        return game.blocks().get(3);
    }
}
