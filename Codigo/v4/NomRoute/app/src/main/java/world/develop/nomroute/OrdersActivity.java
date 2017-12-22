package world.develop.nomroute;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Miguel on 01/12/2016.
 */

public class OrdersActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ListView lista;
    private ArrayAdapter adapter;
    private ArrayList<String> arrayList;
    private String username;
    private DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

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
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);




        lista = (ListView) findViewById(R.id.ordersList);
        arrayList = new ArrayList<String>();
        arrayList = fillList(username);

        adapter = new ArrayAdapter<String>(this, R.layout.simplesorderlist, arrayList);

        lista.setAdapter(adapter);
    }

    public ArrayList fillList(String username) {
        ArrayList<String> array = new ArrayList<>();
        Cursor res = myDb.getUserTracks(username);

        if (res.getCount() != 0) {
            while (res.moveToNext()) {
                array.add(res.getString(2) + " - " + res.getString(1));
            }
        }
        return array;

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
