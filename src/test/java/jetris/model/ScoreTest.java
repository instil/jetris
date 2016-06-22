package jetris.model;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ScoreTest {

    @Test
    public void shouldScoreSingleLineClear() throws Exception {
        assertThat(Score.completed(1, 1), is(100));
    }

    @Test
    public void shouldScoreDoubleLineClear() throws Exception {
        assertThat(Score.completed(2, 1), is(200));
    }

    @Test
    public void shouldScoreTripleLineClear() throws Exception {
        assertThat(Score.completed(3, 1), is(400));
    }

    @Test
    public void shouldScoreQuadrupleLineClear() throws Exception {
        assertThat(Score.completed(4, 1), is(800));
    }

    @Test
    public void shouldScoreHardDrop() throws Exception {
        assertThat(Score.hardDrop(14, 1), is(28));
        assertThat(Score.hardDrop(15, 3), is(90));
    }
}
