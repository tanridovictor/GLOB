package com.enseval.gcmuser.Activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.enseval.gcmuser.API.JSONRequest;
import com.enseval.gcmuser.API.QueryEncryption;
import com.enseval.gcmuser.API.RetrofitClient;
import com.enseval.gcmuser.Adapter.CheckoutCompanyAdapter;
import com.enseval.gcmuser.Adapter.TanggalKirimAdapter;
import com.enseval.gcmuser.Fragment.LoadingDialog;
import com.enseval.gcmuser.Model.Cart;
import com.enseval.gcmuser.Model.Company;
import com.enseval.gcmuser.Model.KalenderLibur;
import com.enseval.gcmuser.Model.LimitKirimBarang;
import com.enseval.gcmuser.Model.TanggalKirim;
import com.enseval.gcmuser.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;

public class PermintaanKirimActivity extends AppCompatActivity {

    private ArrayList<Cart> listBarang;
    private static ArrayList<TanggalKirim> listTanggalKirim;
    private RecyclerView rvPermintaanKirim;
    private TanggalKirimAdapter tanggalKirimAdapter;
    private RadioGroup rblist;
    private String tipeKirim = "tunggal";
    private Button btnSimpanTanggalKirim;
    private long lastClickTime=0;
    private LoadingDialog loadingDialog;

    private ArrayList<Company> listCheckoutCompany;
    private long total;

    private ArrayList<KalenderLibur> listLibur = new ArrayList<>();
    private ArrayList<LimitKirimBarang> limitKirim = new ArrayList<>();
    private int company_id_seller;

    private CardView cvTanggal1;
    private TextView tvTanggal1;
    private String tglLibur, ketLibur;

    public String tanggalTunggal;
    private int count;

    private String tipe_pengiriman, tgl_kirim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permintaan_kirim);

        loadingDialog = new LoadingDialog(this);

        getKalenderLibur();

        listCheckoutCompany = getIntent().getParcelableArrayListExtra("listSeller");
        total = getIntent().getLongExtra("total",0);
        listBarang = getIntent().getParcelableArrayListExtra("listbarang");
        company_id_seller = getIntent().getIntExtra("id_company_seller",0);
        tipe_pengiriman = getIntent().getStringExtra("tipe_pengiriman");
        tgl_kirim = getIntent().getStringExtra("tanggal");

        rvPermintaanKirim = findViewById(R.id.rvTanggalKirim);
        rblist = findViewById(R.id.rbList);
        btnSimpanTanggalKirim = findViewById(R.id.btnSimpanTanggalKirim);

        cvTanggal1 = findViewById(R.id.cvTanggal1);
        tvTanggal1 = findViewById(R.id.tvTanggal1);

        if (!tgl_kirim.equals("")){
            tvTanggal1.setText(tgl_kirim);
        }

        rvPermintaanKirim.setVisibility(GONE);
        final RadioButton rbTunggal = findViewById(R.id.rbTunggal);
        if (tipe_pengiriman.equals("tunggal")){
            rblist.check(R.id.rbTunggal);
        }else{
            rblist.check(R.id.rbBanyak);
        }
//        rbTunggal.setChecked(true);
        rblist.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.rbTunggal :
                        tipeKirim = "tunggal";
                        rvPermintaanKirim.setVisibility(GONE);
                        cvTanggal1.setVisibility(View.VISIBLE);
                        getKalenderLibur();
                        break;

                    case R.id.rbBanyak :
                        tipeKirim = "multi";
                        rvPermintaanKirim.setVisibility(View.VISIBLE);
                        cvTanggal1.setVisibility(GONE);
                        getKalenderLibur();
                        break;
                }
            }
        });

        listTanggalKirim = new ArrayList<>();

        for(Cart od : listBarang){
//            listTanggalKirim.add(new TanggalKirim("", od, 0));
            listTanggalKirim.add(new TanggalKirim("", 0));
        }

//        listTanggalKirim.add(new TanggalKirim("", 0));

        btnSimpanTanggalKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SystemClock.elapsedRealtime()-lastClickTime<1000){
                    return;
                }
                else {
                    saveSendDate();
//                    Log.d("ido", "cek isi array: "+listTanggalKirim.size());
//                    for (int i=0; i<listTanggalKirim.size(); i++){
//                        Log.d("ido", "cek isi array: "+listTanggalKirim.get(i).getTglKirim());
//                    }
                }
                lastClickTime= SystemClock.elapsedRealtime();
            }
        });

        cvTanggal1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTanggal();
            }
        });

    }

    public static void setLineId(int position, int value){
        listTanggalKirim.get(position).setLine_id(value);
        Log.d("ido", "ke hit");
    }

    public static void setTglKirim(int position, String value){
        listTanggalKirim.get(position).setTglKirim(value);
//        checkButtonEnabled();
    }

    /**Method pengecekan apakah form sudah terisi semua*/
    public static void checkButtonEnabled(){
        boolean allFilled = true; //flag
        if(listTanggalKirim.size()>0){
            for(int i = 0; i< listTanggalKirim.size(); i++){
                if(listTanggalKirim.get(i).getTglKirim().isEmpty()){
                    allFilled = false;
                }
            }
//            btnKirim.setEnabled(allFilled);
            Log.d("ido", "checkButtonEnabled: kesini");
        }
        else{
            Log.d("ido", "checkButtonEnabled: kesono");
        }
    }

    private void saveSendDate(){
        loadingDialog.showDialog();
        String query = "insert into gcm_tanggal_kirim (master_cart_id, line_id, tgl_permintaan_kirim, qty, tipe_pengiriman) values ";
        String loopQuery = "";
        if (tipeKirim.equals("tunggal")){
            for (int i=0; i<listBarang.size(); i++){
                loopQuery = loopQuery + "("+listBarang.get(i).getId()+", "+1+", '"+tanggalTunggal+"', "+listBarang.get(i).getQty()+", '"+tipeKirim+"')";

                if(i< listTanggalKirim.size()-1){
                    loopQuery = loopQuery.concat(",");
                }
                else{
                    loopQuery = loopQuery.concat(" on conflict (master_cart_id, line_id) do update set tgl_permintaan_kirim = excluded.tgl_permintaan_kirim");
                }
            }
        }else {
            for (int i=0; i<listBarang.size(); i++){
                loopQuery = loopQuery + "("+listBarang.get(i).getId()+", "+1+", '"+tanggalTunggal+"', "+listBarang.get(i).getQty()+", '"+tipeKirim+"')";

                if(i< listTanggalKirim.size()-1){
                    loopQuery = loopQuery.concat(",");
                }
                else{
                    loopQuery = loopQuery.concat(" on conflict (master_cart_id, line_id) do update set tgl_permintaan_kirim = excluded.tgl_permintaan_kirim");
                }
            }
        }
        String query2 = "";
        String fullQuery = query.concat(loopQuery);
        Log.d("ido", "saveSendDate: "+fullQuery);

        try {
            Call<JsonObject> saveShippedDate = RetrofitClient
                    .getInstance()
                    .getApi()
                    .request(new JSONRequest(QueryEncryption.Encrypt(fullQuery)));
            saveShippedDate.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()){
                        loadingDialog.hideDialog();
                        Toast.makeText(getApplicationContext(), "Berhasil insert", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getApplicationContext(), CheckoutActivity.class);
                        i.putParcelableArrayListExtra("listSeller", listCheckoutCompany);
                        i.putExtra("total", total);
                        startActivity(i);
                        finish();
                    }else{
                        loadingDialog.hideDialog();
                        Toast.makeText(getApplicationContext(), "Gagal insert", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    loadingDialog.hideDialog();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getKalenderLibur(){
        loadingDialog.showDialog();
//        String get_kalender = "SELECT * FROM gcm_kalender_libur where tanggal >= now() - interval '1day' order by tanggal asc";
        String get_kalender = "SELECT * FROM gcm_kalender_libur order by tanggal asc";
        try{
            Call<JsonObject> callKalender = RetrofitClient
                    .getInstance()
                    .getApi()
                    .request(new JSONRequest(QueryEncryption.Encrypt(get_kalender)));
            callKalender.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()){
                        String status = response.body().getAsJsonObject().get("status").getAsString();
                        if (status.equals("success")){
                            JsonArray jsonArray = response.body().getAsJsonObject().get("data").getAsJsonArray();
//                            listLibur = new ArrayList<>();
                            for (int i=0; i<jsonArray.size(); i++){
                                JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
                                int id = jsonObject.get("id").getAsInt();
                                String tanggal = jsonObject.get("tanggal").getAsString();
                                String keterangan = jsonObject.get("keterangan").getAsString();
                                listLibur.add(new KalenderLibur(id, tanggal, keterangan));
                            }
                            getlimitkirim();
                        }else{
                            loadingDialog.hideDialog();
                        }
                    }else{
                        loadingDialog.hideDialog();
                    }
                }
                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    loadingDialog.hideDialog();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getlimitkirim(){
        String query = "select * from gcm_limit_setting a where company_id = "+company_id_seller+";";
        Log.d("ido", "hasil query: "+query);
        try {
            Call<JsonObject> getlimitkirim = RetrofitClient
                    .getInstance()
                    .getApi()
                    .request(new JSONRequest(QueryEncryption.Encrypt(query)));
            getlimitkirim.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()){
                        String status = response.body().getAsJsonObject().get("status").getAsString();
                        if (status.equals("success")){
                            JsonArray jsonArray = response.body().getAsJsonObject().get("data").getAsJsonArray();
//                            limitKirim = new ArrayList<>();
                            for (int i=0; i<jsonArray.size(); i++){
                                JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
                                long id = jsonObject.get("id").getAsInt();
                                long company_id = jsonObject.get("company_id").getAsInt();
                                long limit_hari_kirim = jsonObject.get("limit_hari_kirim").getAsInt();
                                limitKirim.add(new LimitKirimBarang(id, company_id, limit_hari_kirim));
                            }
                            rvPermintaanKirim.setLayoutManager(new LinearLayoutManager(PermintaanKirimActivity.this));
                            rvPermintaanKirim.setItemAnimator(new DefaultItemAnimator());
                            tanggalKirimAdapter = new TanggalKirimAdapter(PermintaanKirimActivity.this, listBarang, tipeKirim, listLibur, limitKirim);
                            rvPermintaanKirim.setAdapter(tanggalKirimAdapter);
                            loadingDialog.hideDialog();
                        }else{
                            loadingDialog.hideDialog();
                        }
                    }else{
                        loadingDialog.hideDialog();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    loadingDialog.hideDialog();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setTanggal(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            final Calendar calendar = Calendar.getInstance();
            final int Day = calendar.get(Calendar.DAY_OF_MONTH);
            final int Month = calendar.get(Calendar.MONTH);
            final int Year = calendar.get(Calendar.YEAR);

            final DatePickerDialog dpd = new DatePickerDialog(PermintaanKirimActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    int k = 0;
                    while (k < listLibur.size()){
                        if (String.valueOf(month).length()==1){
                            if (String.valueOf(year+"-0"+(month+1)+"-"+dayOfMonth).equals(listLibur.get(k).getTanggal().substring(0, 10))){
                                tglLibur = listLibur.get(k).getTanggal().substring(0,10);
                                ketLibur = listLibur.get(k).getKeterangan();
                                break;
                            }else{
                                tglLibur = "tdkLibur";
                                ketLibur = "tdkLibur";
                            }
                        }else {
                            if (String.valueOf(year + "-" + (month + 1) + "-" + dayOfMonth).equals(listLibur.get(k).getTanggal().substring(0, 10))) {
                                tglLibur = listLibur.get(k).getTanggal().substring(0,10);
                                ketLibur = listLibur.get(k).getKeterangan();
                                break;
                            }else{
                                tglLibur = "tdkLibur";
                                ketLibur = "tdkLibur";
                            }
                        }
                        k++;
                    }
//                            for (int i=0; i<listLibur.size(); i++){
//                                if (String.valueOf(month).length()==1){
//                                    Log.d(TAG, "onDateSet: "+String.valueOf(year+"-0"+(month+1)+"-"+dayOfMonth));
//                                    if (String.valueOf(year+"-0"+(month+1)+"-"+dayOfMonth).equals(listLibur.get(i).getTanggal().substring(0, 10))){
//                                        tglLibur = listLibur.get(i).getTanggal().substring(0,10);
//                                        ketLibur = listLibur.get(i).getKeterangan();
//                                    }else{
//                                        tglLibur = "tdkLibur";
//                                        ketLibur = "tdkLibur";
//                                    }
//                                }else {
//                                    if (String.valueOf(year + "-" + (month + 1) + "-" + dayOfMonth).equals(listLibur.get(i).getTanggal().substring(0, 10))) {
//                                        tglLibur = listLibur.get(i).getTanggal().substring(0,10);
//                                        ketLibur = listLibur.get(i).getKeterangan();
//                                    }else{
//                                        tglLibur = "tdkLibur";
//                                        ketLibur = "tdkLibur";
//                                    }
//                                }
//                            }
                    SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE");
                    Date date = new Date(year, month, dayOfMonth-1);
                    String dayOfWeek = simpledateformat.format(date);
                    if (dayOfWeek.equals("Sunday")||dayOfWeek.equals("Saturday")){
                        Toast.makeText(getApplicationContext(), "Silahkan pilih tanggal pada hari kerja (Senin - Jumat).", Toast.LENGTH_LONG).show();
                    }else{
                        if (String.valueOf(month).length()==1) {
                            if (!tglLibur.equals("tdkLibur") && tglLibur.equals(String.valueOf(year + "-0" + (month + 1) + "-" + dayOfMonth))) {
                                Toast.makeText(getApplicationContext(), "Tanggal yang dipilih jatuh pada hari libur nasional.", Toast.LENGTH_LONG).show();
                            } else {
                                tvTanggal1.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
//                            for (int i = 0; i < listBarangPerCompany.size(); i++) {
//                                setKirim(dayOfMonth, (month + 1), year, holder.listBarangPerCompany.get(i).getId());
//                            }
//                            Intent i = new Intent(_context, CheckoutActivity.class);
//                            i.putParcelableArrayListExtra("listSeller", listCheckoutCompany);
//                            i.putExtra("total", total);
//                            //i.putExtra("kurs", kurs);
//                            _context.startActivity(i);
//                            ((Activity)_context).finish();
                            }
                        }else{
                            if (!tglLibur.equals("tdkLibur") && tglLibur.equals(String.valueOf(year + "-" + (month + 1) + "-" + dayOfMonth))) {
                                Toast.makeText(getApplicationContext(), "Tanggal yang dipilih jatuh pada hari libur nasional.", Toast.LENGTH_LONG).show();
                            } else {
                                tvTanggal1.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
//                            for (int i = 0; i < holder.listBarangPerCompany.size(); i++) {
//                                setKirim(dayOfMonth, (month + 1), year, holder.listBarangPerCompany.get(i).getId());
//                            }
//                            Intent i = new Intent(_context, CheckoutActivity.class);
//                            i.putParcelableArrayListExtra("listSeller", listCheckoutCompany);
//                            i.putExtra("total", total);
//                            //i.putExtra("kurs", kurs);
//                            _context.startActivity(i);
//                            ((Activity)_context).finish();
                            }
                        }
                    }
                    tanggalTunggal = String.valueOf(year) + "-" + String.valueOf((month + 1)) + "-" + String.valueOf(dayOfMonth);
                }
            },Year, Month, Day);
            DatePicker datePicker = dpd.getDatePicker();
            datePicker.setFirstDayOfWeek(android.icu.util.Calendar.MONDAY);
            dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            int maxdate = (int) limitKirim.get(0).getlimit_hari_kirim();
            calendar.add(Calendar.DAY_OF_MONTH, maxdate);
            dpd.getDatePicker().setMaxDate(calendar.getTimeInMillis());
            dpd.show();
        }
    }

    private void CheckTanggal(final int id_master_cart, String tipePengiriman){
        String query = "select count(id) from gcm_tanggal_kirim gtk where master_cart_id = "+id_master_cart+" and tipe_pengiriman = '"+tipePengiriman+"'";
        try{
            Call<JsonObject> checktgl = RetrofitClient
                    .getInstance()
                    .getApi()
                    .request(new JSONRequest(QueryEncryption.Encrypt(query)));
            checktgl.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()){
                        String status = response.body().getAsJsonObject().get("status").getAsString();
                        if (status.equals("success")){
                            JsonArray jsonArray = response.body().getAsJsonObject().get("data").getAsJsonArray();
                            int count = jsonArray.get(0).getAsJsonObject().get("count").getAsInt();
                            if (count>0){
                                deletetgl(id_master_cart);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void deletetgl(int master_cart_id){
        String query = "delete from gcm_tanggal_kirim where master_cart_id in ("+master_cart_id+")";
        try{
            Call<JsonObject> deletetgl = RetrofitClient
                    .getInstance()
                    .getApi()
                    .request(new JSONRequest(QueryEncryption.Encrypt(query)));
            deletetgl.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()){

                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
