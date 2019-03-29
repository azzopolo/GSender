package com.icanstudioz.taxicustomer.fragement;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.icanstudioz.taxicustomer.R;
import com.icanstudioz.taxicustomer.Server.Server;
import com.icanstudioz.taxicustomer.acitivities.HomeActivity;
import com.icanstudioz.taxicustomer.custom.GPSTracker;
import com.icanstudioz.taxicustomer.pojo.PendingRequestPojo;
import com.icanstudioz.taxicustomer.pojo.Tracking;
import com.icanstudioz.taxicustomer.session.SessionManager;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.thebrownarrow.permissionhelper.FragmentManagePermission;
import com.thebrownarrow.permissionhelper.PermissionUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import cz.msebera.android.httpclient.Header;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by android on 14/3/17.
 */

public class AcceptedDetailFragment1 extends FragmentManagePermission {
    AppCompatButton accept;
    TextView pickup_location, txt_gitk_tarix, drop_location, txt_volume,
            txt_end_time, txt_price, txt_offercount;

    String request = "";
    String permissions[] = {PermissionUtils.Manifest_ACCESS_FINE_LOCATION, PermissionUtils.Manifest_ACCESS_COARSE_LOCATION};

    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    private View view;
    private String pickup = "";
    private String drop = "";
    private String driver = "";
    private String basefare = "";
    private SwipeRefreshLayout swipeRefreshLayout;
    private String mobile = "";
    private String ride_id = "";
    private String user_id = "";
    private String driver_id = "";
    String id = "0";
    boolean is_update = false;

    private String paymnt_status = "";
    private String paymnt_mode = "";
    PendingRequestPojo pojo;

    GPSTracker gpsTracker;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.accepted_detail_fragmnet1, container, false);
        ((HomeActivity) getActivity()).fontToTitleBar(getString(R.string.passenger_info));
        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        BindView();

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("ride_id", ride_id);
                OffersRequestFragment detailFragment = new OffersRequestFragment();
                detailFragment.setArguments(bundle);
                ((HomeActivity) getActivity()).changeFragment(detailFragment,
                        ride_id + " Ihale Teklifleri");
            }
        });
    }

    public void AlertDialogCreate(String title, String message, final String status) {
        Drawable drawable = ContextCompat.getDrawable(getActivity(), R.mipmap.ic_warning_white_24dp);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, Color.RED);
        new AlertDialog.Builder(getActivity())
                .setIcon(drawable)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("cancel", null)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
//                        getInfo();
//                        CheckOffer();

                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }

    public void BindView() {
        gpsTracker = new GPSTracker(getActivity());
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        accept = (AppCompatButton) view.findViewById(R.id.btn_accept);
        pickup_location = (TextView) view.findViewById(R.id.txt_pickuplocation);
        drop_location = (TextView) view.findViewById(R.id.txt_droplocation);
        txt_volume = (TextView) view.findViewById(R.id.txt_volume);
        txt_end_time = (TextView) view.findViewById(R.id.txt_end_time);
        txt_gitk_tarix = view.findViewById(R.id.txt_gitk_tarix);
        txt_price = (TextView) view.findViewById(R.id.txt_price);
        txt_offercount = view.findViewById(R.id.txt_offercount);

        pickup_location.setSelected(true);
        drop_location.setSelected(true);
        Bundle bundle = getArguments();



        if (bundle != null) {
            pojo = (PendingRequestPojo) bundle.getSerializable("data");
            pickup = pojo.getPickup_adress();
            drop = pojo.getDrop_address();
            driver = pojo.getUser_name();
            basefare = pojo.getAmount();
            ride_id = pojo.getRide_id();
            user_id = pojo.getUser_id();
            driver_id = pojo.getDriver_id();
            mobile = pojo.getUser_mobile();
            paymnt_mode = pojo.getPayment_mode();

            if (pojo.getOffersCount() == null) accept.setVisibility(View.GONE);
            if (pojo.getOffersCount().equals("0")) accept.setVisibility(View.GONE);
            if (pojo.getOffersCount().equals("null")) accept.setVisibility(View.GONE);

            if (pickup != null) {
                pickup_location.setText(pickup);
            }
            if (drop != null) {
                drop_location.setText(drop);
            }

            String amount = pojo.getOfferMinValue();
            if (amount != null) {
                txt_price.setText(amount + " TL");
            }

            String volume = pojo.getFullTotalL() + " KG / " + pojo.getFullTotalW() + " M3";
            if (volume != null) {
                txt_volume.setText(volume);
            }

            String end_time = pojo.getDropListDate();
            if (end_time != null) {
                txt_end_time.setText(pojo.getDropListDate());
            }

            String cccount = pojo.getOffersCount();
            if (cccount != null) {
                txt_offercount.setText(cccount);
            }


            String pick_time = pojo.getPickupDate();
            if (pick_time != null) {
                txt_gitk_tarix.setText(pick_time);
            }



            if (paymnt_mode == null) {
                paymnt_mode = "";
            }
            if (ride_id != null) {

            } else {
                ride_id = "";
            }
            request = pojo.getStatus();

        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }




    public void startService() {
        Intent myIntent = new Intent(getActivity(), LocationServices.class);
        pendingIntent = PendingIntent.getService(getActivity(), 0, myIntent, 0);
        alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 60); // first time
        long frequency = 60 * 1000; // in ms
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), frequency, pendingIntent);
    }

    public Boolean GPSEnable() {
        GPSTracker gpsTracker = new GPSTracker(getActivity());
        if (gpsTracker.canGetLocation()) {
            return true;

        } else {
            gpsTracker.showSettingsAlert();
            return false;
        }


    }

    void isStarted() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Tracking/" + pojo.getRide_id());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.hasChildren()) {
                    Tracking tracking = dataSnapshot.getValue(Tracking.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void launchNavigation() {


        if (GPSEnable()) {

            try {
                String[] latlong = pojo.getPikup_location().split(",");
                double latitude = Double.parseDouble(latlong[0]);
                double longitude = Double.parseDouble(latlong[1]);
                String[] latlong1 = pojo.getDrop_locatoin().split(",");
                double latitude1 = Double.parseDouble(latlong1[0]);
                double longitude1 = Double.parseDouble(latlong1[1]);


// Create a NavigationViewOptions object to package everything together
                Point origin = Point.fromLngLat(longitude, latitude);
                Point destination = Point.fromLngLat(longitude1, latitude1);

                fetchRoute(origin, destination);
           /*     NavigationLauncherOptions.Builder navigationLauncherOptions = NavigationLauncherOptions.builder();
                navigationLauncherOptions.origin(origin);
                navigationLauncherOptions.destination(destination);
                navigationLauncherOptions.shouldSimulateRoute(false);
                navigationLauncherOptions.enableOffRouteDetection(true);
                navigationLauncherOptions.snapToRoute(true);
                                *//*NavigationLauncher.startNavigation(getActivity(), o, d,
                                        null, false);*//*
                NavigationLauncher.startNavigation(getActivity(), navigationLauncherOptions.build());
           */
            } catch (Exception e) {
                Toast.makeText(getActivity(), e.toString() + " ", Toast.LENGTH_SHORT).show();
            }
        } else {
            gpsTracker.showSettingsAlert();
        }
    }

    private void fetchRoute(Point origin, Point destination) {
        NavigationRoute.builder(getActivity())
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        DirectionsRoute directionsRoute = response.body().routes().get(0);
                        startNavigation(directionsRoute);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {

                    }
                });
    }

    private void startNavigation(DirectionsRoute directionsRoute) {
        NavigationLauncherOptions.Builder navigationLauncherOptions = NavigationLauncherOptions.builder();
        navigationLauncherOptions.shouldSimulateRoute(false);
        navigationLauncherOptions.enableOffRouteDetection(true);
        navigationLauncherOptions.snapToRoute(true);
        navigationLauncherOptions.directionsRoute(directionsRoute);
        NavigationLauncher.startNavigation(getActivity(), navigationLauncherOptions.build());


    }

    private void gotoMap() {
        if (GPSEnable()) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("data", pojo);
            MapView mapView = new MapView();
            mapView.setArguments(bundle);
            ((HomeActivity) getActivity()).changeFragment(mapView, "MapView");
        } else {
            gpsTracker.showSettingsAlert();
        }
    }


}
