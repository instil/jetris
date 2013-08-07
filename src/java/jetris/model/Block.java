package jetris.model;

public class Block extends GameObject {

    private int x;

    private int y;

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

    public Block(Color color, int x, int y) {
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
        return new Block(color, --x, y);
    }

    Block moveRight() {
        return new Block(color, ++x, y);
    }

    Block moveUp() {
        return new Block(color, x, --y);
    }

    Block moveDown() {
        return new Block(color, x, ++y);
    }

    Block rotateBy(int deltaX, int deltaY) {
        return new Block(color, (x + deltaX), (y + deltaY));
    }
}
