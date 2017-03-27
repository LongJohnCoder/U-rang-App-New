package us.tier5.u_rang;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import HelperClasses.AsyncResponse;
import HelperClasses.AsyncResponse6;
import HelperClasses.AsyncResponse7;
import HelperClasses.RegisterUser;
import HelperClasses.RegisterUser6;
import HelperClasses.RegisterUser7;
import Others.SaveUserData;

public class PickUpOptional extends AppCompatActivity implements View.OnClickListener, AsyncResponse.Response, AsyncResponse6.Response6, AsyncResponse7.Response7 {

    // Flag is set to prevent pickUp service
    boolean preventPickup = true;
    //page variable
    //Switch swboxed_or_hung;

    Spinner boxOrHungSpinner;

    TextView tvboxed_or_hung;
    Spinner strach_type;
    Switch swUrang_bag;
    TextView tvUrang_bag;
    EditText etSpecialInstruction;
    EditText etDrivingInstruction;
    Spinner client_type;
    TextView submitScheduleOptional;

    //server variables
    HashMap<String, String> data = new HashMap<String,String>();
    String route = "/V1/place-order";
    String routeProfileDetails = "/V1/getProgileDetails";
    RegisterUser registerUser = new RegisterUser("POST");
    RegisterUser6 registerUser6 = new RegisterUser6("POST");

    String routeUpdateLastPickupAddress = "/V1/updateProfileAddress";
    RegisterUser7 registerUser7 = new RegisterUser7("POST");

    //loading variables
    ProgressDialog loadingPlaceOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_up_optional);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        registerUser.delegate = this;
        registerUser6.delegate = this;
        registerUser7.delegate = this;


        SaveUserData.data_total.put("pay_method","1");
        SharedPreferences prefs = getSharedPreferences("U-rang", Context.MODE_PRIVATE);
        int user_id = prefs.getInt("user_id", 0);

        data.put("user_id",Integer.toString(user_id));
        registerUser6.register(data, routeProfileDetails);

        tvboxed_or_hung= (TextView) findViewById(R.id.tvboxed_or_hung);
        tvUrang_bag = (TextView) findViewById(R.id.tvUrang_bag);
        etSpecialInstruction = (EditText) findViewById(R.id.etSpecialInstruction);
        etDrivingInstruction = (EditText) findViewById(R.id.etDrivingInstruction);
        submitScheduleOptional= (TextView) findViewById(R.id.submitScheduleOptional);

        submitScheduleOptional.setOnClickListener(this);

        boxOrHungSpinner = (Spinner) findViewById(R.id.boxOrHungSpinner);

        //SaveUserData.data_total.put("boxed_or_hung","Not Specified");

        boxOrHungSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0)
                {
                    SaveUserData.data_total.put("boxed_or_hung","Not Specified");
                }
                else if(i==1)
                {
                    SaveUserData.data_total.put("boxed_or_hung","Boxed");
                }
                else if(i==2)
                {
                    SaveUserData.data_total.put("boxed_or_hung","Hung");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                SaveUserData.data_total.put("boxed_or_hung","Not Specified");
            }
        });

        /*swboxed_or_hung= (Switch) findViewById(R.id.swboxed_or_hung);
        swboxed_or_hung.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    tvboxed_or_hung.setText("Hung");
                    SaveUserData.data_total.put("boxed_or_hung","Hung");
                }
                else
                {
                    tvboxed_or_hung.setText("Boxed");
                    SaveUserData.data_total.put("boxed_or_hung","Boxed");
                }
            }
        });*/

        strach_type= (Spinner) findViewById(R.id.strach_type);
        strach_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getApplicationContext(),strach_type.getSelectedItem().toString(),Toast.LENGTH_SHORT).show();
                if(strach_type.getSelectedItem().toString().equals("Select"))
                {
                    SaveUserData.data_total.put("strach_type","No");
                }
                else
                {
                    if(strach_type.getSelectedItem().toString().equals("Very Light Starch"))
                    {
                        SaveUserData.data_total.put("strach_type","Very_light_starch");
                    }
                    else if(strach_type.getSelectedItem().toString().equals("Light Starch"))
                    {
                        SaveUserData.data_total.put("strach_type","Light_starch");
                    }
                    else if(strach_type.getSelectedItem().toString().equals("Medium Starch"))
                    {
                        SaveUserData.data_total.put("strach_type","Medium_starch");
                    }
                    else if(strach_type.getSelectedItem().toString().equals("Heavy Starch"))
                    {
                        SaveUserData.data_total.put("strach_type","Heavy_starch");
                    }
                    else
                    {
                        SaveUserData.data_total.put("strach_type","Not Specified");
                    }

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                SaveUserData.data_total.put("strach_type","Not Specified");
            }
        });

        swUrang_bag = (Switch) findViewById(R.id.swUrang_bag);
        swUrang_bag.setChecked(false);
        swUrang_bag.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    tvUrang_bag.setText("yes");
                    SaveUserData.data_total.put("urang_bag","1");
                }
                else
                {
                    tvUrang_bag.setText("no");
                    SaveUserData.data_total.remove("urang_bag");
                }
            }
        });

        /*client_type= (Spinner) findViewById(R.id.client_type);
        client_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getApplicationContext(),client_type.getSelectedItem().toString(),Toast.LENGTH_SHORT).show();
                if(client_type.getSelectedItem().toString().equals("Select"))
                {
                    SaveUserData.data_total.put("client_type","Not Specified");
                }
                else
                {
                    SaveUserData.data_total.put("client_type",client_type.getSelectedItem().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                SaveUserData.data_total.put("client_type","Not Specified");
            }
        });*/


        /*if(SaveUserData.data_total.get("driving_ins")!=null)
        {
            etDrivingInstruction.setText(SaveUserData.data_total.get("driving_ins"));
        }

        if(SaveUserData.data_total.get("spcl_ins")!=null)
        {
            etSpecialInstruction.setText(SaveUserData.data_total.get("spcl_ins"));
        }
        Log.i("kingsukmajumder","strach type "+SaveUserData.data_total.get("strach_type"));

        if(SaveUserData.data_total.get("urang_bag").equals("1"))
        {
            tvUrang_bag.setText("yes");
            swUrang_bag.setChecked(true);
        }
        else if(SaveUserData.data_total.get("urang_bag").equals("0"))
        {
            tvUrang_bag.setText("no");
            swUrang_bag.setChecked(false);
        }

        if(SaveUserData.data_total.get("strach_type")!=null)
        {
            if(SaveUserData.data_total.get("strach_type").equals("Very_light_starch"))
            {
                strach_type.setSelection(1);
            }
            else if(SaveUserData.data_total.get("strach_type").equals("Light_starch"))
            {
                strach_type.setSelection(2);
            }
            else if(SaveUserData.data_total.get("strach_type").equals("Medium_starch"))
            {
                strach_type.setSelection(3);
            }
            else if(SaveUserData.data_total.get("strach_type").equals("Heavy_starch"))
            {
                strach_type.setSelection(4);
            }
        }

        if(SaveUserData.data_total.get("boxed_or_hung")!=null)
        {
            if(SaveUserData.data_total.get("boxed_or_hung").equals("Boxed"))
            {
                //swboxed_or_hung.setChecked(false);
                tvboxed_or_hung.setText("Boxed");
                boxOrHungSpinner.setSelection(1);
            }
            else if(SaveUserData.data_total.get("boxed_or_hung").equals("Hung"))
            {
                //swboxed_or_hung.setChecked(true);
                tvboxed_or_hung.setText("Hung");
                boxOrHungSpinner.setSelection(2);
            }
        }*/


    }

    @Override
    public void onClick(View v) {
        preventPickup = false;
        registerUser6.register(data, routeProfileDetails);
    }

    @Override
    public void processFinish(String output) {
        Log.i("kingsukmajumder",output);
        loadingPlaceOrder.dismiss();
        try
        {
            registerUser6.register(data, routeProfileDetails);
            JSONObject jsonObject = new JSONObject(output);
            if (jsonObject.getBoolean("status")) {
                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PickUpOptional.this, DashboardNew.class);
                intent.putExtra("classname", "FragmentClasses.Orders_fragment");
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PickUpOptional.this, DashboardNew.class);
                intent.putExtra("classname", "FragmentClasses.Orders_fragment");
                startActivity(intent);
            }
        }
        catch (Exception e)
        {
            Log.i("kingsukmajumder",e.toString());
        }
    }

    @Override
    public void processFinish6(String output) {
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
            } else if (jsonObject.getInt("status_code") == 200) {
                JSONObject response = jsonObject.getJSONObject("response");
                String userEmail = response.getString("email");
                JSONObject userDetails = response.getJSONObject("user_details");
                String userName = userDetails.getString("name");
                String userPersonalPhone = userDetails.getString("personal_ph");
                String userSpecialInstruction = userDetails.getString("spcl_instructions");
                String userDrivingInstruction = userDetails.getString("driving_instructions");
                SharedPreferences sharedPreferences = getSharedPreferences("U-rang", Context.MODE_PRIVATE);
                SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
                sharedPreferencesEditor.putString("name", userName);
                sharedPreferencesEditor.putString("email", userEmail);
                sharedPreferencesEditor.putString("personal_phone", userPersonalPhone);
                sharedPreferencesEditor.putString("spcl_instructions", userSpecialInstruction);
                sharedPreferencesEditor.putString("driving_instructions", userDrivingInstruction);

                sharedPreferencesEditor.apply();

                // Start pickUp service
                SaveUserData.data_total.put("spcl_ins",etSpecialInstruction.getText().toString());
                SaveUserData.data_total.put("driving_ins",etDrivingInstruction.getText().toString());
                Log.i("kingsukmajumder",SaveUserData.data_total.toString());
                if (!preventPickup) {
                    SharedPreferences prefs = getSharedPreferences("U-rang", Context.MODE_PRIVATE);
                    String name = prefs.getString("name", "");
                    String email = prefs.getString("email", "");
                    String personalPhone = prefs.getString("personal_phone", "");
                    SaveUserData.data_total.put("email", email);
                    SaveUserData.data_total.put("name", name);
                    SaveUserData.data_total.put("personal_phone", personalPhone);
                    SaveUserData.data_total.put("spcl_instructions", etSpecialInstruction.getText().toString());
                    SaveUserData.data_total.put("driving_instructions", etDrivingInstruction.getText().toString());
                    Log.i("SAVED_USER_DATA", SaveUserData.data_total.toString());
                    registerUser7.register(SaveUserData.data_total, routeUpdateLastPickupAddress);

                    loadingPlaceOrder = ProgressDialog.show(PickUpOptional.this, "", "Placing order", true, false);
                    registerUser.register(SaveUserData.data_total, route);
                    preventPickup = true;
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

    @Override
    public void processFinish7(String output) {
        try {
            JSONObject jsonObject = new JSONObject(output);
            Log.i("UPDATE_ADDRESS", jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
