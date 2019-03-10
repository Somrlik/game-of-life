package cz.somrlik;

import java.util.Timer;
import java.util.TimerTask;

public class ConsoleApplication {

    private static final int STEP_PERIOD = 500;

    private static boolean shouldStop = false;

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: width[int] height[int]");
            System.exit(1);
        }

        int width = 0, height = 0;
        try {
            width = Integer.parseInt(args[0]);
            height = Integer.parseInt(args[1]);

            if (width <= 0 || height <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            System.out.println("Please provide two positive integers as arguments.");
            System.exit(1);
        }

        GameOfLife gof = GameOfLife.generateGlider(width, height);

        gof.addOnStepFinishedListener(ConsoleApplication::printBoard);

        // I run the simulation in a different thread, if you want to do something else in the main thread, be my guest
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new GameOfLifeStepTask(gof), 0, STEP_PERIOD);

        long counter = 0;
        try {
            while (! shouldStop) {
                Thread.yield();
                // The sleep value does not really matter
                Thread.sleep(1000);
                if (counter++ > 1e9) shouldStop = true;
            }
        } catch (Exception e) {
            timer.cancel();
            System.exit(0);
        }

    }

    static class GameOfLifeStepTask extends TimerTask {
        private GameOfLife gameOfLife;

        GameOfLifeStepTask(GameOfLife gameOfLife) {
            this.gameOfLife = gameOfLife;
        }

        @Override
        public void run() {
            synchronized (GameOfLifeStepTask.class) {
                gameOfLife.step();
            }
        }
    }

    private static void printBoard(GameOfLife gameOfLife) {
        for (int i = 0; i < gameOfLife.getBoard().getWidth(); i++) {
            for (int j = 0; j < gameOfLife.getBoard().getHeight(); j++) {
                System.out.print(gameOfLife.getCellState(i, j) ? "#" : " ");
            }
            System.out.print("\n\r");
        }
        System.out.println(String.format(
                String.format("%%%ds", gameOfLife.getBoard().getWidth()),
                " "
            ).replace(" ", "-"));
    }

}
