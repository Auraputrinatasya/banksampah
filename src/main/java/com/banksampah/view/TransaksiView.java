package com.banksampah.view;

import com.banksampah.dao.JenisSampahDAO;
import com.banksampah.dao.SetorSampahDAO;
import com.banksampah.dao.UserDAO;
import com.banksampah.model.DetailSetor;
import com.banksampah.model.JenisSampah;
import com.banksampah.model.SetorSampah;
import com.banksampah.model.User;
import com.banksampah.util.ReportServer;
import com.banksampah.util.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.net.URI;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TransaksiView {
    private VBox root;
    private TableView<SetorSampah> table;
    private final SetorSampahDAO dao = new SetorSampahDAO();
    private final NumberFormat IDR = NumberFormat.getInstance(new Locale("id", "ID"));

    public TransaksiView() {
        buildUI();
    }

    private boolean isAdmin() {
        return SessionManager.isAdmin();
    }

    private void buildUI() {
        root = new VBox(0);
        root.setStyle("-fx-background-color: #F4F6FA;");

        // Topbar
        HBox topbar = new HBox();
        topbar.getStyleClass().add("topbar");
        topbar.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label(isAdmin() ? "Semua Transaksi" : "Transaksi Saya");
        title.getStyleClass().add("page-title");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button btnTambah = new Button("➕  Tambah");
        btnTambah.getStyleClass().add("btn-primary");
        btnTambah.setOnAction(e -> showFormDialog(null));
        Button btnRefresh = new Button("🔄  Refresh");
        btnRefresh.getStyleClass().add("btn-secondary");
        btnRefresh.setOnAction(e -> loadData());
        topbar.getChildren().addAll(title, spacer, btnTambah, new Region() {
            {
                setMinWidth(8);
            }
        }, btnRefresh);

        // Content
        VBox content = new VBox(16);
        content.setPadding(new Insets(24));

        table = new TableView<>();
        table.getStyleClass().add("table-view");
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<SetorSampah, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("idSetor"));
        colId.setPrefWidth(50);

        TableColumn<SetorSampah, String> colTgl = new TableColumn<>("Tanggal");
        colTgl.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTanggal().toString()));
        colTgl.setPrefWidth(110);

        TableColumn<SetorSampah, String> colNasabah = new TableColumn<>("Nasabah");
        colNasabah.setCellValueFactory(new PropertyValueFactory<>("namaNasabah"));
        colNasabah.setPrefWidth(160);

        TableColumn<SetorSampah, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setPrefWidth(110);
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    return;
                }
                Label badge = new Label(item.toUpperCase());
                badge.getStyleClass().add("badge-" + item);
                setGraphic(badge);
            }
        });

        TableColumn<SetorSampah, String> colTotal = new TableColumn<>("Total (Rp)");
        colTotal.setCellValueFactory(c -> new SimpleStringProperty(IDR.format((long) c.getValue().getTotalSubtotal())));
        colTotal.setPrefWidth(130);

        TableColumn<SetorSampah, String> colAksi = new TableColumn<>("Aksi");
        colAksi.setPrefWidth(250);
        colAksi.setCellFactory(col -> new TableCell<>() {
            final Button btnEdit = new Button("✏️");
            final Button btnHapus = new Button("🗑️");
            final Button btnVerif = new Button("✅ Verifikasi");
            final Button btnPrint = new Button("🖨️");
            {
                btnEdit.setStyle(
                        "-fx-background-color:#3B82F6;-fx-text-fill:white;-fx-cursor:hand;-fx-padding:4 10;-fx-background-radius:5;-fx-font-size:12px;");
                btnHapus.setStyle(
                        "-fx-background-color:#EF4444;-fx-text-fill:white;-fx-cursor:hand;-fx-padding:4 10;-fx-background-radius:5;-fx-font-size:12px;");
                btnVerif.setStyle(
                        "-fx-background-color:#16A34A;-fx-text-fill:white;-fx-cursor:hand;-fx-padding:4 10;-fx-background-radius:5;-fx-font-size:12px;");
                btnPrint.setStyle(
                        "-fx-background-color:#8B5CF6;-fx-text-fill:white;-fx-cursor:hand;-fx-padding:4 10;-fx-background-radius:5;-fx-font-size:12px;");
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }
                SetorSampah s = getTableView().getItems().get(getIndex());
                boolean sudahDiproses = "diterima".equals(s.getStatus()) || "ditolak".equals(s.getStatus());
                HBox box = new HBox(4);
                box.setAlignment(Pos.CENTER_LEFT);

                if (!sudahDiproses) {
                    btnEdit.setOnAction(e -> showFormDialog(s));
                    box.getChildren().add(btnEdit);
                }
                if (!sudahDiproses || isAdmin()) {
                    btnHapus.setOnAction(e -> handleHapus(s));
                    if (sudahDiproses)
                        btnHapus.setStyle(
                                "-fx-background-color:#9CA3AF;-fx-text-fill:white;-fx-cursor:hand;-fx-padding:4 10;-fx-background-radius:5;-fx-font-size:12px;");
                    else
                        btnHapus.setStyle(
                                "-fx-background-color:#EF4444;-fx-text-fill:white;-fx-cursor:hand;-fx-padding:4 10;-fx-background-radius:5;-fx-font-size:12px;");
                    box.getChildren().add(btnHapus);
                }
                if (isAdmin() && "menunggu".equals(s.getStatus())) {
                    btnVerif.setOnAction(e -> handleVerifikasi(s));
                    box.getChildren().add(btnVerif);
                }
                btnPrint.setOnAction(e -> {
                    try {
                        Desktop.getDesktop().browse(new URI(ReportServer.getDetailUrl(s.getIdSetor())));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
                box.getChildren().add(btnPrint);
                setGraphic(box);
            }
        });

        table.getColumns().addAll(colId, colTgl, colNasabah, colStatus, colTotal, colAksi);
        table.setPlaceholder(new Label("Belum ada transaksi"));
        loadData();

        content.getChildren().add(table);
        VBox.setVgrow(content, Priority.ALWAYS);
        root.getChildren().addAll(topbar, content);
    }

    private void loadData() {
        try {
            List<SetorSampah> list = isAdmin()
                    ? dao.getAll()
                    : dao.getByUser(SessionManager.getCurrentUser().getIdUser());
            table.setItems(FXCollections.observableArrayList(list));
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
        }
    }

    private void handleHapus(SetorSampah s) {
        if (!isAdmin() && ("diterima".equals(s.getStatus()) || "ditolak".equals(s.getStatus()))) {
            showAlert("Tidak Bisa Dihapus", "Transaksi yang sudah " + s.getStatus() + " tidak dapat dihapus!");
            return;
        }
        String pesan = ("diterima".equals(s.getStatus()) || "ditolak".equals(s.getStatus()))
                ? "Transaksi #" + s.getIdSetor() + " sudah " + s.getStatus() + ".\nYakin ingin dihapus? (hanya Admin)"
                : "Hapus transaksi #" + s.getIdSetor() + "?";
        Alert conf = new Alert(Alert.AlertType.CONFIRMATION, pesan, ButtonType.YES, ButtonType.NO);
        conf.showAndWait().ifPresent(b -> {
            if (b == ButtonType.YES) {
                try {
                    dao.delete(s.getIdSetor());
                    loadData();
                } catch (Exception e) {
                    showAlert("Error", e.getMessage());
                }
            }
        });
    }

    private void handleVerifikasi(SetorSampah s) {
        if (!isAdmin()) {
            showAlert("Akses Ditolak", "Hanya Admin yang bisa memverifikasi!");
            return;
        }
        Alert dlg = new Alert(Alert.AlertType.CONFIRMATION);
        dlg.setTitle("Verifikasi Transaksi");
        dlg.setHeaderText("Transaksi #" + s.getIdSetor() + " - " + s.getNamaNasabah());
        dlg.setContentText("Pilih tindakan verifikasi:");
        ButtonType terima = new ButtonType("✅ Terima");
        ButtonType tolak = new ButtonType("❌ Tolak");
        ButtonType batal = new ButtonType("Batal", ButtonBar.ButtonData.CANCEL_CLOSE);
        dlg.getButtonTypes().setAll(terima, tolak, batal);
        dlg.showAndWait().ifPresent(b -> {
            try {
                if (b == terima)
                    dao.updateStatus(s.getIdSetor(), "diterima", SessionManager.getCurrentUser().getIdUser());
                else if (b == tolak)
                    dao.updateStatus(s.getIdSetor(), "ditolak", SessionManager.getCurrentUser().getIdUser());
                loadData();
            } catch (Exception e) {
                showAlert("Error", e.getMessage());
            }
        });
    }

    private void showFormDialog(SetorSampah existing) {
        if (existing != null) {
            boolean sudah = "diterima".equals(existing.getStatus()) || "ditolak".equals(existing.getStatus());
            if (sudah) {
                showAlert("Tidak Bisa Diedit", "Transaksi yang sudah " + existing.getStatus() + " tidak dapat diedit!");
                return;
            }
        }

        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(existing == null ? "Tambah Transaksi" : "Edit Transaksi");

        VBox root = new VBox(12);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: #F8FAFC;");

        ComboBox<User> cmbNasabah = new ComboBox<>();
        cmbNasabah.setMaxWidth(Double.MAX_VALUE);
        if (isAdmin()) {
            try {
                cmbNasabah.setItems(FXCollections.observableArrayList(new UserDAO().getNasabah()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            root.getChildren().addAll(new Label("Nasabah:"), cmbNasabah);
        } else {
            User self = SessionManager.getCurrentUser();
            cmbNasabah.getItems().add(self);
            cmbNasabah.setValue(self);
        }

        DatePicker dpTgl = new DatePicker(LocalDate.now());
        dpTgl.setMaxWidth(Double.MAX_VALUE);
        TextField txtCatatan = new TextField();
        txtCatatan.setPromptText("Catatan (opsional)");

        Label lblDetail = new Label("Detail Sampah:");
        lblDetail.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

        ComboBox<JenisSampah> cmbJenis = new ComboBox<>();
        cmbJenis.setMaxWidth(Double.MAX_VALUE);
        try {
            cmbJenis.setItems(FXCollections.observableArrayList(new JenisSampahDAO().getAll()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        TextField txtBerat = new TextField();
        txtBerat.setPromptText("Berat (kg)");
        Button btnAdd = new Button("➕ Tambah Item");
        btnAdd.getStyleClass().add("btn-success");

        ListView<String> listItems = new ListView<>();
        listItems.setPrefHeight(120);
        List<DetailSetor> details = new ArrayList<>();

        btnAdd.setOnAction(e -> {
            JenisSampah j = cmbJenis.getValue();

            // ✅ DIPERBAIKI: Validasi ComboBox jenis wajib dipilih
            if (j == null) {
                showAlert("Validasi", "Jenis sampah harus dipilih!");
                return;
            }

            // ✅ DIPERBAIKI: Validasi berat — format desimal dan nilai > 0
            double berat;
            try {
                berat = Double.parseDouble(txtBerat.getText().trim());
                if (berat <= 0) {
                    showAlert("Validasi", "Berat sampah harus lebih dari 0!");
                    return;
                }
            } catch (NumberFormatException ex) {
                showAlert("Validasi", "Berat sampah harus berupa angka!");
                return;
            }

            // ✅ DIPERBAIKI: Validasi bisnis — harga per kg tidak boleh nol
            if (j.getHargaPerKg() <= 0) {
                showAlert("Harga Belum Diatur",
                        "Harga per kg untuk kategori '" + j.getNamaJenis() + "' belum dikonfigurasi!");
                return;
            }

            double subtotal = berat * j.getHargaPerKg();
            DetailSetor d = new DetailSetor();
            d.setIdJenis(j.getIdJenis());
            d.setBerat(berat);
            d.setHargaSatuan(j.getHargaPerKg());
            d.setSubtotal(subtotal);
            d.setNamaJenis(j.getNamaJenis());
            details.add(d);
            listItems.getItems().add(j.getNamaJenis() + " | " + berat + " kg | Rp " + IDR.format((long) subtotal));
            txtBerat.clear();
        });

        if (existing != null) {
            if (isAdmin()) {
                for (User u : cmbNasabah.getItems())
                    if (u.getIdUser() == existing.getIdUser()) {
                        cmbNasabah.setValue(u);
                        break;
                    }
            }
            dpTgl.setValue(existing.getTanggal());
            txtCatatan.setText(existing.getCatatan());
            try {
                for (DetailSetor d : dao.getDetail(existing.getIdSetor())) {
                    details.add(d);
                    listItems.getItems().add(
                            d.getNamaJenis() + " | " + d.getBerat() + " kg | Rp " + IDR.format((long) d.getSubtotal()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Button btnSimpan = new Button("💾  Simpan");
        btnSimpan.getStyleClass().add("btn-primary");
        btnSimpan.setMaxWidth(Double.MAX_VALUE);
        btnSimpan.setOnAction(e -> {

            // Validasi ComboBox nasabah wajib
            if (cmbNasabah.getValue() == null) {
                showAlert("Validasi", "Pilih nasabah!");
                return;
            }

            // Validasi tanggal wajib
            if (dpTgl.getValue() == null) {
                showAlert("Validasi", "Tanggal tidak boleh kosong!");
                return;
            }

            // Validasi minimal 1 item
            if (details.isEmpty()) {
                showAlert("Validasi", "Minimal tambah 1 item sampah!");
                return;
            }

            // ✅ DIPERBAIKI: Dialog konfirmasi ringkasan transaksi sebelum simpan
            double totalNilai = details.stream().mapToDouble(DetailSetor::getSubtotal).sum();
            String namaKategori = details.size() > 0 ? details.get(0).getNamaJenis() : "-";
            double totalBerat = details.stream().mapToDouble(DetailSetor::getBerat).sum();

            String ringkasan = String.format(
                    "Nasabah   : %s%nKategori  : %s%nBerat     : %.2f kg%nTotal     : Rp %,.0f%nTanggal   : %s",
                    cmbNasabah.getValue().toString(),
                    namaKategori + (details.size() > 1 ? " +" + (details.size() - 1) + " lainnya" : ""),
                    totalBerat,
                    totalNilai,
                    dpTgl.getValue().toString());

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Konfirmasi Transaksi Sampah");
            confirm.setHeaderText("Periksa data transaksi berikut:");
            confirm.setContentText(ringkasan);
            confirm.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

            if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK)
                return;

            // Proses penyimpanan data ke basis data
            try {
                SetorSampah s = existing != null ? existing : new SetorSampah();
                s.setIdUser(cmbNasabah.getValue().getIdUser());
                s.setTanggal(dpTgl.getValue());
                s.setCatatan(txtCatatan.getText());
                int idSetor;
                if (existing == null) {
                    s.setStatus("menunggu");
                    idSetor = dao.insert(s);
                } else {
                    idSetor = existing.getIdSetor();
                    dao.update(s);
                    dao.deleteDetail(idSetor);
                }
                for (DetailSetor d : details) {
                    d.setIdSetor(idSetor);
                    dao.insertDetail(d);
                }

                // ✅ DIPERBAIKI: Alert INFORMATION sukses setelah simpan
                showInfo("Transaksi Berhasil",
                        String.format("Transaksi dicatat! Total nilai: Rp %,.0f", totalNilai));

                loadData();
                dialog.close();
            } catch (Exception ex) {
                showAlert("Error", ex.getMessage());
            }
        });

        HBox itemRow = new HBox(8, cmbJenis, txtBerat, btnAdd);
        HBox.setHgrow(cmbJenis, Priority.ALWAYS);
        root.getChildren().addAll(
                new Label("Tanggal:"), dpTgl,
                new Label("Catatan:"), txtCatatan,
                lblDetail, itemRow, listItems,
                btnSimpan);

        ScrollPane sp = new ScrollPane(root);
        sp.setFitToWidth(true);
        Scene scene = new Scene(sp, 500, 560);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        dialog.setScene(scene);
        dialog.show();
    }

    // ✅ DIPERBAIKI: showAlert menggunakan AlertType.ERROR untuk validasi (bukan
    // INFORMATION)
    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void showInfo(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    public Parent getView() {
        return root;
    }
}