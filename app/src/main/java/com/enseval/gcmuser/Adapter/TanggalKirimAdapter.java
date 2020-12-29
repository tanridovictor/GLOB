package com.enseval.gcmuser.Adapter;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.enseval.gcmuser.Activity.CheckoutActivity;
import com.enseval.gcmuser.Activity.KonfirmasiPembayaranActivity;
import com.enseval.gcmuser.Activity.PermintaanKirimActivity;
import com.enseval.gcmuser.Model.Alamat;
import com.enseval.gcmuser.Model.Barang;
import com.enseval.gcmuser.Model.Cart;
import com.enseval.gcmuser.Model.Company;
import com.enseval.gcmuser.Model.KalenderLibur;
import com.enseval.gcmuser.Model.LimitKirimBarang;
import com.enseval.gcmuser.Model.Note;
import com.enseval.gcmuser.Model.Payment;
import com.enseval.gcmuser.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TanggalKirimAdapter extends RecyclerView.Adapter<TanggalKirimAdapter.ViewHolder> {

    private Context _context;
    private ArrayList<Cart> listBarangPerCompany;
    private String tipeKirim;
    private int days, months, years;
    private ArrayList<KalenderLibur> listLibur;
    private ArrayList<LimitKirimBarang> limitKirim;
    private String tglLibur, ketLibur;
    String TAG = "ido";

    private int limithari;

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvNamaBarang;
        private ImageView ivBarang;
        private EditText etQty1, etQty2, etQty3, etQty4;
        private CardView cvTanggal1, cvTanggal2, cvTanggal3, cvTanggal4;
        private TextView tvTanggal1, tvTanggal2, tvTanggal3, tvTanggal4;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNamaBarang = itemView.findViewById(R.id.tvNama);
            ivBarang = itemView.findViewById(R.id.ivBarang);
            cvTanggal1 = itemView.findViewById(R.id.cvTanggal1);
            cvTanggal2 = itemView.findViewById(R.id.cvTanggal2);
            cvTanggal3 = itemView.findViewById(R.id.cvTanggal3);
            cvTanggal4 = itemView.findViewById(R.id.cvTanggal4);
            etQty1 = itemView.findViewById(R.id.etQty1);
            etQty2 = itemView.findViewById(R.id.etQty2);
            etQty3 = itemView.findViewById(R.id.etQty3);
            etQty4 = itemView.findViewById(R.id.etQty4);
            tvTanggal1 = itemView.findViewById(R.id.tvTanggal1);
            tvTanggal2 = itemView.findViewById(R.id.tvTanggal2);
            tvTanggal3 = itemView.findViewById(R.id.tvTanggal3);
            tvTanggal4 = itemView.findViewById(R.id.tvTanggal4);
        }
    }

    public TanggalKirimAdapter(Context _context, ArrayList<Cart> listBarangPerCompany, String tipeKirim, ArrayList<KalenderLibur> listLibur, ArrayList<LimitKirimBarang> limitKirim) {
        this._context = _context;
        this.listBarangPerCompany = listBarangPerCompany;
        this.tipeKirim = tipeKirim;
        this.listLibur = listLibur;
        this.limitKirim = limitKirim;
    }

    @NonNull
    @Override
    public TanggalKirimAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(_context)
                .inflate(R.layout.dialog_pecah_tanggal_dan_qty, parent, false);
        return new TanggalKirimAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final TanggalKirimAdapter.ViewHolder holder, final int position) {
        Barang barang = listBarangPerCompany.get(position).getBarang();

        if (tipeKirim.equals("tunggal")) {
            holder.etQty1.setVisibility(View.GONE);
            holder.etQty2.setVisibility(View.GONE);
            holder.etQty3.setVisibility(View.GONE);
            holder.etQty4.setVisibility(View.GONE);
            holder.cvTanggal2.setVisibility(View.GONE);
            holder.cvTanggal3.setVisibility(View.GONE);
            holder.cvTanggal4.setVisibility(View.GONE);
        } else if (tipeKirim.equals("multi")) {
            holder.etQty1.setVisibility(View.VISIBLE);
            holder.etQty2.setVisibility(View.VISIBLE);
            holder.etQty3.setVisibility(View.VISIBLE);
            holder.etQty4.setVisibility(View.VISIBLE);
            holder.cvTanggal2.setVisibility(View.VISIBLE);
            holder.cvTanggal3.setVisibility(View.VISIBLE);
            holder.cvTanggal4.setVisibility(View.VISIBLE);
        }

        holder.tvNamaBarang.setText(barang.getNama());
        Glide.with(_context)
                .load(barang.getFoto())
                .into(holder.ivBarang);

        holder.cvTanggal1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTanggal(holder, position, 1);
            }
        });
        holder.cvTanggal2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTanggal(holder, position, 2);
            }
        });
        holder.cvTanggal3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTanggal(holder, position, 3);
            }
        });
        holder.cvTanggal4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTanggal(holder, position, 4);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listBarangPerCompany.size();
    }

    private void setTanggal(final ViewHolder holder, final int position, final int lineId){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            final Calendar calendar = Calendar.getInstance();
            final int Day = calendar.get(Calendar.DAY_OF_MONTH);
            final int Month = calendar.get(Calendar.MONTH);
            final int Year = calendar.get(Calendar.YEAR);

            final DatePickerDialog dpd = new DatePickerDialog(_context, new DatePickerDialog.OnDateSetListener() {
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
                        Toast.makeText(_context, "Silahkan pilih tanggal pada hari kerja (Senin - Jumat).", Toast.LENGTH_LONG).show();
                    }else{
                        if (String.valueOf(month).length()==1) {
                            if (!tglLibur.equals("tdkLibur") && tglLibur.equals(String.valueOf(year + "-0" + (month + 1) + "-" + dayOfMonth))) {
                                Toast.makeText(_context, "Tanggal yang dipilih jatuh pada hari libur nasional.", Toast.LENGTH_LONG).show();
                            } else {
                                if (lineId==1){
                                    holder.tvTanggal1.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                                    String tanggal = String.valueOf(year) + "-" + String.valueOf((month + 1)) + "-" + String.valueOf(dayOfMonth);
                                    PermintaanKirimActivity.setTglKirim(position, tanggal);
                                    PermintaanKirimActivity.setLineId(position, lineId);
                                }else if (lineId==2){
                                    holder.tvTanggal2.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                                    String tanggal = String.valueOf(year) + "-" + String.valueOf((month + 1)) + "-" + String.valueOf(dayOfMonth);
                                    PermintaanKirimActivity.setTglKirim(position, tanggal);
                                    PermintaanKirimActivity.setLineId(position, lineId);
                                }else if (lineId==3){
                                    holder.tvTanggal3.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                                    String tanggal = String.valueOf(year) + "-" + String.valueOf((month + 1)) + "-" + String.valueOf(dayOfMonth);
                                    PermintaanKirimActivity.setTglKirim(position, tanggal);
                                    PermintaanKirimActivity.setLineId(position, lineId);
                                }else if (lineId==4){
                                    holder.tvTanggal4.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                                    String tanggal = String.valueOf(year) + "-" + String.valueOf((month + 1)) + "-" + String.valueOf(dayOfMonth);
                                    PermintaanKirimActivity.setTglKirim(position, tanggal);
                                    PermintaanKirimActivity.setLineId(position, lineId);
                                }
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
                                Toast.makeText(_context, "Tanggal yang dipilih jatuh pada hari libur nasional.", Toast.LENGTH_LONG).show();
                            } else {
                                if (lineId==1){
                                    holder.tvTanggal1.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                                    String tanggal = String.valueOf(year) + "-" + String.valueOf((month + 1)) + "-" + String.valueOf(dayOfMonth);
                                    PermintaanKirimActivity.setTglKirim(position, tanggal);
                                    PermintaanKirimActivity.setLineId(position, lineId);
                                }else if (lineId==2){
                                    holder.tvTanggal2.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                                    String tanggal = String.valueOf(year) + "-" + String.valueOf((month + 1)) + "-" + String.valueOf(dayOfMonth);
                                    PermintaanKirimActivity.setTglKirim(position, tanggal);
                                    PermintaanKirimActivity.setLineId(position, lineId);
                                }else if (lineId==3){
                                    holder.tvTanggal3.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                                    String tanggal = String.valueOf(year) + "-" + String.valueOf((month + 1)) + "-" + String.valueOf(dayOfMonth);
                                    PermintaanKirimActivity.setTglKirim(position, tanggal);
                                    PermintaanKirimActivity.setLineId(position, lineId);
                                }else if (lineId==4){
                                    holder.tvTanggal4.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                                    String tanggal = String.valueOf(year) + "-" + String.valueOf((month + 1)) + "-" + String.valueOf(dayOfMonth);
                                    PermintaanKirimActivity.setTglKirim(position, tanggal);
                                    PermintaanKirimActivity.setLineId(position, lineId);
                                }
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
}
