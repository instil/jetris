package jetris.model;

import org.apache.commons.lang.math.RandomUtils;

import jetris.model.Tetromino.Shape;


class RandomTetrominoFactory implements TetrominoFactory {

    @Override
    public Tetromino create() {
        Shape shape = Shape.values()[RandomUtils.nextInt(Shape.values().length)];
        return new Tetromino(shape);
    }
}
