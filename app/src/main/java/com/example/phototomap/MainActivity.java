package com.example.phototomap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.phototomap.database.DatabaseHandler;
import com.example.phototomap.model.LocationModel;
import com.example.phototomap.util.DatabaseUtil;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    GoogleMap map;
    FloatingActionButton floatingActionButton, floatingPinButton;
    ImageView imageDisplay;
    FusedLocationProviderClient client;
    SupportMapFragment supportMapFragment;
    double lat, lng;
    TextView address;
    String _address;
    byte[] img;
    ConstraintLayout constraintLayout;
    DatabaseHandler databaseHandler;
    ArrayList<LocationModel> arrayListLocation = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        // mapFragment.getMapAsync(MainActivity.this);
        databaseHandler = new DatabaseHandler(MainActivity.this);
        //Initialize fused location
        client = LocationServices.getFusedLocationProviderClient(this);

        //Check permission
        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            //When permission granted 
            //Call method
            getCurrentLocation();
        } else {
            //When permission denied
            //Request permission
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);

        }


        //Floating button for camera
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);

            }
        });

        //Floating button for pin drop
        floatingPinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOnDatabase();
            }
        });
    }

    //When pin is clicked add the marker values (lat,lng,address & img)
    private void addOnDatabase() {

        boolean check = databaseHandler.addLocation(String.valueOf(lat), String.valueOf(lng),
                _address, img);
        if (check) {
            Toast.makeText(MainActivity.this, "Stored!", Toast.LENGTH_SHORT).show();
            setUpMarker();
        }

    }

    //getting all previous markers and setting up new marker
    private void setUpMarker() {
        arrayListLocation = databaseHandler.getAllLocations();
        for (int i = 0; i < arrayListLocation.size(); i++) {
            LocationModel locationModel = arrayListLocation.get(i);
            createMarker(locationModel.getLat(), locationModel.getLng(),
                    locationModel.getAddress(), locationModel.getImg());
        }
    }

    //Creating marker on google maps and displaying values when on marker is clicked
    private void createMarker(final String lat, String lng, final String _address, final byte[] img) {

        LatLng latLng2 = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
        MarkerOptions option = new MarkerOptions().position(latLng2)
                .title(_address);

        //Zoom map
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng2, 18));

        //Add marker on map
        map.addMarker(option);

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                floatingPinButton.setVisibility(View.GONE);
                constraintLayout.setVisibility(View.VISIBLE);
                address.setText(_address);
                if (img != null) {
                    Bitmap bitmap = DatabaseUtil.getImage(img);
                    imageDisplay.setImageBitmap(bitmap);
                }

                return false;
            }
        });


    }


    // getting current location when app is opened
    private void getCurrentLocation() {
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(final Location location) {
                //successful
                if (location != null) {
                    //sync map
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            //Initalize lat lng
                            LatLng latLng = new LatLng(location.getLatitude(),
                                    location.getLongitude());

                            lat = location.getLatitude();
                            lng = location.getLongitude();
                            map = googleMap;
                            setUpMarker();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //When permission granted
                //Call method
                getCurrentLocation();
            }
        }
    }

    //get image from camera
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        imageDisplay.setImageBitmap(bitmap);
        _address = getCompleteAddressString(lat, lng);
        address.setText(_address);
        img = DatabaseUtil.getBytes(bitmap);
        constraintLayout.setVisibility(View.VISIBLE);
        floatingPinButton.setVisibility(View.VISIBLE);


    }

    //Initialize variables
    private void initUI() {
        floatingActionButton = findViewById(R.id.floatingActionButton);
        imageDisplay = findViewById(R.id.imageView_picture);
        address = findViewById(R.id.textView_address);
        floatingPinButton = findViewById(R.id.floatingPinButton);
        constraintLayout = findViewById(R.id.constraintLayout);
    }


    // taking in lat & long parameter and then converting it into address
    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current location address", strReturnedAddress.toString());
            } else {
                Log.w("My Current location address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current location address", "Cannot get Address!");
        }
        return strAdd;
    }

}
