package com.icanstudioz.taxicustomer.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.icanstudioz.taxicustomer.R;
import com.icanstudioz.taxicustomer.Server.Server;
import com.icanstudioz.taxicustomer.acitivities.HomeActivity;
import com.icanstudioz.taxicustomer.custom.CheckConnection;
import com.icanstudioz.taxicustomer.fragement.AcceptedDetailFragment1;
import com.icanstudioz.taxicustomer.pojo.Offers;
import com.icanstudioz.taxicustomer.pojo.PendingRequestPojo;
import com.icanstudioz.taxicustomer.session.SessionManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by android on 8/3/17.
 */

public class OffersRequestAdapter extends RecyclerView.Adapter<OffersRequestAdapter.Holder> {
    List<Offers> list;
    Context context;
    boolean is_running = false;

    public OffersRequestAdapter(Context context, List<Offers> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.offer_request_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        final Offers pojo = list.get(position);
//        holder.from_add.setText(pojo.getPickup_adress());
//        holder.to_add.setText(pojo.getDrop_address());
//        holder.drivername.setText(pojo.getOfferMinValue() + " TL");
//        holder.dn.setText(pojo.getFullTotalW() + " KG / " + pojo.getFullTotalL() + " M");


        holder.dt.setText("TASIMACI ID: " + pojo.getDriver_id());
        holder.time.setText("ONERILEN: " + pojo.getAmount());
        holder.tvttt.setText(pojo.getTotal() + " TL");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullTrack_dialog(pojo);
            }
        });

        BookFont(holder, holder.f);
        BookFont(holder, holder.t);
        BookFont(holder, holder.dn);
        BookFont(holder, holder.dt);

        MediumFont(holder, holder.from_add);
        MediumFont(holder, holder.to_add);
        MediumFont(holder, holder.date);


    }

    public void fullTrack_dialog(Offers offers) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.approval_dialog);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;

        final TextView txt_calan = dialog.findViewById(R.id.txt_calan);
        final TextView txt_gitk_tarix = dialog.findViewById(R.id.txt_gitk_tarix);
        final TextView txt_droplocation = dialog.findViewById(R.id.txt_droplocation);
        final TextView txt_pickuplocation = dialog.findViewById(R.id.txt_pickuplocation);

        txt_pickuplocation.setText(offers.getPickup_adress());
        txt_droplocation.setText(offers.getDrop_address());
        txt_gitk_tarix.setText(offers.getPickupDate());

        AppCompatButton btn_change = (AppCompatButton) dialog.findViewById(R.id.set_pick_up);

        new CountDownTimer(1000 * 60 * 30, 1000) {

            public void onTick(long millisUntilFinished) {
                int munites = (int) (millisUntilFinished / 1000) / 60;
                int seconds = (int) (millisUntilFinished / 1000) % 60;

                txt_calan.setText(munites + " : " + seconds);
                if (seconds < 3 && !is_running) {
                    is_running = true;
                    updateTimer(offers);
                }
            }

            public void onFinish() {
                Toast.makeText(context, "Rider isn't accept it", Toast.LENGTH_SHORT).show();
            }

        }.start();

        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.cancel();
            }
        });
        dialog.show();

    }

    public void updateTimer(Offers offers) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String currentDateandTime = sdf.format(new Date());

        RequestParams params = new RequestParams();
        params.put("id", offers.getId());
        params.put("AcceptTime", currentDateandTime);

        Server.setHeader(SessionManager.getKEY());
        Server.post("api/driver/addAccept/format/json", params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {

                        Log.e("success", response.toString());

                    } else {

                        String error = response.getString("data");

                    }
                } catch (JSONException e) {
                    Log.d("catch", e.toString());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

                Log.e("fail", responseString);

            }

            @Override
            public void onFinish() {
                new CountDownTimer(1000 * 60, 1000) {

                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        is_running = true;
                    }

                }.start();
                super.onFinish();
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {


        TextView drivername, from_add, to_add, date, time, tvttt;
        TextView f, t, dn, dt, tiime, count;

        public Holder(View itemView) {
            super(itemView);

            f = (TextView) itemView.findViewById(R.id.from);
            t = (TextView) itemView.findViewById(R.id.to);

            dn = (TextView) itemView.findViewById(R.id.drivername);
            dt = (TextView) itemView.findViewById(R.id.datee);
            tiime = itemView.findViewById(R.id.tiime);
            count = itemView.findViewById(R.id.count);
            tvttt = itemView.findViewById(R.id.tvttt);

            drivername = (TextView) itemView.findViewById(R.id.txt_drivername);
            from_add = (TextView) itemView.findViewById(R.id.txt_from_add);
            to_add = (TextView) itemView.findViewById(R.id.txt_to_add);
            date = (TextView) itemView.findViewById(R.id.date);
            time = (TextView) itemView.findViewById(R.id.time);
        }
    }

    public void BookFont(Holder holder, TextView view1) {
        Typeface font1 = Typeface.createFromAsset(holder.itemView.getContext().getAssets(), "font/AvenirLTStd_Book.otf");
        view1.setTypeface(font1);
    }

    public void MediumFont(Holder holder, TextView view) {
        Typeface font = Typeface.createFromAsset(holder.itemView.getContext().getAssets(), "font/AvenirLTStd_Medium.otf");
        view.setTypeface(font);
    }
}
