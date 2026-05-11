package com.banksampah.view;

import com.banksampah.dao.UserDAO;
import com.banksampah.model.User;
import com.banksampah.util.SessionManager;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class LoginView {
    private final Stage stage;
    private VBox root;
    private Label errorLabel;

    public LoginView(Stage stage) {
        this.stage = stage;
        buildUI();
    }

    private void buildUI() {
        root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("login-bg");

        VBox card = new VBox(18);
        card.setAlignment(Pos.CENTER_LEFT);
        card.getStyleClass().add("card");
        card.setPrefWidth(360);
        card.setMaxWidth(360);

        HBox logoBox = new HBox(12);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        Rectangle logoRect = new Rectangle(44, 44);
        logoRect.setArcWidth(12);
        logoRect.setArcHeight(12);
        logoRect.setFill(Color.web("#16A34A"));
        Label logoIcon = new Label("♻️");
        logoIcon.setFont(Font.font(22));
        StackPane logoStack = new StackPane(logoRect, logoIcon);
        VBox logoText = new VBox(2);
        Label appName = new Label("Bank Sampah");
        appName.getStyleClass().add("title-label");
        appName.setStyle("-fx-font-size: 18px;");
        Label appSub = new Label("Sistem Pengelolaan Bank Sampah");
        appSub.getStyleClass().add("subtitle-label");
        logoText.getChildren().addAll(appName, appSub);
        logoBox.getChildren().addAll(logoStack, logoText);

        Separator sep = new Separator();

        Label loginTitle = new Label("Masuk ke Akun");
        loginTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");
        Label loginSub = new Label("Masukkan username dan password Anda");
        loginSub.getStyleClass().add("subtitle-label");

        Label usernameLabel = new Label("Username");
        usernameLabel.getStyleClass().add("section-label");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Masukkan username...");
        usernameField.setPrefHeight(42);

        Label passwordLabel = new Label("Password");
        passwordLabel.getStyleClass().add("section-label");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Masukkan password...");
        passwordField.setPrefHeight(42);

        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #EF4444; -fx-font-size: 12px;");
        errorLabel.setWrapText(true);
        errorLabel.setMaxWidth(320);
        errorLabel.setManaged(true);
        errorLabel.setVisible(true);
        errorLabel.setText("");

        Button loginBtn = new Button("Masuk");
        loginBtn.getStyleClass().add("btn-primary");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setPrefHeight(44);

        Label footer = new Label("© 2026 Sistem Bank Sampah");
        footer.setStyle("-fx-font-size: 11px; -fx-text-fill: #94A3B8;");

        // ✅ DIPERBAIKI: validasi field kosong + validasi panjang minimum username
        loginBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            // Validasi field kosong (sudah tersedia pada kode asli)
            if (username.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Username dan password tidak boleh kosong!");
                return;
            }

            // ✅ TAMBAHAN: validasi panjang minimum username
            if (username.length() < 3) {
                errorLabel.setText("Username minimal 3 karakter!");
                return;
            }

            errorLabel.setText("Menghubungkan...");
            doLogin(username, password);
        });

        // Event Enter pada PasswordField (sudah tersedia pada kode asli)
        passwordField.setOnAction(e -> loginBtn.fire());

        // ✅ TAMBAHAN: sembunyikan pesan error saat pengguna mulai mengetik
        usernameField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (errorLabel.isVisible() && !errorLabel.getText().isEmpty()) {
                errorLabel.setText("");
            }
        });

        card.getChildren().addAll(
                logoBox, sep, loginTitle, loginSub,
                usernameLabel, usernameField,
                passwordLabel, passwordField,
                errorLabel, loginBtn, footer);
        VBox.setMargin(footer, new Insets(4, 0, 0, 0));
        root.getChildren().add(card);
    }

    private void doLogin(String username, String password) {
        new Thread(() -> {
            try {
                UserDAO dao = new UserDAO();
                User u = dao.login(username, password);
                if (u != null) {
                    SessionManager.setCurrentUser(u);
                    Platform.runLater(() -> {
                        try {
                            // ✅ TAMBAHAN: Alert INFORMATION pesan sambutan setelah login berhasil
                            Alert info = new Alert(Alert.AlertType.INFORMATION,
                                    "Selamat datang, " + u.getNama() + "!");
                            info.setTitle("Login Berhasil");
                            info.showAndWait();

                            MainView mainView = new MainView(stage);
                            Scene scene = new Scene(mainView.getView(), 1050, 700);
                            scene.getStylesheets().add(
                                    getClass().getResource("/styles.css").toExternalForm());
                            stage.setScene(scene);
                            stage.setResizable(true);
                            stage.centerOnScreen();
                        } catch (Exception ex) {
                            errorLabel.setText("Gagal buka dashboard: " + ex.getMessage());
                            ex.printStackTrace();
                        }
                    });
                } else {
                    Platform.runLater(() -> errorLabel.setText("Username atau password salah!"));
                }
            } catch (Exception ex) {
                Platform.runLater(() -> errorLabel.setText("Koneksi DB gagal: " + ex.getMessage()));
                ex.printStackTrace();
            }
        }).start();
    }

    public Parent getView() {
        return root;
    }
}