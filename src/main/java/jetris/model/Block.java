package jetris.model;

/**
 * A block value object - an immutable representation of
 * a block
 */
public class Block extends GameObject {

    private final int x;

    private final int y;

    private final Color color;

    public enum Color {
        CYAN,
        BLUE,
        ORANGE,
        YELLOW,
        GREEN,
        MAGENTA,
        RED
    }

    Block(Color color, int x, int y) {
        this.color = color;
        this.x = x;
        this.y = y;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public Color color() {
        return color;
    }

    Block moveLeft() {
        return new Block(color, (x - 1), y);
    }

    Block moveRight() {
        return new Block(color, (x + 1), y);
    }

    Block moveDown() {
        return new Block(color, x, (y + 1));
    }
}
