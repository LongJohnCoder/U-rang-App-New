package us.tier5.u_rang;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import HelperClasses.AsyncResponse;
import HelperClasses.CheckNetwork;
import HelperClasses.RegisterUser;
import Others.SaveUserData;

public class Detailed_pickup extends AppCompatActivity implements AsyncResponse.Response,View.OnClickListener{

    //server variable
    HashMap<String, String> data = new HashMap<String,String>();
    String route = "/V1/get-prices";
    RegisterUser registerUser = new RegisterUser("POST");

    //page variables
    LinearLayout llparentPrice;
    TextView tvTotalCount;
    TextView tvAllTotalPrice;
    ArrayList<View> pagesAdded = new ArrayList<>();
    TextView tvSchedule;

    //loading variable
    ProgressDialog loadingPriceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_pickup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        registerUser.delegate = this;

        llparentPrice = (LinearLayout) findViewById(R.id.llparentPrice);
        tvTotalCount = (TextView) findViewById(R.id.tvTotalCount);
        tvAllTotalPrice = (TextView) findViewById(R.id.tvAllTotalPrice);

        if(CheckNetwork.isInternetAvailable(getApplicationContext())) //returns true if internet available
        {

            /*mGifLoadingView.show(manager, "Loading");
            mGifLoadingView.setBlurredActionBar(true);*/
            loadingPriceList = ProgressDialog.show(Detailed_pickup.this, "Please Wait",null, true, true);
            registerUser.register(data,route);
        }
        else
        {
            Toast.makeText(getApplicationContext(),"No Internet Connection",Toast.LENGTH_SHORT).show();
        }

        tvSchedule = (TextView) findViewById(R.id.tvSchedule);
        tvSchedule.setOnClickListener(this);
    }

    @Override
    public void processFinish(String output) {
        Log.i("kingsukmajumder",output);

        try
        {
            JSONObject jsonObject = new JSONObject(output);
            if(jsonObject.getBoolean("status"))
            {
                String jsonArrayString = jsonObject.getString("response");
                JSONArray jsonArray = new JSONArray(jsonArrayString);
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject currentObj = jsonArray.getJSONObject(i);
                    String categoryName = currentObj.getString("name");
                    JSONArray priceListArray = currentObj.getJSONArray("pricelists");
                    if(priceListArray.length()!=0)
                    {

                        View inflatedCategory= getLayoutInflater().inflate(R.layout.categorylist, null, false);
                        TextView tvCategoryName = (TextView) inflatedCategory.findViewById(R.id.tvCategoryName);
                        tvCategoryName.setText(categoryName);
                        llparentPrice.addView(inflatedCategory);


                        for (int j=0;j<priceListArray.length();j++)
                        {
                            JSONObject currentPriceObj = priceListArray.getJSONObject(j);
                            //Log.i("kingsukmajumder",currentPriceObj.toString());
                            String itemName = currentPriceObj.getString("item");
                            String itemPrice = currentPriceObj.getString("price");
                            int id = currentPriceObj.getInt("id");

                            View inflatedLayout= getLayoutInflater().inflate(R.layout.listview, null, false);
                            TextView title = (TextView) inflatedLayout.findViewById(R.id.title);
                            title.setText(itemName);
                            title.setTag(id);
                            final TextView tvPrice = (TextView) inflatedLayout.findViewById(R.id.tvPrice);
                            tvPrice.setTag(itemPrice);
                            tvPrice.setText("$ "+itemPrice);
                            final TextView tvQuantity = (TextView) inflatedLayout.findViewById(R.id.tvQuantity);
                            TextView tvPlus = (TextView) inflatedLayout.findViewById(R.id.tvPlus);

                            tvPlus.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int previousQuantity = Integer.parseInt(tvQuantity.getText().toString());
                                    int nowQuantity = previousQuantity+1;
                                    tvQuantity.setText(""+nowQuantity);
                                    int totalCount = Integer.parseInt(tvTotalCount.getText().toString());
                                    int nowTotalCount = totalCount+1;
                                    tvTotalCount.setText(""+nowTotalCount);
                                    Float allTotalPrice = Float.parseFloat(tvAllTotalPrice.getText().toString());
                                    Float thisItemPrice = Float.parseFloat(tvPrice.getTag().toString());
                                    Float nowAllTotalPrice = allTotalPrice+thisItemPrice;
                                    String nowAllTotalPriceString = String.format("%.2f", nowAllTotalPrice);
                                    tvAllTotalPrice.setText(nowAllTotalPriceString);
                                }
                            });

                            TextView tvMinus = (TextView) inflatedLayout.findViewById(R.id.tvMinus);

                            tvMinus.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int previousQuantity = Integer.parseInt(tvQuantity.getText().toString());
                                    if(previousQuantity>0)
                                    {
                                        int nowQuantity = previousQuantity-1;
                                        tvQuantity.setText(""+nowQuantity);
                                        if(Integer.parseInt(tvTotalCount.getText().toString())>0)
                                        {
                                            int totalCount = Integer.parseInt(tvTotalCount.getText().toString());
                                            int nowTotalCount = totalCount-1;
                                            tvTotalCount.setText(""+nowTotalCount);
                                            Float allTotalPrice = Float.parseFloat(tvAllTotalPrice.getText().toString());
                                            Float thisItemPrice = Float.parseFloat(tvPrice.getTag().toString());
                                            Float nowAllTotalPrice = allTotalPrice-thisItemPrice;
                                            String nowAllTotalPriceString = String.format("%.2f", nowAllTotalPrice);
                                            tvAllTotalPrice.setText(nowAllTotalPriceString);
                                        }
                                    }
                                }
                            });
                            pagesAdded.add(inflatedLayout);
                            llparentPrice.addView(inflatedLayout);

                        }
                    }
                    else
                    {
                        Log.i("kingsukmajumder","pricelist is 0");
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
            Toast.makeText(getApplicationContext(),"Error in fetching price list",Toast.LENGTH_SHORT).show();
            Log.i("kingsukmajumder",e.toString());
        }
        loadingPriceList.dismiss();
    }

    @Override
    public void onClick(View v) {
        JSONArray jsonArrayNew = new JSONArray();
        for(int i=0;i<pagesAdded.size();i++)
        {
            TextView quantity = (TextView) pagesAdded.get(i).findViewById(R.id.tvQuantity);
            int itemQuantity = Integer.parseInt(quantity.getText().toString());
            if(itemQuantity>0)
            {
                TextView tvPriceNew = (TextView) pagesAdded.get(i).findViewById(R.id.tvPrice);
                TextView titleNew = (TextView) pagesAdded.get(i).findViewById(R.id.title);

                JSONObject newJsonObject = new JSONObject();
                try{
                    newJsonObject.put("id",titleNew.getTag());
                    newJsonObject.put("number_of_item",Integer.toString(itemQuantity));
                    newJsonObject.put("item_name",titleNew.getText());
                    newJsonObject.put("item_price",tvPriceNew.getTag());
                }
                catch (Exception e)
                {
                    Log.i("kingsukmajumder",e.toString());
                }
                jsonArrayNew.put(newJsonObject);
            }
        }

        Log.i("kingsukmajumder",jsonArrayNew.toString());

        if(jsonArrayNew.length()>0)
        {
            SaveUserData.data_total.put("list_items_json",jsonArrayNew.toString());
            SaveUserData.data_total.put("pick_up_type","0");
            Intent intent = new Intent(getApplicationContext(), Order_details.class);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(getApplicationContext(),"You need to select at least one item!",Toast.LENGTH_SHORT).show();
        }
    }
}
