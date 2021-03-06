package sg.edu.rp.c347.id19023980.gettingmylocationenhanced;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.FileWriter;

public class MainActivity extends AppCompatActivity {
    private GoogleMap map;
    Button btnUpdate, btnRemove, btnRecords;
    TextView tvLat, tvLog;
    LocationRequest mLocationRequest;
    LocationCallback mLocationCallback;
    FusedLocationProviderClient client;
    LatLng poi_Marker;
    String folderLocation;
    ToggleButton toggleMusic;
//    Double lat,log;
    boolean toggleCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnUpdate = findViewById(R.id.btnGetUpdate);
        btnRemove = findViewById(R.id.btnRemoveUpdate);
        btnRecords = findViewById(R.id.btnRecords);
        tvLat = findViewById(R.id.tvLat);
        tvLog = findViewById(R.id.tvLog);
        toggleMusic = findViewById(R.id.toggleMusic);

        String[] permission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(MainActivity.this, permission, 0);
        if(checkPermission()){
            client = LocationServices.getFusedLocationProviderClient(MainActivity.this);
            Task<Location> task = client.getLastLocation();
            task.addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // Log.i("check",location.toString());
                    String msg = "";
                    if (location != null) {
                        poi_Marker = new LatLng(location.getLatitude(), location.getLongitude());
                        tvLat.setText("Latitude: " + location.getLatitude());
                        tvLog.setText("Longitude: " + location.getLongitude());
                        msg = "Marker Exists";
                    } else {
                        msg = "No last known location found";
                    }
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            });
        }



        FragmentManager fm = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                UiSettings ui = map.getUiSettings();
                ui.setCompassEnabled(true);
                ui.setZoomControlsEnabled(true);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(1.4431133093468227, 103.78554439536298),15));
                int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION);

                if (permissionCheck == PermissionChecker.PERMISSION_GRANTED) {
                    map.setMyLocationEnabled(true);
                } else {
                    Log.e("GMap - Permission", "GPS access has not been granted");
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                }
//                Marker marker = map.addMarker(new MarkerOptions()
//                        .position(poi_Marker)
//                        .title("random area")
//                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            }
        });
        //folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/folderMap";
//        mLocationRequest = new LocationRequest();
        //

//        mLocationCallback = new LocationCallback() {
//            public void onLocationResult(LocationResult locationResult) {
//                if (locationResult != null) {
//                    Location data = locationResult.getLastLocation();
//                    Double lat = data.getLatitude();
//                    Double log = data.getLongitude();
//                    try {
//                        folderLocation =
//                                Environment.getExternalStorageDirectory()
//                                        .getAbsolutePath() + "/Folder";
//                        File folder = new File(folderLocation);
//                        if (folder.exists() == false) {
//                            boolean result = folder.mkdir();
//                            if (result == true) {
//                                Log.d("File Read/Write", "Folder created");
//                            }
//                        }
//                        try {
//                            folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Folder";
//                            File targetFile = new File(folderLocation, "data2.txt");
//                            FileWriter writer = new FileWriter(targetFile, true);
//                            writer.write(lat +"," + log+"\n");
//                            writer.flush();
//                            writer.close();
//                        } catch (Exception e) {
//                            Log.d("folder",folderLocation.toString());
//                            Toast.makeText(MainActivity.this, "Failed to write!", Toast.LENGTH_LONG).show();
//                            e.printStackTrace();
//                        }
//                    } catch (Exception e) {
//                        Toast.makeText(MainActivity.this, "Failed to create folder!", Toast.LENGTH_LONG).show();
//                        e.printStackTrace();
//                    }
//                    Toast.makeText(MainActivity.this, lat + "\n" + log, Toast.LENGTH_SHORT).show();
//                }
//            }
//        };
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkPermission()) {
//                    Intent i = new Intent(MainActivity.this,MyServiceLocation.class);
//                    bindService(i,connection,BIND_AUTO_CREATE);
                    startService(new Intent(MainActivity.this,MyServiceLocation.class));
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
            }
        });
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermission()) {
                    stopService(new Intent(MainActivity.this,MyServiceLocation.class));
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
            }
        });
        btnRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,RecordActivity.class);
                startActivity(i);
            }
        });
        toggleMusic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    toggleCheck = true;
                    startService(new Intent(MainActivity.this,MyService.class));

                }else{
                    toggleCheck = false;
                    stopService(new Intent(MainActivity.this,MyService.class));
                }
            }
        });


    }

    private boolean checkPermission() {
//        int permissionCheck_Coarse = ContextCompat.checkSelfPermission(
//                MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck_Fine = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (
//                permissionCheck_Coarse == PermissionChecker.PERMISSION_GRANTED ||
                permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED
        ) {
            return true;
        } else {
//            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},0);
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(checkPermission()){
            client = LocationServices.getFusedLocationProviderClient(MainActivity.this);
            Task<Location> task = client.getLastLocation();
            task.addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // Log.i("check",location.toString());
                    String msg = "";
                    if (location != null) {
                        poi_Marker = new LatLng(location.getLatitude(), location.getLongitude());
                        tvLat.setText("Latitude: " + location.getLatitude());
                        tvLog.setText("Longitude: " + location.getLongitude());
                        //msg = "Marker Exists again";
                    } else {
                        msg = "No last known location found";
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }

                }
            });
            if (toggleCheck){
                toggleMusic.setChecked(true);
            }else{
                toggleMusic.setChecked(false);
            }
        }
    }

}