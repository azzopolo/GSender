package com.icanstudioz.taxicustomer.fragement;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.Gravity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.model.Direction;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.icanstudioz.taxicustomer.R;
import com.icanstudioz.taxicustomer.Server.Server;
import com.icanstudioz.taxicustomer.acitivities.HomeActivity;
import com.icanstudioz.taxicustomer.custom.CheckConnection;
import com.icanstudioz.taxicustomer.custom.GPSTracker;
import com.icanstudioz.taxicustomer.pojo.NearbyData;
import com.icanstudioz.taxicustomer.pojo.Pass;
import com.icanstudioz.taxicustomer.session.SessionManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.thebrownarrow.permissionhelper.FragmentManagePermission;
import com.thebrownarrow.permissionhelper.PermissionResult;
import com.thebrownarrow.permissionhelper.PermissionUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;


/**
 * Created by android on 7/3/17.
 */

public class HomeFragment extends FragmentManagePermission implements OnMapReadyCallback, DirectionCallback, Animation.AnimationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, View.OnClickListener {
    private static final String TAG = "HomeFragment";
    private String driver_id;
    private String cost;
    private String unit;
    private int PLACE_PICKER_REQUEST = 7896;
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1234;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Double currentLatitude;
    private Double currentLongitude;
    private View rootView;
    Boolean flag = false;
    GoogleMap myMap;
    ImageView current_location, clear;
    // PlaceDetectionClient mPlaceDetectionClient;
    private RelativeLayout header, footer;
    Animation animFadeIn, animFadeOut;

    TextView pickup_location, drop_location, tvfull_truck, tv_llt_truck,
            tv_pick_up_date, tv_truck_type, tv_drop, tv_listing, tv_drop_person, tv_pick_up_person;

    EditText et_note;

    RelativeLayout relative_drop, relative_drop_date, relative_last_date, relative_note;
    RelativeLayout linear_pickup, linear_pickup_date, linear_truck_type;

    TextView txt_vehicleinfo, rate, txt_info, txt_cost, txt_color, txt_address, request_ride;
    LinearLayout linear_request;

    String permissionAsk[] = {PermissionUtils.Manifest_CAMERA, PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE, PermissionUtils.Manifest_READ_EXTERNAL_STORAGE, PermissionUtils.Manifest_ACCESS_FINE_LOCATION, PermissionUtils.Manifest_ACCESS_COARSE_LOCATION};
    private String drivername;
    MapView mMapView;
    Pass pass;
    Place pickup, drop;
    ProgressBar progressBar;
    private PlacesClient placesClient;

    String pickUpFullName, pickUpPhone, pickUpAddress, truckType;
    String dropFullName, dropPhone, dropAddress;
    String TotalW, TotalL, place_count, strWidth, strLength, strHeight, strWeight;
    String pickUpDate, dropDate, listingLastDate;
    String strNote;
    private int mYear, mMonth, mDay, mHour, mMinute;

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //  MapsInitializer.initialize(this.getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        try {
            rootView = inflater.inflate(R.layout.home_fragment, container, false);
            ((HomeActivity) getActivity()).fontToTitleBar(getString(R.string.home));
            bindView(savedInstanceState);
            if (!CheckConnection.haveNetworkConnection(getActivity())) {
                Toast.makeText(getActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                askCompactPermissions(permissionAsk, new PermissionResult() {
                    @Override
                    public void permissionGranted() {
                        if (!GPSEnable()) {
                            tunonGps();
                        } else {
                            getcurrentlocation();
                        }
                    }

                    @Override
                    public void permissionDenied() {

                    }

                    @Override
                    public void permissionForeverDenied() {

                        openSettingsApp(getActivity());
                    }
                });

            } else {
                if (!GPSEnable()) {
                    tunonGps();
                } else {
                    getcurrentlocation();
                }

            }


            clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (header.getVisibility() == View.VISIBLE && footer.getVisibility() == View.VISIBLE) {
                        header.startAnimation(animFadeOut);
                        footer.startAnimation(animFadeOut);
//                        header.setVisibility(View.GONE);
//                        footer.setVisibility(View.GONE);
                    }
                }
            });
            rootView.findViewById(R.id.request_rides).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (CheckConnection.haveNetworkConnection(getActivity())) {
                        if (pickup_location.getText().toString().trim().equals("")) {
                            Toast.makeText(getActivity(), getString(R.string.select_pickup_location), Toast.LENGTH_LONG).show();
                        } else if (drop_location.getText().toString().trim().equals("")) {
                            Toast.makeText(getActivity(), getString(R.string.select_drop_location), Toast.LENGTH_LONG).show();
                        } else if (pickup == null || drop == null) {
                            Toast.makeText(getActivity(), getString(R.string.invalid_location), Toast.LENGTH_LONG).show();
//                        } else if (driver_id == null || drivername == null) {
//                            Toast.makeText(getActivity(), getString(R.string.select_driver), Toast.LENGTH_LONG).show();
//                        } else if (cost == null || unit == null) {
//                            Toast.makeText(getActivity(), getString(R.string.invalid_fare), Toast.LENGTH_SHORT).show();
                        }else if (tv_pick_up_date.getText().toString().trim().equals("")) {
                            Toast.makeText(getActivity(), "select pick up date", Toast.LENGTH_LONG).show();
                        } else if (tv_truck_type.getText().toString().trim().equals("")) {
                            Toast.makeText(getActivity(), "select truck type", Toast.LENGTH_LONG).show();
                        } else if (tvfull_truck.getText().toString().trim().equals("")) {
                            Toast.makeText(getActivity(), "select full truck", Toast.LENGTH_LONG).show();
                        } else if (tv_llt_truck.getText().toString().trim().equals("")) {
                            Toast.makeText(getActivity(), "select pick up datellt truck", Toast.LENGTH_LONG).show();
                        }  else if (tv_drop.getText().toString().trim().equals("")) {
                            Toast.makeText(getActivity(), "select drop date", Toast.LENGTH_LONG).show();
                        }  else if (tv_listing.getText().toString().trim().equals("")) {
                            Toast.makeText(getActivity(), "select listing last date", Toast.LENGTH_LONG).show();
                        }  else if (et_note.getText().toString().trim().equals("")) {
                            Toast.makeText(getActivity(), "input note", Toast.LENGTH_LONG).show();
                        }

                        else {
                            Bundle bundle = new Bundle();
                            pass.setFromPlace(pickup);
                            pass.setToPlace(drop);

                            pass.setPick_up_date(pickUpDate);
                            pass.setTruck_type(truckType);
                            pass.setPickupFullName(pickUpFullName);
                            pass.setPickupPhone(pickUpPhone);
                            pass.setPickupAdress(pickUpAddress);

                            pass.setDrop_date(dropDate);
                            pass.setLiting_date(listingLastDate);
                            pass.setDropFullName(dropFullName);
                            pass.setDropPhone(dropPhone);
                            pass.setDropAddress(dropAddress);

                            pass.setFullTotalW(TotalW);
                            pass.setFullTotalL(TotalL);

                            pass.setLLTPieceCount(place_count);
                            pass.setLLTWight(strWidth);
                            pass.setLLTHeight(strHeight);
                            pass.setLLTLenght(strLength);
                            pass.setLLTWeight(strWeight);

                            pass.setNote(et_note.getText().toString());

                            pass.setDriverId("");
                            pass.setFare("12");
                            pass.setDriverName("");
                            bundle.putSerializable("data", pass);
                            RequestFragment fragobj = new RequestFragment();
                            fragobj.setArguments(bundle);
                            ((HomeActivity) getActivity()).changeFragment(fragobj, getString(R.string.request_ride));
                        }
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();

                    }
                }
            });

            tv_drop_person.setOnClickListener(this);
            tv_pick_up_person.setOnClickListener(this);
            linear_pickup_date.setOnClickListener(this);
            linear_request.setOnClickListener(this);
            linear_truck_type.setOnClickListener(this);
            relative_drop_date.setOnClickListener(this);
            relative_last_date.setOnClickListener(this);
            tvfull_truck.setOnClickListener(this);
            tv_llt_truck.setOnClickListener(this);
            tv_pick_up_date.setOnClickListener(this);
            tv_truck_type.setOnClickListener(this);

            pickup_location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /* Intent intent =
                             new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                     .build(getActivity());
                     startActivityForResult(intent, PLACE_PICKER_REQUEST);*/


                    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

// Start the autocomplete intent.
                    Intent intent = new Autocomplete.IntentBuilder(
                            AutocompleteActivityMode.FULLSCREEN, fields)
                            .build(getActivity());
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);
                }
            });

            drop_location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*  Intent intent =
                              new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                      .build(getActivity());
                      startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);

                  */

                    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

// Start the autocomplete intent.
                    Intent intent = new Autocomplete.IntentBuilder(
                            AutocompleteActivityMode.FULLSCREEN, fields)
                            .build(getActivity());
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                }
            });

        } catch (InflateException e) {

        }

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("result");
                getcurrentlocation();
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        } else if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                pickup = Autocomplete.getPlaceFromIntent(data);

                pickup_location.setText(pickup.getAddress());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.e(TAG, status.toString());
                Toast.makeText(getActivity(), status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                drop = Autocomplete.getPlaceFromIntent(data);
                drop_location.setText(drop.getAddress());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(getActivity(), status.getStatusMessage(), Toast.LENGTH_LONG).show();

            }
        }
    }

    public void pick_up_dialog(boolean is_pick_up, String title) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.pick_up_dialog);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;

        TextView tle = (TextView) dialog.findViewById(R.id.title);
        final EditText input_address = (EditText) dialog.findViewById(R.id.input_address);
        final EditText input_full_name = (EditText) dialog.findViewById(R.id.input_full_name);
        final EditText input_phone = (EditText) dialog.findViewById(R.id.input_phone);

        AppCompatButton btn_change = (AppCompatButton) dialog.findViewById(R.id.set_pick_up);

        tle.setText(title);
        btn_change.setText(getString(R.string.change));


        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getActivity().getCurrentFocus();
                if (view != null) {
                    CheckConnection.hideKeyboard(getActivity(), view);
                }

                if (is_pick_up) {
                    pickUpFullName = input_full_name.getText().toString().trim();
                    pickUpAddress = input_address.getText().toString().trim();
                    pickUpPhone = input_phone.getText().toString().trim();

                } else {
                    dropFullName = input_full_name.getText().toString().trim();
                    dropAddress = input_address.getText().toString().trim();
                    dropPhone = input_phone.getText().toString().trim();

                }
                dialog.cancel();
            }
        });
        dialog.show();

    }

    public void fullTrack_dialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.full_track_dialog);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;

        final EditText input_total_lenght = (EditText) dialog.findViewById(R.id.input_total_lenght);
        final EditText input_total_w = (EditText) dialog.findViewById(R.id.input_total_w);

        AppCompatButton btn_change = (AppCompatButton) dialog.findViewById(R.id.set_pick_up);

        btn_change.setText(getString(R.string.change));

        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getActivity().getCurrentFocus();
                if (view != null) {
                    CheckConnection.hideKeyboard(getActivity(), view);
                }

                TotalW = input_total_w.getText().toString().trim();
                TotalL = input_total_lenght.getText().toString().trim();

                tvfull_truck.setText(TotalW + " " + TotalL);
                dialog.cancel();
            }
        });
        dialog.show();

    }

    public void date_pick_dialog(int pos){
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        String str_date = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        switch (pos){
                            case 0:
                                pickUpDate = str_date;
                                tv_pick_up_date.setText(str_date);
                                break;
                            case 1:
                                dropDate = str_date;
                                tv_drop.setText(str_date);
                                break;
                            case 2:
                                listingLastDate = str_date;
                                tv_listing.setText(str_date);
                                break;
                        }
                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }

    public void LLTTrack_dialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.llt_track_dialog);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;

        final EditText input_piece_count = (EditText) dialog.findViewById(R.id.input_piece_count);
        final EditText input_wight = (EditText) dialog.findViewById(R.id.input_wight);
        final EditText input_lenght = (EditText) dialog.findViewById(R.id.input_lenght);
        final EditText input_height = (EditText) dialog.findViewById(R.id.input_height);
        final EditText input_weight = (EditText) dialog.findViewById(R.id.input_weight);

        AppCompatButton btn_change = (AppCompatButton) dialog.findViewById(R.id.set_pick_up);

        btn_change.setText(getString(R.string.change));

        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getActivity().getCurrentFocus();
                if (view != null) {
                    CheckConnection.hideKeyboard(getActivity(), view);
                }

                strLength = input_lenght.getText().toString().trim();
                strHeight = input_height.getText().toString().trim();
                strWeight = input_weight.getText().toString().trim();
                strWidth = input_wight.getText().toString().trim();
                place_count = input_piece_count.getText().toString().trim();

                tv_llt_truck.setText(strLength + " " + strHeight);
                dialog.cancel();
            }
        });
        dialog.show();

    }


    @Override
    public void onPause() {
        super.onPause();
        if (mMapView != null) {
            mMapView.onPause();
        }
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                mGoogleApiClient.disconnect();
            }
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mMapView != null) {
            mMapView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMapView != null) {
            mMapView.onDestroy();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMapView != null) {
            mMapView.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mMapView != null) {
            mMapView.onLowMemory();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mMapView != null) {

            mMapView.onStart();
            if (currentLatitude != null && !currentLatitude.equals(0.0) && currentLongitude != null && !currentLongitude.equals(0.0)) {
                NeaBy(String.valueOf(currentLatitude), String.valueOf(currentLongitude));
            }
        }
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMapView != null) {

            mMapView.onResume();

        }
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    public void multipleMarker(List<NearbyData> list) {
        if (list != null) {
            for (NearbyData location : list) {
                Double latitude = null;
                Double longitude = null;
                try {
                    latitude = Double.valueOf(location.getLatitude());
                    longitude = Double.valueOf(location.getLongitude());

                    Marker marker = myMap.addMarker(new MarkerOptions()
                            .position(new LatLng(latitude, longitude))
                            .title(location.getName())
                            .snippet(location.getVehicle_info()));
                    marker.setTag(location);
                } catch (NumberFormatException e) {

                }

                CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude, currentLongitude), 14);
                myMap.animateCamera(camera);
            }
        }

    }


    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {


    }

    @Override
    public void onDirectionFailure(Throwable t) {

    }


    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    public void bindView(Bundle savedInstanceState) {
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        MapsInitializer.initialize(this.getActivity());
        current_location = (ImageView) rootView.findViewById(R.id.current_location);
        clear = (ImageView) rootView.findViewById(R.id.clear);
        txt_vehicleinfo = (TextView) rootView.findViewById(R.id.txt_vehicleinfo);
        rate = (TextView) rootView.findViewById(R.id.rate);

        txt_info = (TextView) rootView.findViewById(R.id.txt_info);
        txt_address = (TextView) rootView.findViewById(R.id.txt_addresss);
        request_ride = (TextView) rootView.findViewById(R.id.request_rides);
        txt_color = (TextView) rootView.findViewById(R.id.txt_color);
        txt_cost = (TextView) rootView.findViewById(R.id.txt_cost);
        mMapView = (MapView) rootView.findViewById(R.id.mapview);
        linear_request = (LinearLayout) rootView.findViewById(R.id.linear_request);
        header = (RelativeLayout) rootView.findViewById(R.id.header);
        footer = (RelativeLayout) rootView.findViewById(R.id.footer);
        pickup_location = (TextView) rootView.findViewById(R.id.pickup_location);
        drop_location = (TextView) rootView.findViewById(R.id.drop_location);
        linear_pickup = (RelativeLayout) rootView.findViewById(R.id.linear_pickup);
        relative_drop = (RelativeLayout) rootView.findViewById(R.id.relative_drop);

        /*mPlaceDetectionClient = Places.getPlaceDetectionClient(getActivity(), null);*/

        tv_truck_type = rootView.findViewById(R.id.tv_truck_type);
        tvfull_truck = rootView.findViewById(R.id.tvfull_truck);
        tv_llt_truck = rootView.findViewById(R.id.tv_llt_truck);
        tv_pick_up_date = rootView.findViewById(R.id.tv_pick_up_date);
        tv_drop = rootView.findViewById(R.id.tv_drop);
        tv_listing = rootView.findViewById(R.id.tv_listing);
        et_note = rootView.findViewById(R.id.et_note);
        tv_drop_person = rootView.findViewById(R.id.tv_drop_person);
        tv_pick_up_person = rootView.findViewById(R.id.tv_pick_up_person);

        relative_drop_date = rootView.findViewById(R.id.relative_drop_date);
        relative_last_date = rootView.findViewById(R.id.relative_last_date);
        relative_note = rootView.findViewById(R.id.relative_note);
        linear_pickup_date = rootView.findViewById(R.id.linear_pickup_date);
        linear_truck_type = rootView.findViewById(R.id.linear_truck_type);


        mMapView.getMapAsync(this);
        mMapView.onCreate(savedInstanceState);
        Places.initialize(getApplicationContext(), getString(R.string.google_android_map_api_key));
        pass = new Pass();
        // load animations
        animFadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
        animFadeOut = AnimationUtils.loadAnimation(getActivity(),
                R.anim.fade_out);
        animFadeIn.setAnimationListener(this);
        animFadeOut.setAnimationListener(this);
        applyfonts();
        placesClient = Places.createClient(getActivity());


        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drop_location.setText("");
                if (header.getVisibility() == View.VISIBLE && footer.getVisibility() == View.VISIBLE) {
                    header.startAnimation(animFadeOut);
                    footer.startAnimation(animFadeOut);
//                    header.setVisibility(View.GONE);
//                    footer.setVisibility(View.GONE);
                }
            }
        });

        current_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    askCompactPermissions(permissionAsk, new PermissionResult() {
                        @Override
                        public void permissionGranted() {
                            if (pickup_location.getText().toString().trim().equals("")) {
                                setCurrentLocation();
                            } else {
                                pickup_location.setText("");
                                current_location.setColorFilter(ContextCompat.getColor(getActivity(), R.color.black));
                            }
                        }

                        @Override
                        public void permissionDenied() {

                        }

                        @Override
                        public void permissionForeverDenied() {
                            Snackbar.make(rootView, getString(R.string.allow_permission), Snackbar.LENGTH_LONG).show();
                            openSettingsApp(getActivity());

                        }
                    });
                } else {
                    if (!GPSEnable()) {
                        tunonGps();
                    } else {
                        if (pickup_location.getText().toString().trim().equals("")) {
                            setCurrentLocation();
                        } else {
                            pickup_location.setText("");
                            current_location.setColorFilter(ContextCompat.getColor(getActivity(), R.color.black));
                        }

                    }

                }
            }
        });

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        int result = ContextCompat.checkSelfPermission(getActivity(), ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            android.location.Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            } else {
                //If everything went fine lets get latitude and longitude
                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();
                if (!currentLatitude.equals(0.0) && !currentLongitude.equals(0.0)) {
                    if (!flag) {
                        NeaBy(String.valueOf(currentLatitude), String.valueOf(currentLongitude));
                    }
                } else {

                    Toast.makeText(getActivity(), getString(R.string.couldnt_get_location), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            askCompactPermissions(permissionAsk, new PermissionResult() {
                @Override
                public void permissionGranted() {

                }

                @Override
                public void permissionDenied() {
                }

                @Override
                public void permissionForeverDenied() {
                    Snackbar.make(rootView, getString(R.string.allow_permission), Snackbar.LENGTH_LONG).show();
                    openSettingsApp(getActivity());
                }
            });

        }


    }

    private void setCurrentLocation() {
        if (!GPSEnable()) {
            tunonGps();

        } else {
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                try {
                    /*@SuppressLint("MissingPermission") Task<PlaceLikelihoodBufferResponse> placeResult = mPlaceDetectionClient.getCurrentPlace(null);
                    placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                            try {
                                if (task.isSuccessful()) {
                                    PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
                                    for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                                        pickup = placeLikelihood.getPlace().freeze();
                                        pickup_location.setText(placeLikelihood.getPlace().getAddress());
                                        current_location.setColorFilter(ContextCompat.getColor(getActivity(), R.color.current_lolcation));

                                    }
                                    likelyPlaces.release();
                                }
                            } catch (Exception e) {

                            }

                        }
                    });*/

                    // Use fields to define the data types to return.
                    List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

// Use the builder to create a FindCurrentPlaceRequest.
                    FindCurrentPlaceRequest request =
                            FindCurrentPlaceRequest.builder(placeFields).build();

// Call findCurrentPlace and handle the response (first check that the user has granted permission).
                    if (ContextCompat.checkSelfPermission(getActivity(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
                        placeResponse.addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FindCurrentPlaceResponse response = task.getResult();
                                /*for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                                    Log.i(TAG, String.format("Place '%s' has likelihood: %f",
                                            placeLikelihood.getPlace().getName(),
                                            placeLikelihood.getLikelihood()));
                                    pickup = placeLikelihood.getPlace();
                                    pickup_location.setText(placeLikelihood.getPlace().getAddress());
                                    current_location.setColorFilter(ContextCompat.getColor(getActivity(), R.color.current_lolcation));

                                }
*/
                                if (response != null && response.getPlaceLikelihoods() != null) {
                                    PlaceLikelihood placeLikelihood = response.getPlaceLikelihoods().get(0);
                                    pickup = placeLikelihood.getPlace();
                                    pickup_location.setText(placeLikelihood.getPlace().getAddress());
                                    current_location.setColorFilter(ContextCompat.getColor(getActivity(), R.color.current_lolcation));

                                }
                            } else {
                                Exception exception = task.getException();
                                if (exception instanceof ApiException) {
                                    ApiException apiException = (ApiException) exception;
                                    Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                                }
                            }
                        });
                    }
                } catch (Exception e) {

                }


            }
        }


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {

                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
        }

    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        if (location != null) {
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();
        }
    }


    public void applyfonts() {
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "font/AvenirLTStd_Medium.otf");
        Typeface font1 = Typeface.createFromAsset(getActivity().getAssets(), "font/AvenirLTStd_Book.otf");
        pickup_location.setTypeface(font);
        drop_location.setTypeface(font);
        txt_vehicleinfo.setTypeface(font1);
        rate.setTypeface(font1);

        txt_color.setTypeface(font);
        txt_address.setTypeface(font);
        request_ride.setTypeface(font1);


    }

    public void NeaBy(String latitude, String longitude) {
        flag = true;
        RequestParams params = new RequestParams();
        params.put("lat", latitude);
        params.put("long", longitude);
        Server.setHeader(SessionManager.getKEY());
        Server.get("api/user/nearby/format/json", params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                progressBar.setVisibility(View.VISIBLE);

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {
                        Gson gson = new GsonBuilder().create();
                        List<NearbyData> list = gson.fromJson(response.getJSONArray("data").toString(), new TypeToken<List<NearbyData>>() {

                        }.getType());

                        multipleMarker(list);

                        cost = response.getJSONObject("fair").getString("cost");
                        unit = response.getJSONObject("fair").getString("unit");

                        SessionManager.setUnit(unit);
                        SessionManager.setCost(cost);

                    }
                } catch (JSONException e) {

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (getActivity() != null) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }


    public void getcurrentlocation() {

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                // The next two lines tell the new client that “this” current class will handle connection stuff
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                //fourth line adds the LocationServices API endpoint from GooglePlayServices
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);
    }

    public void tunonGps() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(LocationServices.API).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            mGoogleApiClient.connect();
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(30 * 1000);
            mLocationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(mLocationRequest);

            // **************************
            builder.setAlwaysShow(true); // this is the key ingredient
            // **************************

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                    .checkLocationSettings(mGoogleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result
                            .getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All location settings are satisfied. The client can
                            // initialize location
                            // requests here.
                            getcurrentlocation();
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be
                            // fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling
                                // startResolutionForResult(),
                                // and setting the result in onActivityResult().
                                status.startResolutionForResult(getActivity(), 1000);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have
                            // no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        }

    }

    public Boolean GPSEnable() {
        GPSTracker gpsTracker = new GPSTracker(getActivity());
        if (gpsTracker.canGetLocation()) {
            return true;

        } else {
            return false;
        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
        myMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {

                return null;
            }

            @Override
            public View getInfoContents(final Marker marker) {

                View v = getActivity().getLayoutInflater().inflate(R.layout.view_custom_marker, null);

                LatLng latLng = marker.getPosition();
                TextView title = (TextView) v.findViewById(R.id.t);
                TextView t1 = (TextView) v.findViewById(R.id.t1);
                TextView t2 = (TextView) v.findViewById(R.id.t2);
                ImageView imageView = (ImageView) v.findViewById(R.id.profile_image);
                Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "font/AvenirLTStd_Medium.otf");
                t1.setTypeface(font);
                t2.setTypeface(font);
                String name = marker.getTitle();
                title.setText(name);
                String info = marker.getSnippet();
                t1.setText(info);

                NearbyData nearbyData = (NearbyData) marker.getTag();
                if (nearbyData != null) {
                    pass.setVehicleName(nearbyData.getVehicle_info());
                    txt_info.setText(nearbyData.getVehicle_info());
                    txt_address.setText("");
                    driver_id = nearbyData.getUser_id();
                    drivername = marker.getTitle();
                    t2.setVisibility(View.VISIBLE);
                } else {
                    t2.setVisibility(View.GONE);
                }
                txt_cost.setText(cost + "  " + unit);
                txt_address.setText(getAdd(Double.valueOf(nearbyData.getLatitude()), Double.valueOf(nearbyData.getLongitude())) + " ");


                return v;

            }
        });

        myMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                NearbyData nearbyData = (NearbyData) marker.getTag();
                if (nearbyData != null) {
                    driver_id = nearbyData.getUser_id();
                    drivername = marker.getTitle();
                }


                if (header.getVisibility() == View.VISIBLE && footer.getVisibility() == View.VISIBLE) {
                    header.startAnimation(animFadeOut);
                    footer.startAnimation(animFadeOut);
//                    header.setVisibility(View.GONE);
//                    footer.setVisibility(View.GONE);
                } else {

                    header.setVisibility(View.VISIBLE);
                    footer.setVisibility(View.VISIBLE);
                    header.startAnimation(animFadeIn);
                    footer.startAnimation(animFadeIn);
                }

            }
        });

        if (myMap != null) {
            tunonGps();
        }

    }

    private String getAdd(double latitude, double longitude) {
        String finalAddress = null;
        try {

            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(getActivity(), Locale.getDefault());
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            finalAddress = address + ", " + city + "," + state + "," + country;


        } catch (Exception e) {

        }
        return finalAddress;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_drop_person:
                pick_up_dialog(false, "Drop person");
                break;
            case R.id.tv_pick_up_person:
                pick_up_dialog(true, "Pick up person");
                break;

            case R.id.tvfull_truck:
                fullTrack_dialog();
                break;

            case R.id.tv_llt_truck:
                LLTTrack_dialog();
                break;

            case R.id.linear_pickup_date:
            case R.id.tv_pick_up_date:
                date_pick_dialog(0);
                break;

            case R.id.relative_last_date:
                date_pick_dialog(2);
                break;

            case R.id.relative_drop_date:
                date_pick_dialog(1);
                break;

            case R.id.linear_truck_type:
            case R.id.tv_truck_type:
                PopupMenu popup = new PopupMenu(getActivity(), linear_truck_type);
                popup.getMenuInflater().inflate(R.menu.truck_type, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        truckType = item.getTitle().toString();
                        tv_truck_type.setText(truckType);
                        return true;
                    }
                });
                popup.show();
                break;
        }
    }
}


