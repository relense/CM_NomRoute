package world.develop.nomroute;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Miguel on 21/11/2016.
 */

public class LoginActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText editTextUsername, editTextPassword;
    DatabaseHelper myDb;
    public Button btnLogin;
    public static String PREFS_NAME="mypre";
    public static String PREF_USERNAME="username";
    public static String PREF_PASSWORD="password";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(getResources().getBoolean(R.bool.portrait_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        myDb = new DatabaseHelper(this);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editTextUsername = (EditText) findViewById(R.id.username);
        editTextPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.login);

        editTextUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    editTextUsername.setHint("");
                else
                    editTextUsername.setHint(R.string.username);
            }
        });

        editTextPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    editTextPassword.setHint("");
                else
                    editTextPassword.setHint(R.string.password);
            }
        });

    }

    public void login(View v){
        Cursor res = myDb.getUsername(editTextUsername.getText().toString(), editTextPassword.getText().toString());

        if (res.getCount() == 0) {
            Toast.makeText(this, R.string.invalid_username_or_password, Toast.LENGTH_LONG).show();
            return;
        }
        rememberMe(editTextUsername.getText().toString(), editTextPassword.getText().toString());
        showMain(editTextUsername.getText().toString());
    }

    @Override
    public void onStart(){
        super.onStart();
        getUser();
    }

    public void rememberMe(String user, String password){
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
                .putString(PREF_USERNAME,user)
                .putString(PREF_PASSWORD, password)
                .commit();
    }

    public void showMain(String username){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("user", username);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }

    public void getUser(){
        SharedPreferences pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String username = pref.getString(PREF_USERNAME, null);
        String password = pref.getString(PREF_PASSWORD, null);

        if(username != null || password != null){
            showMain(username);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
