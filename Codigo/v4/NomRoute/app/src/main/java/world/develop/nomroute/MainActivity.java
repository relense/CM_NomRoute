package world.develop.nomroute;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private Toolbar mToolbar;
    private MyActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ListView mLeftDrawer;
    private ArrayAdapter mLeftAdapter;
    private EditText editTextTrack;
    private DatabaseHelper myDb;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.MyTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(getResources().getBoolean(R.bool.portrait_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        SharedPreferences pref = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE);
        String username = pref.getString(LoginActivity.PREF_USERNAME, null);
        String password = pref.getString(LoginActivity.PREF_PASSWORD, null);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if (b != null) {
            username = b.getString("user");
        }

        if (username != null || password != null) {
            this.username = username;
        }

        myDb = new DatabaseHelper(this);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mLeftDrawer = (ListView) findViewById(R.id.left_drawer);
        editTextTrack = (EditText) findViewById(R.id.track);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (username == null || username == "") {
            mLeftAdapter = new ArrayAdapter<String>(this, R.layout.simpleslistitem1, getResources()
                    .getStringArray(R.array.planets_array));
        } else {
            mLeftAdapter = new ArrayAdapter<String>(this, R.layout.simpleslistitem1, getResources()
                    .getStringArray(R.array.planets_array2));
        }
        mLeftDrawer.setAdapter(mLeftAdapter);
        mLeftDrawer.setOnItemClickListener(this);

        mDrawerToggle = new MyActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open,
                R.string.drawer_closed);
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();

        editTextTrack.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    editTextTrack.setHint("");
                else
                    editTextTrack.setHint(R.string.input_track_id);
            }
        });
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
                    profile(view);
                    break;
                case 1:
                    logOut(view);
                    break;
            }
        }
    }

    public void login(View v) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void register(View v) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void profile(View v) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("user", username);
        startActivity(intent);
    }

    public void logOut(View v) {
        SharedPreferences sharedPrefs = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.clear();
        editor.commit();
        username = "";
        getIntent().removeExtra("user");

        recreate();
    }

    public void button_track(View v) {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo m3G = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (mWifi.isConnected() || m3G.isConnected()) {
            if (myDb.getCodigo(editTextTrack.getText().toString(), username)) {
                Intent intent = new Intent(this, TrackActivity.class);
                intent.putExtra("user", username);
                intent.putExtra("codigo", editTextTrack.getText().toString());
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, R.string.track_id_doesnt_exist, Toast.LENGTH_LONG).show();

            }
        } else {
            Toast.makeText(MainActivity.this, R.string.please_turn_wifi_on, Toast.LENGTH_LONG).show();
        }
    }
}
