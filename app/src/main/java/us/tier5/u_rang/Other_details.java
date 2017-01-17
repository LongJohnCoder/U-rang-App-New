package us.tier5.u_rang;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cooltechworks.creditcarddesign.CardEditActivity;
import com.cooltechworks.creditcarddesign.CreditCardUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import HelperClasses.AsyncResponse;
import HelperClasses.AsyncResponse2;
import HelperClasses.AsyncResponse3;
import HelperClasses.AsyncResponse4;
import HelperClasses.AsyncResponse5;
import HelperClasses.RegisterUser;
import HelperClasses.RegisterUser2;
import HelperClasses.RegisterUser3;
import HelperClasses.RegisterUser4;
import HelperClasses.RegisterUser5;
import Others.SaveUserData;

public class Other_details extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener,TimePickerDialog.OnTimeSetListener,AdapterView.OnItemSelectedListener,View.OnClickListener, AsyncResponse.Response, AsyncResponse2.Response2 , AsyncResponse3.Response3, AsyncResponse4.Response4, AsyncResponse5.Response5{

    //page variable
    LinearLayout PickUpTimeLL;
    Switch swDoorman;
    int doorman=1;
    Spinner pay_method;
    Switch swEmergency;
    Switch swWashFold;
    Switch swDonateToSchool;
    TextView submitPickup;
    String startTimeFrame = "__:__";
    String endTimeFrame = "__:__";
    Spinner spinner;
    TextView tvAdditionalOptions;
    List<String> spinnerArray =  new ArrayList<String>();
    List<Integer> spinnerId = new ArrayList<Integer>();
    Switch swHaveCoupone;
    TextView tvAppliedCoupon;
    String couponString = "";
    int schoolDonationInPreviousOrder = 0;
    boolean showCouponDialogeOrNot = true;
    boolean cardDetailsareThere=false;

    TextView tvStartTimeFrame;
    TextView tvEndTimeFrame;

    //
    TimePickerDialog startTimePicker;
    TimePickerDialog enddTimePicker;
    TimePickerDialog startTimePickerTV;
    TimePickerDialog enddTimePickerTV;
    String startTime = "";
    String endTime = "";

    //server variable
    HashMap<String, String> data = new HashMap<String,String>();
    HashMap<String,String> dataSchool = new HashMap<>();
    String route = "/V1/place-order";
    String routeGetCardInfo = "/V1/get-card-details";
    RegisterUser registerUser = new RegisterUser("POST");
    RegisterUser2 registerUser2 = new RegisterUser2("POST");
    String routeSchoolDonation = "/V1/get-school-preferences";
    RegisterUser3 registerUser3 = new RegisterUser3("POST");
    String routeCoupon = "/V1/postCoupon";
    RegisterUser4 registerUser4 = new RegisterUser4("POST");

    HashMap<String, String> dataToGetOrderDetails = new HashMap<String,String>();
    String routeGetPreviousOrderDetails = "/V1/lastPickup";
    RegisterUser5 registerUser5 = new RegisterUser5("POST");

    //timer formatter
    SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a");
    int First24Hour;
    int First24Min;
    int Second24Hour;
    int Second24Min;


    //Loading variable
    ProgressDialog loadingCoupon;
    ProgressDialog loadingCreditCard;
    ProgressDialog loadingPostingOrder;
    ProgressDialog loadingPreviousOrder;
    ProgressDialog loadingForSchoolDonations;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        registerUser.delegate = this;
        registerUser2.delegate = this;
        registerUser3.delegate = this;
        registerUser4.delegate = this;
        registerUser5.delegate = this;




        //user data variable initialization
        SaveUserData.data_total.put("pay_method","");
        //SaveUserData.data_total.put("isEmergency","0");
        SaveUserData.data_total.put("wash_n_fold","0");
        SaveUserData.data_total.put("urang_bag","1");
        SaveUserData.data_total.put("doorman","1");
        SaveUserData.data_total.put("strach_type","No");
        SaveUserData.data_total.put("schedule","Not Specified");
        SaveUserData.data_total.put("client_type","Not Specified");


        PickUpTimeLL = (LinearLayout) findViewById(R.id.PickUpTimeLL);
        tvAdditionalOptions = (TextView) findViewById(R.id.tvAdditionalOptions);

        swDoorman = (Switch) findViewById(R.id.swDoorman);
        swDoorman.setChecked(true);
        swDoorman.setOnCheckedChangeListener(this);

        //timepicker initialization
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);

        startTimePicker = new TimePickerDialog(Other_details.this,this, hour, minute, true);//Yes 24 hour time
        startTimePicker.setTitle("Select PickUp Start Time Frame");

        //enddTimePicker = new TimePickerDialog(Other_details.this,this, hour, minute, true);//Yes 24 hour time
        //enddTimePicker.setTitle("Select PickUp End Time Frame");

        pay_method = (Spinner) findViewById(R.id.pay_method);
        pay_method.setOnItemSelectedListener(this);

        swEmergency = (Switch) findViewById(R.id.swEmergency);
        swEmergency.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    SaveUserData.data_total.put("isEmergency","1");
                    //Toast.makeText(getApplicationContext(),"emergency",Toast.LENGTH_SHORT).show();

                }
                else
                {
                    SaveUserData.data_total.remove("isEmergency");
                }
            }
        });
        swWashFold = (Switch) findViewById(R.id.swWashFold);
        swWashFold.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    SaveUserData.data_total.put("wash_n_fold","1");

                }
                else
                {
                    SaveUserData.data_total.put("wash_n_fold","0");
                }
            }
        });

        submitPickup = (TextView) findViewById(R.id.submitPickup);
        submitPickup.setOnClickListener(this);

        spinner = (Spinner) findViewById(R.id.spinner);

        SaveUserData.data_total.put("pay_method","1");
        SharedPreferences prefs = getSharedPreferences("U-rang", Context.MODE_PRIVATE);
        int user_id = prefs.getInt("user_id", 0);

        //Toast.makeText(getContext(),""+user_id,Toast.LENGTH_SHORT).show();
        dataSchool.put("user_id",Integer.toString(user_id));
        SaveUserData.data_total.put("user_id",Integer.toString(user_id));
        dataToGetOrderDetails.put("id",Integer.toString(user_id));



        //calling to get the last pickup
        //Toast.makeText(getApplicationContext(),"Calling to get previous order data",Toast.LENGTH_SHORT).show();
        loadingPreviousOrder = ProgressDialog.show(Other_details.this, "","Please wait", true, false);
        registerUser5.register(dataToGetOrderDetails,routeGetPreviousOrderDetails);





        swDonateToSchool = (Switch) findViewById(R.id.swDonateToSchool);
        swDonateToSchool.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {

                    SaveUserData.data_total.put("isDonate","on");
                    loadingForSchoolDonations = ProgressDialog.show(Other_details.this, "","Getting all schools", true, false);
                    registerUser3.register(dataSchool,routeSchoolDonation);
                }
                else
                {
                    spinner.setVisibility(View.INVISIBLE);
                    SaveUserData.data_total.remove("isDonate");
                }
            }
        });



        tvAdditionalOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!couponString.equals(""))
                {
                    SaveUserData.data_total.put("coupon",couponString);
                }
                SaveUserData.data_total.put("time_frame_start",startTimeFrame);
                SaveUserData.data_total.put("time_frame_end",endTimeFrame);
                if(!SaveUserData.data_total.get("pay_method").equals(""))
                {
                    if(endTimeFrame.equals("__:__") && SaveUserData.data_total.get("doorman").equals("0"))
                    {
                        Toast.makeText(getApplicationContext(),"You have to select a end time frame!",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        if(SaveUserData.data_total.get("pay_method").equals("1"))
                        {
                            if(cardDetailsareThere)
                            {
                                //Toast.makeText(getApplicationContext(),"You have to select a end time frame!",Toast.LENGTH_SHORT).show();
                                Log.i("kingsukmajumder",SaveUserData.data_total.toString());
                                Intent intent = new Intent(Other_details.this,PickUpOptional.class);
                                startActivity(intent);
                            }
                            else
                            {
                                final int GET_NEW_CARD = 2;
                                Toast.makeText(getApplicationContext(), "Please enter the card details", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Other_details.this, CardEditActivity.class);
                                startActivityForResult(intent,GET_NEW_CARD);
                            }
                        }
                        else
                        {
                            //Toast.makeText(getApplicationContext(),"You have to select a end time frame!",Toast.LENGTH_SHORT).show();
                            Log.i("kingsukmajumder",SaveUserData.data_total.toString());
                            Intent intent = new Intent(Other_details.this,PickUpOptional.class);
                            startActivity(intent);
                        }

                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Select a payment method",Toast.LENGTH_SHORT).show();
                }
            }
        });

        swHaveCoupone = (Switch) findViewById(R.id.swHaveCoupone);
        tvAppliedCoupon = (TextView) findViewById(R.id.tvAppliedCoupon);
        swHaveCoupone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    if(showCouponDialogeOrNot)
                    {
                        showCouponDialoge();
                    }
                }
                else
                {
                    tvAppliedCoupon.setText("");
                    tvAppliedCoupon.setVisibility(View.INVISIBLE);
                    couponString="";
                }
            }
        });


    }

    public void inflateTimeFrame(int firsthourOfDay, int firstminute)
    {
        PickUpTimeLL.removeAllViews();

        //putting in global
        First24Hour = firsthourOfDay;
        First24Min = firstminute;

        startTimeFrame = changeDateFormat(firsthourOfDay+":"+firstminute);

        View inflatedLayoutRR_OrderDetail = getLayoutInflater().inflate(R.layout.order_details_page, null, false);
        tvStartTimeFrame = (TextView) inflatedLayoutRR_OrderDetail.findViewById(R.id.tvStartTimeFrame);
        tvEndTimeFrame = (TextView) inflatedLayoutRR_OrderDetail.findViewById(R.id.tvEndTimeFrame);
        PickUpTimeLL.addView(inflatedLayoutRR_OrderDetail);


        tvStartTimeFrame.setText(changeDateFormat(firsthourOfDay+":"+firstminute));


        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);

        final TimePickerDialog secondTimePicker;

        secondTimePicker = new TimePickerDialog(Other_details.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                //initialize to global
                Second24Hour = selectedHour;
                Second24Min = selectedMinute;

                if(selectedHour<First24Hour)
                {
                    Toast.makeText(getApplicationContext(),"End time cannot be before or same as START TIME",Toast.LENGTH_LONG).show();
                    showSecondTimer(tvEndTimeFrame);
                }
                else if(selectedHour==First24Hour)
                {
                    if(selectedMinute<First24Min || selectedMinute==First24Min)
                    {
                        Toast.makeText(getApplicationContext(),"End time cannot be before or same as START TIME",Toast.LENGTH_LONG).show();
                        showSecondTimer(tvEndTimeFrame);
                    }
                    else
                    {
                        tvEndTimeFrame.setText(changeDateFormat(selectedHour+":"+selectedMinute));
                        endTimeFrame = changeDateFormat(selectedHour+":"+selectedMinute);
                    }
                }
                else
                {
                    tvEndTimeFrame.setText(changeDateFormat(selectedHour+":"+selectedMinute));
                    endTimeFrame = changeDateFormat(selectedHour+":"+selectedMinute);
                }
            }
        }, hour, minute, true);//Yes 24 hour time
        secondTimePicker.setTitle("Select PickUp End Time Frame");

        secondTimePicker.show();

        tvStartTimeFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFirstTimer(tvStartTimeFrame);
            }
        });

        tvEndTimeFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSecondTimer(tvEndTimeFrame);
            }
        });
    }

    public void showFirstTimer(final TextView tvStartTimeFrame)
    {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog secondTimePicker;
        secondTimePicker = new TimePickerDialog(Other_details.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                //putting in global
                First24Hour = selectedHour;
                First24Min = selectedMinute;

                if(selectedHour>Second24Hour)
                {
                    Toast.makeText(getApplicationContext(),"Start time cannot be after END TIMER",Toast.LENGTH_LONG).show();
                    showFirstTimer(tvStartTimeFrame);
                }
                else if(selectedHour==Second24Hour)
                {
                    if(selectedMinute>Second24Min || selectedMinute==Second24Min)
                    {
                        Toast.makeText(getApplicationContext(),"Start time cannot be after END TIMER",Toast.LENGTH_LONG).show();
                        showFirstTimer(tvStartTimeFrame);
                    }
                    else
                    {
                        tvStartTimeFrame.setText(changeDateFormat(selectedHour+":"+selectedMinute));
                        startTimeFrame = changeDateFormat(selectedHour+":"+selectedMinute);
                    }
                }
                else
                {
                    tvStartTimeFrame.setText(changeDateFormat(selectedHour+":"+selectedMinute));
                    startTimeFrame = changeDateFormat(selectedHour+":"+selectedMinute);
                }


            }
        }, hour, minute, true);//Yes 24 hour time
        secondTimePicker.setTitle("Select PickUp Start Time Frame");
        secondTimePicker.show();
    }

    public void showSecondTimer(final TextView tvEndTimeFrame)
    {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog secondTimePicker;
        secondTimePicker = new TimePickerDialog(Other_details.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                //initialize to global
                Second24Hour = selectedHour;
                Second24Min = selectedMinute;


                if(selectedHour<First24Hour)
                {
                    Toast.makeText(getApplicationContext(),"End time cannot be before or same as START TIME",Toast.LENGTH_LONG).show();
                    showSecondTimer(tvEndTimeFrame);
                }
                else if(selectedHour==First24Hour)
                {
                    if(selectedMinute<First24Min || selectedMinute == First24Min)
                    {
                        Toast.makeText(getApplicationContext(),"End time cannot be before or same as START TIME",Toast.LENGTH_LONG).show();
                        showSecondTimer(tvEndTimeFrame);
                    }
                    else
                    {
                        tvEndTimeFrame.setText(changeDateFormat(selectedHour+":"+selectedMinute));
                        endTimeFrame = changeDateFormat(selectedHour+":"+selectedMinute);
                    }
                }
                else
                {
                    tvEndTimeFrame.setText(changeDateFormat(selectedHour+":"+selectedMinute));
                    endTimeFrame = changeDateFormat(selectedHour+":"+selectedMinute);
                }
            }
        }, hour, minute, true);//Yes 24 hour time
        secondTimePicker.setTitle("Select PickUp End Time Frame");
        secondTimePicker.show();
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked)
        {
            doorman = 1;
            PickUpTimeLL.removeAllViews();
            SaveUserData.data_total.put("doorman","1");

        }
        else
        {
            doorman = 0;
            startTimePicker.show();
            startTime = "";
            SaveUserData.data_total.put("doorman","0");
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        //Toast.makeText(getApplicationContext(),hourOfDay+":"+minute,Toast.LENGTH_SHORT).show();
        inflateTimeFrame(hourOfDay,minute);
    }

    public String changeDateFormat(String time)
    {
        String properTime="";
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            final Date dateObj = sdf.parse(time);
            //System.out.println(dateObj);
            properTime = new SimpleDateFormat("K:mm a").format(dateObj);
        } catch (final ParseException e) {
            e.printStackTrace();
        }

        return properTime;
    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Toast.makeText(getApplicationContext(),""+pay_method.getItemAtPosition(position),Toast.LENGTH_SHORT).show();
        if(pay_method.getItemAtPosition(position).equals("Charge my credit card"))
        {
            SaveUserData.data_total.put("pay_method","1");
            SharedPreferences prefs = getSharedPreferences("U-rang", Context.MODE_PRIVATE);
            int user_id = prefs.getInt("user_id", 0);

            //Toast.makeText(getContext(),""+user_id,Toast.LENGTH_SHORT).show();
            data.put("user_id",Integer.toString(user_id));
            loadingCreditCard = ProgressDialog.show(Other_details.this, "","Searching for your credit card details", true, false);
            registerUser2.register(data,routeGetCardInfo);
        }
        else if(pay_method.getItemAtPosition(position).equals("Check"))
        {
            SaveUserData.data_total.put("pay_method","3");
        }
        else
        {
            SaveUserData.data_total.put("pay_method","");
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        if(!couponString.equals(""))
        {
            SaveUserData.data_total.put("coupon",couponString);
        }
        SaveUserData.data_total.put("time_frame_start",startTimeFrame);
        SaveUserData.data_total.put("time_frame_end",endTimeFrame);
        Log.i("kingsukmajumder",SaveUserData.data_total.toString());
        if(!SaveUserData.data_total.get("pay_method").equals(""))
        {
            if(endTimeFrame.equals("__:__") && SaveUserData.data_total.get("doorman").equals("0"))
            {
                Toast.makeText(getApplicationContext(),"You have to select a end time frame!",Toast.LENGTH_SHORT).show();
            }
            else
            {
                if(SaveUserData.data_total.get("pay_method").equals("1"))
                {
                    if(cardDetailsareThere)
                    {
                        loadingPostingOrder = ProgressDialog.show(Other_details.this, "","Placing order", true, false);
                        registerUser.register(SaveUserData.data_total,route);
                    }
                    else
                    {
                        final int GET_NEW_CARD = 2;
                        Toast.makeText(this, "Please enter the card details", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Other_details.this, CardEditActivity.class);
                        startActivityForResult(intent,GET_NEW_CARD);
                    }
                }
                else
                {
                    loadingPostingOrder = ProgressDialog.show(Other_details.this, "","Placing order", true, false);
                    registerUser.register(SaveUserData.data_total,route);
                }

            }

        }
        else
        {
            Toast.makeText(getApplicationContext(),"Select a payment method!",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void processFinish(String output) {
        Log.i("kingsukmajumder",output);
        loadingPostingOrder.dismiss();
        try
        {
            JSONObject jsonObject = new JSONObject(output);
            if(jsonObject.getBoolean("status"))
            {
                Toast.makeText(getApplicationContext(),jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Other_details.this,DashboardNew.class);
                intent.putExtra("classname","FragmentClasses.Orders_fragment");
                startActivity(intent);
            }
            else
            {
                Toast.makeText(getApplicationContext(),jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Other_details.this,DashboardNew.class);
                intent.putExtra("classname","FragmentClasses.Orders_fragment");
                startActivity(intent);
            }
        }
        catch (Exception e)
        {
            Log.i("kingsukmajumder",e.toString());
        }
    }

    @Override
    public void processFinish2(String output) {
        loadingCreditCard.dismiss();
        Log.i("kingsukmajumder","credit card info "+output);
        try
        {
            JSONObject jsonObject = new JSONObject(output);
            if(!jsonObject.getBoolean("status"))
            {
                final int GET_NEW_CARD = 2;

                Intent intent = new Intent(Other_details.this, CardEditActivity.class);
                startActivityForResult(intent,GET_NEW_CARD);
                Toast.makeText(getApplicationContext(),"Please enter your card details",Toast.LENGTH_SHORT).show();
                cardDetailsareThere=false;
            }
            else
            {
                Toast.makeText(getApplicationContext(),"Your card details are already saved",Toast.LENGTH_SHORT).show();
                cardDetailsareThere=true;
            }
        }
        catch (Exception e)
        {
            Log.i("kingsukmajumder",e.toString());
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {

            String cardHolderName = data.getStringExtra(CreditCardUtils.EXTRA_CARD_HOLDER_NAME);
            String cardNumber = data.getStringExtra(CreditCardUtils.EXTRA_CARD_NUMBER);
            String expiry = data.getStringExtra(CreditCardUtils.EXTRA_CARD_EXPIRY);
            String cvv = data.getStringExtra(CreditCardUtils.EXTRA_CARD_CVV);

            if(cardHolderName.equals("")||cardNumber.equals("")||expiry.equals("")||cvv.equals(""))
            {
                Toast.makeText(getApplicationContext(),"All credit card info needed",Toast.LENGTH_SHORT).show();
                final int GET_NEW_CARD = 2;

                Intent intent = new Intent(Other_details.this, CardEditActivity.class);
                startActivityForResult(intent,GET_NEW_CARD);

            }
            else
            {
                List<String> exp_year = Arrays.asList(expiry.split("/"));
                SaveUserData.data_total.put("isCard","yes");
                SaveUserData.data_total.put("name",cardHolderName);
                SaveUserData.data_total.put("card_no",cardNumber);
                SaveUserData.data_total.put("cvv",cvv);
                SaveUserData.data_total.put("exp_month",exp_year.get(0));
                SaveUserData.data_total.put("exp_year",exp_year.get(1));
                cardDetailsareThere=true;
            }
    }
    }

    @Override
    public void processFinish3(String output) {
        Log.i("kingsukmajumder",output);
        loadingForSchoolDonations.dismiss();
        try
        {
            JSONObject jsonObject = new JSONObject(output);

            if(jsonObject.getBoolean("status"))
            {
                JSONArray school_listArray = new JSONArray(jsonObject.getString("school_list"));
                if(school_listArray.length()==0)
                {
                    //Toast.makeText(getContext(),"No schools have been added yet!",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    spinnerId.clear();
                    spinnerArray.clear();

                    int schoolToSelectByDefault = 0;

                    for(int i=0;i<school_listArray.length();i++)
                    {

                        JSONObject school_listObject = school_listArray.getJSONObject(i);
                        //Log.i("schoolList",school_listObject.toString());
                        spinnerArray.add(school_listObject.getString("school_name"));
                        spinnerId.add(school_listObject.getInt("id"));

                        if(schoolDonationInPreviousOrder==school_listObject.getInt("id"))
                        {
                            schoolToSelectByDefault = i;
                        }
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getApplicationContext(), R.layout.spinner_item, spinnerArray);

                    spinner.setAdapter(adapter);
                    spinner.setSelection(schoolToSelectByDefault);
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            //Toast.makeText(getApplicationContext(),""+spinnerId.get(position),Toast.LENGTH_SHORT).show();
                            SaveUserData.data_total.put("school_donation_id",String.valueOf(spinnerId.get(position)));
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            SaveUserData.data_total.remove("spinnerId.get(position)");
                        }
                    });

                    spinner.setVisibility(View.VISIBLE);
                }
            }
            else
            {
                Toast.makeText(getApplicationContext(),jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {

        }
    }

    public void showCouponDialoge()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.popup_date, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.etCoupone);

        dialogBuilder.setTitle("Enter coupon code:");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                if(edt.getText().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext(),"You need to enter a coupon",Toast.LENGTH_SHORT).show();
                    swHaveCoupone.setChecked(false);
                }
                else
                {
                    data.put("coupon",edt.getText().toString());
                    loadingCoupon = ProgressDialog.show(Other_details.this, "","Applying coupon", true, false);
                    registerUser4.register(data,routeCoupon);
                    couponString="";
                }

            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                swHaveCoupone.setChecked(false);
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    @Override
    public void processFinish4(String output) {
        loadingCoupon.dismiss();
        Log.i("kingsukmajumder",output);
        try{
            JSONObject jsonObject = new JSONObject(output);
            if(jsonObject.getBoolean("status"))
            {
                JSONObject response = new JSONObject(jsonObject.getString("response"));
                tvAppliedCoupon.setVisibility(View.VISIBLE);
                tvAppliedCoupon.setText(response.getString("coupon_code")+" ("+response.getString("discount")+"% discount)");
                couponString=response.getString("coupon_code");
            }
            else
            {
                swHaveCoupone.setChecked(false);
                couponString="";
                tvAppliedCoupon.setVisibility(View.INVISIBLE);
                tvAppliedCoupon.setText("");
                Toast.makeText(getApplicationContext(),jsonObject.getString("message"),Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e)
        {
            Log.i("kingsukmajumder","error in getting coupone "+e.toString());
            swHaveCoupone.setChecked(false);
            tvAppliedCoupon.setVisibility(View.INVISIBLE);
            tvAppliedCoupon.setText("");
            couponString="";
        }
    }

    @Override
    public void processFinish5(String output) {
        //Log.i("kingsukmajumder", "previous order data is "+output);

        try{
            JSONObject jsonObject = new JSONObject(output);
            if(jsonObject.getBoolean("status"))
            {
                JSONObject responseObj = new JSONObject(jsonObject.getString("response"));
                //Log.i("kingsukmajumder","response is "+responseObj);
                int paymentType = responseObj.getInt("payment_type");
                if(paymentType==3)
                {
                    pay_method.setSelection(2);
                }
                else if(paymentType==1)
                {
                    pay_method.setSelection(1);
                }

                int emergencyStatus = responseObj.getInt("is_emergency");

                if(emergencyStatus==1)
                {
                    swEmergency.setChecked(true);
                }

                int doorMan = responseObj.getInt("door_man");
                if(doorMan==1)
                {
                    swDoorman.setChecked(true);
                    doorman = 1;
                    PickUpTimeLL.removeAllViews();
                    SaveUserData.data_total.put("doorman","1");
                }
                else
                {
                    swDoorman.setChecked(false);
                    doorman = 0;
                    //startTimePicker.show();
                    startTime = "";
                    SaveUserData.data_total.put("doorman","0");
                    View inflatedLayoutRR_OrderDetail = getLayoutInflater().inflate(R.layout.order_details_page, null, false);
                    tvStartTimeFrame = (TextView) inflatedLayoutRR_OrderDetail.findViewById(R.id.tvStartTimeFrame);
                    tvStartTimeFrame.setText(responseObj.getString("time_frame_start"));
                    startTimeFrame = responseObj.getString("time_frame_start");
                    tvEndTimeFrame = (TextView) inflatedLayoutRR_OrderDetail.findViewById(R.id.tvEndTimeFrame);
                    tvEndTimeFrame.setText(responseObj.getString("time_frame_end"));
                    endTimeFrame = responseObj.getString("time_frame_end");

                    tvStartTimeFrame.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showFirstTimer(tvStartTimeFrame);
                        }
                    });

                    tvEndTimeFrame.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showSecondTimer(tvEndTimeFrame);
                        }
                    });

                    PickUpTimeLL.addView(inflatedLayoutRR_OrderDetail);
                }

                //for wash and fold
                int washAndFold = responseObj.getInt("wash_n_fold");
                if(washAndFold==1)
                {
                    swWashFold.setChecked(true);
                }
                else
                {
                    swWashFold.setChecked(false);
                }

                //getting schooldonations
                if(!responseObj.isNull("school_donation_id"))
                {
                    swDonateToSchool.setChecked(true);
                    //Toast.makeText(getApplicationContext(),"saved school id "+responseObj.getInt("school_donation_id"),Toast.LENGTH_SHORT).show();
                    schoolDonationInPreviousOrder = responseObj.getInt("school_donation_id");
                }

                if(!responseObj.isNull("coupon"))
                {
                    showCouponDialogeOrNot = false;
                    swHaveCoupone.setChecked(true);

                    data.put("coupon",responseObj.getString("coupon"));
                    loadingCoupon = ProgressDialog.show(Other_details.this, "","Applying coupon", true, false);
                    registerUser4.register(data,routeCoupon);
                    couponString="";
                }


                if(!responseObj.isNull("starch_type"))
                {
                    SaveUserData.data_total.put("strach_type",responseObj.getString("starch_type"));

                }
                if(!responseObj.isNull("delivary_type"))
                {
                    SaveUserData.data_total.put("boxed_or_hung",responseObj.getString("delivary_type"));
                }
                if(responseObj.getInt("need_bag")==1)
                {
                    SaveUserData.data_total.put("urang_bag","1");
                }
                else
                {
                    SaveUserData.data_total.put("urang_bag","0");
                }

                if(!responseObj.isNull("special_instructions"))
                {
                    SaveUserData.data_total.put("spcl_ins",responseObj.getString("special_instructions"));
                }

                if(!responseObj.isNull("driving_instructions"))
                {
                    SaveUserData.data_total.put("driving_ins",responseObj.getString("driving_instructions"));
                }
            }
            else
            {
                Log.i("kingsukmajumder","no previous order details to show");
            }
        }
        catch (Exception e)
        {
            Log.i("kingsukmajumder","error in getting previous order data "+e.toString());
        }

        loadingPreviousOrder.dismiss();
    }
}






