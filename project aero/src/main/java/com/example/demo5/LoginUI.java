package com.example.demo5;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.control.Label;

public class LoginUI {

    public VBox createLoginScreen(Runnable onUserLogin, Runnable onAdminLogin) {

        VBox box = new VBox(20);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(40));
        box.setStyle("-fx-background-color: linear-gradient(to bottom, #bbdefb, #90caf9);");

        Label title = new Label("Welcome to Flight System");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 32));

        Button userBtn = new Button("Login as User");
        userBtn.setFont(Font.font(18));
        userBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 10 20;");
        userBtn.setOnAction(e -> onUserLogin.run());

        Button adminBtn = new Button("Login as Administrator");
        adminBtn.setFont(Font.font(18));
        adminBtn.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-padding: 10 20;");
        adminBtn.setOnAction(e -> onAdminLogin.run());

        box.getChildren().addAll(title, userBtn, adminBtn);
        return box;
    }
}
