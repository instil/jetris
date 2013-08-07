package jetris.model;

import jetris.model.Block.Color;

import com.google.common.collect.ImmutableList;


final class Tetromino extends GameObject {

    enum Shape {
        /**
         * I SHAPE:
         * <pre>
         *  _ _ _ _
         * |_|_|_|_|
         * </pre>
         */
        I,

        /**
         * J SHAPE:
         * <pre>
         *  _
         * |_|_ _
         * |_|_|_|
         * </pre>
         */
        J,

        /**
         * L SHAPE:
         * <pre>
         *      _
         *  _ _|_|
         * |_|_|_|
         * </pre>
         */
        L,

        /**
         * O SHAPE:
         * <pre>
         *  _ _
         * |_|_|
         * |_|_|
         * </pre>
         */
        O,

        /**
         * S SHAPE:
         * <pre>
         *    _ _
         *  _|_|_|
         * |_|_|
         * </pre>
         */
        S,

        /**
         * T SHAPE:
         * <pre>
         *    _
         *  _|_|_
         * |_|_|_|
         * </pre>
         */
        T,

        /**
         * Z SHAPE:
         * <pre>
         *  _ _
         * |_|_|_
         *   |_|_|
         * </pre>
         */
        Z;
    }

    private static final int MAX_ROTATIONS = 4;

    private static final int[][][] ROTATION_DELTAS = {
        /* I Shape rotations */
        {{-1, 1}, { 0, 0}, { 1,-1}, { 2,-2}},
        {{ 1,-1}, { 0, 0}, {-1, 1}, {-2, 2}},
        {{-1, 1}, { 0, 0}, { 1,-1}, { 2,-2}},
        {{ 1,-1}, { 0, 0}, {-1, 1}, {-2, 2}},

        /* J Shape rotations */
        {{ 0,-2}, {-1,-1}, { 0, 0}, { 1, 1}},
        {{ 2, 0}, { 1,-1}, { 0, 0}, {-1, 1}},
        {{ 0, 2}, { 1, 1}, { 0, 0}, {-1,-1}},
        {{-2, 0}, {-1, 1}, { 0, 0}, { 1,-1}},

        /* L Shape rotations */
        {{ 2, 0}, {-1,-1}, { 0, 0}, { 1, 1}},
        {{ 0, 2}, { 1,-1}, { 0, 0}, {-1, 1}},
        {{-2, 0}, { 1, 1}, { 0, 0}, {-1,-1}},
        {{ 0,-2}, {-1, 1}, { 0, 0}, { 1,-1}},

        /* O Shape rotations */
        {{ 0, 0}, { 0, 0}, { 0, 0}, { 0, 0}},
        {{ 0, 0}, { 0, 0}, { 0, 0}, { 0, 0}},
        {{ 0, 0}, { 0, 0}, { 0, 0}, { 0, 0}},
        {{ 0, 0}, { 0, 0}, { 0, 0}, { 0, 0}},

        /* S Shape rotations */
        {{ 0, 0}, { 0,-1}, {-2,-1}, { 0, 0}},
        {{ 0, 0}, { 0, 1}, { 2, 1}, { 0, 0}},
        {{ 0, 0}, { 0,-1}, {-2,-1}, { 0, 0}},
        {{ 0, 0}, { 0, 1}, { 2, 1}, { 0, 0}},

        /* T Shape rotations */
        {{ 1,-1}, {-1,-1}, { 0, 0}, { 1, 1}},
        {{ 1, 1}, { 1,-1}, { 0, 0}, {-1, 1}},
        {{-1, 1}, { 1, 1}, { 0, 0}, {-1,-1}},
        {{-1,-1}, {-1, 1}, { 0, 0}, { 1,-1}},

        /* Z Shape rotations */
        {{ 0,-1}, { 0, 0}, { 0, 0}, { 2,-1}},
        {{ 0, 1}, { 0, 0}, { 0, 0}, {-2, 1}},
        {{ 0,-1}, { 0, 0}, { 0, 0}, { 2,-1}},
        {{ 0, 1}, { 0, 0}, { 0, 0}, {-2, 1}}
    };

    private ImmutableList<Block> blocks;
    private final Shape shape;
    private int rotationIndex;

    public Tetromino(Shape shape) {
        this.shape = shape;
        this.blocks = blocksFor(shape);
    }

    ImmutableList<Block> blocks() {
        return blocks;
    }

    void moveLeft() {
        ImmutableList.Builder<Block> builder = ImmutableList.builder();
        for (Block block : blocks) {
            builder.add(block.moveLeft());
        }
        blocks = builder.build();
    }

    void moveRight() {
        ImmutableList.Builder<Block> builder = ImmutableList.builder();
        for (Block block : blocks) {
            builder.add(block.moveRight());
        }
        blocks = builder.build();
    }

    void moveDown() {
        ImmutableList.Builder<Block> builder = ImmutableList.builder();
        for (Block block : blocks) {
            builder.add(block.moveDown());
        }
        blocks = builder.build();
    }

    void moveUp() {
        ImmutableList.Builder<Block> builder = ImmutableList.builder();
        for (Block block : blocks) {
            builder.add(block.moveUp());
        }
        blocks = builder.build();
    }

    void rotateLeft() {
        rotateRight();
        rotateRight();
        rotateRight();
    }

    void rotateRight() {
        if (++rotationIndex == MAX_ROTATIONS) {
            rotationIndex = 0;
        }
        rotate();
    }

    private void rotate() {
        ImmutableList.Builder<Block> builder = ImmutableList.builder();
        int index = (shape.ordinal() * MAX_ROTATIONS) + rotationIndex;
        for (int i = 0; i < ROTATION_DELTAS[index].length; i++) {
            int deltaX = ROTATION_DELTAS[index][i][0];
            int deltaY = ROTATION_DELTAS[index][i][1];
            builder.add(blocks.get(i).rotateBy(deltaX, deltaY));
        }
        blocks = builder.build();
    }

    private ImmutableList<Block> blocksFor(Shape shape) {
        switch (shape) {
          case I: return blocksFor(shape, 0,0, 1,0, 2,0, 3,0);
          case J: return blocksFor(shape, 0,0, 0,1, 1,1, 2,1);
          case L: return blocksFor(shape, 2,0, 0,1, 1,1, 2,1);
          case O: return blocksFor(shape, 0,0, 1,0, 0,1, 1,1);
          case S: return blocksFor(shape, 1,0, 2,0, 0,1, 1,1);
          case T: return blocksFor(shape, 1,0, 0,1, 1,1, 2,1);
          default:
              assert shape == Shape.Z;
              return blocksFor(shape, 0,0, 1,0, 1,1, 2,1);
        }
    }

    private ImmutableList<Block> blocksFor(Shape shape,
                                           int x1, int y1,
                                           int x2, int y2,
                                           int x3, int y3,
                                           int x4, int y4) {
        Color color = colorFor(shape);
        return ImmutableList.of(new Block(color, x1, y1),
                                new Block(color, x2, y2),
                                new Block(color, x3, y3),
                                new Block(color, x4, y4));
    }

    private Color colorFor(Shape shape) {
        return Color.values()[shape.ordinal()];
    }
}
