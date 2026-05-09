package com.banksampah.view;

import com.banksampah.dao.SetorSampahDAO;
import com.banksampah.dao.UserDAO;
import com.banksampah.model.SetorSampah;
import com.banksampah.util.SessionManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class DashboardView {
    private VBox root;
    private final NumberFormat IDR = NumberFormat.getInstance(new Locale("id", "ID"));

    public DashboardView() {
        buildUI();
    }

    private void buildUI() {
        root = new VBox(0);
        root.setStyle("-fx-background-color: #F4F6FA;");

        // Topbar
        HBox topbar = new HBox();
        topbar.getStyleClass().add("topbar");
        topbar.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Dashboard");
        title.getStyleClass().add("page-title");
        Label sub = new Label("   ·  Selamat datang, " + SessionManager.getCurrentUser().getNama());
        sub.setStyle("-fx-text-fill: #64748B; -fx-font-size: 14px;");
        topbar.getChildren().addAll(title, sub);

        VBox content = new VBox(24);
        content.setPadding(new Insets(28));

        // Stat cards
        HBox statsRow = new HBox(16);
        statsRow.setFillHeight(true);

        int bulanIni = 0, menunggu = 0, nasabah = 0;
        double pendapatan = 0;
        try {
            SetorSampahDAO sdao = new SetorSampahDAO();
            UserDAO udao = new UserDAO();
            bulanIni = sdao.countBulanIni();
            menunggu = sdao.countMenunggu();
            nasabah = udao.countNasabah();
            pendapatan = sdao.totalPendapatan();
        } catch (Exception e) {
            System.err.println("Gagal load stats: " + e.getMessage());
        }

        statsRow.getChildren().addAll(
                statCard("🌿", "Transaksi Bulan Ini", String.valueOf(bulanIni), "#16A34A", "#DCFCE7"),
                statCard("⏳", "Menunggu Verifikasi", String.valueOf(menunggu), "#D97706", "#FEF3C7"),
                statCard("👥", "Total Nasabah", String.valueOf(nasabah), "#2563EB", "#DBEAFE"),
                statCard("💰", "Total Pendapatan", "Rp " + IDR.format((long) pendapatan), "#7C3AED", "#EDE9FE"));

        for (var node : statsRow.getChildren()) {
            HBox.setHgrow((Region) node, Priority.ALWAYS);
        }

        Label sectionTitle = new Label("Transaksi Terbaru");
        sectionTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");

        VBox txList = buildRecentTx();

        content.getChildren().addAll(statsRow, sectionTitle, txList);
        VBox.setVgrow(content, Priority.ALWAYS);
        root.getChildren().addAll(topbar, content);
    }

    private VBox statCard(String icon, String label, String value, String accentColor, String bgColor) {
        VBox card = new VBox(8);
        card.getStyleClass().add("stat-card");
        card.setPadding(new Insets(20));

        Rectangle iconBg = new Rectangle(38, 38);
        iconBg.setArcWidth(10);
        iconBg.setArcHeight(10);
        iconBg.setFill(Color.web(bgColor));
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 18px;");
        HBox iconRow = new HBox(10);
        iconRow.setAlignment(Pos.CENTER_LEFT);
        iconRow.getChildren().add(new StackPane(iconBg, iconLabel));

        Label numLabel = new Label(value);
        numLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + accentColor + ";");
        numLabel.setWrapText(true);
        Label descLabel = new Label(label);
        descLabel.getStyleClass().add("stat-label");

        card.getChildren().addAll(iconRow, numLabel, descLabel);
        return card;
    }

    private VBox buildRecentTx() {
        VBox box = new VBox(0);
        box.setStyle(
                "-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #E2E8F0; -fx-border-radius: 10;");

        HBox header = new HBox();
        header.setPadding(new Insets(14, 20, 14, 20));
        header.setStyle(
                "-fx-background-color: #F8FAFC; -fx-background-radius: 10 10 0 0; -fx-border-color: transparent transparent #E2E8F0 transparent;");
        String[] cols = { "#", "Nasabah", "Tanggal", "Status", "Total (Rp)" };
        double[] widths = { 40, 200, 110, 110, 150 };
        for (int i = 0; i < cols.length; i++) {
            Label lbl = new Label(cols[i]);
            lbl.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #374151;");
            lbl.setPrefWidth(widths[i]);
            header.getChildren().add(lbl);
        }
        box.getChildren().add(header);

        try {
            SetorSampahDAO dao = new SetorSampahDAO();
            List<SetorSampah> list = dao.getAll();
            int count = Math.min(list.size(), 8);
            boolean odd = false;
            for (int i = 0; i < count; i++) {
                SetorSampah s = list.get(i);
                odd = !odd;
                HBox row = new HBox();
                row.setPadding(new Insets(12, 20, 12, 20));
                row.setStyle("-fx-background-color: " + (odd ? "#FAFAFA" : "white") + ";");

                String status = s.getStatus() != null ? s.getStatus() : "menunggu";
                String badgeStyle = switch (status) {
                    case "diterima" ->
                        "-fx-background-color:#D1FAE5;-fx-text-fill:#065F46;-fx-background-radius:20;-fx-padding:2 10;-fx-font-size:11px;-fx-font-weight:bold;";
                    case "ditolak" ->
                        "-fx-background-color:#FEE2E2;-fx-text-fill:#991B1B;-fx-background-radius:20;-fx-padding:2 10;-fx-font-size:11px;-fx-font-weight:bold;";
                    default ->
                        "-fx-background-color:#FEF3C7;-fx-text-fill:#92400E;-fx-background-radius:20;-fx-padding:2 10;-fx-font-size:11px;-fx-font-weight:bold;";
                };

                String tglStr = s.getTanggal() != null ? s.getTanggal().toString() : "-";
                String nasabah = s.getNamaNasabah() != null ? s.getNamaNasabah() : "-";

                Label idCell = cell(String.valueOf(s.getIdSetor()), widths[0]);
                Label nasabahCell = cell(nasabah, widths[1]);
                Label tglCell = cell(tglStr, widths[2]);
                Label statusCell = new Label(status.toUpperCase());
                statusCell.setStyle(badgeStyle);
                HBox statusWrap = new HBox(statusCell);
                statusWrap.setPrefWidth(widths[3]);
                Label totalCell = cell(IDR.format((long) s.getTotalSubtotal()), widths[4]);

                row.getChildren().addAll(idCell, nasabahCell, tglCell, statusWrap, totalCell);
                box.getChildren().add(row);
            }
        } catch (Exception ex) {
            Label err = new Label("Gagal memuat data: " + ex.getMessage());
            err.setStyle("-fx-text-fill: #EF4444; -fx-padding: 16;");
            box.getChildren().add(err);
        }
        return box;
    }

    private Label cell(String text, double width) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size: 13px; -fx-text-fill: #374151;");
        l.setPrefWidth(width);
        return l;
    }

    public Parent getView() {
        return root;
    }
}