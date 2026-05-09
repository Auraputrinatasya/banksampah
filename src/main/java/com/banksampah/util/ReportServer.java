package com.banksampah.util;

import com.banksampah.dao.SetorSampahDAO;
import com.banksampah.model.DetailSetor;
import com.banksampah.model.SetorSampah;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class ReportServer {
    private static HttpServer server;
    private static final int PORT = 8787;
    private static final NumberFormat IDR = NumberFormat.getInstance(new Locale("id", "ID"));
    private static final DateTimeFormatter DFT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void start() throws IOException {
        if (server != null)
            return;
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/laporan", exchange -> {
            String query = exchange.getRequestURI().getQuery();
            String html;
            if (query != null && query.startsWith("id=")) {
                int id = Integer.parseInt(query.split("=")[1]);
                html = buildDetailHtml(id);
            } else {
                html = buildLaporanHtml();
            }
            byte[] bytes = html.getBytes("UTF-8");
            exchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        });
        server.setExecutor(null);
        server.start();
    }

    public static String getUrl() {
        return "http://localhost:" + PORT + "/laporan";
    }

    public static String getDetailUrl(int id) {
        return "http://localhost:" + PORT + "/laporan?id=" + id;
    }

    private static String buildLaporanHtml() {
        StringBuilder sb = new StringBuilder();
        try {
            SetorSampahDAO dao = new SetorSampahDAO();
            List<SetorSampah> list = dao.getAll();

            sb.append("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>Laporan Bank Sampah</title>");
            sb.append("<style>");
            sb.append("body{font-family:Arial,sans-serif;margin:20px;color:#222;}");
            sb.append("h1{color:#2d7d32;text-align:center;margin-bottom:4px;}");
            sb.append(".sub{text-align:center;color:#555;margin-bottom:20px;}");
            sb.append("table{width:100%;border-collapse:collapse;margin-top:10px;}");
            sb.append("th{background:#2d7d32;color:white;padding:8px;text-align:left;}");
            sb.append("td{padding:7px 8px;border-bottom:1px solid #ddd;}");
            sb.append("tr:nth-child(even){background:#f1f8e9;}");
            sb.append(".badge{padding:3px 8px;border-radius:12px;font-size:12px;font-weight:bold;}");
            sb.append(".diterima{background:#c8e6c9;color:#1b5e20;}");
            sb.append(".menunggu{background:#fff9c4;color:#f57f17;}");
            sb.append(".ditolak{background:#ffcdd2;color:#b71c1c;}");
            sb.append(".total{font-size:13px;font-weight:bold;text-align:right;margin-top:10px;color:#2d7d32;}");
            sb.append("@media print{.no-print{display:none;} body{margin:5mm;}}");
            sb.append("</style></head><body>");
            sb.append("<h1>🌿 SISTEM BANK SAMPAH</h1>");
            sb.append("<div class='sub'>Laporan Seluruh Transaksi Setor Sampah</div>");
            sb.append("<div class='no-print' style='text-align:right;margin-bottom:10px;'>");
            sb.append(
                    "<button onclick='window.print()' style='background:#2d7d32;color:white;border:none;padding:8px 20px;border-radius:5px;cursor:pointer;font-size:14px;'>🖨️ Cetak Laporan</button></div>");
            sb.append("<table><thead><tr>");
            sb.append(
                    "<th>#</th><th>Tanggal</th><th>Nasabah</th><th>Status</th><th style='text-align:right'>Total (Rp)</th><th class='no-print'>Aksi</th>");
            sb.append("</tr></thead><tbody>");

            double grandTotal = 0;
            for (int i = 0; i < list.size(); i++) {
                SetorSampah s = list.get(i);
                grandTotal += s.getTotalSubtotal();
                sb.append("<tr>");
                sb.append("<td>").append(i + 1).append("</td>");
                sb.append("<td>").append(s.getTanggal().format(DFT)).append("</td>");
                sb.append("<td>").append(s.getNamaNasabah()).append("</td>");
                sb.append("<td><span class='badge ").append(s.getStatus()).append("'>")
                        .append(s.getStatus().toUpperCase()).append("</span></td>");
                sb.append("<td style='text-align:right'>").append(IDR.format((long) s.getTotalSubtotal()))
                        .append("</td>");
                sb.append("<td class='no-print'><a href='/laporan?id=").append(s.getIdSetor())
                        .append("' target='_blank' style='color:#2d7d32;font-size:12px;'>Detail</a></td>");
                sb.append("</tr>");
            }
            sb.append("</tbody></table>");
            sb.append("<div class='total'>GRAND TOTAL : Rp ").append(IDR.format((long) grandTotal)).append("</div>");
            sb.append("</body></html>");
        } catch (Exception e) {
            sb.append("<p style='color:red'>Error: ").append(e.getMessage()).append("</p>");
        }
        return sb.toString();
    }

    private static String buildDetailHtml(int idSetor) {
        StringBuilder sb = new StringBuilder();
        try {
            SetorSampahDAO dao = new SetorSampahDAO();
            List<DetailSetor> details = dao.getDetail(idSetor);

            sb.append("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>Detail Transaksi</title>");
            sb.append("<style>");
            sb.append("body{font-family:Arial,sans-serif;margin:20px;color:#222;}");
            sb.append("h2{color:#2d7d32;margin-bottom:4px;}");
            sb.append("table{width:100%;border-collapse:collapse;margin-top:10px;}");
            sb.append("th{background:#2d7d32;color:white;padding:8px;text-align:left;}");
            sb.append("td{padding:7px 8px;border-bottom:1px solid #ddd;}");
            sb.append(".total-row{font-weight:bold;background:#e8f5e9;}");
            sb.append("@media print{.no-print{display:none;}}");
            sb.append("</style></head><body>");
            sb.append("<h2>🌿 Detail Transaksi #").append(idSetor).append("</h2>");
            sb.append("<div class='no-print' style='margin-bottom:12px;'>");
            sb.append(
                    "<button onclick='window.print()' style='background:#2d7d32;color:white;border:none;padding:8px 18px;border-radius:5px;cursor:pointer;'>🖨️ Cetak</button> ");
            sb.append(
                    "<button onclick='history.back()' style='background:#555;color:white;border:none;padding:8px 18px;border-radius:5px;cursor:pointer;'>← Kembali</button></div>");
            sb.append(
                    "<table><thead><tr><th>No</th><th>Jenis Sampah</th><th>Berat (kg)</th><th>Harga/kg</th><th>Subtotal</th></tr></thead><tbody>");

            double total = 0;
            for (int i = 0; i < details.size(); i++) {
                DetailSetor d = details.get(i);
                total += d.getSubtotal();
                sb.append("<tr><td>").append(i + 1).append("</td>");
                sb.append("<td>").append(d.getNamaJenis()).append("</td>");
                sb.append("<td>").append(d.getBerat()).append("</td>");
                sb.append("<td>Rp ").append(IDR.format((long) d.getHargaSatuan())).append("</td>");
                sb.append("<td>Rp ").append(IDR.format((long) d.getSubtotal())).append("</td></tr>");
            }
            sb.append("<tr class='total-row'><td colspan='4' style='text-align:right'>TOTAL</td>");
            sb.append("<td>Rp ").append(IDR.format((long) total)).append("</td></tr>");
            sb.append("</tbody></table></body></html>");
        } catch (Exception e) {
            sb.append("<p style='color:red'>Error: ").append(e.getMessage()).append("</p>");
        }
        return sb.toString();
    }
}
