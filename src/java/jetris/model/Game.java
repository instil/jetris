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

    private final List<Block> fixedBlocks = new ArrayList<Block>();

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
        activeTetromino.moveLeft();
        if (!isActiveTetrominoPositionValid()) {
            activeTetromino.moveRight();
            return false;
        }
        return true;
    }

    boolean moveActiveTetrominoRight() {
        activeTetromino.moveRight();
        if (!isActiveTetrominoPositionValid()) {
            activeTetromino.moveLeft();
            return false;
        }
        return true;
    }

    boolean moveActiveTetrominoDown() {
        activeTetromino.moveDown();
        if (!isActiveTetrominoPositionValid()) {
            activeTetromino.moveUp();
            return false;
        }
        return true;
    }

    boolean dropActiveTetrominoDown() {
        int droppedRows = 0;
        while (isActiveTetrominoPositionValid()) {
            activeTetromino.moveDown();
            droppedRows++;
        }
        assert droppedRows > 0;
        activeTetromino.moveUp();
        return droppedRows > 1;
    }

    boolean rotateActiveTetrominoLeft() {
        activeTetromino.rotateLeft();
        if (!isActiveTetrominoPositionValid()) {
            activeTetromino.rotateRight();
            return false;
        }
        return true;
    }

    boolean rotateActiveTetrominoRight() {
        activeTetromino.rotateRight();
        if (!isActiveTetrominoPositionValid()) {
            activeTetromino.rotateLeft();
            return false;
        }
        return true;
    }

    boolean activateNextTetromino() {
        fixActiveTetrominoBlocksToBoard();
        scoreAndRemoveCompletedLines();
        activeTetromino = nextTetromino;
        nextTetromino = tetrominoFactory.create();
        return positionNewTetrominoOnBoard();
    }

    private void fixActiveTetrominoBlocksToBoard() {
        if (activeTetromino != null) {
            score += scoreActiveTetromino();
            fixedBlocks.addAll(activeTetromino.blocks());
        }
    }

    private void scoreAndRemoveCompletedLines() {
        int completedLines = 0;
        for (int y = 0; y < BOARD_HEIGHT; y++) {
            if (isLineCompleted(y)) {
                removeLine(y);
                completedLines++;
            }
        }
        lines += completedLines;
        score += scoreFor(completedLines);
        updateLevel();
    }

    private boolean isLineCompleted(int y) {
        Set<Integer> used = new HashSet<Integer>();
        for (Block block : fixedBlocks) {
            if (block.y() == y) {
                used.add(block.x());
            }
        }
        return used.size() == BOARD_WIDTH;
    }

    private void removeLine(int y) {
        removeBlocksAtLine(y);
        moveBlocksDownTo(y);
    }

    private void removeBlocksAtLine(int y) {
        for (Block block : new ArrayList<Block>(fixedBlocks)) {
            if (block.y() == y) {
                fixedBlocks.remove(block);
            }
        }
    }

    private void moveBlocksDownTo(int y) {
        for (Block block : new ArrayList<Block>(fixedBlocks)) {
            fixedBlocks.remove(block);
            if (block.y() < y) {
                block = block.moveDown();
            }
            fixedBlocks.add(block);
        }
    }

    private int scoreFor(int completedLines) {
        // 1 row = 100, 2 rows = 200, 3 rows = 400, 4 rows = 800:
        return (1 << (completedLines - 1)) * 100;
    }

    private void updateLevel() {
        if (level < 10) {
            level = lines / 10;
        }
    }

    private int scoreActiveTetromino() {
        if (activeTetromino == null) {
            return 0;
        }
        int rowIndex = BOARD_HEIGHT;
        for (Block block : activeTetromino.blocks()) {
            if (block.y() < rowIndex) {
                rowIndex = block.y();
            }
        }
        return rowIndex + 1;
    }

    private boolean isActiveTetrominoPositionValid() {
        for (Block block : activeTetromino.blocks()) {
            if (!isValidPosition(block.x(), block.y())) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidPosition(int x, int y) {
        for (Block fixedBlock : fixedBlocks) {
            if ((fixedBlock.x() == x) && (fixedBlock.y() == y)) {
                return false;
            }
        }
        // shapes are allowed to rotate and have y that is less than 0
        return (x >= 0) && (x < BOARD_WIDTH) && (y < BOARD_HEIGHT);
    }

    private boolean positionNewTetrominoOnBoard() {
        for (int i = 0; i < 3; i++) {
            activeTetromino.moveRight();
            if (!isActiveTetrominoPositionValid()) {
                return false;
            }
        }
        return true;
    }
}
