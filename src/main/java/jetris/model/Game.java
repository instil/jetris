package jetris.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;

/**
 * Represents an instance of a single game
 */
class Game extends GameObject {

    private static final int BOARD_WIDTH = 10;

    private static final int BOARD_HEIGHT = 17;

    private final TetrominoFactory tetrominoFactory;

    private final List<Block> fixedBlocks = new ArrayList<>();

    private Tetromino nextTetromino;

    private Tetromino activeTetromino;

    private int score;

    private int lines;

    private int level = 1;

    Game(TetrominoFactory tetrominoFactory) {
        this.tetrominoFactory = tetrominoFactory;
        this.nextTetromino = tetrominoFactory.create();
        activateNextTetromino();
    }

    ImmutableList<Block> blocks() {
        return new ImmutableList.Builder<Block>()
                    .addAll(fixedBlocks)
                    .addAll(activeTetromino.blocks())
                    .build();
    }

    ImmutableList<Block> nextTetromino() {
        return nextTetromino.blocks();
    }

    int score() {
        return score;
    }

    int lines() {
        return lines;
    }

    int level() {
        return level;
    }

    boolean moveActiveTetrominoLeft() {
        Tetromino provisional = activeTetromino.moveLeft();
        if (isValidTetrominoPosition(provisional)) {
            activeTetromino = provisional;
            return true;
        }
        return false;
    }

    boolean moveActiveTetrominoRight() {
        Tetromino provisional = activeTetromino.moveRight();
        if (isValidTetrominoPosition(provisional)) {
            activeTetromino = provisional;
            return true;
        }
        return false;
    }

    boolean moveActiveTetrominoDown() {
        Tetromino provisional = activeTetromino.moveDown();
        if (isValidTetrominoPosition(provisional)) {
            activeTetromino = provisional;
            return true;
        }
        score += Score.softDrop(level);
        return false;
    }

    boolean dropActiveTetrominoDown() {
        int droppedDistance = 0;
        Tetromino provisional = activeTetromino.moveDown();
        while (isValidTetrominoPosition(provisional)) {
            activeTetromino = provisional;
            provisional = provisional.moveDown();
            droppedDistance++;
        }
        score += Score.hardDrop(droppedDistance, level);
        return droppedDistance > 0;
    }

    boolean rotateActiveTetrominoLeft() {
        Tetromino provisional = activeTetromino.rotateLeft();
        if (isValidTetrominoPosition(provisional)) {
            activeTetromino = provisional;
            return true;
        }
        return false;
    }

    boolean rotateActiveTetrominoRight() {
        Tetromino provisional = activeTetromino.rotateRight();
        if (isValidTetrominoPosition(provisional)) {
            activeTetromino = provisional;
            return true;
        }
        return false;
    }

    boolean activateNextTetromino() {
        fixActiveTetrominoBlocksToBoard();
        removeAndScoreCompletedLines();
        maybeUpdateLevel();
        activeTetromino = nextTetromino;
        nextTetromino = tetrominoFactory.create();
        return positionNewTetrominoOnBoard();
    }

    private void fixActiveTetrominoBlocksToBoard() {
        if (activeTetromino != null) {
            fixedBlocks.addAll(activeTetromino.blocks());
        }
    }

    private void removeAndScoreCompletedLines() {
        int linesCleared = 0;
        for (int y = 0; y < BOARD_HEIGHT; y++) {
            if (isLineCompleted(y)) {
                removeLine(y);
                linesCleared++;
            }
        }
        lines += linesCleared;
        score += Score.completed(linesCleared, level);
    }

    private boolean isLineCompleted(int y) {
        Set<Integer> used = new HashSet<>();
        for (Block block : fixedBlocks) {
            if (block.y() == y) {
                used.add(block.x());
            }
        }
        return used.size() == BOARD_WIDTH;
    }

    private void removeLine(int y) {
        fixedBlocks.removeIf(block -> block.y() == y);
        moveFixedBlocksDownTo(y);
    }

    private void moveFixedBlocksDownTo(int y) {
        for (Block block : new ArrayList<>(fixedBlocks)) {
            if (block.y() < y) {
                fixedBlocks.remove(block);
                block = block.moveDown();
                fixedBlocks.add(block);
            }
        }
    }

    private void maybeUpdateLevel() {
        if (level < 10) {
            level = (lines / 10) + 1;
        }
    }

    private boolean isValidTetrominoPosition(Tetromino tetromino) {
        return tetromino.blocks().stream()
                        .allMatch(b -> isValidPosition(b));
    }

    private boolean isValidPosition(Block block) {
        if (fixedBlocks.stream().anyMatch(b -> (b.x() == block.x()) && (b.y() == block.y()))) {
            return false;
        }
        // shapes are allowed to rotate and have y that is less than 0
        return (block.x() >= 0) && (block.x() < BOARD_WIDTH) && (block.y() < BOARD_HEIGHT);
    }

    private boolean positionNewTetrominoOnBoard() {
        for (int i = 0; i < 3; i++) {
            Tetromino provisional = activeTetromino.moveRight();
            if (!isValidTetrominoPosition(provisional)) {
                return false;
            }
            activeTetromino = provisional;
        }
        return true;
    }
}
