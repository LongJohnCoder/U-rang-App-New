package us.tier5.u_rang;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import HelperClasses.AsyncResponse;
import HelperClasses.RegisterUser;
import Others.GPSTracker;

public class Add_address extends AppCompatActivity implements View.OnClickListener, AsyncResponse.Response {
    // GPSTracker class
    GPSTracker gps;

    //page variables
    LinearLayout llGetLocation;
    EditText etLocality;
    EditText etFlatNo;
    EditText etLandmark;
    TextView tvSaveAddress;
    String tagName = "";
    ImageView ivHomeTag;
    ImageView ivOfficeTag;
    ImageView ivOtherTag;
    int user_id = 0;

    //city and zipcode adding
    EditText etCity;
    EditText etState;
    EditText etZipCode;


    //loading variables
    ProgressDialog loading;

    //server variable
    HashMap<String, String> data = new HashMap<>();
    String routeProfileDetails = "/V1/getProgileDetails";
    RegisterUser registerUser = new RegisterUser("POST");

    public static final int MY_PERMISSION_GET_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        registerUser.delegate = this;


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences prefs = getSharedPreferences("U-rang", Context.MODE_PRIVATE);
        user_id = prefs.getInt("user_id", 0);

        data.put("user_id",Integer.toString(user_id));

        registerUser.register(data, routeProfileDetails);

        etLocality = (EditText) findViewById(R.id.etLocality);
        etFlatNo = (EditText) findViewById(R.id.etFlatNo);
        etLandmark = (EditText) findViewById(R.id.etLandmark);
        tvSaveAddress = (TextView) findViewById(R.id.tvSaveAddress);
        tvSaveAddress.setOnClickListener(this);

        //city and zipcode adding
        etCity = (EditText) findViewById(R.id.etCity);
        etState = (EditText) findViewById(R.id.etState);
        etZipCode = (EditText) findViewById(R.id.etZipCode);


        llGetLocation = (LinearLayout) findViewById(R.id.llGetLocation);
        llGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(getApplicationContext(),""+ Build.VERSION.SDK_INT,Toast.LENGTH_SHORT).show();

                if (ContextCompat.checkSelfPermission(Add_address.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(Add_address.this,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {

                        // Show an expanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                        // create class object
                        gps = new GPSTracker(Add_address.this);

                        // check if GPS enabled
                        if (gps.canGetLocation()) {

                            double latitude = gps.getLatitude();
                            double longitude = gps.getLongitude();

                            // \n is for new line
                            //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                        } else {
                            // can't get location
                            // GPS or Network is not enabled
                            // Ask user to enable GPS/network in settings
                            gps.showSettingsAlert();
                        }

                    } else {

                        // No explanation needed, we can request the permission.

                        ActivityCompat.requestPermissions(Add_address.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                MY_PERMISSION_GET_LOCATION);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                } else {
                    // create class object
                    gps = new GPSTracker(Add_address.this);

                    // check if GPS enabled
                    if (gps.canGetLocation()) {

                        double latitude = gps.getLatitude();
                        double longitude = gps.getLongitude();

                        // \n is for new line
                        //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                        HashMap<String, String> address = getAddress(latitude, longitude);
                        if (!address.equals(null) || !address.equals("")) {
                            etLocality.setText(address.get("addressLine"));
                            etCity.setText(address.get("city"));
                            etState.setText(address.get("state"));
                            etZipCode.setText(address.get("zipCode"));
                        } else {
                            Toast.makeText(getApplicationContext(), "Cannot get your address currently,Enter manually", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        // can't get location
                        // GPS or Network is not enabled
                        // Ask user to enable GPS/network in settings
                        gps.showSettingsAlert();
                    }
                }

            }
        });

        ivHomeTag = (ImageView) findViewById(R.id.ivHomeTag);
        ivHomeTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivHomeTag.setImageResource(R.drawable.home_enable);
                ivOfficeTag.setImageResource(R.drawable.office);
                ivOtherTag.setImageResource(R.drawable.other);
                tagName = "Home";

            }
        });
        ivOfficeTag = (ImageView) findViewById(R.id.ivOfficeTag);
        ivOfficeTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivOfficeTag.setImageResource(R.drawable.office_enable);
                ivHomeTag.setImageResource(R.drawable.home);
                ivOtherTag.setImageResource(R.drawable.other);
                tagName = "Office";
            }
        });
        ivOtherTag = (ImageView) findViewById(R.id.ivOtherTag);
        ivOtherTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivOtherTag.setImageResource(R.drawable.other_enable);
                ivHomeTag.setImageResource(R.drawable.home);
                ivOfficeTag.setImageResource(R.drawable.office);
                tagName = "Other";
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_GET_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    // create class object
                    gps = new GPSTracker(Add_address.this);

                    // check if GPS enabled
                    if (gps.canGetLocation()) {
                        double latitude = gps.getLatitude();
                        double longitude = gps.getLongitude();

                        HashMap<String, String> address = getAddress(latitude, longitude);
                        if (!address.isEmpty()) {
                            etLocality.setText(address.get("addressLine"));
                            etCity.setText(address.get("city"));
                            etState.setText(address.get("state"));
                            etZipCode.setText(address.get("zipCode"));
                        } else {
                            Toast.makeText(getApplicationContext(), "Cannot get your address currently,Enter manually", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        // can't get location
                        // GPS or Network is not enabled
                        // Ask user to enable GPS/network in settings
                        gps.showSettingsAlert();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "You have to provide permission to show location", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public HashMap<String, String> getAddress(double latitude, double longitude) {
        Geocoder geocoder;
        HashMap<String, String> addressMap = new HashMap<>();
        List<Address> addressList;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addressList = geocoder.getFromLocation(
                    latitude, longitude, 1);
            Log.i("ADDRESS_ARRAY", addressList.toString());
            if (!addressList.isEmpty() && addressList.size() > 0) {
                Address address = addressList.get(0);
                addressMap.put("addressLine", address.getAddressLine(0));
                addressMap.put("city", address.getLocality());
                addressMap.put("state", address.getAdminArea());
                addressMap.put("zipCode", address.getPostalCode());
            }
            return addressMap;
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Cannot get your address currently,Enter manually", Toast.LENGTH_LONG).show();
        }

        return addressMap;
    }

    @Override
    public void onClick(View view) {
        registerUser.register(data, routeProfileDetails);
        String apartmentNo = etFlatNo.getText().toString();
        String addressLine = etLocality.getText().toString();
        String city = etCity.getText().toString();
        String state = etState.getText().toString();
        String zipCode = etZipCode.getText().toString();
        String landmark = etLandmark.getText().toString();

        if (isEmpty(apartmentNo) || isEmpty(addressLine) || isEmpty(city) || isEmpty(state) || isEmpty(zipCode)) {
            Snackbar.make(view, "All mandatory fields are required", Snackbar.LENGTH_LONG).show();
        } else {
            if (!tagName.equals("")) {
                SharedPreferences prefs = getSharedPreferences("U-rang", MODE_PRIVATE);
                String address_json = prefs.getString("address_json", "");

                if (!address_json.isEmpty()) {
                    //Toast.makeText(getApplicationContext(),"not empty append new",Toast.LENGTH_SHORT).show();
                    try {
                        JSONArray jsonArray = new JSONArray(address_json);
                        JSONObject jsonObject = new JSONObject();

                        jsonObject.put("index", jsonArray.length() + 1);
                        jsonObject.put("user_id", Integer.toString(user_id));
                        jsonObject.put("tagname", tagName);
                        jsonObject.put("flat", apartmentNo);
                        jsonObject.put("landmark", landmark);
                        jsonObject.put("locality", addressLine + "," + city + "," + state + "," + zipCode);

                        jsonArray.put(jsonObject);

                        SharedPreferences.Editor editor = getSharedPreferences("U-rang", MODE_PRIVATE).edit();
                        editor.putString("address_json", jsonArray.toString());
                        if (editor.commit()) {
                            Toast.makeText(getApplicationContext(), "New address added successfully.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Add_address.this, Order_details.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "Error while adding new address!", Toast.LENGTH_SHORT).show();
                        }

                        Log.i("kingsukmajumder", jsonArray.toString());
                    } catch (Exception e) {
                        Log.i("kingsukmajumder", e.toString());
                        Toast.makeText(getApplicationContext(), "Error while saving the address!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    JSONArray jsonArray = new JSONArray();
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("index", 1);
                        jsonObject.put("user_id", Integer.toString(user_id));
                        jsonObject.put("tagname", tagName);
                        jsonObject.put("flat", etFlatNo.getText().toString());
                        jsonObject.put("landmark", etLandmark.getText().toString());
                        jsonObject.put("locality", addressLine + "," + city + "," + state + "," + zipCode);

                        jsonArray.put(jsonObject);

                        SharedPreferences.Editor editor = getSharedPreferences("U-rang", MODE_PRIVATE).edit();
                        editor.putString("address_json", jsonArray.toString());
                        if (editor.commit()) {
                            Toast.makeText(getApplicationContext(), "New address added successfully.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Add_address.this, Order_details.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "Error while adding new address!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.i("kingsukmajumder", e.toString());
                        Toast.makeText(getApplicationContext(), "Error while saving the new address!", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Snackbar.make(view, "Select an address type", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private boolean isEmpty(String string) {
        return string.trim().length() <= 0;
    }

    @Override
    public void processFinish(String output) {
        try {
            JSONObject jsonObject = new JSONObject(output);
            Log.v("PROFILE_STATUS:", jsonObject.toString());
            if (jsonObject.getInt("status_code") == 301) {
                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                LoginManager.getInstance().logOut();
                SharedPreferences.Editor editor = getSharedPreferences("U-rang", Context.MODE_PRIVATE).edit();
                editor.putInt("user_id", 0);
                if (editor.commit()) {
                    Intent intent = new Intent(getApplicationContext(), Splash.class);
                    startActivity(intent);
                }
            } else if (jsonObject.getInt("status_code") == 400) {
                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                LoginManager.getInstance().logOut();
                SharedPreferences.Editor editor = getSharedPreferences("U-rang", Context.MODE_PRIVATE).edit();
                editor.putInt("user_id", 0);
                editor.putBoolean("is_social_registered", false);
                if (editor.commit()) {
                    Intent intent = new Intent(getApplicationContext(), Splash.class);
                    startActivity(intent);
                }
            } else {
                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e)
        {
            //Toast.makeText(getContext(),"Error in fetching order history!",Toast.LENGTH_SHORT).show();
            Log.i("kingsukmajumder","error in profile"+e.toString());
        }
    }

}
