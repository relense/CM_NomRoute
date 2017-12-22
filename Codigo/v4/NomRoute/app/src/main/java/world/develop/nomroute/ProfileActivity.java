package world.develop.nomroute;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Miguel on 22/11/2016.
 */

public class ProfileActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private Toolbar mToolbar;
    private MyActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ListView mLeftDrawer;
    private ArrayAdapter mLeftAdapter;
    private String username;
    private TextView numberPictures, user_username, user_email, userTracks, userSaves;
    private DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if(getResources().getBoolean(R.bool.portrait_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if (b != null) {
            username = b.getString("user");
        }

        myDb = new DatabaseHelper(this);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mLeftDrawer = (ListView) findViewById(R.id.left_drawer);
        numberPictures = (TextView) findViewById(R.id.numberPictures);
        user_username = (TextView) findViewById(R.id.user_username);
        user_email = (TextView) findViewById(R.id.user_email);
        userTracks = (TextView) findViewById(R.id.userTracks);
        userSaves = (TextView) findViewById(R.id.userSaves);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mLeftAdapter = new ArrayAdapter<String>(this, R.layout.simpleslistitem1, getResources()
                .getStringArray(R.array.profile_array));
        mLeftDrawer.setAdapter(mLeftAdapter);
        mLeftDrawer.setOnItemClickListener(this);

        mDrawerToggle = new MyActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open,
                R.string.drawer_closed);
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();

        numberPictures.setText(Integer.toString(myDb.numberOfPhotos(username)));
        userTracks.setText(Integer.toString(myDb.userNumberTracks(username)));
        user_username.setText(username);
        user_email.setText(myDb.getEmail(username));
        userSaves.setText(Integer.toString(myDb.getUserSavedRoutes(username)));
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
        switch (position) {
            case 0:
                mainMenu(view);
                break;
            case 1:
                savedOrders(view);
                break;
            case 2:
                savedPicutres(view);
        }
    }

    public void mainMenu(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("user", username);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void savedPicutres(View v) {
        Intent intent = new Intent(this, SavedPicturesActivity.class);
        startActivity(intent);
    }

    public void savedOrders(View v) {
        if (myDb.getUserSavedRoutes(username) == 0) {
            Toast.makeText(getBaseContext(), R.string.no_orders_saved, Toast.LENGTH_SHORT).show();
        } else {

            Intent intent = new Intent(this, OrdersActivity.class);
            intent.putExtra("user", username);
            startActivity(intent);
        }
    }

    public void editMail(View v) {

        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.edit_mail, null);

        final EditText newMail1 = (EditText) alertLayout.findViewById(R.id.enterMail1);
        final EditText newMail2 = (EditText) alertLayout.findViewById(R.id.enterMail2);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.change_email);
        alert.setView(alertLayout);
        alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alert.setPositiveButton(R.string.change, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String mailBox1 = newMail1.getText().toString();
                String mailBox2 = newMail2.getText().toString();

                if (mailBox1 == null || mailBox1.isEmpty()) {
                    Toast.makeText(getBaseContext(), R.string.fields_cant_be_empty, Toast.LENGTH_SHORT).show();

                } else {
                    if (mailBox1.equals(mailBox2)) {
                        String mail = mailBox1;
                        myDb.changeMail(mailBox1, username);
                        user_email.setText(mailBox1);

                        Toast.makeText(getBaseContext(), R.string.sucess, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getBaseContext(), R.string.both_fields_must_match, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        newMail1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    newMail1.setHint("");
                else
                    newMail1.setHint(R.string.enter_new_email);
            }
        });

        newMail2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    newMail2.setHint("");
                else
                    newMail2.setHint(R.string.confirm_email);
            }
        });

        AlertDialog dialog = alert.create();
        dialog.show();
    }


    public void editPassword(View v) {

        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.edit_password, null);

        final EditText newPassword1 = (EditText) alertLayout.findViewById(R.id.enterPassword1);
        final EditText newPassword2 = (EditText) alertLayout.findViewById(R.id.enterPassword2);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.change_password);
        alert.setView(alertLayout);
        alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alert.setPositiveButton(R.string.change, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String passwordBox1 = newPassword1.getText().toString();
                String passwordBox2 = newPassword2.getText().toString();

                if (passwordBox1 == null || passwordBox1.isEmpty() || passwordBox1 == "") {
                    Toast.makeText(getBaseContext(), R.string.fields_cant_be_empty, Toast.LENGTH_SHORT).show();

                } else {
                    if (passwordBox1.equals(passwordBox2)) {
                        String password = passwordBox1;
                        myDb.changePassword(passwordBox1, username);
                        Toast.makeText(getBaseContext(), R.string.sucess, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        newPassword1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    newPassword1.setHint("");
                else
                    newPassword1.setHint(R.string.enter_new_password);
            }
        });

        newPassword2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    newPassword2.setHint("");
                else
                    newPassword2.setHint(R.string.confirm_password);
            }
        });

        final AlertDialog dialog = alert.create();
        dialog.show();
    }
}
