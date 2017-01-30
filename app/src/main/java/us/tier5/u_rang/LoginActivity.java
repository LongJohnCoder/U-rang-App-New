package us.tier5.u_rang;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.roger.gifloadinglibrary.GifLoadingView;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;

import HelperClasses.AsyncResponse;
import HelperClasses.AsyncResponseSms;
import HelperClasses.AsyncSmsTracker;
import HelperClasses.CheckNetwork;
import HelperClasses.RegisterSms;
import HelperClasses.RegisterUser;

public class LoginActivity extends AppCompatActivity implements AsyncResponse.Response,View.OnClickListener,GoogleApiClient.OnConnectionFailedListener, AsyncResponseSms.ResponseSms {
    TextView signUp;
    Button siginInButton;
    HashMap<String, String> data = new HashMap<String,String>();
    TextView email;
    TextView pass;
    String route = "/V1/login";
    String socialLoginRoute = "/V1/social-login";
    int user_id;
    ProgressDialog loading;
    LoginButton loginButton;
    SignInButton signInButton;
    CallbackManager callbackManager;
    GoogleApiClient mGoogleApiClient;
    int RC_SIGN_IN = 1;
    TextView forgotPass;
    
    RegisterUser registerUser = new RegisterUser("POST");

    //loading
    GifLoadingView mGifLoadingView;



    //layouts for animation
    LinearLayout LLEmailAddress;
    LinearLayout LLPassword;
    LinearLayout LLForgotPassword;
    FrameLayout FLLoginButton;

    //Animation Files
    Animation slideInRight;
    Animation slideInLeft;
    Animation pushUpIn;

    TextView edt;
    String smsVerificationCode="";

    RegisterSms registerSms = new RegisterSms("POST");
    HashMap<String,String> dataSms = new HashMap<>();
    String routeSms = "https://www.textinbulk.com/app/api/send/ND";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize the SDK before executing any other operations,
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        setContentView(R.layout.activit_login);
        //Log.i("kingsukmajumder","here");




        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
// options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });


        //checking if the user already logged in or not
        SharedPreferences prefs = getSharedPreferences("U-rang", MODE_PRIVATE);
        int restoredText = prefs.getInt("user_id", 0);

        if (restoredText != 0)
        {
            Intent i = new Intent(LoginActivity.this,Dashboard.class);
            startActivity(i);
        }


        registerUser.delegate = this;
        registerSms.delegate = this;

        email = (TextView) findViewById(R.id.email);
        pass = (TextView) findViewById(R.id.pass);


        signUp = (TextView) findViewById(R.id.signUp);


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(i);
            }
        });

        siginInButton = (Button) findViewById(R.id.siginInButton);
        siginInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String InputEmail = email.getText().toString();
                String InputPass = pass.getText().toString();

                if(!InputEmail.equals("") && !InputPass.equals(""))
                {
                    data.put("email",InputEmail);
                    data.put("password",InputPass);

                    if(CheckNetwork.isInternetAvailable(getApplication())) //returns true if internet available
                    {
                        //starting gif animation
                        mGifLoadingView = new GifLoadingView();
                        mGifLoadingView.setImageResource(R.drawable.loading_3);
                        mGifLoadingView.show(getFragmentManager(), "Loading");
                        mGifLoadingView.setBlurredActionBar(true);

                        registerUser.register(data,route);
                    }
                    else
                    {
                        Toast.makeText(getApplication(),"No Internet Connection",Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(),"Email And Password Required",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //facebook login start
        loginButton = (LoginButton) findViewById(R.id.login_button_facebook);


        callbackManager = CallbackManager.Factory.create();

        //loginButton.setOnClickListener(this);


        //Log.i("kingsukmajumder","Access token : "+AccessToken.getCurrentAccessToken().toString());

        //Auto-login
        //LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        loginButton.setReadPermissions(Arrays.asList("email"));
        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code

                GraphRequest request = GraphRequest.newMeRequest(
                        AccessToken.getCurrentAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                // Application code
                                //Log.i("kingsukmajumder",response.toString());
                                facebookLogin(response);
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,link,location,gender,verified,updated_time,birthday");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                // App code
                Log.i("kingsukmajumder","cancel");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.i("kingsukmajumder",exception.toString());
            }
        });
        //facebook login end

        forgotPass = (TextView) findViewById(R.id.forgotPass);
        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,ForgotPassword.class);
                startActivity(intent);
            }
        });


        //initialization for anims
        LLEmailAddress = (LinearLayout) findViewById(R.id.LLEmailAddress);
        LLPassword = (LinearLayout) findViewById(R.id.LLPassword);
        LLForgotPassword = (LinearLayout) findViewById(R.id.LLForgotPassword);
        FLLoginButton = (FrameLayout) findViewById(R.id.FLLoginButton);


        //animation initialization
        slideInRight = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_in_right);
        slideInLeft = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_in_left);
        pushUpIn = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.push_up_in);

        showPageLoadAnimations();
    }

    public void showPageLoadAnimations()
    {
        LLEmailAddress.startAnimation(slideInRight);
        LLPassword.startAnimation(slideInLeft);

        LLForgotPassword.startAnimation(slideInLeft);
        FLLoginButton.startAnimation(slideInRight);

        loginButton.startAnimation(pushUpIn);
        signInButton.startAnimation(pushUpIn);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // An unresolvable error has occurred and a connection to Google APIs
        // could not be established. Display an error message, or handle
        // the failure silently
        Log.i("kingsukmajumder","on connection fail listner");
        Toast.makeText(getApplicationContext(),"Connection Failed",Toast.LENGTH_SHORT).show();
        // ...
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {

        Log.i("kingsukmajumder","googleSignIn result is: "+result.getSignInAccount());

        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            //Log.d("kingsukmajumder", "handleSignInResult:" + acct.getEmail());

            data.put("email",acct.getEmail());
            data.put("password","google");
            data.put("name",acct.getDisplayName());
            data.put("social_id",acct.getId());
            data.put("social_network_name","google");

            createSmsPopup();

            /*mGifLoadingView = new GifLoadingView();
            mGifLoadingView.setImageResource(R.drawable.loading_3);
            mGifLoadingView.show(getFragmentManager(), "Loading");
            mGifLoadingView.setBlurredActionBar(true);
            registerUser.register(data,socialLoginRoute);*/

        } else {
                Toast.makeText(getApplicationContext(),"Problem occured in google sign in",Toast.LENGTH_SHORT).show();
                Log.i("kingsukmajumder",result.toString());
        }
    }

    @Override
    public void processFinish(String output) {
        //Toast.makeText(getApplicationContext(),output,Toast.LENGTH_SHORT).show();
        mGifLoadingView.dismiss();
        Log.i("kingsukmajumder","response after login: "+output);
        try
        {
            JSONObject jsonObject = new JSONObject(output);
            Boolean status = jsonObject.getBoolean("status");

            if(status)
            {
                String responseObj = jsonObject.getString("response");
                JSONObject user_data = new JSONObject(responseObj);

                user_id = user_data.getInt("id");
                
                /*for (int i=0;i<jsonArray.length();i++)
                {
                    user_id = jsonArray.getJSONObject(i).getInt("id");

                }*/
                SharedPreferences.Editor editor = getSharedPreferences("U-rang", MODE_PRIVATE).edit();
                editor.putInt("user_id", user_id);
                if(editor.commit())
                {
                    Toast.makeText(getApplicationContext(),jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this,Dashboard.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);               }
                else
                {
                    Toast.makeText(getApplication(),"Some problem occoured, You may have to login again when you launch the app!",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(LoginActivity.this,Dashboard.class);
                    startActivity(intent);
                }

            }
            else
            {
                Snackbar.make(this.findViewById(android.R.id.content), jsonObject.getString("message"), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Toast.makeText(getApplicationContext(),jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
            }

        }
        catch (Exception e)
        {
            Log.i("kingsukmajumder",e.toString());
            Toast.makeText(getApplication(),"Error in login",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onClick(View v) {

    }

    public void facebookLogin(GraphResponse response)
    {
        //Log.i("kingsukmajumder","facebook response "+response);
        try
        {
            //JSONObject jsonObject = new JSONObject(response.toString());
            String social_id = response.getJSONObject().getString("id");
            String email = response.getJSONObject().getString("email");
            String name = response.getJSONObject().getString("name");
            String social_network_name = "facebook";
            String password = "facebook";
            data.put("email",email);
            data.put("password",password);
            data.put("name",name);
            data.put("social_id",social_id);
            data.put("social_network_name",social_network_name);

            createSmsPopup();

            /*mGifLoadingView = new GifLoadingView();
            mGifLoadingView.setImageResource(R.drawable.loading_3);
            mGifLoadingView.show(getFragmentManager(), "Loading");
            mGifLoadingView.setBlurredActionBar(true);
            registerUser.register(data,socialLoginRoute);*/
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),"Cannot do a facebook login right now!",Toast.LENGTH_SHORT).show();
            Log.i("kingsukmajumder",e.toString());
        }
    }

    private int backButtonCount = 0;
    @Override
    public void onBackPressed()
    {
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

    @Override
    protected void onRestart() {
        super.onRestart();
        showPageLoadAnimations();
    }

    public void createSmsPopup()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.popupphoneno, null);
        dialogBuilder.setView(dialogView);

        edt = (EditText) dialogView.findViewById(R.id.etCoupone);
        edt.setText("+1");

        // edt.setText(String.valueOf((int)(Math.random()*9000)+1000));

        dialogBuilder.setCancelable(false);
        dialogBuilder.setTitle("Enter Phone No:");
        dialogBuilder.setMessage("Please keep your country code too.");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                if(edt.getText().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext(),"You need to enter a the message code",Toast.LENGTH_SHORT).show();

                }
                else
                {

                    if(!edt.getText().toString().equals(""))
                    {
                        data.put("personal_phone",edt.getText().toString());
                        smsVerificationCode=String.valueOf((int)(Math.random()*9000)+1000);
                        Log.i("kingsukmajumder","sms verification code is "+smsVerificationCode);
                        dataSms.put("to",edt.getText().toString());
                        dataSms.put("body",smsVerificationCode);
                        registerSms.register(dataSms,routeSms);
                        createSmsPopupForOtp();
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this, "Please Enter a phone no!", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });

        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    @Override
    public void processFinishSms(String output) {
        Log.i("kingsukmajumder",output);
    }

    public void createSmsPopupForOtp()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.popupphoneno, null);
        dialogBuilder.setView(dialogView);

        edt = (EditText) dialogView.findViewById(R.id.etCoupone);

        // edt.setText(String.valueOf((int)(Math.random()*9000)+1000));

        dialogBuilder.setCancelable(false);
        dialogBuilder.setTitle("Enter sms verification code:");
        dialogBuilder.setMessage("Please wait for sometime to receive it in your phone.");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                if(edt.getText().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext(),"You need to enter a the message code",Toast.LENGTH_SHORT).show();

                }
                else
                {
                    if(smsVerificationCode.equals(edt.getText().toString()))
                    {
                        mGifLoadingView = new GifLoadingView();
                        mGifLoadingView.setImageResource(R.drawable.loading_3);
                        mGifLoadingView.show(getFragmentManager(), "Loading");
                        mGifLoadingView.setBlurredActionBar(true);
                        registerUser.register(data,socialLoginRoute);
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this, "Verification code did'nt matched!", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });

        AlertDialog b = dialogBuilder.create();
        b.show();
    }
}
