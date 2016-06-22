package jetris.model;

import static jetris.model.Block.Color.BLUE;
import static jetris.model.Block.Color.CYAN;
import static jetris.model.Block.Color.GREEN;
import static jetris.model.Block.Color.MAGENTA;
import static jetris.model.Block.Color.ORANGE;
import static jetris.model.Block.Color.RED;
import static jetris.model.Block.Color.YELLOW;

import com.google.common.collect.ImmutableList;

import jetris.model.Block.Color;

/**
 * Represents a tetromino
 */
final class Tetromino extends GameObject {

    /**
     * Tetromino shapes, each known by its letter name.
     * <pre>
     *        _ _ _ _
     * I :   |_|_|_|_|
     *        _
     * J :   |_|_ _
     *       |_|_|_|
     * L :        _
     *        _ _|_|
     *       |_|_|_|
     * O :    _ _
     *       |_|_|
     *       |_|_|
     * S :    _ _
     *      _|_|_|
     *     |_|_|
     * T :    _
     *      _|_|_
     *     |_|_|_|
     * Z :  _ _
     *     |_|_|_
     *       |_|_|
     * </pre>
     */
    enum Shape {
        I,
        J,
        L,
        O,
        S,
        T,
        Z;
    }

    private final ImmutableList<Block> blocks;
    private final Shape shape;

    Tetromino(Shape shape) {
        this.shape = shape;
        this.blocks = blocksFor(shape);
    }

    ImmutableList<Block> blocks() {
        return blocks;
    }

    Tetromino moveLeft() {
        ImmutableList.Builder<Block> builder = ImmutableList.builder();
        for (Block block : blocks) {
            builder.add(block.moveLeft());
        }
        return new Tetromino(shape, builder.build());
    }

    Tetromino moveRight() {
        ImmutableList.Builder<Block> builder = ImmutableList.builder();
        for (Block block : blocks) {
            builder.add(block.moveRight());
        }
        return new Tetromino(shape, builder.build());
    }

    Tetromino moveDown() {
        ImmutableList.Builder<Block> builder = ImmutableList.builder();
        for (Block block : blocks) {
            builder.add(block.moveDown());
        }
        return new Tetromino(shape, builder.build());
    }

    Tetromino rotateLeft() {
        return rotateRight().rotateRight().rotateRight();
    }

    Tetromino rotateRight() {
        if (shape == Shape.O) {
            return this;
        }
        return rotate90Degrees();
    }

    private Tetromino rotate90Degrees() {
        /*
         * As y axis points down not up, standard transformation
         * matrices need to be inverted. Therefore we apply 270 
         * transform matrix to affect 90 degree turn around 
         * first block.
         */ 
        int centreX = blocks.get(0).x();
        int centreY = blocks.get(0).y();
        ImmutableList.Builder<Block> builder = ImmutableList.builder();
        for (Block block : blocks) {
            int x = ((centreY - block.y()) + centreX);
            int y = (-1 * (centreX - block.x())) + centreY;
            builder.add(new Block(block.color(), x, y));
        }
        return new Tetromino(shape, builder.build());
    }

    private Tetromino(Shape shape,
                      ImmutableList<Block> blocks) {
        this.shape = shape;
        this.blocks = blocks;
    }

    private static ImmutableList<Block> blocksFor(Shape shape) {
        switch (shape) {
          case I: return blocksFor(CYAN, 1,0, 0,0, 2,0, 3,0);
          case J: return blocksFor(BLUE, 1,1, 0,0, 0,1, 2,1);
          case L: return blocksFor(ORANGE, 1,1, 2,0, 0,1, 2,1);
          case O: return blocksFor(YELLOW, 0,0, 1,0, 0,1, 1,1);
          case S: return blocksFor(GREEN, 1,1, 1,0, 2,0, 0,1);
          case T: return blocksFor(MAGENTA, 1,1, 1,0, 0,1, 2,1);
          default:
              assert shape == Shape.Z;
              return blocksFor(RED, 1,1, 0,0, 1,0, 2,1);
        }
    }

    /*
     * Returns list of colored blocks, centered around the first
     * set of x,y coordinates
     */
    private static ImmutableList<Block> blocksFor(Color color,
                                                  int x1, int y1,
                                                  int x2, int y2,
                                                  int x3, int y3,
                                                  int x4, int y4) {
        return ImmutableList.of(new Block(color, x1, y1),
                                new Block(color, x2, y2),
                                new Block(color, x3, y3),
                                new Block(color, x4, y4));
    }
}
