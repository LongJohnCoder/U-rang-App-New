package FragmentClasses;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.roger.gifloadinglibrary.GifLoadingView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import HelperClasses.AsyncResponse;
import HelperClasses.AsyncResponse2;
import HelperClasses.CheckNetwork;
import HelperClasses.RegisterUser;
import HelperClasses.RegisterUser2;
import Others.SaveUserData;
import us.tier5.u_rang.Order_details;
import us.tier5.u_rang.R;
import us.tier5.u_rang.Splash;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PriceList_fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PriceList_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PriceList_fragment extends Fragment implements AsyncResponse.Response, AsyncResponse2.Response2, View.OnClickListener{
    //page variables
    LinearLayout llparentPrice;
    TextView tvTotalCount;
    TextView tvAllTotalPrice;
    ArrayList<View> pagesAdded = new ArrayList<>();


    //configuration variables
    FragmentManager manager;
    Bundle mysavedInstance;
    TextView tvSchedule;
    //server variable
    HashMap<String, String> data = new HashMap<String,String>();
    String route = "/V1/get-prices";
    String routeProfileDetails = "/V1/getProgileDetails";
    RegisterUser registerUser = new RegisterUser("POST");
    RegisterUser2 registerUser2 = new RegisterUser2("POST");

    //gif loader variables

    GifLoadingView mGifLoadingView;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public PriceList_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PriceList_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PriceList_fragment newInstance(String param1, String param2) {
        PriceList_fragment fragment = new PriceList_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragView = inflater.inflate(R.layout.fragment_price_list_fragment, container, false);

        if (mListener != null) {
            mListener.onFragmentInteraction("U-Rang");
        }

        llparentPrice = (LinearLayout) fragView.findViewById(R.id.llparentPrice);
        tvTotalCount = (TextView) fragView.findViewById(R.id.tvTotalCount);
        tvAllTotalPrice = (TextView) fragView.findViewById(R.id.tvAllTotalPrice);
        //Toast.makeText(getContext(),"Pricelist",Toast.LENGTH_SHORT).show();

        //server and loader variable initialization
        mysavedInstance = savedInstanceState;
        registerUser.delegate = this;
        registerUser2.delegate = this;


        SharedPreferences prefs = this.getActivity().getSharedPreferences("U-rang", Context.MODE_PRIVATE);
        int user_id = prefs.getInt("user_id", 0);

        //Toast.makeText(getContext(),""+user_id,Toast.LENGTH_SHORT).show();
        data.put("user_id",Integer.toString(user_id));

        registerUser2.register(data, routeProfileDetails);


        manager = ((Activity) getContext()).getFragmentManager();
        /*mGifLoadingView = new GifLoadingView();
        mGifLoadingView.setImageResource(R.drawable.loading_3);*/

        if(CheckNetwork.isInternetAvailable(getContext())) //returns true if internet available
        {

            /*mGifLoadingView.show(manager, "Loading");
            mGifLoadingView.setBlurredActionBar(true);*/
            registerUser.register(data,route);
        }
        else
        {
            Toast.makeText(getContext(),"No Internet Connection",Toast.LENGTH_SHORT).show();
        }

        tvSchedule = (TextView) fragView.findViewById(R.id.tvSchedule);
        tvSchedule.setOnClickListener(this);

        return fragView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void processFinish(String output) {
        //mGifLoadingView.dismiss();

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
                        View inflatedCategory= getLayoutInflater(mysavedInstance).inflate(R.layout.categorylist, null, false);
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

                            View inflatedLayout= getLayoutInflater(mysavedInstance).inflate(R.layout.listview, null, false);
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
                Toast.makeText(getContext(),jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            Toast.makeText(getContext(),"Error in fetching price list",Toast.LENGTH_SHORT).show();
            Log.i("kingsukmajumder",e.toString());
        }
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
            Intent intent = new Intent(getContext(), Order_details.class);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(getContext(),"You need to select at least one item!",Toast.LENGTH_SHORT).show();
        }


    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
        void onFragmentInteraction(String title);
    }

    @Override
    public void processFinish2(String output) {
        try {
            JSONObject jsonObject = new JSONObject(output);
            Log.v("PROFILE_STATUS:", jsonObject.toString());
            if (jsonObject.getInt("status_code") == 301) {
                Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                LoginManager.getInstance().logOut();
                SharedPreferences.Editor editor = this.getActivity().getSharedPreferences("U-rang", Context.MODE_PRIVATE).edit();
                editor.putInt("user_id", 0);
                if (editor.commit()) {
                    Intent intent = new Intent(getContext(), Splash.class);
                    startActivity(intent);
                }
            } else if (jsonObject.getInt("status_code") == 400) {
                Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                LoginManager.getInstance().logOut();
                SharedPreferences.Editor editor = this.getActivity().getSharedPreferences("U-rang", Context.MODE_PRIVATE).edit();
                editor.putInt("user_id", 0);
                editor.putBoolean("is_social_registered", false);
                if (editor.commit()) {
                    Intent intent = new Intent(getContext(), Splash.class);
                    startActivity(intent);
                }
            } else {
                Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e)
        {
            //Toast.makeText(getContext(),"Error in fetching order history!",Toast.LENGTH_SHORT).show();
            Log.i("kingsukmajumder","error in profile"+e.toString());
        }
    }
}
