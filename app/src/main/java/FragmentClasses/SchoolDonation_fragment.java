package FragmentClasses;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.login.LoginManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import HelperClasses.AsyncResponse;
import HelperClasses.AsyncResponse2;
import HelperClasses.CheckNetwork;
import HelperClasses.RegisterUser;
import HelperClasses.RegisterUser2;
import HelperClasses.UserConstants;
import us.tier5.u_rang.R;
import us.tier5.u_rang.Splash;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SchoolDonation_fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SchoolDonation_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SchoolDonation_fragment extends Fragment implements AsyncResponse.Response, AsyncResponse2.Response2, AdapterView.OnItemSelectedListener,View.OnClickListener{

    //page variables
    LinearLayout LLSchoolList;
    Bundle mysavedInstance;
    Spinner spinner;
    List<String> spinnerArray =  new ArrayList<String>();
    List<Integer> spinnerId = new ArrayList<Integer>();
    Integer selectedSchoolId = 0;
    TextView tvAddSchool;
    int user_id;
    int processfinishCall = 0;

    //server variable
    HashMap<String, String> data = new HashMap<String,String>();
    String route = "/V1/get-school-preferences";
    String routeProfileDetails = "/V1/getProgileDetails";
    RegisterUser registerUser = new RegisterUser("POST");
    RegisterUser2 registerUser2 = new RegisterUser2("POST");

    //loading
    ProgressDialog loadingAddSchool;

    //object variables
    ArrayList<AsyncTask> imageLoadingThread= new ArrayList<AsyncTask>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public SchoolDonation_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SchoolDonation_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SchoolDonation_fragment newInstance(String param1, String param2) {
        SchoolDonation_fragment fragment = new SchoolDonation_fragment();
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
        View fragView = inflater.inflate(R.layout.fragment_school_donation_fragment, container, false);

        if (mListener != null) {
            mListener.onFragmentInteraction("U-Rang");
        }

        registerUser.delegate = this;
        registerUser2.delegate = this;
        mysavedInstance = savedInstanceState;

        LLSchoolList = (LinearLayout) fragView.findViewById(R.id.LLSchoolList);

        //connet to server to get order tracker
        //SharedPreferences preferences = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences prefs = this.getActivity().getSharedPreferences("U-rang", Context.MODE_PRIVATE);
        user_id = prefs.getInt("user_id", 0);

        //Toast.makeText(getContext(),""+user_id,Toast.LENGTH_SHORT).show();
        data.put("user_id",Integer.toString(user_id));

        if(CheckNetwork.isInternetAvailable(getContext())) //returns true if internet available
        {
            registerUser2.register(data, routeProfileDetails);
            registerUser.register(data,route);
        }
        else
        {
            Toast.makeText(getContext(),"No Internet Connection",Toast.LENGTH_SHORT).show();
        }



        spinner = (Spinner) fragView.findViewById(R.id.spinner);


        tvAddSchool = (TextView) fragView.findViewById(R.id.tvAddSchool);
        tvAddSchool.setOnClickListener(this);

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

        if(processfinishCall==0)
        {
            Log.i("schooldonation",output);
            try
            {
                JSONObject jsonObject = new JSONObject(output);
                if(jsonObject.getBoolean("status"))
                {
                    JSONArray jsonArray = new JSONArray(jsonObject.getString("response"));
                    JSONArray school_listArray = new JSONArray(jsonObject.getString("school_list"));
                    if(school_listArray.length()==0)
                    {
                        //Toast.makeText(getContext(),"No schools have been added yet!",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        spinnerId.clear();
                        spinnerArray.clear();
                        spinnerArray.add("Select School");
                        spinnerId.add(0);
                        for(int i=0;i<school_listArray.length();i++)
                        {

                            JSONObject school_listObject = school_listArray.getJSONObject(i);
                            //Log.i("schoolList",school_listObject.toString());
                            spinnerArray.add(school_listObject.getString("school_name"));
                            spinnerId.add(school_listObject.getInt("id"));
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, spinnerArray);

                        spinner.setAdapter(adapter);
                        spinner.setOnItemSelectedListener(this);
                    }
                    if(jsonArray.length()==0)
                    {
                        View inflatedLayoutNoSchools = getLayoutInflater(mysavedInstance).inflate(R.layout.no_schools_added, null, false);
                        LLSchoolList.addView(inflatedLayoutNoSchools);
                        //Toast.makeText(getContext(),"No favourite schools added yet",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        LLSchoolList.removeAllViews();
                        for(int i=0;i<jsonArray.length();i++)
                        {
                            JSONObject currentObj = jsonArray.getJSONObject(i);
                            JSONObject schoolDonationObject = new JSONObject(currentObj.getString("school_donation"));
                            String schoolName = schoolDonationObject.getString("school_name");
                            Double gainedMoney = Double.parseDouble(schoolDonationObject.getString("actual_total_money_gained"));
                            Double pendingMoney = Double.parseDouble(schoolDonationObject.getString("actual_pending_money"));
                            String image = schoolDonationObject.getString("image");
                            String imageUrl = UserConstants.BASE_URL+UserConstants.IMAGE_FOLDER+image;


                            View inflatedLayoutNoSchools = getLayoutInflater(mysavedInstance).inflate(R.layout.school_donation_schools, null, false);
                            TextView tvSchoolNameDonation = (TextView) inflatedLayoutNoSchools.findViewById(R.id.tvSchoolNameDonation);
                            TextView tvSchoolGainedMoney = (TextView) inflatedLayoutNoSchools.findViewById(R.id.tvSchoolGainedMoney);
                            TextView tvSchoolPendingMoney = (TextView) inflatedLayoutNoSchools.findViewById(R.id.tvSchoolPendingMoney);
                            final ImageView ivSchoolImageDonation = (ImageView) inflatedLayoutNoSchools.findViewById(R.id.ivSchoolImageDonation);

                            Glide.with(this).load(UserConstants.BASE_URL+UserConstants.IMAGE_FOLDER+image).placeholder(R.drawable.loadingimage).crossFade().into(ivSchoolImageDonation);

                            /*AsyncTask asyncTask = new AsyncTask<Void, Void, Void>() {
                                Bitmap bmp;
                                @Override
                                protected void onPreExecute() {
                                    super.onPreExecute();
                                }
                                @Override
                                protected Void doInBackground(Void... params) {

                                    try {
                                        InputStream in = new URL(imageUrl).openStream();
                                        bmp = BitmapFactory.decodeStream(in);
                                    } catch (Exception e) {
                                        //Toast.makeText(getContext(),"Some error occoured while loading images!",Toast.LENGTH_LONG).show();
                                        Log.i("kingsukmajumder","error in loading images "+e.toString());
                                    }
                                    return null;
                                }

                                @Override
                                protected void onPostExecute(Void result) {
                                    //loading.dismiss();
                                    if (bmp != null)

                                        ivSchoolImageDonation.setImageBitmap(bmp);
                                }
                            }.execute();*/

                            //imageLoadingThread.add(asyncTask);


                            tvSchoolNameDonation.setText(schoolName);
                            tvSchoolGainedMoney.setText("$ "+round(gainedMoney,2));
                            tvSchoolPendingMoney.setText("$ "+round(pendingMoney,2));

                            LLSchoolList.addView(inflatedLayoutNoSchools);
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
                //Toast.makeText(getContext(),"Error in fetching user data",Toast.LENGTH_SHORT).show();

            }
        }
        else
        {
            loadingAddSchool.dismiss();
            try
            {
                JSONObject jsonObject = new JSONObject(output);
                if(jsonObject.getBoolean("status"))
                {
                    registerUser.register(data,route);

                }
                else
                {
                    Toast.makeText(getContext(),jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception e)
            {
                Log.i("kingsukmajumder",e.toString());
            }
            processfinishCall = 0;

        }

    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedSchoolId = spinnerId.get(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        selectedSchoolId = 0;
    }

    @Override
    public void onClick(View v) {
        registerUser2.register(data, routeProfileDetails);
        if(selectedSchoolId==0)
        {
            Toast.makeText(getContext(),"Select a school first!",Toast.LENGTH_SHORT).show();
        }
        else
        {

            //server variable
            HashMap<String, String> data_addSchool = new HashMap<String,String>();
            String routeaddSchoolPreference = "/V1/add-school-preferences";
            RegisterUser registerUserAdd_school = new RegisterUser("POST");

            data_addSchool.put("user_id",Integer.toString(user_id));
            data_addSchool.put("school_id",Integer.toString(selectedSchoolId));
            processfinishCall++;
            registerUserAdd_school.delegate = this;
            loadingAddSchool = ProgressDialog.show(getContext(), "Please Wait",null, true, true);
            registerUserAdd_school.register(data_addSchool,routeaddSchoolPreference);

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("kingsukmajumder","pause");
        for(int i=0;i<imageLoadingThread.size();i++)
        {
            imageLoadingThread.get(i).cancel(true);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("kingsukmajumder","destroy");
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
