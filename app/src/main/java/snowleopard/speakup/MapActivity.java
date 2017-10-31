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
import android.view.View;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap;
import java.io.IOException;
import java.util.List;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentLocationMarker;
    private Marker searchLocationMarker;
    public static final int REQUEST_LOCATION_CODE = 99;
    private Address myAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkLocationPermission();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void searchLocation(View view){
        EditText tf_location = (EditText)findViewById(R.id.TF_location);
        String location = tf_location.getText().toString();
        List<Address> addressList = null;
        MarkerOptions mo = new MarkerOptions();

        if(searchLocationMarker != null){
            searchLocationMarker.remove();
        }

        if(!location.equals("")){
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            for(int i = 0; i<addressList.size(); i++){
                myAddress = addressList.get(i);
                LatLng latLng = new LatLng(myAddress.getLatitude(), myAddress.getLongitude());
                mo.position(latLng);
                mo.title(location);
                mo.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                mo.draggable(true);

                searchLocationMarker = mMap.addMarker(mo);
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));





            }
        }

    }


//    The function called when + button pressed
//    public void getInfoContents(View view){
//
//        String lat = Double.toString(myAddress.getLatitude());
//        String lng = Double.toString(myAddress.getLongitude());
//
//        Intent intent = new Intent(MapActivity.this, AddStoryActivity.class);
//        intent.putExtra("lat", lat);
//        intent.putExtra("lng", lng);
//
//        startActivity(intent);
//
//    }


//    public void getInfoContents(Marker searchLocationMarker ) {
//
//        View v =getLayoutInflater().inflate(R.layout.activity_add_story, null);
//
//        //TextView tvlocality = (TextView) v.findViewById(R.id.tv_locality);
//        TextView tvlat = (TextView) v.findViewById(R.id.tv_lat);
//        TextView tvlng = (TextView) v.findViewById(R.id.tv_lng);
//        //TextView tvsnippet = (TextView) v.findViewById(R.id.tv_snippet);
//
//        LatLng ll = searchLocationMarker.getPosition();
//        //tvlocality.setText(searchLocationMarker .getTitle());
//        tvlat.setText("Latitude: " + ll.latitude);
//        tvlng.setText("Longitude: " + ll.longitude);
//        //tvsnippet.setText(searchLocationMarker .getSnippet());
//
//
//   //     return v;
//    }





//J pass lat lng
//    public void passLocation(View view){
//        EditText tf_location = (EditText)findViewById(R.id.TF_location);
//        String location = tf_location.getText().toString();
//        List<Address> addressList = null;
//        MarkerOptions mo = new MarkerOptions();
//
//
//        if(!location.equals("")){
//            Geocoder geocoder = new Geocoder(this);
//            try {
//                addressList = geocoder.getFromLocationName(location, 1);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            for(int i = 0; i<addressList.size(); i++){
//                Address myAddress = addressList.get(i);
//                LatLng latLng = new LatLng(myAddress.getLatitude(), myAddress.getLongitude());
//
//                TextView tvlat = (TextView) view.findViewById(R.id.tv_lat);
//                TextView tvlng = (TextView) view.findViewById(R.id.tv_lng);
//
//                tvlat.setText("Latitude: " + myAddress.getLatitude());
//                tvlng.setText("Longitude: " + myAddress.getLongitude());
//
////                Intent intent = new Intent(MapActivity.this, AddStoryActivity.class);
////                startActivity(intent);
//            }
//        }
//
//
//    }

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


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }


//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


        mMap.setOnInfoWindowClickListener(this);


        if(mMap != null){

            mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener(){


                @Override
                public void onMarkerDragStart(Marker marker) {

                }

                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {

                    Geocoder gc = new Geocoder(MapActivity.this);
                    LatLng ll = marker.getPosition();
                    List<android.location.Address> list = null;
                    try {
                        list = gc.getFromLocation(ll.latitude, ll.longitude, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    android.location.Address add = list.get(0);
                    marker.setTitle(add.getLocality());
                    marker.showInfoWindow();



                }
            });

            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {

                    View v =getLayoutInflater().inflate(R.layout.info_window, null);

                    TextView tvlocality = (TextView) v.findViewById(R.id.tv_locality);
                    TextView tvlat = (TextView) v.findViewById(R.id.tv_lat);
                    TextView tvlng = (TextView) v.findViewById(R.id.tv_lng);
//                    TextView tvsnippet = (TextView) v.findViewById(R.id.tv_snippet);

                    LatLng ll = marker.getPosition();
                    tvlocality.setText(marker.getTitle());
                    tvlat.setText("Latitude: " + ll.latitude);
                    tvlng.setText("Longitude: " + ll.longitude);
//                    tvsnippet.setText(marker.getSnippet());


                    return v;
                }
            });


        }



    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, "Location added to story!",
                Toast.LENGTH_SHORT).show();

        LatLng ll = marker.getPosition();

        String lat = Double.toString(ll.latitude);
        String lng = Double.toString(ll.longitude);

//        Intent intent = new Intent(MapActivity.this, AddStoryActivity.class);
//        intent.putExtra("lat", lat);
//        intent.putExtra("lng", lng);
//
//        startActivity(intent);
//        finish();

//        Intent intent = new Intent();
//        intent.putExtra("lat", lat);
//        intent.putExtra("lng", lng);
//        setResult(RESULT_OK, intent);
//        super.
        finish(lat, lng);

    }

    public void finish(String lat, String lng) {
        // Prepare data intent
        Intent data = new Intent();
        data.putExtra("lat", lat);
        data.putExtra("lng", lng);
        // Activity finished ok, return the data
        setResult(RESULT_OK, data);
        super.finish();
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
        markerOptions.draggable(true);

        currentLocationMarker = mMap.addMarker(markerOptions);

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
}
