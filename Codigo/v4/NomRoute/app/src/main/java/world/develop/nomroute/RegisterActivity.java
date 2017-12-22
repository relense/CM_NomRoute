package world.develop.nomroute;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static android.text.TextUtils.isEmpty;

/**
 * Created by Miguel on 22/11/2016.
 */

public class RegisterActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText editTextUsername, editTextPassword, editTextEmail, editTextReEnterPassword;
    public Button btnRegister;
    DatabaseHelper myDb;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if(getResources().getBoolean(R.bool.portrait_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        myDb = new DatabaseHelper(this);
        db = myDb.getWritableDatabase();
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnRegister = (Button) findViewById(R.id.register);
        editTextUsername = (EditText) findViewById(R.id.username);
        editTextPassword = (EditText) findViewById(R.id.password);
        editTextEmail = (EditText) findViewById(R.id.email);
        editTextReEnterPassword = (EditText) findViewById(R.id.reenterpassowrd);

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

        editTextEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    editTextEmail.setHint("");
                else
                    editTextEmail.setHint(R.string.email);
            }
        });

        editTextReEnterPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    editTextReEnterPassword.setHint("");
                else
                    editTextReEnterPassword.setHint(R.string.confirm_password);
            }
        });
    }

    public void registar(View v) {
        if (editTextPassword.getText().toString().equals(editTextReEnterPassword.getText().toString())) {
            if (hasValue(editTextEmail) && hasValue(editTextPassword) && hasValue(editTextReEnterPassword) && hasValue(editTextUsername)) {
                boolean isInserted = myDb.insertData(db, editTextUsername.getText().toString(),
                        editTextPassword.getText().toString(), editTextEmail.getText().toString(), 0, 0, 0);
                if (isInserted == true) {
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            } else
                Toast.makeText(RegisterActivity.this, R.string.fill_all_fields, Toast.LENGTH_LONG).show();
        } else
            Toast.makeText(RegisterActivity.this, R.string.password_doesnt_match, Toast.LENGTH_LONG).show();
    }

    public boolean hasValue(EditText editText) {
        if (isEmpty(editText.getText().toString()))
            return false;
        else
            return true;

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
