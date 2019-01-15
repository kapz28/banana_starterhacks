package com.thecodecity.mapsdirection;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.thecodecity.mapsdirection.directionhelpers.FetchURL;
import com.thecodecity.mapsdirection.directionhelpers.TaskLoadedCallback;

import java.util.ArrayList;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback,  View.OnClickListener, AdapterView.OnItemClickListener {

    private GoogleMap mMap;
    private MarkerOptions place1, place2, place3, place4;
    //Button getDirection;
    SeekBar seekBar;
    TextView textView;
    private Polyline currentPolyline;
    LocationManager locationManager;

    private EditText itemET;

    private Button btn;

    private ListView itemsList;



    private ArrayList<String> items;

    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);



        itemET = findViewById(R.id.item_edit_text);

        btn = findViewById(R.id.add_btn);

        itemsList = findViewById(R.id.items_list);



        items = FileHelper.readData(this);



        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);

        itemsList.setAdapter(adapter);



        btn.setOnClickListener(this);

        itemsList.setOnItemClickListener((AdapterView.OnItemClickListener) this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        //ction = findViewById(R.id.btnGetDirection);

        //27.658143,85.3199503
        //27.667491,85.3208583

        place1 = new MarkerOptions().position(new LatLng(43.458760, -80.539680)).title("Location 1");
        place2 = new MarkerOptions().position(new LatLng(43.477370, -80.530970)).title("Location 2");
        place3 = new MarkerOptions().position(new LatLng(43.471191, -80.509682)).title("Location 3");
        place4 = new MarkerOptions().position(new LatLng(43.461150, -80.536940)).title("Location 4");

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int seekBarProgress = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                seekBarProgress = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                toastMessage(seekBarProgress);
            }
        });


        final MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.mapNearBy);
        mapFragment.getMapAsync(this);
        //mapFragment.getView().setVisibility(View.INVISIBLE);
        /*getDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //new FetchURL(MapActivity.this).execute(getUrl(place1.getPosition(), place2.getPosition(), "driving"), "driving");
                mapFragment.getView().setVisibility(View.GONE);
            }
        });*/

    }

    public void toastMessage(int seekBarProgress) {
        String message = "";

        if(seekBarProgress <= 1) {
            mMap.clear();
            Circle addcircle = mMap.addCircle(new CircleOptions()
                    .center(new LatLng(43.471872, -80.543559))
                    .radius(50)
                    .strokeColor(Color.RED)
                    .fillColor(Color.BLUE));
        }
        else if(seekBarProgress <= 2) {
            mMap.clear();
            mMap.addMarker(place1);
            mMap.addMarker(place2);
            Circle addcircle = mMap.addCircle(new CircleOptions()
                    .center(new LatLng(43.471872, -80.543559))
                    .radius(50)
                    .strokeColor(Color.RED)
                    .fillColor(Color.BLUE));
        }
        else if (seekBarProgress <= 3){
            mMap.clear();
            mMap.addMarker(place1);
            mMap.addMarker(place2);
            mMap.addMarker(place3);
            Circle addcircle = mMap.addCircle(new CircleOptions()
                    .center(new LatLng(43.471872, -80.543559))
                    .radius(50)
                    .strokeColor(Color.RED)
                    .fillColor(Color.BLUE));
        }
        else  {
            mMap.clear();
            mMap.addMarker(place1);
            mMap.addMarker(place2);
            mMap.addMarker(place3);
            mMap.addMarker(place4);
            Circle addcircle = mMap.addCircle(new CircleOptions()
                    .center(new LatLng(43.471872, -80.543559))
                    .radius(50)
                    .strokeColor(Color.RED)
                    .fillColor(Color.BLUE));
        }
        Toast.makeText(MapActivity.this, message, Toast.LENGTH_LONG ).show();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.d("mylog", "Added Markers");

        Circle addcircle = mMap.addCircle(new CircleOptions()
                .center(new LatLng(43.471872, -80.543559))
                .radius(50)
                .strokeColor(Color.RED)
                .fillColor(Color.BLUE));


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(43.4679, -80.540330),10.2f));
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }

    @Override

    public void onClick(View v) {

        switch(v.getId()){

            case R.id.add_btn:

                String itemEntered = itemET.getText().toString();


                adapter.add(itemEntered);

                itemET.setText("banana");

                FileHelper.writeData(items, this);

                mMap.addMarker(place1);

                Toast.makeText(this, "Closest location shown", Toast.LENGTH_SHORT).show();



                break;

        }

    }





    @Override

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        items.remove(position);
        mMap.clear();
        Circle addcircle = mMap.addCircle(new CircleOptions()
                .center(new LatLng(43.471872, -80.543559))
                .radius(50)
                .strokeColor(Color.RED)
                .fillColor(Color.BLUE));

        adapter.notifyDataSetChanged();

        FileHelper.writeData(items, this);

        Toast.makeText(this, "delete", Toast.LENGTH_SHORT).show();



    }



}
