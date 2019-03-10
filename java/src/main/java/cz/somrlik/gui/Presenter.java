package cz.somrlik.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.LongStringConverter;

import java.util.HashSet;
import java.util.Set;

class Presenter {

    private final static int MIN_WINDOW_WIDTH = 1000;
    private final static int MIN_WINDOW_HEIGHT = 600;

    private final Model model;
    private final Stage primaryStage;

    private Canvas backgroundLayer = new Canvas();
    private AnchorPane ap = new AnchorPane();

    private Set<BooleanProperty> toDisableWhenRunning = new HashSet<>();

    private volatile boolean drawing = false;

    Presenter(Stage primaryStage, Model model) {
        this.model = model;
        this.primaryStage = primaryStage;

        init();
    }

    private void init() {
        primaryStage.setTitle("The Game of Life");

        primaryStage.setMinWidth(MIN_WINDOW_WIDTH);
        primaryStage.setMinHeight(MIN_WINDOW_HEIGHT);

        BorderPane mainPane = new BorderPane();
        backgroundLayer = new Canvas(primaryStage.getHeight(), primaryStage.getHeight());
        mainPane.setCenter(ap);
        ap.getChildren().add(backgroundLayer);

        AnchorPane.setBottomAnchor(backgroundLayer, 0.0);
        AnchorPane.setTopAnchor(backgroundLayer, 0.0);
        AnchorPane.setLeftAnchor(backgroundLayer, 0.0);
        AnchorPane.setRightAnchor(backgroundLayer, 0.0);

        GridPane right = new GridPane();
        mainPane.setRight(right);
        right.setMinSize(MIN_WINDOW_WIDTH - MIN_WINDOW_HEIGHT, MIN_WINDOW_HEIGHT);
        right.setPadding(new Insets(10, 10, 10, 10));
        right.setVgap(5);
        right.setHgap(5);
        right.setAlignment(Pos.CENTER);
        right.getStyleClass().add("gridPane");

        int row = 0;

        right.add(new Label("Left click to give life"), 0, row, 3, 1);
        row++;
        right.add(new Label("Right click to euthanize"), 0, row, 3, 1);
        row++;
        right.add(new Separator(), 0, row, 3, 1);
        row++;

        HBox controls = new HBox();
        controls.setAlignment(Pos.CENTER);
        controls.setSpacing(20);

        Button runPause = new Button(">");
        runPause.setOnAction(event -> model.startPause());
        controls.getChildren().add(runPause);

        Button stepButton = new Button("STEP");
        toDisableWhenRunning.add(stepButton.disableProperty());
        stepButton.setOnAction(event -> model.getGameOfLife().step());
        controls.getChildren().add(stepButton);

        right.add(controls, 0, row, 3, 1);

        row++;
        right.add(new Separator(), 0, row, 3, 1);

        row++;

        // Height
        right.add(new Label("Height"), 0 , row);

        Slider heightSlider = new Slider();
        toDisableWhenRunning.add(heightSlider.disableProperty());
        heightSlider.valueProperty().bindBidirectional(model.heightProperty());
        heightSlider.setMin(5);
        heightSlider.setMax(200);
        heightSlider.setShowTickLabels(true);
        heightSlider.setShowTickMarks(true);
        heightSlider.setMajorTickUnit(45);
        heightSlider.setMinorTickCount(5);
        heightSlider.setBlockIncrement(1);

        right.add(heightSlider, 1, row);

        Text heightValueText = new Text();
        heightValueText.textProperty().bindBidirectional(
            model.heightProperty(),
            (StringConverter) (new LongStringConverter())
        );
        right.add(heightValueText, 2, row);

        row++;

        // Width
        right.add(new Label("Width"), 0 ,row);

        Slider widthSlider = new Slider();
        toDisableWhenRunning.add(widthSlider.disableProperty());
        widthSlider.valueProperty().bindBidirectional(model.widthProperty());
        widthSlider.setMin(5);
        widthSlider.setMax(200);
        widthSlider.setShowTickLabels(true);
        widthSlider.setShowTickMarks(true);
        widthSlider.setMajorTickUnit(45);
        widthSlider.setMinorTickCount(5);
        widthSlider.setBlockIncrement(1);

        right.add(widthSlider, 1, row);

        Text widthValueText = new Text();
        widthValueText.textProperty().bindBidirectional(
                model.widthProperty(),
                (StringConverter) (new LongStringConverter())
        );
        right.add(widthValueText, 2, row);

        row++;

        // Speed
        right.add(new Label("Speed"), 0 ,row);

        Slider speedSlider = new Slider();
        speedSlider.valueProperty().bindBidirectional(model.timeBetweenStepsProperty());
        speedSlider.setMin(10);
        speedSlider.setMax(1000);
        speedSlider.setBlockIncrement(1);

        right.add(speedSlider, 1, row);

        row++;
        right.add(new Separator(), 0, row, 3, 1);

        row++;
        Button generateRandom = new Button("Generate random");
        toDisableWhenRunning.add(generateRandom.disableProperty());
        generateRandom.setOnAction(event -> model.getGameOfLife().randomize());
        right.add(generateRandom, 0, row, 3, 1);

        row++;
        right.add(new Separator(), 0, row, 3, 1);

        row++;
        right.add(new Label("Color"), 0, row);

        ColorPicker colorPicker = new ColorPicker();
        toDisableWhenRunning.add(colorPicker.disableProperty());
        colorPicker.setValue(model.drawingColorProperty().getValue());
        colorPicker.setOnAction(event -> model.drawingColorProperty().setValue(colorPicker.getValue()));
        right.add(colorPicker, 1, row, 2, 1);

        Scene scene = new Scene(mainPane);
        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ESCAPE:
                    primaryStage.close();
                    break;
                case SPACE:
                    model.startPause();
                    break;
                case P:
                    GraphicsContext gc = backgroundLayer.getGraphicsContext2D();
                    gc.setFill(Color.RED);
                    gc.fillRect(20, 20, 500, 500);
                    break;
            }
        });

        primaryStage.setScene(scene);
        primaryStage.show();

        redrawMainDisplay();

        ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) -> updateMainDisplay();

        primaryStage.widthProperty().addListener(stageSizeListener);
        primaryStage.heightProperty().addListener(stageSizeListener);
        primaryStage.maximizedProperty().addListener((observable, oldValue, newValue) -> updateMainDisplay());
        model.heightProperty().addListener((observable, oldValue, newValue) -> redrawMainDisplay());
        model.widthProperty().addListener((observable, oldValue, newValue) -> redrawMainDisplay());
        model.runningProperty().addListener((observable, oldValue, newValue) -> redrawMainDisplay());

        // Platform.runLater is a kind of hack to prevent JavaFX from locking up
        // TODO: Maybe use Timeline and animation frames?
        model.getGameOfLife().addOnStepFinishedListener(gameOfLife -> Platform.runLater(this::redrawMainDisplay));

        backgroundLayer.addEventHandler(MouseEvent.MOUSE_PRESSED, this::drawOnMouseEvent);
        backgroundLayer.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::drawOnMouseEvent);

        model.runningProperty().addListener((observable, oldValue, newValue) -> {
            for (BooleanProperty disabled: toDisableWhenRunning) {
                disabled.setValue(newValue);
            }

            runPause.setText(newValue ? "||" : ">");
        });

        model.drawingColorProperty().addListener((observable, oldValue, newValue) -> updateMainDisplay());
    }

    private void drawOnMouseEvent(MouseEvent event) {
        if (model.isRunning()) return;

        double x = model.getWidth() * (event.getSceneX() / backgroundLayer.getWidth());
        double y = model.getHeight() * (event.getSceneY() / backgroundLayer.getHeight());

        model.getGameOfLife().setCellState((int) Math.floor(x), (int) Math.floor(y), event.isPrimaryButtonDown());
    }

    private void redrawMainDisplay() {
        if (drawing) return;

        drawing = true;
        double singleCellWidth = backgroundLayer.getWidth() / model.getWidth();
        double singleCellHeight = backgroundLayer.getHeight() / model.getHeight();

        for (int i = 0; i < model.getWidth(); i++) {
            for (int j = 0; j < model.getHeight(); j++) {
                drawSingleCell(
                        i * singleCellWidth,
                        j * singleCellHeight,
                        singleCellWidth,
                        singleCellHeight,
                        model.getGameOfLife().getCellState(i, j)
                );
            }
        }

        drawing = false;
    }

    private void drawSingleCell(double x, double y, double width, double height, boolean alive) {
        GraphicsContext gc = backgroundLayer.getGraphicsContext2D();
        gc.setFill(alive ? model.drawingColorProperty().getValue() : Color.WHITE);
        gc.fillRect(x, y, width, height);
    }

    private void updateMainDisplay() {
        backgroundLayer.setWidth(primaryStage.getHeight());
        backgroundLayer.setHeight(primaryStage.getHeight());
        redrawMainDisplay();
    }
}
