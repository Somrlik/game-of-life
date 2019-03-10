package cz.somrlik;

import org.junit.Test;

import static org.junit.Assert.*;

public class GameOfLifeTest {

    @Test
    public void step() {
        GameOfLife gof = GameOfLife.generateGlider(10, 10);

        assertSame(false, gof.getCellState(1, 1));
        assertSame(true,  gof.getCellState(1, 2));
        assertSame(false, gof.getCellState(1, 3));
        assertSame(false, gof.getCellState(2, 1));
        assertSame(false, gof.getCellState(2, 2));
        assertSame(true,  gof.getCellState(2, 3));
        assertSame(true,  gof.getCellState(3, 1));
        assertSame(true,  gof.getCellState(3, 2));
        assertSame(true,  gof.getCellState(3, 3));

        gof.step();

        assertSame(false, gof.getCellState(1, 1));
        assertSame(false, gof.getCellState(1, 2));
        assertSame(false, gof.getCellState(1, 3));
        assertSame(true,  gof.getCellState(2, 1));
        assertSame(false, gof.getCellState(2, 2));
        assertSame(true,  gof.getCellState(2, 3));
        assertSame(false, gof.getCellState(3, 1));
        assertSame(true,  gof.getCellState(3, 2));
        assertSame(true,  gof.getCellState(3, 3));
        assertSame(false, gof.getCellState(4, 1));
        assertSame(true,  gof.getCellState(4, 2));
        assertSame(false, gof.getCellState(4, 3));
        assertSame(false, gof.getCellState(5, 1));
        assertSame(false, gof.getCellState(5, 2));
        assertSame(false, gof.getCellState(5, 3));

        gof.step();

        assertSame(false, gof.getCellState(1, 1));
        assertSame(false, gof.getCellState(1, 2));
        assertSame(false, gof.getCellState(1, 3));
        assertSame(false, gof.getCellState(2, 1));
        assertSame(false, gof.getCellState(2, 2));
        assertSame(true,  gof.getCellState(2, 3));
        assertSame(true,  gof.getCellState(3, 1));
        assertSame(false, gof.getCellState(3, 2));
        assertSame(true,  gof.getCellState(3, 3));
        assertSame(false, gof.getCellState(4, 1));
        assertSame(true,  gof.getCellState(4, 2));
        assertSame(true,  gof.getCellState(4, 3));
        assertSame(false, gof.getCellState(5, 1));
        assertSame(false, gof.getCellState(5, 2));
        assertSame(false, gof.getCellState(5, 3));
    }

    @Test
    public void addOnStepFinishedListener() {
        GameOfLife gameOfLife = GameOfLife.generateRandom(10, 10);

        final int[] called = {0};
        gameOfLife.addOnStepFinishedListener(gameOfLife1 -> {
            called[0]++;
            assertSame(gameOfLife, gameOfLife1);
        });

        gameOfLife.step();
        assertSame(1, called[0]);
        gameOfLife.step();
        assertSame(2, called[0]);
    }

    @Test
    public void getCellState() {
        GameOfLife gof = GameOfLife.generateGlider(10, 10);

        assertSame(false, gof.getCellState(1, 1));
        assertSame(true,  gof.getCellState(1, 2));
        assertSame(false, gof.getCellState(1, 3));
        assertSame(false, gof.getCellState(2, 1));
        assertSame(false, gof.getCellState(2, 2));
        assertSame(true,  gof.getCellState(2, 3));
        assertSame(true,  gof.getCellState(3, 1));
        assertSame(true,  gof.getCellState(3, 2));
        assertSame(true,  gof.getCellState(3, 3));
    }

    @Test
    public void setCellState() {
        GameOfLife gof = GameOfLife.generateGlider(10, 10);
        gof.setCellState(0, 0, true);
        assertSame(true,  gof.getCellState(0, 0));
        gof.setCellState(9, 9, true);
        assertSame(true,  gof.getCellState(9, 9));

        gof.setCellState(10, 10, true);
        assertSame(false,  gof.getCellState(10, 10));
    }
}
