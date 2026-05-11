package com.banksampah.view;

import com.banksampah.util.SessionManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class MainView {

    private final Stage stage;

    private BorderPane root;
    private StackPane contentArea;
    private Button activeBtn;

    public MainView(Stage stage) {
        this.stage = stage;
        buildUI();
    }

    private void buildUI() {

        root = new BorderPane();

        // =====================================
        // CONTENT AREA
        // =====================================
        contentArea = new StackPane();
        contentArea.setStyle("-fx-background-color: #F4F6FA;");

        root.setCenter(contentArea);

        // =====================================
        // SIDEBAR
        // =====================================
        VBox sidebar = new VBox();
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(220);

        // =====================================
        // HEADER SIDEBAR
        // =====================================
        VBox sideHeader = new VBox(4);
        sideHeader.getStyleClass().add("sidebar-header");
        sideHeader.setPadding(new Insets(22, 20, 18, 20));

        HBox brandRow = new HBox(10);
        brandRow.setAlignment(Pos.CENTER_LEFT);

        Rectangle iconBox = new Rectangle(36, 36);
        iconBox.setArcWidth(10);
        iconBox.setArcHeight(10);
        iconBox.setFill(Color.web("#16A34A"));

        Label iconLabel = new Label("♻️");

        StackPane iconStack = new StackPane(iconBox, iconLabel);

        VBox brandText = new VBox(2);

        Label brandName = new Label("Bank Sampah");
        brandName.getStyleClass().add("sidebar-title");

        Label brandSub = new Label("Sistem Pengelolaan");
        brandSub.getStyleClass().add("sidebar-sub");

        brandText.getChildren().addAll(brandName, brandSub);

        brandRow.getChildren().addAll(iconStack, brandText);

        sideHeader.getChildren().add(brandRow);

        // =====================================
        // USER INFO
        // =====================================
        VBox userInfo = new VBox(2);
        userInfo.setPadding(new Insets(12, 20, 12, 20));

        Label userLabel = new Label(
                "👤  " + SessionManager.getCurrentUser().getNama());

        userLabel.setStyle(
                "-fx-text-fill: #BBF7D0; -fx-font-size: 13px;");

        Label roleLabel = new Label(
                SessionManager.isAdmin()
                        ? "🔑  Administrator"
                        : "🌿  Nasabah");

        roleLabel.setStyle(
                "-fx-text-fill: #86EFAC; -fx-font-size: 11px;");

        userInfo.getChildren().addAll(userLabel, roleLabel);

        Separator sideSep = new Separator();

        // =====================================
        // NAVIGATION
        // =====================================
        VBox navGroup = new VBox(2);
        navGroup.setPadding(new Insets(10, 0, 0, 0));

        if (SessionManager.isAdmin()) {

            Button btnDashboard = navButton("🏠  Dashboard");
            Button btnTransaksi = navButton("♻️  Semua Transaksi");
            Button btnJenis = navButton("🗂️  Jenis Sampah");
            Button btnNasabah = navButton("👥  Nasabah");
            Button btnLaporan = navButton("📊  Laporan");

            navGroup.getChildren().addAll(
                    btnDashboard,
                    btnTransaksi,
                    btnJenis,
                    btnNasabah,
                    btnLaporan);

            // DEFAULT PAGE
            setActivePage(btnDashboard, new DashboardView().getView());
            setActive(btnDashboard);

            // ACTION BUTTONS
            btnDashboard.setOnAction(e -> {
                setActivePage(btnDashboard, new DashboardView().getView());
                setActive(btnDashboard);
            });

            btnTransaksi.setOnAction(e -> {
                setActivePage(btnTransaksi, new TransaksiView().getView());
                setActive(btnTransaksi);
            });

            btnJenis.setOnAction(e -> {
                setActivePage(btnJenis, new JenisSampahView().getView());
                setActive(btnJenis);
            });

            btnNasabah.setOnAction(e -> {
                setActivePage(btnNasabah, new NasabahView().getView());
                setActive(btnNasabah);
            });

            btnLaporan.setOnAction(e -> openLaporan());

        } else {

            Button btnTransaksi = navButton("♻️  Transaksi Saya");

            navGroup.getChildren().add(btnTransaksi);

            setActivePage(btnTransaksi, new TransaksiView().getView());
            setActive(btnTransaksi);

            btnTransaksi.setOnAction(e -> {
                setActivePage(btnTransaksi, new TransaksiView().getView());
                setActive(btnTransaksi);
            });
        }

        // =====================================
        // LOGOUT BUTTON
        // =====================================
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button btnLogout = new Button("🚪  Logout");

        btnLogout.setStyle("""
                    -fx-background-color: #7F1D1D;
                    -fx-text-fill: #FCA5A5;
                    -fx-font-size: 13px;
                    -fx-padding: 10 20;
                    -fx-pref-width: 220;
                    -fx-alignment: CENTER_LEFT;
                    -fx-cursor: hand;
                """);

        // ✅ DIPERBAIKI: logout sekarang ada dialog konfirmasi sesuai laporan
        btnLogout.setOnAction(e -> doLogout());

        sidebar.getChildren().addAll(
                sideHeader,
                userInfo,
                sideSep,
                navGroup,
                spacer,
                btnLogout);

        root.setLeft(sidebar);
    }

    private Button navButton(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("nav-btn");
        btn.setMaxWidth(Double.MAX_VALUE);
        return btn;
    }

    private void setActive(Button btn) {
        if (activeBtn != null) {
            activeBtn.getStyleClass().remove("nav-btn-active");
        }
        btn.getStyleClass().add("nav-btn-active");
        activeBtn = btn;
    }

    private void setActivePage(Button btn, Parent view) {
        if (contentArea != null) {
            contentArea.getChildren().setAll(view);
        }
    }

    private void openLaporan() {
        try {
            java.awt.Desktop.getDesktop().browse(
                    new java.net.URI(
                            com.banksampah.util.ReportServer.getUrl()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ✅ DIPERBAIKI: doLogout() sekarang menggunakan Alert CONFIRMATION sesuai
    // laporan
    private void doLogout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Yakin ingin keluar dari aplikasi?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Konfirmasi Logout");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                SessionManager.logout();

                LoginView loginView = new LoginView(stage);

                Scene scene = new Scene(
                        loginView.getView(),
                        420,
                        500);

                scene.getStylesheets().add(
                        getClass()
                                .getResource("/styles.css")
                                .toExternalForm());

                stage.setScene(scene);
                stage.setResizable(false);
                stage.centerOnScreen();
            }
        });
    }

    public Parent getView() {
        return root;
    }
}