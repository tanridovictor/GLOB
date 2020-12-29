package com.enseval.gcmuser.Model;

public class TanggalKirim {

    private String tglKirim;
    private Cart cart;
    private int line_id;

    private int master_cart_id;
    private String tgl_permintaan_kirim_edit;
    private int qty_kirim;
    private String tipe_pengiriman;
    private int company_id;
    private int barang_id;

    private String tgl_permintaan_kirim;
    private String nama_perusahaan;

    public TanggalKirim(String tglKirim, Cart cart, int line_id) {
        this.tglKirim = tglKirim;
        this.cart = cart;
        this.line_id = line_id;
    }

    public TanggalKirim(String tglKirim, int line_id) {
        this.tglKirim = tglKirim;
        this.line_id = line_id;
    }

    //    public TanggalKirim(String tglKirim, int line_id) {
//        this.tglKirim = tglKirim;
//        this.line_id = line_id;
//    }
//
//    public TanggalKirim(String tgl_permintaan_kirim, String nama_perusahaan) {
//        this.tgl_permintaan_kirim = tgl_permintaan_kirim;
//        this.nama_perusahaan = nama_perusahaan;
//    }


    public TanggalKirim(int line_id, int master_cart_id, String tgl_permintaan_kirim_edit, int qty_kirim, String tipe_pengiriman, int company_id, int barang_id, String nama_perusahaan) {
        this.line_id = line_id;
        this.master_cart_id = master_cart_id;
        this.tgl_permintaan_kirim_edit = tgl_permintaan_kirim_edit;
        this.qty_kirim = qty_kirim;
        this.tipe_pengiriman = tipe_pengiriman;
        this.company_id = company_id;
        this.barang_id = barang_id;
        this.nama_perusahaan = nama_perusahaan;
    }

    public int getMaster_cart_id() {
        return master_cart_id;
    }

    public String getTgl_permintaan_kirim_edit() {
        return tgl_permintaan_kirim_edit;
    }

    public int getQty_kirim() {
        return qty_kirim;
    }

    public String getTipe_pengiriman() {
        return tipe_pengiriman;
    }

    public int getCompany_id() {
        return company_id;
    }

    public int getBarang_id() {
        return barang_id;
    }

    public void setLine_id(int line_id) {
        this.line_id = line_id;
    }

    public int getLine_id() {
        return line_id;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public String getTglKirim() {
        return tglKirim;
    }

    public void setTglKirim(String tglKirim) {
        this.tglKirim = tglKirim;
    }

    public String getTgl_permintaan_kirim() {
        return tgl_permintaan_kirim;
    }

    public String getNama_perusahaan() {
        return nama_perusahaan;
    }
}
