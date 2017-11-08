package snowleopard.speakup;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;

/**
 * Created by jcsha on 28-10-2017.
 */

public class MappingActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnInfoWindowClickListener{

    private GoogleMap mMap;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentLocationMarker;
    private Marker searchLocationMarker;
    public static final int REQUEST_LOCATION_CODE = 99;
    private Address myAddress;
    private DatabaseReference mLoc;
    private DatabaseReference mTitle;
    private String show_title;
    private FirebaseAuth mAuth;
    private Double passedLat;
    private Double passedLng;



    static private String Id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapping);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkLocationPermission();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapping);
        mapFragment.getMapAsync(this);
        mLoc = (DatabaseReference) FirebaseDatabase.getInstance().getReference().child("Story");
        mAuth = FirebaseAuth.getInstance();
        mTitle = (DatabaseReference) FirebaseDatabase.getInstance().getReference().child("Story");

        passedLat = Double.parseDouble(getIntent().getStringExtra("lat"));
        passedLng = Double.parseDouble(getIntent().getStringExtra("long"));

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case REQUEST_LOCATION_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        if(client == null){
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                }
                return;
        }
    }

    public void openListView(View view){

        Intent intent = new Intent(MappingActivity.this, ListViewActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }


        mMap.setOnInfoWindowClickListener(this);


        if(mMap != null){


            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(final Marker marker) {

                    View v =getLayoutInflater().inflate(R.layout.info_window, null);

                    TextView tvlocality = (TextView) v.findViewById(R.id.tv_locality);
                    TextView tvlat = (TextView) v.findViewById(R.id.tv_lat);
                    TextView tvlng = (TextView) v.findViewById(R.id.tv_lng);
//                    TextView tvsnippet = (TextView) v.findViewById(R.id.tv_snippet);


                    LatLng ll = marker.getPosition();

//                    mTitle.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            show_title = dataSnapshot.child(marker.getTitle()).child("Title").getValue().toString();
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });

                    tvlocality.setText(marker.getTitle());
                    tvlat.setText("Latitude: " + ll.latitude);
                    tvlng.setText("Longitude: " + ll.longitude);
//                    tvsnippet.setText(marker.getSnippet());


                    return v;
                }
            });


            //Loop to put markers on map
            mLoc.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Id = snapshot.getKey().toString();

                        double Lat = Double.parseDouble(snapshot.child("Latitude").getValue().toString());
                        double Long = Double.parseDouble(snapshot.child("Longitude").getValue().toString());
                        String Title = snapshot.child("Title").getValue().toString();

                        LatLng latLng = new LatLng(Lat, Long);

                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title(Title);
                        markerOptions.snippet(Id);

                        if(passedLat == Lat && passedLng == Long) {
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        }

                        else {
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                        }

                        mMap.addMarker(markerOptions);

                        if(passedLat == Lat && passedLng == Long) {
//                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 21));
//                            mMap.animateCamera(CameraUpdateFactory.zoomIn());
//                            mMap.animateCamera(CameraUpdateFactory.zoomTo(21), 2000, null);

                            //Build camera position
                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(latLng)
                                    .zoom(21).build();
                            //Zoom in and animate the camera.
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        }


                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


//            if(passedLat != null) {
//                LatLng passedlatLng = new LatLng(passedLat, passedLng);
//
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(passedlatLng, 20));
//            }

//            Toast.makeText(getApplicationContext(),passedLat.toString(), Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onInfoWindowClick(Marker marker) {

        Intent viewActivity = new Intent(MappingActivity.this,ViewStoryActivity.class);
        viewActivity.putExtra("Key",marker.getSnippet());
        startActivity(viewActivity);
        finish();

    }


    protected synchronized void buildGoogleApiClient(){

        client = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();

        client.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;

        if(currentLocationMarker != null){
            currentLocationMarker.remove();
        }

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));

//        currentLocationMarker = mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
//        mMap.animateCamera(CameraUpdateFactory.zoomBy(10));

        if(client != null){
            LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
        }

    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest = new LocationRequest();

        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
        }
    }

    public boolean checkLocationPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            }

            else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);

            }
            return false;
        }
        else return true;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case  R.id.action_add:
                Intent mainIntent = new Intent(MappingActivity.this, AddStoryActivity.class);
                startActivity(mainIntent);
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(MappingActivity.this, ProfileActivity.class);
                startActivity(intent);
                return true;


            case R.id.action_logout:

//                Intent intent = new Intent(ListViewActivity.this, MainActivity.class);
                mAuth.signOut();
//                startActivity(intent);
//                finish();
                return true;


            default:

                return super.onOptionsItemSelected(item);

        }

    }






}