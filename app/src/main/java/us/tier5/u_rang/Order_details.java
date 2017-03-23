package us.tier5.u_rang;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.datetimepicker.date.DatePickerDialog;
import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;
import com.facebook.login.LoginManager;
import com.roger.gifloadinglibrary.GifLoadingView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import HelperClasses.AsyncResponse;
import HelperClasses.AsyncResponse2;
import HelperClasses.CheckNetwork;
import HelperClasses.RegisterUser;
import HelperClasses.RegisterUser2;
import Others.SaveUserData;

public class Order_details extends AppCompatActivity implements AsyncResponse.Response, AsyncResponse2.Response2, CompoundButton.OnCheckedChangeListener,View.OnClickListener,DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateChangedListener{
    //page variables
    LinearLayout parentLL;
    ArrayList<RadioButton> radioArray = new ArrayList<>();
    String ADDRESS = "";
    LinearLayout lladdNewAddress;
    ImageView ivDatePicker;
    TextView tvContinue;
    TextView tvShowSelectedDate;

    Button btnDateTomorroy;
    Button btnDateSecond;
    Button btnDayThird;
    Button btnDateToday;

    //server variable
    HashMap<String, String> data = new HashMap<>();
    String route = "/V1/get-user-details";
    String routeProfileDetails = "/V1/getProgileDetails";
    RegisterUser registerUser = new RegisterUser("POST");
    RegisterUser2 registerUser2 = new RegisterUser2("POST");

    //user variables
    int user_id;

    boolean notChecked = true;

    //gif loader variables
    GifLoadingView mGifLoadingView;

    //total data variable
    //HashMap<String, String> data_total = SaveUserData.data_total;

    //date picker
    private Calendar calendar;
    private DateFormat dateFormat;
    private SimpleDateFormat timeFormat;
    private static final String TIME_PATTERN = "HH:mm";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*Intent previousIntent = getIntent();
        data_total =(HashMap<String, String>) previousIntent.getSerializableExtra("data_total");*/
        //Toast.makeText(getApplicationContext(),data_total.toString(),Toast.LENGTH_SHORT).show();

        registerUser.delegate = this;
        registerUser2.delegate = this;
        mGifLoadingView = new GifLoadingView();
        mGifLoadingView.setImageResource(R.drawable.loading_3);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        parentLL = (LinearLayout) findViewById(R.id.parentLL);
        tvContinue = (TextView) findViewById(R.id.tvContinue);
        tvShowSelectedDate = (TextView) findViewById(R.id.tvShowSelectedDate);

        //getting current date
        Calendar c = Calendar.getInstance();

        final SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        final SimpleDateFormat properDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());
        //setting current date
        SaveUserData.data_total.put("pick_up_date",properDateFormat.format(c.getTime()));
        tvShowSelectedDate.setText(formattedDate);
        SaveUserData.data_total.put("SHOW_pick_up_date",df.format(c.getTime()));




        tvContinue.setOnClickListener(this);

        //connet to server to get user details
        SharedPreferences prefs = getSharedPreferences("U-rang", Context.MODE_PRIVATE);
        user_id = prefs.getInt("user_id", 0);

        //Toast.makeText(getContext(),""+user_id,Toast.LENGTH_SHORT).show();
        data.put("user_id",Integer.toString(user_id));

        registerUser2.register(data, routeProfileDetails);

        //calender and date picker variables
        calendar = Calendar.getInstance();
        dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        timeFormat = new SimpleDateFormat(TIME_PATTERN, Locale.getDefault());


        if(CheckNetwork.isInternetAvailable(getApplicationContext())) //returns true if internet available
        {

            mGifLoadingView.show(getFragmentManager(), "Loading");
            mGifLoadingView.setBlurredActionBar(true);
            registerUser.register(data,route);
        }
        else
        {
            Toast.makeText(getApplicationContext(),"No Internet Connection",Toast.LENGTH_SHORT).show();
        }

        lladdNewAddress = (LinearLayout) findViewById(R.id.lladdNewAddress);
        lladdNewAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),"add address page",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Order_details.this,Add_address.class);
                startActivity(intent);
            }
        });

        ivDatePicker = (ImageView) findViewById(R.id.ivDatePicker);
        ivDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        Order_details.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
                dpd.setMinDate(now);
            }
        });

        final SimpleDateFormat dateFormatGlobalToShow = new SimpleDateFormat("dd-MMM-yyyy");
        final SimpleDateFormat dateFormatGlobalProper = new SimpleDateFormat("yyyy-MM-dd");

        btnDateToday = (Button) findViewById(R.id.btnDateToday);
        btnDateToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendarNow = Calendar.getInstance();
                calendarNow.add(Calendar.DAY_OF_YEAR, 0);
                Log.i("kingsukmajumder","next date is "+ calendarNow.getTime());

                SaveUserData.data_total.put("pick_up_date",dateFormatGlobalProper.format(calendarNow.getTime()));
                tvShowSelectedDate.setText(dateFormatGlobalToShow.format(calendarNow.getTime()));
                SaveUserData.data_total.put("SHOW_pick_up_date",dateFormatGlobalToShow.format(calendar.getTime()));

                changeIntent();
            }
        });

        btnDateTomorroy = (Button) findViewById(R.id.btnDateTomorroy);

        btnDateTomorroy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendarNow = Calendar.getInstance();
                calendarNow.add(Calendar.DAY_OF_YEAR, 1);
                Log.i("kingsukmajumder","next date is "+ calendarNow.getTime());

                SaveUserData.data_total.put("pick_up_date",dateFormatGlobalProper.format(calendarNow.getTime()));
                tvShowSelectedDate.setText(dateFormatGlobalToShow.format(calendarNow.getTime()));
                SaveUserData.data_total.put("SHOW_pick_up_date",dateFormatGlobalToShow.format(calendar.getTime()));

                changeIntent();
            }
        });

        btnDateSecond = (Button) findViewById(R.id.btnDateSecond);
        Calendar calendarDateToShowOnButton = Calendar.getInstance();
        calendarDateToShowOnButton.add(Calendar.DAY_OF_YEAR, 2);

        btnDateSecond.setText(dateFormatGlobalToShow.format(calendarDateToShowOnButton.getTime()));
        btnDateSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendarNow = Calendar.getInstance();
                calendarNow.add(Calendar.DAY_OF_YEAR, 2);
                Log.i("kingsukmajumder","next date is "+ calendarNow.getTime());

                SaveUserData.data_total.put("pick_up_date",dateFormatGlobalProper.format(calendarNow.getTime()));
                tvShowSelectedDate.setText(dateFormatGlobalToShow.format(calendarNow.getTime()));
                SaveUserData.data_total.put("SHOW_pick_up_date",dateFormatGlobalToShow.format(calendarNow.getTime()));

                changeIntent();

            }
        });

        btnDayThird = (Button) findViewById(R.id.btnDayThird);
        calendarDateToShowOnButton.add(Calendar.DAY_OF_YEAR, 1);

        btnDayThird.setText(dateFormatGlobalToShow.format(calendarDateToShowOnButton.getTime()));
        btnDayThird.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendarNow = Calendar.getInstance();
                calendarNow.add(Calendar.DAY_OF_YEAR, 3);
                Log.i("kingsukmajumder","next date is "+ calendarNow.getTime());

                SaveUserData.data_total.put("pick_up_date",dateFormatGlobalProper.format(calendarNow.getTime()));
                tvShowSelectedDate.setText(dateFormatGlobalToShow.format(calendarNow.getTime()));
                SaveUserData.data_total.put("SHOW_pick_up_date",dateFormatGlobalToShow.format(calendarNow.getTime()));

                changeIntent();
            }
        });

    }

    @Override
    public void processFinish(String output) {
        Log.i("kingsukmajumder",output);
        if (mGifLoadingView != null) {
            mGifLoadingView.dismiss();
        }
        try
        {
            JSONObject jsonObject = new JSONObject(output);
            if(jsonObject.getBoolean("status"))
            {
                JSONObject response = new JSONObject(jsonObject.getString("response"));
                JSONObject user_details = new JSONObject(response.getString("user_details"));

                if(user_details.isNull("address_line_1"))
                {
                    SharedPreferences prefs = getSharedPreferences("U-rang", MODE_PRIVATE);
                    String address_json = prefs.getString("address_json", "");
                    if(address_json.isEmpty())
                    {
                        View inflatedLayout= getLayoutInflater().inflate(R.layout.no_address, null, false);
                        parentLL.addView(inflatedLayout);
                    }
                    else
                    {
                        View inflatedLayout= getLayoutInflater().inflate(R.layout.addresses, null, false);
                        LinearLayout radioParentLL = (LinearLayout) inflatedLayout.findViewById(R.id.radioParentLL);


                        JSONArray address_array = new JSONArray(address_json);
                        Log.i("kingsukmajumder1",""+address_array.length());
                        if(address_array.length()==1)
                        {
                            for(int i=0;i<address_array.length();i++)
                            {
                                JSONObject currentAddress = address_array.getJSONObject(i);

                                int addressUserId = Integer.parseInt(currentAddress.getString("user_id"));
                                //Toast.makeText(getApplicationContext(),""+addressUserId,Toast.LENGTH_SHORT).show();

                                if(addressUserId==user_id)
                                {
                                    View inflatedLayoutRadio= getLayoutInflater().inflate(R.layout.radio_layout, null, false);
                                    RadioButton rbAddress = (RadioButton) inflatedLayoutRadio.findViewById(R.id.rbAddress);
                                    rbAddress.setChecked(true);
                                    TextView tvTagName = (TextView) inflatedLayoutRadio.findViewById(R.id.tvTagName);
                                    final ImageView ivRemoveRadio = (ImageView) inflatedLayoutRadio.findViewById(R.id.ivRemoveRadio);
                                    rbAddress.setId(200+i);

                                    radioArray.add(rbAddress);
                                    rbAddress.setOnCheckedChangeListener(this);
                                    radioParentLL.addView(inflatedLayoutRadio);


                                    String total_address_string = "";
                                    if(currentAddress.getString("flat").equals(""))
                                    {
                                        total_address_string = "";
                                    }
                                    else
                                    {
                                        total_address_string = currentAddress.getString("flat")+",";
                                    }
                                    if(currentAddress.getString("landmark").equals(""))
                                    {
                                        total_address_string = total_address_string;
                                    }
                                    else
                                    {
                                        total_address_string = total_address_string+currentAddress.getString("landmark")+"\n";
                                    }
                                    rbAddress.setText(total_address_string+currentAddress.getString("locality"));
                                    ADDRESS = total_address_string+currentAddress.getString("locality");
                                    rbAddress.setTag(currentAddress.getInt("index"));
                                    tvTagName.setText(currentAddress.getString("tagname"));
                                    ivRemoveRadio.setTag(currentAddress.getInt("index"));

                                    ivRemoveRadio.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            removeRadio(ivRemoveRadio);
                                        }
                                    });
                                }


                            }
                            parentLL.addView(inflatedLayout);
                        }
                        else
                        {
                            for(int i=0;i<address_array.length();i++)
                            {
                                JSONObject currentAddress = address_array.getJSONObject(i);

                                int addressUserId = Integer.parseInt(currentAddress.getString("user_id"));
                                //Toast.makeText(getApplicationContext(),""+addressUserId,Toast.LENGTH_SHORT).show();

                                if(addressUserId==user_id)
                                {
                                    View inflatedLayoutRadio= getLayoutInflater().inflate(R.layout.radio_layout, null, false);
                                    RadioButton rbAddress = (RadioButton) inflatedLayoutRadio.findViewById(R.id.rbAddress);
                                    if(notChecked)
                                    {
                                        rbAddress.setChecked(true);
                                        ADDRESS = currentAddress.getString("locality");
                                        notChecked = false;
                                    }
                                    TextView tvTagName = (TextView) inflatedLayoutRadio.findViewById(R.id.tvTagName);
                                    final ImageView ivRemoveRadio = (ImageView) inflatedLayoutRadio.findViewById(R.id.ivRemoveRadio);
                                    rbAddress.setId(200+i);

                                    radioArray.add(rbAddress);
                                    rbAddress.setOnCheckedChangeListener(this);
                                    radioParentLL.addView(inflatedLayoutRadio);


                                    String total_address_string = "";
                                    if(currentAddress.getString("flat").equals(""))
                                    {
                                        total_address_string = "";
                                    }
                                    else
                                    {
                                        total_address_string = currentAddress.getString("flat")+",";
                                    }
                                    if(currentAddress.getString("landmark").equals(""))
                                    {
                                        total_address_string = total_address_string;
                                    }
                                    else
                                    {
                                        total_address_string = total_address_string+currentAddress.getString("landmark")+"\n";
                                    }
                                    rbAddress.setText(total_address_string+currentAddress.getString("locality"));
                                    rbAddress.setTag(currentAddress.getInt("index"));
                                    tvTagName.setText(currentAddress.getString("tagname"));
                                    ivRemoveRadio.setTag(currentAddress.getInt("index"));

                                    ivRemoveRadio.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            removeRadio(ivRemoveRadio);
                                        }
                                    });
                                }


                            }
                            parentLL.addView(inflatedLayout);
                        }


                    }
                }
                else
                {
                    View inflatedLayout= getLayoutInflater().inflate(R.layout.addresses, null, false);
                    LinearLayout radioParentLL = (LinearLayout) inflatedLayout.findViewById(R.id.radioParentLL);

                    SharedPreferences prefs = getSharedPreferences("U-rang", MODE_PRIVATE);
                    String address_json = prefs.getString("address_json", "");

                    if(address_json.equals(""))
                    {
                        View inflatedLayoutRadio= getLayoutInflater().inflate(R.layout.radio_layout, null, false);
                        RadioButton rbAddress = (RadioButton) inflatedLayoutRadio.findViewById(R.id.rbAddress);
                        rbAddress.setChecked(true);
                        ADDRESS = user_details.getString("address_line_1");
                        TextView tvTagName = (TextView) inflatedLayoutRadio.findViewById(R.id.tvTagName);
                        final ImageView ivRemoveRadio = (ImageView) inflatedLayoutRadio.findViewById(R.id.ivRemoveRadio);
                        rbAddress.setId(200+200);

                        radioArray.add(rbAddress);
                        rbAddress.setOnCheckedChangeListener(this);
                        radioParentLL.addView(inflatedLayoutRadio);

                        rbAddress.setText(user_details.getString("address_line_1"));
                        rbAddress.setTag("savedAddress");
                        tvTagName.setText("Registered Address");
                        ivRemoveRadio.setVisibility(View.INVISIBLE);
                        parentLL.addView(inflatedLayout);

                    }
                    else
                    {
                        JSONArray address_array = new JSONArray(address_json);
                        Log.i("kingsukmajumder",address_array.toString());

                        for(int i=0;i<=address_array.length();i++)
                        {


                            if(i==address_array.length())
                            {
                                View inflatedLayoutRadio= getLayoutInflater().inflate(R.layout.radio_layout, null, false);
                                RadioButton rbAddress = (RadioButton) inflatedLayoutRadio.findViewById(R.id.rbAddress);
                                rbAddress.setChecked(true);
                                ADDRESS = user_details.getString("address_line_1");
                                TextView tvTagName = (TextView) inflatedLayoutRadio.findViewById(R.id.tvTagName);
                                final ImageView ivRemoveRadio = (ImageView) inflatedLayoutRadio.findViewById(R.id.ivRemoveRadio);
                                rbAddress.setId(200+i);

                                radioArray.add(rbAddress);
                                rbAddress.setOnCheckedChangeListener(this);

                                rbAddress.setText(user_details.getString("address_line_1"));
                                rbAddress.setTag("savedAddress");
                                tvTagName.setText("Registered Address");
                                ivRemoveRadio.setVisibility(View.INVISIBLE);
                                radioParentLL.addView(inflatedLayoutRadio);

                                ivRemoveRadio.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        removeRadio(ivRemoveRadio);
                                    }
                                });
                            }
                            else
                            {
                                View inflatedLayoutRadio= getLayoutInflater().inflate(R.layout.radio_layout, null, false);
                                RadioButton rbAddress = (RadioButton) inflatedLayoutRadio.findViewById(R.id.rbAddress);
                                TextView tvTagName = (TextView) inflatedLayoutRadio.findViewById(R.id.tvTagName);
                                final ImageView ivRemoveRadio = (ImageView) inflatedLayoutRadio.findViewById(R.id.ivRemoveRadio);
                                rbAddress.setId(200+i);

                                radioArray.add(rbAddress);
                                rbAddress.setOnCheckedChangeListener(this);
                                JSONObject currentAddress = address_array.getJSONObject(i);

                                int addressUserId = Integer.parseInt(currentAddress.getString("user_id"));
                                //Toast.makeText(getApplicationContext(),""+addressUserId,Toast.LENGTH_SHORT).show();

                                if(addressUserId==user_id)
                                {
                                    String total_address_string = "";
                                    if(currentAddress.getString("flat").equals(""))
                                    {
                                        total_address_string = "";
                                    }
                                    else
                                    {
                                        total_address_string = currentAddress.getString("flat")+",";
                                    }
                                    if(currentAddress.getString("landmark").equals(""))
                                    {
                                        total_address_string = total_address_string;
                                    }
                                    else
                                    {
                                        total_address_string = total_address_string+currentAddress.getString("landmark")+"\n";
                                    }
                                    rbAddress.setText(total_address_string+currentAddress.getString("locality"));
                                    rbAddress.setTag(currentAddress.getInt("index"));
                                    tvTagName.setText(currentAddress.getString("tagname"));
                                    ivRemoveRadio.setTag(currentAddress.getInt("index"));
                                    radioParentLL.addView(inflatedLayoutRadio);
                                }

                                ivRemoveRadio.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        removeRadio(ivRemoveRadio);
                                    }
                                });

                            }

                        }
                        parentLL.addView(inflatedLayout);
                    }


                }
            }
            else
            {
                Toast.makeText(getApplicationContext(),jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            Toast.makeText(getApplication(),"Error in fetching user address",Toast.LENGTH_SHORT).show();
            Log.i("kingsukmajumder",e.toString());
        }
    }


   /* @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        RadioButton radioButton = (RadioButton) findViewById(checkedId);
        Toast.makeText(getApplicationContext(),radioButton.getText(),Toast.LENGTH_SHORT).show();
    }*/

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.i("kingsukmajumder","radio arra is "+radioArray.size());
        if(radioArray.size()>0)
        {
            for(int i=0;i<radioArray.size();i++)
            {

                RadioButton radioButton = (RadioButton) findViewById(radioArray.get(i).getId());
                try
                {
                    radioButton.setChecked(false);
                }
                catch (Exception e)
                {
                    Log.i("kingsukmajumder","no arr in radio array "+i);
                }


            }
            RadioButton radioButton = (RadioButton) findViewById(buttonView.getId());
            radioButton.setChecked(isChecked);
            ADDRESS = radioButton.getText().toString();
            //Toast.makeText(getApplicationContext(),radioButton.getText().toString(),Toast.LENGTH_SHORT).show();
        }


    }



    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
    }

    @Override
    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
        calendar.set(year, monthOfYear, dayOfMonth);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat properDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //String date = year+"-"+(new DecimalFormat("00").format(monthOfYear+1))+"-"+dayOfMonth;
        //Toast.makeText(getApplicationContext(),dateFormat.format(calendar.getTime()),Toast.LENGTH_SHORT).show();
        //Toast.makeText(getApplicationContext(),date,Toast.LENGTH_SHORT).show();

        //tvShowSelectedDate.setText(dayOfMonth+"/"+(new DecimalFormat("00").format(monthOfYear+1))+"/"+year);
        SaveUserData.data_total.put("pick_up_date",properDateFormat.format(calendar.getTime()));
        tvShowSelectedDate.setText(df.format(calendar.getTime()));
        SaveUserData.data_total.put("SHOW_pick_up_date",df.format(calendar.getTime()));

    }

    @Override
    public void onDateChanged() {

    }

    public void removeRadio(final ImageView ivRemoveRadio)
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setTitle("Delete this address?")
                .setMessage("Are you sure?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Yes button clicked, do something

                        //Toast.makeText(getApplicationContext(),ivRemoveRadio.getTag().toString(),Toast.LENGTH_SHORT).show();
                        SharedPreferences prefs = getSharedPreferences("U-rang", MODE_PRIVATE);
                        String address_json = prefs.getString("address_json", "");
                        int index = Integer.parseInt(ivRemoveRadio.getTag().toString());
                        //Log.i("kingsukmajumder",address_json);
                        //Toast.makeText(getApplicationContext(),""+index,Toast.LENGTH_SHORT).show();

                        try {
                            JSONArray jsonArray = new JSONArray(address_json);
                            if(jsonArray.length()==1)
                            {
                                SharedPreferences.Editor editor = getSharedPreferences("U-rang", MODE_PRIVATE).edit();
                                editor.putString("address_json", "");
                                if(editor.commit())
                                {
                                    Intent intent = getIntent();
                                    finish();
                                    startActivity(intent);
                                }
                                else
                                {
                                    Toast.makeText(getApplicationContext(),"Cannot delete address right now!",Toast.LENGTH_SHORT).show();

                                }
                            }
                            else
                            {
                                for(int i=0;i<jsonArray.length();i++)
                                {
                                    JSONObject currentObj = jsonArray.getJSONObject(i);
                                    if(index==currentObj.getInt("index"))
                                    {
                                        jsonArray.remove(i);
                                    }
                                }

                                SharedPreferences.Editor editor = getSharedPreferences("U-rang", MODE_PRIVATE).edit();
                                editor.putString("address_json", jsonArray.toString());
                                if(editor.commit())
                                {
                                    Intent intent = getIntent();
                                    finish();
                                    startActivity(intent);
                                }
                                else
                                {
                                    Toast.makeText(getApplicationContext(),"Cannot delete address right now!",Toast.LENGTH_SHORT).show();

                                }
                            }


                        }
                        catch (Exception e)
                        {
                            Log.i("kingsukmajumder",e.toString());
                            Toast.makeText(getApplicationContext(),"Cannot delete address right now!",Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("No", null)						//Do nothing on no
                .show();




    }

    @Override
    public void onClick(View v) {
        registerUser2.register(data, routeProfileDetails);
        changeIntent();

    }

    public void changeIntent()
    {
        if(ADDRESS.equals(""))
        {
            Toast.makeText(getApplicationContext(),"You need to add an address!",Toast.LENGTH_SHORT).show();
        }
        else
        {
            SaveUserData.data_total.put("showAddress",ADDRESS);
            ADDRESS = ADDRESS.replace("\n", ",");
            SaveUserData.data_total.put("address",ADDRESS);
            Log.i("kingsukmajumder","total data "+SaveUserData.data_total.toString());
            Intent intent = new Intent(Order_details.this,Other_details.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("kingsukmajumder","on resume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("kingsukmajumder","on Pause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("kingsukmajumder","on destroy");
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Order_details.this,Dashboard.class);
        startActivity(intent);
    }

    @Override
    public void processFinish2(String output) {
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
