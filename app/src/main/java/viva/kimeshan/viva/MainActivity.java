package viva.kimeshan.viva;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import java.util.List;

import androidx.annotation.NonNull;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

// classes needed to initialize map
import com.vivala.vivaladestino.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

// classes needed to add the location component

import com.mapbox.mapboxsdk.plugins.building.BuildingPlugin;
import com.mapbox.mapboxsdk.plugins.traffic.TrafficPlugin;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.util.Log;

import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import static android.widget.Toast.LENGTH_SHORT;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, MapboxMap.OnMapClickListener, PermissionsListener {

    private MapView mapView;
    String routeHistory;
    private FirebaseAuth mAuth;
    private Button button;
    String check;

    //Method taken from/adapted from firbase
    //Author: firebase
    //Link: https://docs.mapbox.com/android/java/examples/directions-profile-toggle/
    String car = DirectionsCriteria.PROFILE_DRIVING;
    String walking = DirectionsCriteria.PROFILE_WALKING;
    String cycling = DirectionsCriteria.PROFILE_CYCLING;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String preferredModeOfTransport = null;
    private  DocumentReference docRef;



    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;

    private DirectionsRoute currentRoute;
    private static final String TAG = "DirectionsActivity";
    private NavigationMapRoute navigationMapRoute;
    private TrafficPlugin trafficPlugin;
    Boolean bFlag = false;
    private BuildingPlugin buildingPlugin;
    private String readData;
    private String readHistory;

    //Method taken from/adapted from firbase
    //Author: firebase
    //Link: https://docs.mapbox.com/api/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));

        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(this);
        button = findViewById(R.id.startButton);

        //Starts GPS
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean simulateRoute = false;
                NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                        .directionsRoute(currentRoute)
                        .shouldSimulateRoute(simulateRoute)
                        .build();

                Toast.makeText(MainActivity.this, routeHistory, LENGTH_SHORT).show();
                getHistory();
                storeHistory();
                NavigationLauncher.startNavigation(MainActivity.this, options);
            }
        });

        findViewById(R.id.weather).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mapboxMap != null) {
                    startActivity(new Intent(MainActivity.this,weather.class));
                }
            }
        });

        findViewById(R.id.map).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,profile.class));
            }
        });

    }

    //Gets the history of the user so can be manipulated in the storeHistory method
    public void getHistory(){
        FirebaseUser user = mAuth.getCurrentUser();
        docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                readHistory = documentSnapshot.getString("History");
            }
        });

    }

    //Gets the users data after login vie their UID, in order to get details of transport
    public void loadData(){
        FirebaseUser user = mAuth.getCurrentUser();
        docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                readData  = documentSnapshot.getString("modeOfTransport");
                if (readData.equals("DRIVING")) {
                    preferredModeOfTransport = car;
                }
                else if(readData.equals("CYCLING")){
                    preferredModeOfTransport = cycling;
                }
                else{
                    preferredModeOfTransport = walking;
                }
            }
        });

    }


    //Method taken from/adapted from mapbox
    //Author: mapbox
    //Link: https://docs.mapbox.com/api/
    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.DARK , new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                loadData();
                enableLocationComponent(style);
                addDestinationIconSymbolLayer(style);

                trafficPlugin = new TrafficPlugin(mapView, mapboxMap, style); // Used to display traffic to the user on the map
                buildingPlugin = new BuildingPlugin(mapView, mapboxMap, style);
                buildingPlugin.setMinZoomLevel(15f);
                buildingPlugin.setVisibility(true);
                // Enable the traffic view by default
                trafficPlugin.setVisibility(true);

                findViewById(R.id.traffic_toggle_fab).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mapboxMap != null) {
                            trafficPlugin.setVisibility(!trafficPlugin.isVisible());
                        }
                    }
                });

                mapboxMap.addOnMapClickListener(MainActivity.this);

            }
        });
    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
// Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
// Activate the MapboxMap LocationComponent to show user location
// Adding in LocationComponentOptions is also an optional parameter
            locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(this, loadedMapStyle);
            locationComponent.setLocationComponentEnabled(true);
// Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING_GPS);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    //Permissions from the user for their current location
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    //Method taken from/adapted from fmapbox
    //Author: mapbox
    //Link: https://docs.mapbox.com/api/
    //Displays users current location
    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationComponent(mapboxMap.getStyle());
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    //Adds marker on the point the user clicks on
    private void addDestinationIconSymbolLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addImage("destination-icon-id",
                BitmapFactory.decodeResource(this.getResources(), R.drawable.mapbox_marker_icon_default));
        GeoJsonSource geoJsonSource = new GeoJsonSource("destination-source-id");
        loadedMapStyle.addSource(geoJsonSource);
        SymbolLayer destinationSymbolLayer = new SymbolLayer("destination-symbol-layer-id", "destination-source-id");
        destinationSymbolLayer.withProperties(
                iconImage("destination-icon-id"),
                iconAllowOverlap(true),
                iconIgnorePlacement(true)

        );
        loadedMapStyle.addLayer(destinationSymbolLayer);
    }

    //Method taken from/adapted from mapbox
    //Author: mapbox
    //Link: https://docs.mapbox.com/api/
    @SuppressWarnings( {"MissingPermission"})

    //Gets details of the area the user has clicked on
    @Override
    public boolean onMapClick(@NonNull LatLng point) {

        Point destinationPoint = Point.fromLngLat(point.getLongitude(), point.getLatitude());
        Point originPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
                locationComponent.getLastKnownLocation().getLatitude());

        GeoJsonSource source = mapboxMap.getStyle().getSourceAs("destination-source-id");
        if (source != null) {
          source.setGeoJson(Feature.fromGeometry(destinationPoint));
        }
        getRoute(originPoint, destinationPoint);
        button.setEnabled(true);
        button.setBackgroundResource(R.color.mapbox_blue);

        final MapboxGeocoding reverseGeocode = MapboxGeocoding.builder()
                .accessToken(getString(R.string.access_token))
                .query(Point.fromLngLat(point.getLongitude(), point.getLatitude()))
                .geocodingTypes(GeocodingCriteria.TYPE_ADDRESS)
                .build();

        reverseGeocode.enqueueCall(new Callback<GeocodingResponse>() {
            @Override
            public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {

                List<CarmenFeature> results = response.body().features();

                if (results.size() > 0) {
                    routeHistory = results.get(0).placeName();
                } else {

                    // No result for your request were found.
                    Log.d(TAG, "onResponse: No result found");

                }
            }

            @Override
            public void onFailure(Call<GeocodingResponse> call, Throwable throwable) {
                throwable.printStackTrace();
            }
        });
        return true;

    }

    //Method taken from/adapted from mapbox
    //Author: mapbox
    //Link: https://docs.mapbox.com/api/
    //Builds the route with information such as the user's preferred mode of transport and metric to imperial system
    private void getRoute(Point origin, Point destination) {

        FirebaseUser user = mAuth.getCurrentUser();
        docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                check = documentSnapshot.getString("measurement");
                if (check.equals("MILES")) {
                    bFlag = true;
                } else {
                    bFlag = false;
                }
            }
        });

        //Builds the route based on the user's preference
        if (bFlag == true){
           NavigationRoute.builder(this)
                   .accessToken(Mapbox.getAccessToken())
                   .origin(origin)
                   .destination(destination)
                   .voiceUnits(DirectionsCriteria.METRIC)
                   .profile(preferredModeOfTransport)
                   .build()
                   .getRoute(new Callback<DirectionsResponse>() {
                       @Override
                       public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {

                           // You can get the generic HTTP info about the response
                           Log.d(TAG, "Response code: " + response.code());
                           if (response.body() == null) {
                               Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                               return;
                           } else if (response.body().routes().size() < 1) {
                               Log.e(TAG, "No routes found");
                               return;
                           }

                           currentRoute = response.body().routes().get(0);

                           // Draw the route on the map
                           if (navigationMapRoute != null) {
                               navigationMapRoute.removeRoute();
                           } else {
                               navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
                           }
                           navigationMapRoute.addRoute(currentRoute);

                       }

                       @Override
                       public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                           Log.e(TAG, "Error: " + throwable.getMessage());
                       }
                   });
       }
       else {
           NavigationRoute.builder(this)
                   .accessToken(Mapbox.getAccessToken())
                   .origin(origin)
                   .destination(destination)
                   .voiceUnits(DirectionsCriteria.IMPERIAL)
                   .profile(preferredModeOfTransport)
                   .build()
                   .getRoute(new Callback<DirectionsResponse>() {
                       @Override
                       public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {

                           Log.d(TAG, "Response code: " + response.code());
                           if (response.body() == null) {
                               Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                               return;
                           } else if (response.body().routes().size() < 1) {
                               Log.e(TAG, "No routes found");
                               return;
                           }

                           currentRoute = response.body().routes().get(0);

                           if (navigationMapRoute != null) {
                               navigationMapRoute.removeRoute();
                           } else {
                               navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
                           }
                           navigationMapRoute.addRoute(currentRoute);
                       }
                       @Override
                       public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                           Log.e(TAG, "Error: " + throwable.getMessage());
                       }
                   });
       }
    }


    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    //Method taken from/adapted from firbase
    //Author: firebase
    //Link: https://firebase.google.com/docs/firestore/manage-data/add-data
    //Stores the user's route in firebase
    //In order to avoid overwriting the current data in firebase is taken and added to the new route
    //They are separated via a "-"
    public  void storeHistory() {
        FirebaseUser user = mAuth.getCurrentUser();
        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.update("History", routeHistory + "-" + readHistory);
    }

}


