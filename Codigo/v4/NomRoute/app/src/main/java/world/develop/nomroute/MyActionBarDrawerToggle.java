package world.develop.nomroute;

import android.app.Activity;
import android.support.annotation.StringRes;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.View;

/**
 * Created by Miguel on 21/11/2016.
 */

public class MyActionBarDrawerToggle extends ActionBarDrawerToggle {

    private Activity mHostActivity;
    private int mOpenedResource;
    private int mClosedResource;

    public MyActionBarDrawerToggle(Activity activity, DrawerLayout drawerLayout,
                                   int openDrawerContentDescRes, int closeDrawerContentDescRes) {
        super(activity, drawerLayout, openDrawerContentDescRes, closeDrawerContentDescRes);

        mHostActivity = activity;
        mOpenedResource = openDrawerContentDescRes;
        mClosedResource = closeDrawerContentDescRes;
    }

    public void OnDrawerOpened(View view){
        super.onDrawerOpened(view);
    }

    public void OnDrawerClosed(View view){
        super.onDrawerClosed(view);
    }

    public void OnDrawerSlide(View view, float slideOffSet){
        super.onDrawerSlide(view, slideOffSet);
    }

}
