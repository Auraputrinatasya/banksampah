package com.banksampah.view;

import com.banksampah.dao.UserDAO;
import com.banksampah.model.User;
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

public class NasabahView {
    private VBox root;
    private TableView<User> table;
    private final UserDAO dao = new UserDAO();

    public NasabahView() {
        buildUI();
    }

    private void buildUI() {
        root = new VBox(0);
        root.setStyle("-fx-background-color: #F4F6FA;");

        // Topbar
        HBox topbar = new HBox();
        topbar.getStyleClass().add("topbar");
        topbar.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Data Nasabah");
        title.getStyleClass().add("page-title");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button btnTambah = new Button("➕  Tambah Nasabah");
        btnTambah.getStyleClass().add("btn-primary");
        btnTambah.setOnAction(e -> showDialog(null));
        topbar.getChildren().addAll(title, spacer, btnTambah);

        // Content
        VBox content = new VBox(16);
        content.setPadding(new Insets(24));

        // Table
        table = new TableView<>();
        table.getStyleClass().add("table-view");
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<User, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("idUser"));
        colId.setPrefWidth(55);

        TableColumn<User, String> colNama = new TableColumn<>("Nama");
        colNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        colNama.setPrefWidth(170);

        TableColumn<User, String> colUsername = new TableColumn<>("Username");
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colUsername.setPrefWidth(130);

        TableColumn<User, String> colRole = new TableColumn<>("Role");
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colRole.setPrefWidth(80);

        TableColumn<User, String> colTelp = new TableColumn<>("No. Telp");
        colTelp.setCellValueFactory(new PropertyValueFactory<>("noTelp"));
        colTelp.setPrefWidth(130);

        TableColumn<User, String> colAksi = new TableColumn<>("Aksi");
        colAksi.setPrefWidth(150);
        colAksi.setCellFactory(col -> new TableCell<>() {
            final Button edit = new Button("✏️ Edit");
            final Button hapus = new Button("🗑️ Hapus");
            {
                edit.getStyleClass().add("btn-secondary");
                edit.setStyle("-fx-font-size: 12px; -fx-padding: 5 12;");
                hapus.getStyleClass().add("btn-danger");
                hapus.setStyle("-fx-font-size: 12px; -fx-padding: 5 12;");
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }
                User u = getTableView().getItems().get(getIndex());

                // ✅ DIPERBAIKI: Konfirmasi sebelum membuka form edit
                edit.setOnAction(e -> {
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                            "Edit data nasabah: " + u.getNama() + "?",
                            ButtonType.YES, ButtonType.NO);
                    confirm.setTitle("Konfirmasi Edit");
                    confirm.setHeaderText("Perubahan akan tersimpan langsung ke basis data.");
                    confirm.showAndWait().ifPresent(btn -> {
                        if (btn == ButtonType.YES)
                            showDialog(u);
                    });
                });

                // ✅ DIPERBAIKI: Konfirmasi hapus dengan pesan yang lebih informatif
                hapus.setOnAction(e -> deleteNasabah(u));

                HBox box = new HBox(6, edit, hapus);
                box.setAlignment(Pos.CENTER_LEFT);
                setGraphic(box);
            }
        });

        table.getColumns().addAll(colId, colNama, colUsername, colRole, colTelp, colAksi);
        table.setPlaceholder(new Label("Belum ada data nasabah"));
        loadData();

        content.getChildren().add(table);
        VBox.setVgrow(content, Priority.ALWAYS);
        root.getChildren().addAll(topbar, content);
    }

    private void loadData() {
        try {
            table.setItems(FXCollections.observableArrayList(dao.getAll()));
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
        }
    }

    // ✅ DIPERBAIKI: Metode hapus nasabah dengan konfirmasi sesuai laporan
    private void deleteNasabah(User n) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Hapus nasabah '" + n.getNama() + "' (Username: " + n.getUsername() + ")?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Konfirmasi Hapus");
        confirm.setHeaderText("Tindakan ini tidak dapat dibatalkan!");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                try {
                    dao.delete(n.getIdUser());
                    showInfo("Berhasil", "Nasabah '" + n.getNama() + "' berhasil dihapus.");
                    loadData();
                } catch (Exception ex) {
                    showAlert("Error Hapus", "Gagal menghapus: " + ex.getMessage());
                }
            }
        });
    }

    private void showDialog(User existing) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(existing == null ? "Tambah Nasabah" : "Edit Nasabah");

        VBox root = new VBox(10);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: #F8FAFC;");

        TextField txtNama = new TextField();
        txtNama.setPromptText("Nama lengkap");
        TextField txtUsername = new TextField();
        txtUsername.setPromptText("Username");
        PasswordField txtPass = new PasswordField();
        txtPass.setPromptText("Password (kosongkan jika tidak ganti)");
        ComboBox<String> cmbRole = new ComboBox<>(FXCollections.observableArrayList("user", "admin"));
        cmbRole.setValue("user");
        cmbRole.setMaxWidth(Double.MAX_VALUE);
        // ✅ DIPERBAIKI: prompt text disesuaikan dengan validasi (angka 10-13 digit)
        TextField txtTelp = new TextField();
        txtTelp.setPromptText("No. Telepon (10-13 digit angka)");
        TextArea txtAlamat = new TextArea();
        txtAlamat.setPromptText("Alamat");
        txtAlamat.setPrefRowCount(2);

        if (existing != null) {
            txtNama.setText(existing.getNama());
            txtUsername.setText(existing.getUsername());
            cmbRole.setValue(existing.getRole());
            txtTelp.setText(existing.getNoTelp());
            txtAlamat.setText(existing.getAlamat());
        }

        Button btnSimpan = new Button("💾  Simpan");
        btnSimpan.getStyleClass().add("btn-primary");
        btnSimpan.setMaxWidth(Double.MAX_VALUE);
        btnSimpan.setOnAction(e -> {
            String nama = txtNama.getText().trim();
            String username = txtUsername.getText().trim();
            String noHp = txtTelp.getText().trim();
            String alamat = txtAlamat.getText().trim();

            // ✅ DIPERBAIKI: Validasi field kosong sesuai laporan
            if (nama.isEmpty()) {
                showAlert("Validasi", "Nama nasabah tidak boleh kosong!");
                return;
            }
            if (username.isEmpty()) {
                showAlert("Validasi", "Username tidak boleh kosong!");
                return;
            }
            if (noHp.isEmpty()) {
                showAlert("Validasi", "Nomor HP tidak boleh kosong!");
                return;
            }
            if (alamat.isEmpty()) {
                showAlert("Validasi", "Alamat tidak boleh kosong!");
                return;
            }

            // ✅ DIPERBAIKI: Validasi format No. HP — hanya angka, panjang 10-13 karakter
            if (!noHp.matches("\\d{10,13}")) {
                showAlert("Validasi", "Nomor HP harus berupa angka (10-13 digit)!");
                return;
            }

            // Validasi password untuk nasabah baru
            if (existing == null) {
                if (txtPass.getText().isEmpty()) {
                    showAlert("Validasi", "Password wajib diisi untuk nasabah baru!");
                    return;
                }
            }

            try {
                User u = existing != null ? existing : new User();
                u.setNama(nama);
                u.setUsername(username);
                u.setRole(cmbRole.getValue());
                u.setNoTelp(noHp);
                u.setAlamat(alamat);

                if (existing == null) {
                    u.setPassword(txtPass.getText());
                    dao.insert(u);
                    showInfo("Sukses", "Nasabah berhasil didaftarkan!");
                } else {
                    if (!txtPass.getText().isEmpty())
                        u.setPassword(txtPass.getText());
                    dao.update(u);
                    showInfo("Sukses", "Data nasabah berhasil diperbarui!");
                }
                loadData();
                dialog.close();
            } catch (Exception ex) {
                showAlert("Error", ex.getMessage());
            }
        });

        root.getChildren().addAll(
                new Label("Nama:"), txtNama,
                new Label("Username:"), txtUsername,
                new Label("Password:"), txtPass,
                new Label("Role:"), cmbRole,
                new Label("No. Telp:"), txtTelp,
                new Label("Alamat:"), txtAlamat,
                btnSimpan);

        ScrollPane sp = new ScrollPane(root);
        sp.setFitToWidth(true);
        Scene scene = new Scene(sp, 380, 500);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        dialog.setScene(scene);
        dialog.show();
    }

    // ✅ DIPERBAIKI: Helper methods showAlert() dan showInfo() sesuai laporan
    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg);
        a.setTitle(title);
        a.showAndWait();
    }

    private void showInfo(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg);
        a.setTitle(title);
        a.showAndWait();
    }

    public Parent getView() {
        return root;
    }
}