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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.roger.gifloadinglibrary.GifLoadingView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import HelperClasses.AsyncResponse;
import HelperClasses.AsyncResponse2;
import HelperClasses.AsyncResponse3;
import HelperClasses.CheckNetwork;
import HelperClasses.RegisterUser;
import HelperClasses.RegisterUser2;
import HelperClasses.RegisterUser3;
import us.tier5.u_rang.Dashboard;
import us.tier5.u_rang.LoginActivity;
import us.tier5.u_rang.R;
import us.tier5.u_rang.Splash;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Orders_fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Orders_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Orders_fragment extends Fragment implements AsyncResponse.Response, AsyncResponse2.Response2, AsyncResponse3.Response3 {
    //Page variables
    LinearLayout llTrackStatus;

    //configuration variables
    FragmentManager manager;
    Bundle mysavedInstance;

    //server variable
    HashMap<String, String> data = new HashMap<String,String>();
    HashMap<String, String> dataCancel = new HashMap<>();
    String route = "/V1/order-tracker";
    String routeCancleOrder = "/V1/cancle-order";
    String routeGetProfileDetails = "/V1/getProgileDetails";
    RegisterUser registerUser = new RegisterUser("POST");
    RegisterUser2 registerUser2 = new RegisterUser2("POST");
    RegisterUser3 registerUser3 = new RegisterUser3("POST");

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

    public Orders_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Orders_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Orders_fragment newInstance(String param1, String param2) {
        Orders_fragment fragment = new Orders_fragment();
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
        View fragView = inflater.inflate(R.layout.fragment_orders_fragment, container, false);

        if (mListener != null) {
            mListener.onFragmentInteraction("U-Rang");
        }

        llTrackStatus = (LinearLayout) fragView.findViewById(R.id.llTrackStatus);


        //server and loader variable initialization
        mysavedInstance = savedInstanceState;
        registerUser.delegate = this;
        registerUser2.delegate = this;
        registerUser3.delegate = this;

        registerUser3.register(data, routeGetProfileDetails);

        manager = ((Activity) getContext()).getFragmentManager();
        mGifLoadingView = new GifLoadingView();
        mGifLoadingView.setImageResource(R.drawable.loading_3);

        //connet to server to get order tracker
        //SharedPreferences preferences = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences prefs = this.getActivity().getSharedPreferences("U-rang", Context.MODE_PRIVATE);
        int user_id = prefs.getInt("user_id", 0);

        //Toast.makeText(getContext(),""+user_id,Toast.LENGTH_SHORT).show();
        data.put("user_id",Integer.toString(user_id));



        if(CheckNetwork.isInternetAvailable(getContext())) //returns true if internet available
        {

            mGifLoadingView.show(manager, "Loading");
            mGifLoadingView.setBlurredActionBar(true);
            registerUser.register(data,route);
        }
        else
        {
            Toast.makeText(getContext(),"No Internet Connection",Toast.LENGTH_SHORT).show();
        }

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
        Log.i("kingsukmajumder","output in orders : "+output);
        try
        {
            mGifLoadingView.dismiss();
            JSONObject jsonObject = new JSONObject(output);
            if(jsonObject.getBoolean("status"))
            {
                String jsonArrayString = jsonObject.getString("response");
                JSONArray jsonArray = new JSONArray(jsonArrayString);
                if(jsonArray.length()>0)
                {
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject currentObj = jsonArray.getJSONObject(i);
                        int orderStatus = Integer.parseInt(currentObj.getString("order_status"));
                        final int id = Integer.parseInt(currentObj.getString("pick_up_req_id"));
                        final View inflatedLayout= getLayoutInflater(mysavedInstance).inflate(R.layout.track_orders_layout, null, false);
                        inflatedLayout.setTag(id);
                        /*inflatedLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Toast.makeText(getContext(),"cancle Order"+inflatedLayout.getTag(),Toast.LENGTH_SHORT).show();
                                data.put("pick_up_id",String.valueOf(id));
                                registerUser2.register(data,routeCancleOrder);
                            }
                        });*/
                        ImageView trackImage = (ImageView) inflatedLayout.findViewById(R.id.trackImage);
                        TextView tvPickupInfo = (TextView) inflatedLayout.findViewById(R.id.tvPickupInfo);
                        TextView tvOrderId = (TextView) inflatedLayout.findViewById(R.id.tvOrderId);
                        TextView tvCancle = (TextView) inflatedLayout.findViewById(R.id.tvCancle);
                        tvCancle.setTag(id);
                        tvCancle.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.i("CANCEL_ORDER_ID", String.valueOf(id));
                                dataCancel.put("pick_up_id",String.valueOf(id));
                                registerUser2.register(dataCancel, routeCancleOrder);
                            }
                        });
                        tvOrderId.setText("Order ID: "+currentObj.getString("pick_up_req_id"));
                        if(orderStatus==1)
                        {
                            trackImage.setImageResource(R.drawable.order_tracker_2);
                            tvPickupInfo.setText("Yet to pick up");
                            tvCancle.setVisibility(View.VISIBLE);
                        }
                        else if(orderStatus == 2)
                        {
                            trackImage.setImageResource(R.drawable.order_tracker_3);
                            //tvPickupInfo.setText("Expected delivery: "+currentObj.getString("expected_return_date"));
                            tvPickupInfo.setText("Expected return: "+new SimpleDateFormat("dd'th' MMM").format(new SimpleDateFormat("yyyy-MM-dd").parse(currentObj.getString("expected_return_date"))));

                        }
                        else if(orderStatus == 3)
                        {
                            trackImage.setImageResource(R.drawable.order_tracker_4);
                            //tvPickupInfo.setText("Expected delivery: "+currentObj.getString("expected_return_date"));
                            tvPickupInfo.setText("Expected return: "+new SimpleDateFormat("dd'th' MMM").format(new SimpleDateFormat("yyyy-MM-dd").parse(currentObj.getString("expected_return_date"))));

                        }
                        else if(orderStatus == 4)
                        {
                            trackImage.setImageResource(R.drawable.order_tracker_5);
                            //tvPickupInfo.setText("Return Date: "+currentObj.getString("return_date"));
                            tvPickupInfo.setText("Return Date: "+new SimpleDateFormat("dd'th' MMM").format(new SimpleDateFormat("yyyy-MM-dd").parse(currentObj.getString("return_date"))));
                        }
                        else if(orderStatus == 5)
                        {
                            trackImage.setImageResource(R.drawable.order_tracker_1);
                            //tvPickupInfo.setText("Return Date: "+currentObj.getString("return_date"));
                            tvPickupInfo.setText("Order Cancelled");
                        }
                        llTrackStatus.addView(inflatedLayout);
                    }
                }
                else
                {
                    View inflatedLayout= getLayoutInflater(mysavedInstance).inflate(R.layout.no_order_full_page, null, false);
                    Button btnScheduleAPickUp = (Button) inflatedLayout.findViewById(R.id.btnScheduleAPickUp);
                    btnScheduleAPickUp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), Dashboard.class);
                            startActivity(intent);

                        }
                    });
                    llTrackStatus.addView(inflatedLayout);
                }

            }
            else
            {
                Toast.makeText(getContext(),jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            Toast.makeText(getContext(),"Error in fetching order history!",Toast.LENGTH_SHORT).show();
            Log.i("kingsukmajumder",e.toString());
        }
    }

    @Override
    public void processFinish2(String output) {
        Log.i("CANCEL_ORDER",output);
        try
        {
            JSONObject jsonObject = new JSONObject(output);
            if(jsonObject.getBoolean("status"))
            {
                Toast.makeText(getContext(),jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(),Dashboard.class);
                startActivity(intent);
            }
            else
            {
                Toast.makeText(getContext(),jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(),Dashboard.class);
                startActivity(intent);
            }
        }
        catch (Exception e)
        {
            Log.i("kingsukmajumder","error in cancle order "+e.toString());
        }

    }

    @Override
    public void processFinish3(String output) {
        try {
            JSONObject jsonObject = new JSONObject(output);
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
        void onFragmentInteraction(String title);
    }
}
