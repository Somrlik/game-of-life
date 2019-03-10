package cz.somrlik;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class GameOfLife {

    class Board {

        private int[] board;
        private long width;
        private long height;

        Board(long width, long height) {
            this.width = width;
            this.height = height;
            board = new int[(int) (width * height)];
        }

        long getWidth() {
            return width;
        }

        long getHeight() {
            return height;
        }

        boolean get(int x, int y) {
            if (x < 0 || x >= width || y < 0 || y >= height) return false;
            return board[(int) (y * width + x)] == 1;
        }

        private int getInt(int x, int y) {
            if (x < 0 || x >= width || y < 0 || y >= height) return 0;
            return board[(int) (y * width + x)];
        }

        boolean isAlive(int x, int y) {
            if (x < 0 || x >= width || y < 0 || y >= height) {
                return false;
            }

            int sum =
                    getInt(x - 1, y - 1) +
                    getInt(x    , y - 1) +
                    getInt(x + 1, y - 1) +
                    getInt(x - 1, y    ) +
                    getInt(x + 1, y    ) +
                    getInt(x - 1, y + 1) +
                    getInt(x    , y + 1) +
                    getInt(x + 1, y + 1) ;

            if (sum == 3) {
                return true;
            } else if (sum == 2) {
                return get(x, y);
            } else {
                return false;
            }
        }

        void setCellState(int x, int y, boolean alive) {
            if (x < 0 || x >= width || y < 0 || y >= height) {
                return;
            }
            board[(int) (y * width + x)] = alive ? 1 : 0;
        }

    }
    private Board board;

    private Set<Consumer<GameOfLife>> stepConsumers;

    private GameOfLife(long width, long height) {
        board = new Board(width, height);
        stepConsumers = new HashSet<>();
    }

    public void step() {
        Board tempBoard = new Board(board.width, board.height);

        for (int i = 0; i < board.width; ++i) {
            for (int j = 0; j < board.height; ++j) {
                tempBoard.setCellState(i, j, board.isAlive(i, j));
            }
        }

        board = tempBoard;

        for (Consumer<GameOfLife> consumer: stepConsumers) {
            consumer.accept(this);
        }
    }

    public void addOnStepFinishedListener(Consumer<GameOfLife> consumer) {
        stepConsumers.add(consumer);
    }

    public void randomize() {
        GameOfLife random = generateRandom(board.width, board.height);
        board = random.board;

        for (Consumer<GameOfLife> consumer: stepConsumers) {
            consumer.accept(this);
        }
    }

    public static GameOfLife generateRandom(long width, long height) {
        GameOfLife toReturn = new GameOfLife(width, height);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                toReturn.board.setCellState(i, j, Math.random() < 0.5);
            }
        }
        return toReturn;
    }

    static GameOfLife generateGlider(long width, long height) {
        GameOfLife toReturn = new GameOfLife(width, height);

        toReturn.board.setCellState(1, 2, true);
        toReturn.board.setCellState(2, 3, true);
        toReturn.board.setCellState(3, 1, true);
        toReturn.board.setCellState(3, 2, true);
        toReturn.board.setCellState(3, 3, true);

        return toReturn;
    }

    public void resize(long newWidth, long newHeight) {
        if (newWidth <= 0 || newHeight <= 0) {
            throw new RuntimeException("Failed to resize the board because negative height or width was specified!");
        }

        Board oldBoard = board, newBoard = new Board(newWidth, newHeight);

        for (int i = 0; i < newWidth; i++) {
            for (int j = 0; j < newHeight; j++) {
                newBoard.setCellState(i, j, oldBoard.get(i, j));
            }
        }

        board = newBoard;
    }

    Board getBoard() {
        return board;
    }

    public boolean getCellState(int x, int y) {
        return board.get(x, y);
    }

    public void setCellState(int x, int y, boolean alive) {
        board.setCellState(x, y, alive);

        for (Consumer<GameOfLife> consumer: stepConsumers) {
            consumer.accept(this);
        }
    }
}
