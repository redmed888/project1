package com.example.demo5;

import javafx.geometry.Side;
import demo1.PlaneSeatMap;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;

public class FlightReservationUI {

    private TextField passengerNameField;
    private TextField emailField;
    private TextField phoneField;

    private TextField departureField;
    private TextField destinationField;
    private DatePicker departureDatePicker;
    private ComboBox<String> flightClassComboBox;

    private Spinner<Integer> passengersSpinner;
    private ListView<String> flightsListView;
    private Button reserveSelectedButton;
    private TextArea resultArea; // <-- Booking info TextArea

    private static class Flight {
        String code, departure, destination, time;
        double price;

        Flight(String c, String d, String de, String t, double p) {
            code = c; departure = d; destination = de; time = t; price = p;
        }

        @Override
        public String toString() {
            return code + " - " + time + " - $" + (int)price;
        }
    }

    private List<Flight> flightDatabase = new ArrayList<>();
    private String[] airports = {
            "New York (JFK)", "Los Angeles (LAX)", "Chicago (ORD)",
            "Miami (MIA)", "San Francisco (SFO)", "London (LHR)",
            "Paris (CDG)", "Tokyo (NRT)", "Dubai (DXB)"
    };

    private void loadRandomFlights() {
        String[] times = {"06:00 AM","08:30 AM","11:15 AM","02:45 PM","05:20 PM","09:40 PM"};
        for (int i = 0; i < 30; i++) {
            String dep = airports[(int)(Math.random()*airports.length)];
            String dest;
            do { dest = airports[(int)(Math.random()*airports.length)]; } while (dep.equals(dest));
            String code = "FL" + (1000 + (int)(Math.random()*9000));
            String time = times[(int)(Math.random()*times.length)];
            double price = 200 + Math.random()*600;
            flightDatabase.add(new Flight(code, dep, dest, time, price));
        }
    }

    public VBox createContent() {
        loadRandomFlights();
        VBox main = new VBox(20);
        main.setPadding(new Insets(20));
        main.setStyle("-fx-background-color: linear-gradient(to bottom, #e3f2fd, #bbdefb);");

        main.getChildren().addAll(
                createUserInfoSection(),
                createFlightSearchSection(),
                createResultSection()
        );

        // Booking info TextArea on first page
        resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setPrefHeight(150);
        resultArea.setWrapText(true);
        main.getChildren().add(resultArea);

        return main;
    }

    private VBox createUserInfoSection() {
        VBox section = createSection("Passenger Information");
        passengerNameField = new TextField(); passengerNameField.setPromptText("Full Name");
        emailField = new TextField(); emailField.setPromptText("Email");
        phoneField = new TextField(); phoneField.setPromptText("Phone");

        section.getChildren().addAll(
                labeled("Name:", passengerNameField),
                labeled("Email:", emailField),
                labeled("Phone:", phoneField)
        );
        return section;
    }

    private VBox createFlightSearchSection() {
        VBox section = createSection("Flight Search");

        departureField = createAutoCompleteField("Departure");
        destinationField = createAutoCompleteField("Destination");

        Button swapBtn = new Button("⇄");
        swapBtn.setStyle("-fx-font-size:16px;-fx-background-radius:50%;-fx-background-color:#eceff1;-fx-padding:6;");
        swapBtn.setOnAction(e -> {
            String temp = departureField.getText();
            departureField.setText(destinationField.getText());
            destinationField.setText(temp);
        });

        HBox airportBox = new HBox(10, departureField, swapBtn, destinationField);
        airportBox.setAlignment(Pos.CENTER_LEFT);

        departureDatePicker = new DatePicker();
        flightClassComboBox = new ComboBox<>();
        flightClassComboBox.getItems().addAll("Economy","Business","First Class");
        passengersSpinner = new Spinner<>(1,10,1);

        Button searchBtn = new Button("Search Flights");
        searchBtn.setStyle("-fx-background-color:#0078ff;-fx-text-fill:white;-fx-padding:8 16;-fx-background-radius:12;");
        searchBtn.setOnAction(e -> searchFlights());

        section.getChildren().addAll(
                labeled("Airports:", airportBox),
                labeled("Departure Date:", departureDatePicker),
                labeled("Class:", flightClassComboBox),
                labeled("Passengers:", passengersSpinner),
                searchBtn
        );
        return section;
    }

    private TextField createAutoCompleteField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        ContextMenu suggestions = new ContextMenu();

        field.textProperty().addListener((obs, oldText, newText) -> {
            if(newText.isEmpty()) { suggestions.hide(); return; }
            List<String> filtered = new ArrayList<>();
            for(String airport : airports) {
                if(airport.toLowerCase().contains(newText.toLowerCase())) filtered.add(airport);
            }
            if(filtered.isEmpty()) { suggestions.hide(); return; }

            suggestions.getItems().clear();
            for(String f : filtered) {
                MenuItem item = new MenuItem(f);
                item.setOnAction(e -> {
                    field.setText(f);
                    field.positionCaret(f.length());
                    suggestions.hide();
                });
                suggestions.getItems().add(item);
            }
            if(!suggestions.isShowing()) suggestions.show(field, Side.BOTTOM, 0, 0);
        });

        return field;
    }

    private VBox createResultSection() {
        VBox section = createSection("Available Flights");
        flightsListView = new ListView<>(); flightsListView.setPrefHeight(150);
        reserveSelectedButton = new Button("Reserve Selected Flight");
        reserveSelectedButton.setDisable(true);
        reserveSelectedButton.setStyle("-fx-background-color:#4CAF50;-fx-text-fill:white;-fx-padding:10;");
        flightsListView.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) ->
                reserveSelectedButton.setDisable(newVal==null));
        reserveSelectedButton.setOnAction(e -> reserveSelectedFlight());
        section.getChildren().addAll(flightsListView, reserveSelectedButton);
        return section;
    }

    private void searchFlights() {
        flightsListView.getItems().clear();
        String dep = departureField.getText();
        String dest = destinationField.getText();
        if(dep.isEmpty() || dest.isEmpty()) { showAlert("Enter departure and destination"); return; }

        int count = 0;
        for(Flight f : flightDatabase) {
            if(f.departure.equals(dep) && f.destination.equals(dest)) {
                flightsListView.getItems().add(f.toString());
                count++;
            }
        }
        if(count==0) flightsListView.getItems().add("No flights found.");
    }

    private void reserveSelectedFlight() {
        String selectedFlight = flightsListView.getSelectionModel().getSelectedItem();
        if(selectedFlight==null) { showAlert("Select a flight first"); return; }
        if(!validateForm()) return;

        PlaneSeatMap seatMap = new PlaneSeatMap();
        seatMap.openSeatSelector(passengerNameField.getText(), passengersSpinner.getValue(), selectedSeats -> {
            String details = String.format(
                    "Passenger: %s\nEmail: %s\nPhone: %s\nRoute: %s → %s\nDate: %s\nFlight: %s\nSeats: %s\nClass: %s\nPassengers: %d\nReservation ID: %s\nStatus: Confirmed",
                    passengerNameField.getText(), emailField.getText(), phoneField.getText(),
                    departureField.getText(), destinationField.getText(),
                    departureDatePicker.getValue(), selectedFlight,
                    String.join(", ", selectedSeats), flightClassComboBox.getValue(),
                    passengersSpinner.getValue(), generateReservationId()
            );
            resultArea.setText(details); // <-- Display booking info directly
        });
    }

    private VBox createSection(String title) {
        VBox box = new VBox(10);
        Label label = new Label(title);
        label.setFont(new Font(18));
        box.getChildren().add(label);
        box.setStyle("-fx-padding:15;-fx-border-color:#ccc;-fx-border-width:1;-fx-border-radius:12;-fx-background-radius:12;-fx-background-color:rgba(255,255,255,0.85);");
        return box;
    }

    private HBox labeled(String text, Node node) {
        Label label = new Label(text);
        label.setPrefWidth(150);
        return new HBox(10,label,node);
    }

    private void showAlert(String text) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null); a.setContentText(text); a.showAndWait();
    }

    private boolean validateForm() {
        if(passengerNameField.getText().trim().isEmpty()){ showAlert("Enter passenger name"); return false;}
        if(emailField.getText().trim().isEmpty() || !emailField.getText().contains("@")){ showAlert("Enter valid email"); return false;}
        if(phoneField.getText().trim().isEmpty()){ showAlert("Enter phone number"); return false;}
        return true;
    }

    private String generateReservationId() { return "FL"+System.currentTimeMillis(); }
}
