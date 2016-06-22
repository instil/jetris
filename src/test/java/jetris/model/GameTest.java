package jetris.model;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import jetris.model.Tetromino.Shape;

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
        assertThat(leftMostBlock().y(), is(0));
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
        // After row removed, expect 2 remaining blocks + 4 blocks
        // for new active tetromino
        completeLine();
        assertThat(game.lines(), is(1));
        assertThat(game.blocks().size(), is(6));
        assertThat(game.blocks().stream().filter(b -> b.y() == 0).count(), is(4L));
        assertThat(game.blocks().stream().filter(b -> b.y() == 16).count(), is(2L));
    }

    @Test
    public void shouldRemoveMultipleCompletedRows() throws Exception {
        // COMPLETED ROW   : |        oo|
        // 2 fixed blocks + 4 blocks from active tetromino
        completeLine();
        assertThat(game.blocks().size(), is(6));

        // COMPLETED ROW  : |        oo|
        //                  |        oo|
        // 4 fixed blocks + 4 blocks from active tetromino
        completeLine();
        assertThat(game.blocks().size(), is(8));
        assertThat(game.lines(), is(2));
    }

    @Test
    public void shouldScoreEachTetrominoByNumberOfRowsDropped() throws Exception {
        // I-shape to left and drop (distance 16 => score 32)
        assertThat(game.score(), is(0));
        moveActiveTetrominoLeftBy(3);
        dropAndActivateNextTetromino();
        assertThat(game.score(), is(32));

        // I-shape to left and drop (distance 15 => score 30)
        moveActiveTetrominoLeftBy(3);
        dropAndActivateNextTetromino();
        assertThat(game.score(), is(62));

        // O-shape to left and drop (distance 13 => score 26)
        moveActiveTetrominoLeftBy(3);
        dropAndActivateNextTetromino();
        assertThat(game.score(), is(88));
    }

    @Test
    public void shouldScoreSingleCompletedRowAndDroppedShapes() throws Exception {
        // SCORE:  |          |
        //         |        oo|
        //         |oooooooooo| <-- deleted
        // score = (16 * 2) + (16 * 2) + (15 * 2) + 100 == 194
        completeLine();
        assertThat(game.score(), is(194));
    }

    @Test
    public void shouldScoreDoubleCompletedRow() throws Exception {
        // SCORE:  |          |
        //         |oooooooooo|
        //         |oooooooooo|
        // score = (16 + 16 + 15 + 15 + 15) * 2 + 200
        game = gameThatCreates(Shape.I, Shape.I, Shape.I, Shape.I, Shape.O);
        completeLines(2);
        assertThat(game.score(), is(354));
    }

    @Test
    public void shouldScoreQuadrupleCompletedRow() throws Exception {
        // SCORE:  |oooooooooo|
        //         |oooooooooo|
        //         |oooooooooo|
        //         |oooooooooo|
        // score = ((15 * 2) * 10)  + 800
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
        assertThat(game.score(), is(1100));
    }

    @Test
    public void shouldRotateIShapeLeft() throws Exception {
        // INITIAL SHAPE: oooo
        game = gameThatCreates(Shape.I);
        assertThat(game.blocks(), arePositionedAt(3,0, 4,0, 5,0, 6,0));

        game.rotateActiveTetrominoLeft();
        assertThat(game.blocks(), arePositionedAt(4,1, 4,0, 4,-1, 4,-2));
    }

    @Test
    public void shouldRotateIShapeRight() throws Exception {
        // INITIAL SHAPE: oooo
        game = gameThatCreates(Shape.I);
        game.rotateActiveTetrominoRight();
        assertThat(game.blocks(), arePositionedAt(4,-1, 4,0, 4,1, 4,2));
    }

    @Test
    public void shouldNotRotateIShapeRightWhenAtLeftEdge() throws Exception {
        // INITIAL SHAPE: oooo
        game = gameThatCreates(Shape.I);
        moveActiveTetrominoDownBy(1);
        game.rotateActiveTetrominoLeft();
        assertThat(game.blocks(), arePositionedAt(4,2, 4,1, 4,0, 4,-1));

        moveActiveTetrominoLeftBy(4);
        assertThat(game.rotateActiveTetrominoRight(), is(false));
        assertThat(game.blocks(), arePositionedAt(0,2, 0,1, 0,0, 0,-1));

        assertThat(game.rotateActiveTetrominoLeft(), is(false));
        assertThat(game.blocks(), arePositionedAt(0,2, 0,1, 0,0, 0,-1));

        moveActiveTetrominoRightBy(4);
        assertThat(game.rotateActiveTetrominoRight(), is(true));
        assertThat(game.blocks(), arePositionedAt(3,1, 4,1, 5,1, 6,1));
    }

    @Test
    public void shouldRotateOShapeLeftAndRight() throws Exception {
        // INITIAL SHAPE: oo
        //                oo
        game = gameThatCreates(Shape.O);
        assertThat(game.blocks(), arePositionedAt(3,0, 4,0, 3,1, 4,1));

        game.rotateActiveTetrominoRight();
        assertThat(game.blocks(), arePositionedAt(3,0, 4,0, 3,1, 4,1));

        game.rotateActiveTetrominoLeft();
        assertThat(game.blocks(), arePositionedAt(3,0, 4,0, 3,1, 4,1));
    }

    @Test
    public void shouldRotateLShapeLeft() throws Exception {
        // INITIAL SHAPE:   o
        //                ooo
        game = gameThatCreates(Shape.L);
        assertThat(game.blocks(), arePositionedAt(5,0, 3,1, 4,1, 5,1));

        game.rotateActiveTetrominoLeft();
        assertThat(game.blocks(), arePositionedAt(3,0, 4,2, 4,1, 4,0));

        game.rotateActiveTetrominoLeft();
        assertThat(game.blocks(), arePositionedAt(3,2, 5,1, 4,1, 3,1));

        game.rotateActiveTetrominoLeft();
        assertThat(game.blocks(), arePositionedAt(5,2, 4,0, 4,1, 4,2));

        game.rotateActiveTetrominoLeft();
        assertThat(game.blocks(), arePositionedAt(5,0, 3,1, 4,1, 5,1));
    }

    @Test
    public void shouldRotateLShapeRight() throws Exception {
        // INITIAL SHAPE:   o
        //                ooo
        game = gameThatCreates(Shape.L);
        game.rotateActiveTetrominoRight();
        assertThat(game.blocks(), arePositionedAt(5,2, 4,0, 4,1, 4,2));

        game.rotateActiveTetrominoRight();
        assertThat(game.blocks(), arePositionedAt(3,2, 5,1, 4,1, 3,1));

        game.rotateActiveTetrominoRight();
        assertThat(game.blocks(), arePositionedAt(3,0, 4,2, 4,1, 4,0));

        game.rotateActiveTetrominoRight();
        assertThat(game.blocks(), arePositionedAt(5,0, 3,1, 4,1, 5,1));
    }


    @Test
    public void shouldRotateJShapeLeft() throws Exception {
        // INITIAL SHAPE: o
        //                ooo
        game = gameThatCreates(Shape.J);
        assertThat(game.blocks(), arePositionedAt(3,0, 3,1, 4,1, 5,1));

        game.rotateActiveTetrominoLeft();
        assertThat(game.blocks(), arePositionedAt(3,2, 4,2, 4,1, 4,0));

        game.rotateActiveTetrominoLeft();
        assertThat(game.blocks(), arePositionedAt(5,2, 5,1, 4,1, 3,1));

        game.rotateActiveTetrominoLeft();
        assertThat(game.blocks(), arePositionedAt(5,0, 4,0, 4,1, 4,2));

        game.rotateActiveTetrominoLeft();
        assertThat(game.blocks(), arePositionedAt(3,0, 3,1, 4,1, 5,1));
    }

    @Test
    public void shouldRotateJShapeRight() throws Exception {
        // INITIAL SHAPE: o
        //                ooo
        game = gameThatCreates(Shape.J);
        game.rotateActiveTetrominoRight();
        assertThat(game.blocks(), arePositionedAt(5,0, 4,0, 4,1, 4,2));

        game.rotateActiveTetrominoRight();
        assertThat(game.blocks(), arePositionedAt(5,2, 5,1, 4,1, 3,1));

        game.rotateActiveTetrominoRight();
        assertThat(game.blocks(), arePositionedAt(3,2, 4,2, 4,1, 4,0));

        game.rotateActiveTetrominoRight();
        assertThat(game.blocks(), arePositionedAt(3,0, 3,1, 4,1, 5,1));
    }

    @Test
    public void shouldRotateSShapeLeft() throws Exception {
        // INITIAL SHAPE:  oo
        //                oo
        game = gameThatCreates(Shape.S);
        assertThat(game.blocks(), arePositionedAt(4,0, 5,0, 3,1, 4,1));

        game.rotateActiveTetrominoLeft();
        assertThat(game.blocks(), arePositionedAt(3,1, 3,0, 4,2, 4,1));
    }

    @Test
    public void shouldRotateSShapeRight() throws Exception {
        // INITIAL SHAPE:  oo
        //                oo
        game = gameThatCreates(Shape.S);
        game.rotateActiveTetrominoRight();
        assertThat(game.blocks(), arePositionedAt(5,1, 5,2, 4,0, 4,1));
    }


    @Test
    public void shouldRotateZShapeLeft() throws Exception {
        // INITIAL SHAPE: oo
        //                 oo
        game = gameThatCreates(Shape.Z);
        assertThat(game.blocks(), arePositionedAt(3,0, 4,0, 4,1, 5,1));

        game.rotateActiveTetrominoLeft();
        assertThat(game.blocks(), arePositionedAt(3,2, 3,1, 4,1, 4,0));
    }

    @Test
    public void shouldRotateZShapeRight() throws Exception {
        // INITIAL SHAPE: oo
        //                 oo
        game = gameThatCreates(Shape.Z);
        game.rotateActiveTetrominoRight();
        assertThat(game.blocks(), arePositionedAt(5,0, 5,1, 4,1, 4,2));
    }

    @Test
    public void shouldRotateTShapeLeft() throws Exception {
        // INITIAL SHAPE:  o
        //                ooo
        game = gameThatCreates(Shape.T);
        assertThat(game.blocks(), arePositionedAt(4,0, 3,1, 4,1, 5,1));

        game.rotateActiveTetrominoLeft();
        assertThat(game.blocks(), arePositionedAt(3,1, 4,2, 4,1, 4,0));

        game.rotateActiveTetrominoLeft();
        assertThat(game.blocks(), arePositionedAt(4,2, 5,1, 4,1, 3,1));

        game.rotateActiveTetrominoLeft();
        assertThat(game.blocks(), arePositionedAt(5,1, 4,0, 4,1, 4,2));
    }

    @Test
    public void shouldRotateTShapeRight() throws Exception {
        // INITIAL SHAPE:  o
        //                ooo
        game = gameThatCreates(Shape.T);
        game.rotateActiveTetrominoRight();
        assertThat(game.blocks(), arePositionedAt(5,1, 4,0, 4,1, 4,2));

        game.rotateActiveTetrominoRight();
        assertThat(game.blocks(), arePositionedAt(4,2, 5,1, 4,1, 3,1));

        game.rotateActiveTetrominoRight();
        assertThat(game.blocks(), arePositionedAt(3,1, 4,2, 4,1, 4,0));
    }

    @Test
    public void shouldHaveNextTetrominoAfterGameStarts() throws Exception {
        game = gameThatCreates(Shape.O, Shape.I, Shape.S);
        assertThat(game.blocks(), arePositionedAt(3,0, 4,0, 3,1, 4,1));

        assertThat(game.nextTetromino(), arePositionedAt(0,0, 1,0, 2,0, 3,0));
        game.activateNextTetromino();

        assertThat(game.nextTetromino(), arePositionedAt(1,0, 2,0, 0,1, 1,1));
    }

    @Test
    public void shouldLevelUpAfterTenRows() throws Exception {
        game = gameThatCreates(Shape.I, Shape.I, Shape.I, Shape.I, Shape.O);
        assertThat(game.level(), is(1));
        completeLines(8);
        assertThat(game.level(), is(1));

        completeLines(2);
        assertThat(game.level(), is(2));
        assertThat(game.lines(), is(10));
    }

    @Test
    public void shouldLevelUpEveryTenRows() throws Exception {
        game = gameThatCreates(Shape.I, Shape.I, Shape.I, Shape.I, Shape.O);
        for (int level = 1; level < 10; level++) {
            assertThat(game.level(), is(level));
            completeLines(10);
        }

        completeLines(10);
        assertThat(game.level(), is(10));
    }

    @Test
    public void shouldMoveBlocksDownWhenRowsCompleted() throws Exception {
        game = gameThatCreates(Shape.Z, Shape.I, Shape.I, Shape.I, Shape.I);
        // After first shape is dropped:
        //      |         o|
        //      |        oo|
        //      |        o |
        game.rotateActiveTetrominoRight();
        moveActiveTetrominoRightBy(5);
        dropAndActivateNextTetromino();

        //
        // followed by 4 Is:
        //      |         o|
        //      |oooooooooo|  <-- removed
        //      |ooooooooo |
        //
        //      |         o|
        //      |ooooooooo |
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
        //      |oooooooo  |
        //      |oooooooo  |
        //
        // followed by a single O:
        //      |oooooooooo|
        //      |oooooooooo|
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
        game.dropActiveTetrominoDown();
        game.activateNextTetromino();
    }

    private void completeLine() {
        // I-shape to left
        moveActiveTetrominoLeftBy(3);
        dropAndActivateNextTetromino();

        // I-shape to right of first shape
        moveActiveTetrominoRightBy(1);
        dropAndActivateNextTetromino();

        // O-shape to right of I-shapes
        moveActiveTetrominoRightBy(5);
        dropAndActivateNextTetromino();
    }

    /**
     * Creates games that will use only the supplied shapes
     * in the order given.
     */
    private static Game gameThatCreates(final Shape...shapes) {
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
        return game.blocks().stream().min((b1, b2) -> Integer.compare(b1.x(), b2.x())).get();
    }

    private Block rightMostBlock() {
        return game.blocks().stream().min((b1, b2) -> Integer.compare(b2.x(), b1.x())).get();
    }

    private static Matcher<ImmutableList<Block>> arePositionedAt(final int... points) {
        return new BaseMatcher<ImmutableList<Block>>() {

            @SuppressWarnings("unchecked") @Override
            public boolean matches(Object item) {
                ImmutableList<Block> blocks = (ImmutableList<Block>) item;
                for (int i = 0; i < points.length; i+=2) {
                    final int x = points[i];
                    final int y = points[i + 1];
                    if (blocks.stream().noneMatch(block -> (block.x() == x) && (block.y() == y))) {
                        return false;
                    }
                }
                return true;
            }

            @Override
            public void describeTo(Description description) {
                StringBuilder actual = new StringBuilder();
                for (int i = 0; i < points.length; i+=2) {
                    actual.append("[x=")
                          .append(points[i])
                          .append(", y=")
                          .append(points[i + 1])
                          .append("]");
                }
                description.appendText(actual.toString());
            }
        };
    }
}
