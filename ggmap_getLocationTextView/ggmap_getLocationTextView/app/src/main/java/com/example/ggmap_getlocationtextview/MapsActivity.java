package com.example.ggmap_getlocationtextview;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback  {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private final int locationRequestCode = 1;
    private double currentLatitude = 0.0;
    private double currentLongtitude = 0.0;
    SearchView searchViewLocation;
    ImageButton btn_location;
    Marker markerSearch;
    ImageButton ibtn_clean;
    ImageButton ibtn_report;
    ImageButton ibtn_rank;
    ImageButton ibtn_account;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        checkPermisson();

        getLocation("http://192.168.1.3/androidwebservice/wasteLocation.php");
        reflect();
        searchLocation();

        reportWaste();
        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                location();
            }
        });

        /*btn_direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequestDirection();
            }
        });*/
    }



    private void reportWaste() {
        ibtn_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Geocoder geocoder = new Geocoder(getApplicationContext());
                List<Address> addressList = null;
                try {
                    addressList = geocoder.getFromLocation(currentLatitude, currentLongtitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String addressWaste = addressList.get(0).getAddressLine(0);

                Intent intent = new Intent(MapsActivity.this, reportActivity.class);
                intent.putExtra("wasteLocation_latitude",currentLatitude);
                intent.putExtra("wasteLocation_longtitude",currentLongtitude);
                intent.putExtra("wasteLocation_address",addressWaste);

                startActivity(intent);

            }
        });
    }

    //check user's permisson ( FINE_LOCATION ; COARSE_LOCATION )
    private void checkPermisson() {
        //If user don't have this this permisson
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Call onRequestPermissionsResult to request user's permisson
            ActivityCompat.requestPermissions(this,
                    //Create a String array include 2 permission (FINE_LOCATION ; COARSE_LOCATION)
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}
                    , locationRequestCode);
        } else {
            //If user granted use MAP
            //Get current location
            mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        currentLatitude = location.getLatitude();
                        currentLongtitude = location.getLongitude();

                        //Call onReadyMap()
                        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.map);
                        mapFragment.getMapAsync(MapsActivity.this);
                    }
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case locationRequestCode: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermisson();
                }
            }
        }
    }

    private void location(){
        LatLng latLng = new LatLng(currentLatitude, currentLongtitude);
        mMap.setMyLocationEnabled(true);
        //Set blue point  == true
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        //Set location at LatLng ( user's current location)
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        //Zoom in street
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng latLng = new LatLng(currentLatitude, currentLongtitude);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            return;
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
        }

        //
        mMap.setMyLocationEnabled(true);
        //Set blue point  == true
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        //Set location at LatLng ( user's current location)
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        //Zoom in street
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));

        //
        joinDialog();


    }

    //get waste Marker from database into MAP
    private void getLocation(String url){
        //Handle by Volley
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //Create
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject object = response.getJSONObject(i);

                            double wasteLocation_latitude = Double.parseDouble(object.getString("wasteLocation_latitude"));
                            double wasteLocation_longtitude = Double.parseDouble(object.getString("wasteLocation_longtitude"));
                            LatLng latLng = new LatLng(wasteLocation_latitude, wasteLocation_longtitude);
                            Geocoder geocoder = new Geocoder(getApplicationContext());
                            try {
                                List<Address> addressList = geocoder.getFromLocation(wasteLocation_latitude, wasteLocation_longtitude, 1);
                                String str = addressList.get(0).getAddressLine(0);
                                mMap.addMarker(new MarkerOptions().position(latLng).title(str));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MapsActivity.this,error.toString(),Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void reflect(){
        searchViewLocation = (SearchView) findViewById(R.id.svLocation);
        btn_location = (ImageButton) findViewById(R.id.btn_location);
        ibtn_report = (ImageButton) findViewById(R.id.ibtn_report);
        ibtn_clean = (ImageButton) findViewById(R.id.ibtn_clean);
        ibtn_rank = (ImageButton) findViewById(R.id.ibtn_rank);
        ibtn_account = (ImageButton) findViewById(R.id.ibtn_account);

    }

    private void searchLocation(){

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        searchViewLocation.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                Geocoder geocoder = new Geocoder(getApplicationContext());
                try {
                    //delete the old marker
                    if (markerSearch != null){ //nếu không kiểm tra đk này thì lúc đầu chưa có sẽ xóa luôn marker => k có marker này để mark nữa
                        markerSearch.remove();
                    }
                    //
                    String location = searchViewLocation.getQuery().toString();
                    List <Address> addressList = null;
                    if(location != null || !location.equals("")) {
                        try {
                            addressList = geocoder.getFromLocationName(location,1);
                        } catch (Exception e) {
                        }
                        if (addressList.size() > 0) {
                            Address address = addressList.get(0);
                            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                            //getThroughfare : get street name
                            markerSearch = mMap.addMarker(new MarkerOptions().position(latLng).title(address.getAddressLine(0)));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                        } else {
                            List <Address> addresses = geocoder.getFromLocation(currentLatitude, currentLongtitude, 1);
                            String str = addresses.get(0).getLocality();
                            str = str + " " + addresses.get(0).getAdminArea();
                            str = str + " " + addresses.get(0).getCountryName();

                            location = location.concat(",");
                            location = location + " " + str;

                            addressList = geocoder.getFromLocationName(location, 1);
                            Address address = addressList.get(0);

                            if(address.getThoroughfare() != null){
                                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                                markerSearch = mMap.addMarker(new MarkerOptions().position(latLng).title(address.getAddressLine(0)));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                            }else{
                                Toast.makeText(MapsActivity.this, "Don't have this address", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } catch (Exception e) {
                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        mapFragment.getMapAsync(MapsActivity.this); //when you already implement OnMapReadyCallback in your fragment
    }
  private void joinDialog(){
      //set event for marker to display join dialog
      mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
          @Override
          public boolean onMarkerClick(final Marker m) {
              Geocoder geocoder = new Geocoder(getApplicationContext());
              List<Address> addressList = null;
              try {
                  addressList = geocoder.getFromLocation(m.getPosition().latitude,m.getPosition().longitude, 1);
              } catch (IOException e) {
                  e.printStackTrace();
              }

              String str = addressList.get(0).getAddressLine(0);
              Bundle bundle = new Bundle();
              BottomSheetDialogFragment dialog = new joinDialog();
              bundle.putString("address",str);
              bundle.putDouble("currentLatitude",currentLatitude);
              bundle.putDouble("currentLongtitude",currentLongtitude);
              bundle.putDouble("wasteLatitude",m.getPosition().latitude);
              bundle.putDouble("wasteLongtitude",m.getPosition().longitude);
              dialog.setArguments(bundle);
              dialog.show(getSupportFragmentManager(),"example");
              return true;
          }
      });

  }
}
