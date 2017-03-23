package us.tier5.u_rang;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.login.LoginManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import FragmentClasses.Contact_fragment;
import FragmentClasses.HowItWorks_Fragment;
import FragmentClasses.Orders_fragment;
import FragmentClasses.Profile_fragment;

/**
 * Created by root on 16/8/16.
 */

public class DashboardNew extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        Orders_fragment.OnFragmentInteractionListener,
        Contact_fragment.OnFragmentInteractionListener,
        Profile_fragment.OnFragmentInteractionListener,
        HowItWorks_Fragment.OnFragmentInteractionListener {

    //fragment variables
    Fragment fragment = null;
    Class fragmentClass = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_new);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //toolbar.setTitle("dashboard");



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        String classname = getIntent().getStringExtra("classname");
        try {
            Class<?> clazz = Class.forName(classname);
            //starting the first fragment
            fragmentClass = clazz;
            try
            {
                fragment = (Fragment) fragmentClass.newInstance();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.containerView, fragment).commit();
        }
        catch (Exception e)
        {
            Log.i("kingsukmajumder",e.toString());
        }

    }

    private int backButtonCount = 0;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(backButtonCount >= 1)
            {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            else
            {
                Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
                backButtonCount++;
            }
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();



        if (id == R.id.nav_camera) {
            // Handle the camera action
            Intent intent = new Intent(DashboardNew.this,Dashboard.class);
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {
            fragmentClass = Orders_fragment.class;
            try
            {
                fragment = (Fragment) fragmentClass.newInstance();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.containerView, fragment).commit();
        } else if (id == R.id.nav_contact) {
            fragmentClass = Contact_fragment.class;
            try
            {
                fragment = (Fragment) fragmentClass.newInstance();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.containerView, fragment).commit();
        } else if (id == R.id.profile) {
            fragmentClass = Profile_fragment.class;
            try
            {
                fragment = (Fragment) fragmentClass.newInstance();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.containerView, fragment).commit();

        } else if (id == R.id.howItWorks) {
            fragmentClass = HowItWorks_Fragment.class;
            try
            {
                fragment = (Fragment) fragmentClass.newInstance();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.containerView, fragment).commit();

        } /*else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/ else if (id == R.id.logout) {
            //Log.i("kingsukmajumder","logout");
            LoginManager.getInstance().logOut();
            SharedPreferences.Editor editor = getSharedPreferences("U-rang", MODE_PRIVATE).edit();
            editor.putInt("user_id", 0);
            if(editor.commit())
            {
                Intent intent = new Intent(DashboardNew.this,LoginActivity.class);
                startActivity(intent);
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onFragmentInteraction(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        invokeFragmentManagerNoteStateNotSaved();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void invokeFragmentManagerNoteStateNotSaved() {
        /**
         * For post-Honeycomb devices
         */
        try {
            Class cls = getClass();
            do {
                cls = cls.getSuperclass();
            } while (!"Activity".equals(cls.getSimpleName()));
            Field fragmentMgrField = cls.getDeclaredField("mFragments");
            fragmentMgrField.setAccessible(true);

            Object fragmentMgr = fragmentMgrField.get(this);
            cls = fragmentMgr.getClass();

            Method noteStateNotSavedMethod = cls.getDeclaredMethod("noteStateNotSaved", new Class[] {});
            noteStateNotSavedMethod.invoke(fragmentMgr, new Object[] {});
            Log.d("DLOutState", "Successful call for noteStateNotSaved!!!");
        } catch (Exception ex) {
            Log.e("DLOutState", "Exception on worka FM.noteStateNotSaved", ex);
        }
    }
}

