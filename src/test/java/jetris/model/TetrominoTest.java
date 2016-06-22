package jetris.model;

import static jetris.model.Block.Color.BLUE;
import static jetris.model.Block.Color.CYAN;
import static jetris.model.Block.Color.GREEN;
import static jetris.model.Block.Color.MAGENTA;
import static jetris.model.Block.Color.RED;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import jetris.model.Block.Color;

public class TetrominoTest {

    @Test
    public void tetrominosShouldHaveCorrectColor() throws Exception {
        assertThat(new Tetromino(Tetromino.Shape.I), hasColor(CYAN));
        assertThat(new Tetromino(Tetromino.Shape.Z), hasColor(RED));
        assertThat(new Tetromino(Tetromino.Shape.S), hasColor(GREEN));
        assertThat(new Tetromino(Tetromino.Shape.T), hasColor(MAGENTA));
    }

    @Test
    public void rotatingTetrominoShouldBeIdempotent() throws Exception {
        Tetromino tetromino = new Tetromino(Tetromino.Shape.I);
        assertThat(tetromino.rotateRight(), is(tetromino.rotateRight()));
        assertThat(tetromino.rotateLeft(), is(tetromino.rotateLeft()));
        assertThat(tetromino.moveLeft(), is(tetromino.moveLeft()));
        assertThat(tetromino.moveRight(), is(tetromino.moveRight()));
        assertThat(tetromino.moveDown(), is(tetromino.moveDown()));
    }

    @Test
    public void shouldPositionIShapedTetromino() throws Exception {
        Tetromino tetromino = new Tetromino(Tetromino.Shape.I);
        assertThat(tetromino.blocks(), sameAs(CYAN, 0,0, 1,0, 2,0, 3,0));
    }

    @Test
    public void shouldRotateIClockwiseToRight() throws Exception {
        Tetromino tetromino = new Tetromino(Tetromino.Shape.I).rotateRight();
        assertThat(tetromino.blocks(), sameAs(CYAN, 1,0, 1,-1, 1,1, 1,2));

        tetromino = tetromino.rotateRight();
        assertThat(tetromino.blocks(), sameAs(CYAN, 2,0, 1,0, 0,0, -1,0));

        tetromino = tetromino.rotateRight();
        assertThat(tetromino.blocks(), sameAs(CYAN, 1,1, 1,0, 1,-1, 1,-2));

        tetromino = tetromino.rotateRight();
        assertThat(tetromino.blocks(), sameAs(CYAN, 0,0, 1,0, 2,0, 3,0));
    }

    @Test
    public void shouldRotateIAntiClockwiseToLeft() throws Exception {
        Tetromino tetromino = new Tetromino(Tetromino.Shape.I).rotateLeft();
        assertThat(tetromino.blocks(), sameAs(CYAN, 1,1, 1,0, 1,-1, 1,-2));

        tetromino = tetromino.rotateLeft();
        assertThat(tetromino.blocks(), sameAs(CYAN, 2,0, 1,0, 0,0, -1,0));

        tetromino = tetromino.rotateLeft();
        assertThat(tetromino.blocks(), sameAs(CYAN, 1,-1, 1,0, 1,1, 1,2));

        tetromino = tetromino.rotateLeft();
        assertThat(tetromino.blocks(), sameAs(CYAN, 0,0, 1,0, 2,0, 3,0));
    }

    @Test
    public void shouldRotateSClockwiseToRight() throws Exception {
        Tetromino tetromino = new Tetromino(Tetromino.Shape.S).rotateRight();
        assertThat(tetromino.blocks(), sameAs(GREEN, 2,1, 2,2, 1,0, 1,1));

        tetromino = tetromino.rotateRight();
        assertThat(tetromino.blocks(), sameAs(GREEN, 1,2, 0,2, 2,1, 1,1));

        tetromino = tetromino.rotateRight();
        assertThat(tetromino.blocks(), sameAs(GREEN, 0,1, 0,0, 1,2, 1,1));

        tetromino = tetromino.rotateRight();
        assertThat(tetromino.blocks(), sameAs(GREEN, 1,0, 2,0, 0,1, 1,1));
    }

    @Test
    public void shouldRotateSAntiClockwiseToLeft() throws Exception {
        Tetromino tetromino = new Tetromino(Tetromino.Shape.S).rotateLeft();
        assertThat(tetromino.blocks(), sameAs(GREEN, 0,1, 0,0, 1,2, 1,1));

        tetromino = tetromino.rotateLeft();
        assertThat(tetromino.blocks(), sameAs(GREEN, 1,2, 0,2, 2,1, 1,1));

        tetromino = tetromino.rotateLeft();
        assertThat(tetromino.blocks(), sameAs(GREEN, 2,1, 2,2, 1,0, 1,1));

        tetromino = tetromino.rotateLeft();
        assertThat(tetromino.blocks(), sameAs(GREEN, 1,0, 2,0, 0,1, 1,1));
    }

    @Test
    public void shouldRotateZClockwiseToRight() throws Exception {
        Tetromino tetromino = new Tetromino(Tetromino.Shape.Z).rotateRight();
        assertThat(tetromino.blocks(), sameAs(RED, 2,0, 2,1, 1,1, 1,2));

        tetromino = tetromino.rotateRight();
        assertThat(tetromino.blocks(), sameAs(RED, 2,2, 1,2, 1,1, 0,1));

        tetromino = tetromino.rotateRight();
        assertThat(tetromino.blocks(), sameAs(RED, 0,2, 0,1, 1,1, 1,0));

        tetromino = tetromino.rotateRight();
        assertThat(tetromino.blocks(), sameAs(RED, 0,0, 1,0, 1,1, 2,1));
    }

    @Test
    public void shouldRotateZAntiClockwiseToRight() throws Exception {
        Tetromino tetromino = new Tetromino(Tetromino.Shape.Z).rotateLeft();
        assertThat(tetromino.blocks(), sameAs(RED, 0,2, 0,1, 1,1, 1,0));

        tetromino = tetromino.rotateLeft();
        assertThat(tetromino.blocks(), sameAs(RED, 2,2, 1,2, 1,1, 0,1));

        tetromino = tetromino.rotateLeft();
        assertThat(tetromino.blocks(), sameAs(RED, 2,0, 2,1, 1,1, 1,2));

        tetromino = tetromino.rotateLeft();
        assertThat(tetromino.blocks(), sameAs(RED, 0,0, 1,0, 1,1, 2,1));

    }

    @Test
    public void shouldRotateTClockwiseToRight() throws Exception {
        Tetromino tetromino = new Tetromino(Tetromino.Shape.T);
        assertThat(tetromino.blocks(), sameAs(MAGENTA, 1,0, 0,1, 1,1, 2,1));

        tetromino = tetromino.rotateRight();
        assertThat(tetromino.blocks(), sameAs(MAGENTA, 2,1, 1,0, 1,1, 1,2));

        tetromino = tetromino.rotateRight();
        assertThat(tetromino.blocks(), sameAs(MAGENTA, 1,2, 2,1, 1,1, 0,1));

        tetromino = tetromino.rotateRight();
        assertThat(tetromino.blocks(), sameAs(MAGENTA, 0,1, 1,2, 1,1, 1,0));

        tetromino = tetromino.rotateRight();
        assertThat(tetromino.blocks(), sameAs(MAGENTA, 1,0, 0,1, 1,1, 2,1));

    }

    @Test
    public void shouldRotateJClockwiseToRight() throws Exception {
        Tetromino tetromino = new Tetromino(Tetromino.Shape.J).rotateRight();
        assertThat(tetromino.blocks(), sameAs(BLUE, 2,0, 1,0, 1,1, 1,2));

        tetromino = tetromino.rotateRight();
        assertThat(tetromino.blocks(), sameAs(BLUE, 2,2, 2,1, 1,1, 0,1));

        tetromino = tetromino.rotateRight();
        assertThat(tetromino.blocks(), sameAs(BLUE, 0,2, 1,2, 1,1, 1,0));

        tetromino = tetromino.rotateRight();
        assertThat(tetromino.blocks(), sameAs(BLUE, 0,0, 0,1, 1,1, 2,1));

    }

    @Test
    public void shouldRotateJAntiClockwiseToRight() throws Exception {
        Tetromino tetromino = new Tetromino(Tetromino.Shape.J).rotateLeft();
        assertThat(tetromino.blocks(), sameAs(BLUE, 0,2, 1,2, 1,1, 1,0));

        tetromino = tetromino.rotateLeft();
        assertThat(tetromino.blocks(), sameAs(BLUE, 2,2, 2,1, 1,1, 0,1));

        tetromino = tetromino.rotateLeft();
        assertThat(tetromino.blocks(), sameAs(BLUE, 2,0, 1,0, 1,1, 1,2));

        tetromino = tetromino.rotateLeft();
        assertThat(tetromino.blocks(), sameAs(BLUE, 0,0, 0,1, 1,1, 2,1));

    }

    private static Matcher<Tetromino> hasColor(Color color) {
        return new BaseMatcher<Tetromino>() {

            @Override
            public boolean matches(Object item) {
                ImmutableList<Block> blocks = ((Tetromino) item).blocks();
                return blocks.stream().allMatch(b -> b.color() == color);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Tetromino is not " + color);
            }
        };
    }
    private static Matcher<Iterable<? extends Block>> sameAs(Color color,
                                                            int x1, int y1,
                                                            int x2, int y2,
                                                            int x3, int y3,
                                                            int x4, int y4) {
        return containsInAnyOrder(new Block(color, x1, y1),
                                  new Block(color, x2, y2),
                                  new Block(color, x3, y3),
                                  new Block(color, x4, y4));
    }


}
