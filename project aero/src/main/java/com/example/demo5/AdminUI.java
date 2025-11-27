package com.example.demo5;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.control.Label;

public class AdminUI {

    public VBox createAdminPanel() {
        VBox box = new VBox(20);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(30));

        Label label = new Label("ADMIN DASHBOARD");
        label.setFont(Font.font("Arial", FontWeight.BOLD, 28));

        box.getChildren().add(label);
        return box;
    }
}
