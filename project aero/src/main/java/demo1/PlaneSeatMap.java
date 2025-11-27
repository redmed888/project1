package demo1;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.*;
import java.util.function.Consumer;

public class PlaneSeatMap {

    enum SeatType { INDIVIDUAL, STANDARD, TWO_BY_TWO, VIEW, EXTRA_LEGROOM }

    static class Seat {
        final String id;
        SeatType type;
        String passenger;
        boolean selected;
        Seat(String id, SeatType type) { this.id = id; this.type = type; this.passenger = null; this.selected = false; }
        boolean isReserved() { return passenger != null; }
    }

    private static final int ROWS = 18;
    private final List<Seat> allSeats = new ArrayList<>();

    public void openSeatSelector(String passengerName, int passengerCount, Consumer<List<String>> seatsSelectedCallback) {
        Stage seatStage = new Stage();
        seatStage.setTitle("Select Seats for " + passengerName);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(12));

        Label title = new Label("Select " + passengerCount + " seat(s) for: " + passengerName);
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        BorderPane.setAlignment(title, Pos.CENTER);
        root.setTop(title);

        GridPane seatGrid = buildSeatGrid(passengerName, passengerCount);
        ScrollPane sc = new ScrollPane(seatGrid);
        sc.setFitToWidth(true);
        sc.setFitToHeight(true);
        root.setCenter(sc);

        Button confirmButton = new Button("Confirm Seats");
        confirmButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 10;");
        confirmButton.setOnAction(e -> {
            List<String> selectedSeatIds = new ArrayList<>();
            for (Seat seat : allSeats) {
                if (seat.selected) selectedSeatIds.add(seat.id);
            }
            if (selectedSeatIds.size() != passengerCount) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Selection Error");
                alert.setHeaderText(null);
                alert.setContentText("Please select exactly " + passengerCount + " seat(s).");
                alert.showAndWait();
                return;
            }
            seatStage.close();
            if (seatsSelectedCallback != null) seatsSelectedCallback.accept(selectedSeatIds);
        });

        HBox bottomBox = new HBox(confirmButton);
        bottomBox.setPadding(new Insets(10));
        bottomBox.setAlignment(Pos.CENTER);
        root.setBottom(bottomBox);

        Scene scene = new Scene(root, 1000, 650);
        seatStage.setScene(scene);
        seatStage.show();
    }

    private GridPane buildSeatGrid(String passengerName, int passengerCount) {
        GridPane grid = new GridPane();
        grid.setHgap(18);
        grid.setVgap(8);
        grid.setPadding(new Insets(10));

        VBox rowsBox = new VBox(6);
        rowsBox.setPadding(new Insets(8));
        rowsBox.setAlignment(Pos.TOP_CENTER);

        for (int r = 1; r <= ROWS; r++) {
            HBox rowHBox = new HBox(12);
            rowHBox.setAlignment(Pos.CENTER_LEFT);

            HBox leftBlock = new HBox(6);
            leftBlock.setAlignment(Pos.CENTER);
            for (int c = 0; c < 3; c++) {
                String seatId = r + seatLetterLeft(c);
                Seat seat = new Seat(seatId, decideSeatType(r, c, "LEFT"));
                allSeats.add(seat);
                leftBlock.getChildren().add(makeSeatNode(seat, passengerCount));
            }

            Region aisle1 = new Region(); aisle1.setPrefWidth(28);

            HBox midBlock = new HBox(6);
            midBlock.setAlignment(Pos.CENTER);
            for (int c = 0; c < 2; c++) {
                String seatId = r + seatLetterMid(c);
                Seat seat = new Seat(seatId, decideSeatType(r, c, "MID"));
                allSeats.add(seat);
                midBlock.getChildren().add(makeSeatNode(seat, passengerCount));
            }

            Region aisle2 = new Region(); aisle2.setPrefWidth(28);

            HBox rightBlock = new HBox(6);
            rightBlock.setAlignment(Pos.CENTER);
            for (int c = 0; c < 3; c++) {
                String seatId = r + seatLetterRight(c);
                Seat seat = new Seat(seatId, decideSeatType(r, c, "RIGHT"));
                allSeats.add(seat);
                rightBlock.getChildren().add(makeSeatNode(seat, passengerCount));
            }

            Label rowLabel = new Label(String.valueOf(r));
            rowLabel.setMinWidth(28);
            rowLabel.setAlignment(Pos.CENTER_RIGHT);
            rowLabel.setStyle("-fx-font-weight: bold;");

            rowHBox.getChildren().addAll(rowLabel, leftBlock, aisle1, midBlock, aisle2, rightBlock);
            rowsBox.getChildren().add(rowHBox);
        }

        grid.add(rowsBox, 0, 0);
        return grid;
    }

    private Region makeSeatNode(Seat seat, int passengerCount) {
        StackPane container = new StackPane();
        container.setPrefSize(46, 46);

        Rectangle rect = new Rectangle(40, 40);
        rect.setArcWidth(8); rect.setArcHeight(8);
        rect.setStroke(Color.web("#777")); rect.setStrokeWidth(0.8);
        rect.setFill(colorForSeatType(seat.type));

        Label idLabel = new Label(seat.id);
        idLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold;");

        container.getChildren().addAll(rect, idLabel);

        container.setOnMouseClicked(e -> {
            if (!seat.isReserved()) {
                if (seat.selected) {
                    seat.selected = false;
                    rect.setFill(colorForSeatType(seat.type));
                } else {
                    long selectedCount = allSeats.stream().filter(s -> s.selected).count();
                    if (selectedCount >= passengerCount) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Selection Limit");
                        alert.setHeaderText(null);
                        alert.setContentText("You can select only " + passengerCount + " seat(s).");
                        alert.showAndWait();
                        return;
                    }
                    seat.selected = true;
                    rect.setFill(Color.web("#d9534f"));
                }
            }
        });

        return container;
    }

    private SeatType decideSeatType(int row, int colIndex, String block) {
        if (row <= 2 && block.equals("LEFT") && colIndex == 0) return SeatType.INDIVIDUAL;
        if (row <= 2 && block.equals("RIGHT") && colIndex == 2) return SeatType.INDIVIDUAL;
        if ((row == 8 || row == 12) && (block.equals("LEFT") || block.equals("RIGHT"))) return SeatType.EXTRA_LEGROOM;
        if (row > ROWS - 3 && ((block.equals("LEFT") && (colIndex==0||colIndex==2)) || (block.equals("RIGHT") && (colIndex==0||colIndex==2)))) return SeatType.VIEW;
        if (block.equals("MID")) return SeatType.TWO_BY_TWO;
        return SeatType.STANDARD;
    }

    private String seatLetterLeft(int idx){ return switch(idx){ case 0 -> "A"; case 1 -> "B"; case 2 -> "C"; default -> "?"; }; }
    private String seatLetterMid(int idx){ return switch(idx){ case 0 -> "D"; case 1 -> "E"; default -> "?"; }; }
    private String seatLetterRight(int idx){ return switch(idx){ case 0 -> "F"; case 1 -> "G"; case 2 -> "H"; default -> "?"; }; }

    private Color colorForSeatType(SeatType t){ return switch(t){
        case INDIVIDUAL -> Color.web("#8ed0e6");
        case STANDARD -> Color.web("#f7f7f7");
        case TWO_BY_TWO -> Color.web("#1f6fb2");
        case VIEW -> Color.web("#f39c12");
        case EXTRA_LEGROOM -> Color.web("#9b59b6");
    }; }
}
