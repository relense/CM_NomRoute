package world.develop.nomroute;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


/**
 * Created by Miguel on 23/11/2016.
 */

public class TrackActivity extends AppCompatActivity implements OnMapReadyCallback, AdapterView.OnItemClickListener {


    private Toolbar mToolbar;
    private MyActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ListView mLeftDrawer;
    private ArrayAdapter mLeftAdapter;
    private String username, codigo;
    private GoogleMap mMap;
    private File imageFile;
    private DatabaseHelper myDb;
    private Button buttonSatelite;

    private LatLng ips = new LatLng(38.521890, -8.839094);
    private LatLng deusDosFrangos = new LatLng(38.527390, -8.877440);
    private Marker estafeta = null;
    private Polyline lastPoly = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if (b != null) {
            username = b.getString("user");
            codigo = b.getString("codigo");
        }

        myDb = new DatabaseHelper(this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mLeftDrawer = (ListView) findViewById(R.id.left_drawer);
        buttonSatelite = (Button) findViewById(R.id.butao_satelite);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (username == null || username == "") {

            mLeftAdapter = new ArrayAdapter<String>(this, R.layout.simpleslistitem1, getResources()
                    .getStringArray(R.array.planets_array));

        } else {
            mLeftAdapter = new ArrayAdapter<String>(this, R.layout.simpleslistitem1, getResources()
                    .getStringArray(R.array.track_array));
        }
        mLeftDrawer.setAdapter(mLeftAdapter);
        mLeftDrawer.setOnItemClickListener(this);

        mDrawerToggle = new MyActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_closed);
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        mDrawerLayout.closeDrawer(Gravity.LEFT, false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mDrawerToggle.onOptionsItemSelected(item);
        return super.onOptionsItemSelected(item);
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if (username == null || username == "") {
            switch (position) {
                case 0:
                    register(view);
                    break;
                case 1:
                    login(view);
                    break;
            }

        } else {
            switch (position) {
                case 0:
                    mainMenu(view);
                    break;
                case 1:
                    savedRoute(codigo, username);
                    break;
                case 2:
                    takePicture(view);
                    break;
                case 3:
                    profile(view);
            }
        }
    }

    /**
     * Method to change to activity MainActivty
     *
     * @param v view
     */
    public void mainMenu(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("user", username);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /**
     * Method to change to activity ProfileActivty
     *
     * @param v view
     */
    public void profile(View v) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("user", username);
        startActivity(intent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.addMarker(new MarkerOptions().position(ips).title("IPS"));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(deusDosFrangos)      // Sets the center of the map to Mountain View
                .zoom(15)                   // Sets the zoom
                .build();                   // Creates a CameraPosition from the builder

        String url = "https://maps.googleapis.com/maps/api/directions/json?" + "" +
                "origin=" + deusDosFrangos.latitude + "," + deusDosFrangos.longitude +
                "&destination=" + ips.latitude + "," + ips.longitude +
                "&key=" + "AIzaSyBTmgCzFDzhKlQxLMMXWmbKwV6GcymF90s";

        Log.d("GMAP", url);

        new FetchUrl().execute(url);

    }

    /**
     * Method to take pictures, if the picture is taken and saved. It will be saved in /NomRoute directory.
     *
     * @param v view
     */
    public void takePicture(View v) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String file_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/NomRoute";
        String currentDateAndTime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File dir = new File(file_path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        imageFile = new File(dir, currentDateAndTime + ".jpg");

        Uri tempuri = Uri.fromFile(imageFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempuri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    myDb.updatePhotos(username);
                    break;
                case Activity.RESULT_CANCELED:
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Method for a user to make login, initiates activity LogiActivity
     *
     * @param v view
     */
    public void login(View v) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * Method for a user to register, initiates activity RegisterActivity
     *
     * @param v view
     */
    public void register(View v) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    /**
     * Method to save a route, wich is a track in the database
     *
     * @param codigo   track code
     * @param username user username
     */
    public void savedRoute(String codigo, String username) {
        if (myDb.saveRoute(codigo, username)) {
            mDrawerLayout.closeDrawer(Gravity.LEFT, false);
            Toast.makeText(TrackActivity.this, R.string.order_saved, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Fetches data from url passed
     */
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }

        private String downloadUrl(String strUrl) throws IOException {
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(strUrl);

                // Creating an http connection to communicate with url
                urlConnection = (HttpURLConnection) url.openConnection();

                // Connecting to url
                urlConnection.connect();

                // Reading data from url
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb = new StringBuffer();

                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                data = sb.toString();
                Log.d("downloadUrl", data.toString());
                br.close();

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            } finally {
                iStream.close();
                urlConnection.disconnect();
            }
            return data;
        }

        private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

            // Parsing the data in non-ui thread
            @Override
            protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

                JSONObject jObject;
                List<List<HashMap<String, String>>> routes = null;

                try {
                    jObject = new JSONObject(jsonData[0]);
                    Log.d("ParserTask", jsonData[0].toString());
                    DataParser parser = new DataParser();
                    Log.d("ParserTask", parser.toString());

                    // Starts parsing data
                    routes = parser.parse(jObject);
                    Log.d("ParserTask", "Executing routes");
                    Log.d("ParserTask", routes.toString());

                } catch (Exception e) {
                    Log.d("ParserTask", e.toString());
                    e.printStackTrace();
                }
                return routes;
            }

            // Executes in UI thread, after the parsing process
            @Override
            protected void onPostExecute(List<List<HashMap<String, String>>> result) {
                ArrayList<LatLng> points;

                // Traversing through all the routes
                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList<>();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    final AnimatingMarkersFragment a = new AnimatingMarkersFragment(mMap, points, getResources());
                    a.startAnimation();

                    buttonSatelite.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View view){
                           a.toggleStyle();
                        }
                    });



                    Log.d("onPostExecute", "onPostExecute lineoptions decoded");
                }
            }

            public class DataParser {

                /**
                 * Receives a JSONObject and returns a list of lists containing latitude and longitude
                 */
                public List<List<HashMap<String, String>>> parse(JSONObject jObject) {

                    List<List<HashMap<String, String>>> routes = new ArrayList<>();
                    JSONArray jRoutes;
                    JSONArray jLegs;
                    JSONArray jSteps;

                    try {

                        jRoutes = jObject.getJSONArray("routes");

                        /** Traversing all routes */
                        for (int i = 0; i < jRoutes.length(); i++) {
                            jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                            List path = new ArrayList<>();

                            /** Traversing all legs */
                            for (int j = 0; j < jLegs.length(); j++) {
                                jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                                /** Traversing all steps */
                                for (int k = 0; k < jSteps.length(); k++) {
                                    String polyline = "";
                                    polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                                    List<LatLng> list = decodePoly(polyline);

                                    /** Traversing all points */
                                    for (int l = 0; l < list.size(); l++) {
                                        HashMap<String, String> hm = new HashMap<>();
                                        hm.put("lat", Double.toString((list.get(l)).latitude));
                                        hm.put("lng", Double.toString((list.get(l)).longitude));
                                        path.add(hm);
                                    }
                                }
                                routes.add(path);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                    }


                    return routes;
                }

                /**
                 * Method to decode polyline points
                 * Courtesy : http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
                 */
                private List<LatLng> decodePoly(String encoded) {

                    List<LatLng> poly = new ArrayList<>();
                    int index = 0, len = encoded.length();
                    int lat = 0, lng = 0;

                    while (index < len) {
                        int b, shift = 0, result = 0;
                        do {
                            b = encoded.charAt(index++) - 63;
                            result |= (b & 0x1f) << shift;
                            shift += 5;
                        } while (b >= 0x20);
                        int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                        lat += dlat;

                        shift = 0;
                        result = 0;
                        do {
                            b = encoded.charAt(index++) - 63;
                            result |= (b & 0x1f) << shift;
                            shift += 5;
                        } while (b >= 0x20);
                        int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                        lng += dlng;

                        LatLng p = new LatLng((((double) lat / 1E5)),
                                (((double) lng / 1E5)));
                        poly.add(p);
                    }

                    return poly;
                }
            }
        }
    }
}
