package cz.somrlik.gui;

import cz.somrlik.EditableTimerTask;
import cz.somrlik.GameOfLife;
import javafx.beans.property.*;
import javafx.scene.paint.Color;

class Model {

    private final GameOfLife gameOfLife;

    private LongProperty width = new SimpleLongProperty(10);
    private LongProperty height = new SimpleLongProperty(10);
    private LongProperty timeBetweenSteps = new SimpleLongProperty(100);
    private BooleanProperty running = new SimpleBooleanProperty(false);
    private ObjectProperty<Color> drawingColor = new SimpleObjectProperty<>(Color.BLACK);

    private EditableTimerTask simulationTask;

    Model() {
        gameOfLife = GameOfLife.generateRandom(width.getValue(), height.getValue());

        simulationTask = new EditableTimerTask(
            gameOfLife::step,
            () -> timeBetweenSteps.get()
        );

        width.addListener((observable, oldValue, newValue) -> regenerateBoard((Long) newValue, height.getValue()));

        height.addListener((observable, oldValue, newValue) -> regenerateBoard(width.getValue(), (Long) newValue));

        running.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                simulationTask.startExecution();
            } else {
                simulationTask.stopExecution();
            }
        });
    }

    private void regenerateBoard(long newWidth, long newHeight) {
        gameOfLife.resize(newWidth, newHeight);
    }

    void startPause() {
        running.setValue(! running.getValue());
    }

    void halt() {
        simulationTask.stopExecution();
    }

    GameOfLife getGameOfLife() {
        return gameOfLife;
    }

    long getWidth() {
        return width.get();
    }

    LongProperty widthProperty() {
        return width;
    }

    long getHeight() {
        return height.get();
    }

    LongProperty heightProperty() {
        return height;
    }

    LongProperty timeBetweenStepsProperty() {
        return timeBetweenSteps;
    }

    boolean isRunning() {
        return running.get();
    }

    BooleanProperty runningProperty() {
        return running;
    }

    ObjectProperty<Color> drawingColorProperty() {
        return drawingColor;
    }
}
