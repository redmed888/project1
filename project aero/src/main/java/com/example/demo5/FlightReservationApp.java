package com.example.demo5;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class FlightReservationApp extends Application {

    private BorderPane root;

    @Override
    public void start(Stage primaryStage) {
        root = new BorderPane();

        // === TOP MENU ===
        MenuBar menuBar = new MenuBar();

        Menu modeMenu = new Menu("Mode");
        MenuItem userMode = new MenuItem("User");
        MenuItem adminMode = new MenuItem("Administrator");

        modeMenu.getItems().addAll(userMode, adminMode);
        menuBar.getMenus().add(modeMenu);

        root.setTop(menuBar);

        // Default mode = User
        loadUserUI();

        // Actions
        userMode.setOnAction(e -> loadUserUI());
        adminMode.setOnAction(e -> requestAdminPassword());

        Scene scene = new Scene(root, 900, 650);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Flight Reservation System");
        primaryStage.show();
    }

    // Load normal user interface
    private void loadUserUI() {
        FlightReservationUI userUI = new FlightReservationUI();
        root.setCenter(userUI.createContent());
    }

    // Ask for admin password
    private void requestAdminPassword() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Administrator Login");
        dialog.setHeaderText("Enter administrator password");
        dialog.setContentText("Password:");

        dialog.getEditor().setPromptText("Enter password");

        dialog.showAndWait().ifPresent(password -> {
            if (password.equals("soufiane")) {
                loadAdminUI();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Access Denied");
                alert.setHeaderText("Incorrect Password");
                alert.setContentText("The password you entered is incorrect.");
                alert.showAndWait();
            }
        });
    }

    // Load admin dashboard
    private void loadAdminUI() {
        AdminUI adminUI = new AdminUI();
        root.setCenter(adminUI.createAdminPanel());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
