package FragmentClasses;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.roger.gifloadinglibrary.GifLoadingView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import HelperClasses.AsyncResponse;
import HelperClasses.AsyncResponse2;
import HelperClasses.AsyncResponse3;
import HelperClasses.CheckNetwork;
import HelperClasses.RegisterUser;
import HelperClasses.RegisterUser2;
import HelperClasses.RegisterUser3;
import Others.SaveUserData;
import us.tier5.u_rang.Dashboard;
import us.tier5.u_rang.Detailed_pickup;
import us.tier5.u_rang.LoginActivity;
import us.tier5.u_rang.Order_details;
import us.tier5.u_rang.R;
import us.tier5.u_rang.Splash;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Dashboard_fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Dashboard_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Dashboard_fragment extends Fragment implements AsyncResponse.Response, AsyncResponse2.Response2, AsyncResponse3.Response3 {
    //custom variables
    LoginButton loginButton;
    CallbackManager callbackManager;
    ImageView fastPickUp;
    ImageView detailedPickUp;
    LinearLayout llTrackStatus;
    int pick_up_type=2;
    TextView submitSchedule;

    RelativeLayout RLsubmitSchedule;
    View inflatedLayout;
    LinearLayout LLUpperImage;

    //configuration variables
    FragmentManager manager;
    Bundle mysavedInstance;

    //server variable
    HashMap<String, String> data = new HashMap<String,String>();
    String route = "/V1/order-tracker";
    String routeCancleOrder = "/V1/cancle-order";
    String routeGetProfileDetails = "/V1/getProgileDetails";
    RegisterUser registerUser = new RegisterUser("POST");
    RegisterUser2 registerUser2 = new RegisterUser2("POST");
    RegisterUser3 registerUser3 = new RegisterUser3("POST");

    //gif loader variables
    GifLoadingView mGifLoadingView;

    //total variable
    HashMap<String, String> data_total = new HashMap<String,String>();


    //Animation Files
    Animation slideInRight;
    Animation slideInLeft;
    Animation pushUpIn;
    Animation fadeIn;
    Animation pushDownIn;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Dashboard_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Dashboard_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Dashboard_fragment newInstance(String param1, String param2) {
        Dashboard_fragment fragment = new Dashboard_fragment();
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
        View fragView = inflater.inflate(R.layout.fragment_dashboard_fragment, container, false);

        mysavedInstance = savedInstanceState;
        registerUser.delegate = this;
        registerUser2.delegate = this;
        registerUser3.delegate = this;

        registerUser3.register(data,routeGetProfileDetails);

        manager = ((Activity) getContext()).getFragmentManager();
        mGifLoadingView = new GifLoadingView();
        mGifLoadingView.setImageResource(R.drawable.loading_3);

        llTrackStatus = (LinearLayout) fragView.findViewById(R.id.llTrackStatus);
        //Toast.makeText(getContext(),"Dashboard",Toast.LENGTH_SHORT).show();


        fastPickUp = (ImageView) fragView.findViewById(R.id.fastPickUp);
        //fastPickUp.setVisibility(View.INVISIBLE);
        fastPickUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pick_up_type==1)
                {
                    pick_up_type=2;
                    fastPickUp.setImageResource(R.drawable.fast_pickup);
                    Intent intent = new Intent(getContext(), Order_details.class);
                    SaveUserData.data_total.put("pick_up_type",String.valueOf(pick_up_type));
                    //intent.putExtra("data_total",data_total);
                    startActivity(intent);
                }
                else
                {
                    fastPickUp.setImageResource(R.drawable.fast_pickup_2);
                    detailedPickUp.setImageResource(R.drawable.detail_pickup);
                    pick_up_type=1;
                    Intent intent = new Intent(getContext(), Order_details.class);
                    SaveUserData.data_total.put("pick_up_type",String.valueOf(pick_up_type));
                    //intent.putExtra("data_total",data_total);
                    startActivity(intent);
                }
            }
        });

        detailedPickUp = (ImageView) fragView.findViewById(R.id.detailedPickUp);
        //detailedPickUp.setVisibility(View.INVISIBLE);
        detailedPickUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pick_up_type==0)
                {
                    pick_up_type=2;
                    detailedPickUp.setImageResource(R.drawable.detail_pickup);
                    Intent intent = new Intent(getContext(), Detailed_pickup.class);
                    SaveUserData.data_total.put("pick_up_type",String.valueOf(pick_up_type));
                    //intent.putExtra("data_total",data_total);
                    startActivity(intent);
                }
                else
                {
                    detailedPickUp.setImageResource(R.drawable.detail_pickup_2);
                    fastPickUp.setImageResource(R.drawable.fast_pickup);
                    pick_up_type=0;
                    Intent intent = new Intent(getContext(), Detailed_pickup.class);
                    SaveUserData.data_total.put("pick_up_type",String.valueOf(pick_up_type));
                    //intent.putExtra("data_total",data_total);
                    startActivity(intent);
                }
            }
        });

        submitSchedule = (TextView) fragView.findViewById(R.id.submitSchedule);
        submitSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pick_up_type==2)
                {
                    Toast.makeText(getContext(),"Select a pick up type!",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(pick_up_type==0)
                    {
                        Intent intent = new Intent(getContext(), Detailed_pickup.class);
                        SaveUserData.data_total.put("pick_up_type",String.valueOf(pick_up_type));
                        //intent.putExtra("data_total",data_total);
                        startActivity(intent);
                    }
                    else
                    {
                        Intent intent = new Intent(getContext(), Order_details.class);
                        SaveUserData.data_total.put("pick_up_type",String.valueOf(pick_up_type));
                        //intent.putExtra("data_total",data_total);
                        startActivity(intent);
                    }

                }
            }
        });

        //connet to server to get order tracker
        //SharedPreferences preferences = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences prefs = this.getActivity().getSharedPreferences("U-rang", Context.MODE_PRIVATE);
        int user_id = prefs.getInt("user_id", 0);

        //Toast.makeText(getContext(),""+user_id,Toast.LENGTH_SHORT).show();
        data.put("user_id",Integer.toString(user_id));
        SaveUserData.data_total.put("user_id",Integer.toString(user_id));



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

        RLsubmitSchedule = (RelativeLayout) fragView.findViewById(R.id.RLsubmitSchedule);
        LLUpperImage = (LinearLayout) fragView.findViewById(R.id.LLUpperImage);


        //animation initialization
        slideInRight = AnimationUtils.loadAnimation(getContext(),
                R.anim.slide_in_right);
        slideInLeft = AnimationUtils.loadAnimation(getContext(),
                R.anim.slide_in_left);
        pushUpIn = AnimationUtils.loadAnimation(getContext(),R.anim.push_up_in);

        fadeIn = AnimationUtils.loadAnimation(getContext(),R.anim.push_up_in);

        pushDownIn = AnimationUtils.loadAnimation(getContext(),R.anim.push_down_in);


        return fragView;
    }

    public void showPageLoadAnimations()
    {
        //fastPickUp.setVisibility(View.VISIBLE);
        fastPickUp.startAnimation(slideInLeft);

        //detailedPickUp.setVisibility(View.VISIBLE);
        detailedPickUp.startAnimation(slideInRight);

        LLUpperImage.startAnimation(pushDownIn);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
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

        mGifLoadingView.dismiss();
        Log.i("kingsukmajumder",output);
        int currentIndex = 0;
        showActiveOrderStatus(output,currentIndex);
    }


    public void showActiveOrderStatus(String output, int currentIndex)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(output);
            if(jsonObject.getBoolean("status"))
            {
                JSONArray jsonArray = new JSONArray(jsonObject.getString("response"));

                if(jsonArray.length()>0)
                {
                    if(currentIndex<jsonArray.length())
                    {
                        JSONObject firstObj = jsonArray.getJSONObject(currentIndex);
                        int orderStatus = Integer.parseInt(firstObj.getString("order_status"));
                        final int id = Integer.parseInt(firstObj.getString("pick_up_req_id"));

                        inflatedLayout = getLayoutInflater(mysavedInstance).inflate(R.layout.track_layout, null, false);
                        inflatedLayout.setTag(id);
                        inflatedLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Toast.makeText(getContext(),""+inflatedLayout.getTag(),Toast.LENGTH_SHORT).show();

                            }
                        });
                        ImageView trackImage = (ImageView) inflatedLayout.findViewById(R.id.trackImage);
                        TextView tvPickupInfo = (TextView) inflatedLayout.findViewById(R.id.tvPickupInfo);
                        TextView tvOrderId = (TextView) inflatedLayout.findViewById(R.id.tvOrderId);
                        TextView tvCancle = (TextView) inflatedLayout.findViewById(R.id.tvCancle);
                        tvOrderId.setText("Order ID: "+firstObj.getString("pick_up_req_id"));
                        if(orderStatus==1)
                        {
                            trackImage.setImageResource(R.drawable.order_tracker_2);
                            tvPickupInfo.setText("Yet to pick up");
                            tvCancle.setVisibility(View.VISIBLE);
                            tvCancle.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //Toast.makeText(getContext(),"cancle",Toast.LENGTH_SHORT).show();
                                    data.put("pick_up_id",String.valueOf(id));
                                    registerUser2.register(data,routeCancleOrder);
                                }
                            });
                            llTrackStatus.addView(inflatedLayout);
                            //inflatedLayout.startAnimation(fadeIn);
                        }
                        else if(orderStatus == 2)
                        {
                            trackImage.setImageResource(R.drawable.order_tracker_3);
                            tvPickupInfo.setText("Expected delivery: "+firstObj.getString("expected_return_date"));
                            llTrackStatus.addView(inflatedLayout);
                            //inflatedLayout.startAnimation(fadeIn);
                        }
                        else if(orderStatus == 3)
                        {
                            trackImage.setImageResource(R.drawable.order_tracker_4);
                            tvPickupInfo.setText(firstObj.getString("expected_return_date"));
                            llTrackStatus.addView(inflatedLayout);
                            //inflatedLayout.startAnimation(fadeIn);
                        }
                        else if(orderStatus == 4)
                        {
                            /*View inflatedLayoutNoOrder = getLayoutInflater(mysavedInstance).inflate(R.layout.no_order, null, false);
                            llTrackStatus.addView(inflatedLayoutNoOrder);*/
                            //trackImage.setImageResource(R.drawable.order_tracker_5);
                            showActiveOrderStatus(output,currentIndex+1);
                        }
                        else if(orderStatus == 5)
                        {
                            /*View inflatedLayoutNoOrder = getLayoutInflater(mysavedInstance).inflate(R.layout.no_order, null, false);
                            llTrackStatus.addView(inflatedLayoutNoOrder);*/
                            showActiveOrderStatus(output,currentIndex+1);
                        }
                    }
                    else
                    {
                        View inflatedLayoutNoOrder = getLayoutInflater(mysavedInstance).inflate(R.layout.no_order, null, false);
                        llTrackStatus.addView(inflatedLayoutNoOrder);
                        //inflatedLayout.startAnimation(fadeIn);
                        Log.i("kingsukmajumder","current index is more than length error");
                    }

                }
                else
                {
                    View inflatedLayout= getLayoutInflater(mysavedInstance).inflate(R.layout.no_order, null, false);
                    llTrackStatus.addView(inflatedLayout);
                }
            }
            else
            {
                Toast.makeText(getContext(),jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
            }

            //calling the animation class
            showPageLoadAnimations();
        }

        catch (Exception e)
        {
            Toast.makeText(getContext(),"Error in fetching user data",Toast.LENGTH_SHORT).show();
            Log.i("kingsukmajumder",e.toString());
        }

    }

    @Override
    public void processFinish2(String output) {
        Log.i("kingsukmajumder",output);
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
            } else {
                Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            //Toast.makeText(getContext(),"Error in fetching order history!",Toast.LENGTH_SHORT).show();
            Log.i("kingsukmajumder","error in profile"+e.toString());
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        showPageLoadAnimations();
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
    }
}
