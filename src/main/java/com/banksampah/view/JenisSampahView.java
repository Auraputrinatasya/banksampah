package com.banksampah.view;

import com.banksampah.dao.JenisSampahDAO;
import com.banksampah.model.JenisSampah;
import com.banksampah.util.DBConnection;
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

import java.sql.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class JenisSampahView {
    private VBox root;
    private TableView<JenisSampah> table;
    private final JenisSampahDAO dao = new JenisSampahDAO();
    private final NumberFormat IDR = NumberFormat.getInstance(new Locale("id", "ID"));

    public JenisSampahView() { buildUI(); }

    private void buildUI() {
        root = new VBox(0);
        root.setStyle("-fx-background-color: #F4F6FA;");

        // Topbar
        HBox topbar = new HBox();
        topbar.getStyleClass().add("topbar");
        topbar.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Jenis Sampah");
        title.getStyleClass().add("page-title");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button btnTambah = new Button("➕  Tambah Jenis");
        btnTambah.getStyleClass().add("btn-primary");
        btnTambah.setOnAction(e -> showDialog(null));
        topbar.getChildren().addAll(title, spacer, btnTambah);

        // Content
        VBox content = new VBox(16);
        content.setPadding(new Insets(24));

        table = new TableView<>();
        table.getStyleClass().add("table-view");
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<JenisSampah, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("idJenis"));
        colId.setPrefWidth(55);

        TableColumn<JenisSampah, String> colKategori = new TableColumn<>("Kategori");
        colKategori.setCellValueFactory(new PropertyValueFactory<>("namaKategori"));
        colKategori.setPrefWidth(150);

        TableColumn<JenisSampah, String> colNama = new TableColumn<>("Jenis Sampah");
        colNama.setCellValueFactory(new PropertyValueFactory<>("namaJenis"));
        colNama.setPrefWidth(200);

        TableColumn<JenisSampah, String> colHarga = new TableColumn<>("Harga/kg (Rp)");
        colHarga.setCellValueFactory(c -> new SimpleStringProperty(IDR.format((long) c.getValue().getHargaPerKg())));
        colHarga.setPrefWidth(140);

        TableColumn<JenisSampah, String> colAksi = new TableColumn<>("Aksi");
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
                JenisSampah j = getTableView().getItems().get(getIndex());
                edit.setOnAction(e -> showDialog(j));
                hapus.setOnAction(e -> {
                    Alert c = new Alert(Alert.AlertType.CONFIRMATION, "Hapus " + j.getNamaJenis() + "?", ButtonType.YES, ButtonType.NO);
                    c.showAndWait().ifPresent(b -> {
                        if (b == ButtonType.YES) {
                            try { dao.delete(j.getIdJenis()); loadData(); }
                            catch (Exception ex) { new Alert(Alert.AlertType.ERROR, ex.getMessage()).show(); }
                        }
                    });
                });
                HBox box = new HBox(6, edit, hapus);
                box.setAlignment(Pos.CENTER_LEFT);
                setGraphic(box);
            }
        });

        table.getColumns().addAll(colId, colKategori, colNama, colHarga, colAksi);
        table.setPlaceholder(new Label("Belum ada data jenis sampah"));
        loadData();

        content.getChildren().add(table);
        VBox.setVgrow(content, Priority.ALWAYS);
        root.getChildren().addAll(topbar, content);
    }

    private void loadData() {
        try { table.setItems(FXCollections.observableArrayList(dao.getAll())); }
        catch (Exception e) { new Alert(Alert.AlertType.ERROR, e.getMessage()).show(); }
    }

    private List<String[]> getKategori() {
        List<String[]> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT id_kategori, nama_kategori FROM kategori_sampah ORDER BY nama_kategori")) {
            while (rs.next()) list.add(new String[]{rs.getString("id_kategori"), rs.getString("nama_kategori")});
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    private void showDialog(JenisSampah existing) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(existing == null ? "Tambah Jenis Sampah" : "Edit Jenis Sampah");

        VBox root = new VBox(10);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: #F8FAFC;");

        List<String[]> kategori = getKategori();
        ComboBox<String> cmbKategori = new ComboBox<>();
        cmbKategori.setMaxWidth(Double.MAX_VALUE);
        int[] katIds = new int[kategori.size()];
        for (int i = 0; i < kategori.size(); i++) {
            cmbKategori.getItems().add(kategori.get(i)[1]);
            katIds[i] = Integer.parseInt(kategori.get(i)[0]);
        }

        TextField txtNama  = new TextField(); txtNama.setPromptText("Nama jenis sampah");
        TextField txtHarga = new TextField(); txtHarga.setPromptText("Harga per kg");

        if (existing != null) {
            txtNama.setText(existing.getNamaJenis());
            txtHarga.setText(String.valueOf((long) existing.getHargaPerKg()));
            for (int i = 0; i < kategori.size(); i++)
                if (Integer.parseInt(kategori.get(i)[0]) == existing.getIdKategori()) { cmbKategori.getSelectionModel().select(i); break; }
        }

        Button btnSimpan = new Button("💾  Simpan");
        btnSimpan.getStyleClass().add("btn-primary");
        btnSimpan.setMaxWidth(Double.MAX_VALUE);
        btnSimpan.setOnAction(e -> {
            if (cmbKategori.getValue() == null || txtNama.getText().isEmpty() || txtHarga.getText().isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Semua field wajib diisi!").show(); return;
            }
            try {
                JenisSampah j = existing != null ? existing : new JenisSampah();
                j.setIdKategori(katIds[cmbKategori.getSelectionModel().getSelectedIndex()]);
                j.setNamaJenis(txtNama.getText());
                j.setHargaPerKg(Double.parseDouble(txtHarga.getText()));
                if (existing == null) dao.insert(j); else dao.update(j);
                loadData(); dialog.close();
            } catch (Exception ex) { new Alert(Alert.AlertType.ERROR, ex.getMessage()).show(); }
        });

        root.getChildren().addAll(
            new Label("Kategori:"), cmbKategori,
            new Label("Nama Jenis:"), txtNama,
            new Label("Harga/kg:"), txtHarga,
            btnSimpan
        );

        Scene scene = new Scene(root, 360, 300);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        dialog.setScene(scene);
        dialog.show();
    }

    public Parent getView() { return root; }
}
