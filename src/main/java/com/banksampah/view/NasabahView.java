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

    public NasabahView() { buildUI(); }

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
            final Button edit  = new Button("✏️ Edit");
            final Button hapus = new Button("🗑️ Hapus");
            {
                edit.getStyleClass().add("btn-secondary");
                edit.setStyle("-fx-font-size: 12px; -fx-padding: 5 12;");
                hapus.getStyleClass().add("btn-danger");
                hapus.setStyle("-fx-font-size: 12px; -fx-padding: 5 12;");
            }
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                User u = getTableView().getItems().get(getIndex());
                edit.setOnAction(e -> showDialog(u));
                hapus.setOnAction(e -> {
                    Alert c = new Alert(Alert.AlertType.CONFIRMATION, "Hapus " + u.getNama() + "?", ButtonType.YES, ButtonType.NO);
                    c.showAndWait().ifPresent(b -> {
                        if (b == ButtonType.YES) {
                            try { dao.delete(u.getIdUser()); loadData(); }
                            catch (Exception ex) { new Alert(Alert.AlertType.ERROR, ex.getMessage()).show(); }
                        }
                    });
                });
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
        try { table.setItems(FXCollections.observableArrayList(dao.getAll())); }
        catch (Exception e) { new Alert(Alert.AlertType.ERROR, e.getMessage()).show(); }
    }

    private void showDialog(User existing) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(existing == null ? "Tambah Nasabah" : "Edit Nasabah");

        VBox root = new VBox(10);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: #F8FAFC;");

        TextField txtNama     = new TextField(); txtNama.setPromptText("Nama lengkap");
        TextField txtUsername = new TextField(); txtUsername.setPromptText("Username");
        PasswordField txtPass = new PasswordField(); txtPass.setPromptText("Password (kosongkan jika tidak ganti)");
        ComboBox<String> cmbRole = new ComboBox<>(FXCollections.observableArrayList("user", "admin"));
        cmbRole.setValue("user");
        cmbRole.setMaxWidth(Double.MAX_VALUE);
        TextField txtTelp  = new TextField(); txtTelp.setPromptText("No. Telepon");
        TextArea txtAlamat = new TextArea(); txtAlamat.setPromptText("Alamat"); txtAlamat.setPrefRowCount(2);

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
            if (txtNama.getText().isEmpty() || txtUsername.getText().isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Nama dan username wajib diisi!").show(); return;
            }
            try {
                User u = existing != null ? existing : new User();
                u.setNama(txtNama.getText()); u.setUsername(txtUsername.getText());
                u.setRole(cmbRole.getValue()); u.setNoTelp(txtTelp.getText()); u.setAlamat(txtAlamat.getText());
                if (existing == null) {
                    if (txtPass.getText().isEmpty()) { new Alert(Alert.AlertType.WARNING, "Password wajib diisi untuk nasabah baru!").show(); return; }
                    u.setPassword(txtPass.getText()); dao.insert(u);
                } else {
                    if (!txtPass.getText().isEmpty()) u.setPassword(txtPass.getText());
                    dao.update(u);
                }
                loadData(); dialog.close();
            } catch (Exception ex) { new Alert(Alert.AlertType.ERROR, ex.getMessage()).show(); }
        });

        root.getChildren().addAll(
            new Label("Nama:"), txtNama,
            new Label("Username:"), txtUsername,
            new Label("Password:"), txtPass,
            new Label("Role:"), cmbRole,
            new Label("No. Telp:"), txtTelp,
            new Label("Alamat:"), txtAlamat,
            btnSimpan
        );

        ScrollPane sp = new ScrollPane(root);
        sp.setFitToWidth(true);
        Scene scene = new Scene(sp, 380, 500);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        dialog.setScene(scene);
        dialog.show();
    }

    public Parent getView() { return root; }
}
