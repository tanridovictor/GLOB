package com.enseval.gcmuser.Model;

public class LimitKirimBarang {
    private long id;
    private long company_id;
    private long limit_hari_kirim;

    public LimitKirimBarang(long id, long company_id, long limit_hari_kirim) {
        this.id = id;
        this.company_id = company_id;
        this.limit_hari_kirim = limit_hari_kirim;
    }

    public long getId() {
        return id;
    }

    public long getCompany_id() {
        return company_id;
    }

    public long getlimit_hari_kirim() {
        return limit_hari_kirim;
    }
}
