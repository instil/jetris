package jetris.model;

class Score {

    static int softDrop(int level) {
        return level;
    }

    static int hardDrop(int droppedDistance, int level) {
        return 2 * droppedDistance * level;
    }

    static int completed(int linesCleared, int level) {
        // 1 row = 100, 2 rows = 200, 3 rows = 400, 4 rows = 800
        // multiplied by current level
        return ((1 << (linesCleared - 1)) * 100) * level;
    }
}
