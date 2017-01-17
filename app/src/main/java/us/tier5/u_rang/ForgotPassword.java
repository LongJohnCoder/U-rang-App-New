package us.tier5.u_rang;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;

import HelperClasses.AsyncResponse;
import HelperClasses.EmailValidator;
import HelperClasses.RegisterUser;

public class ForgotPassword extends AppCompatActivity implements AsyncResponse.Response {

    //page variables
    EditText etEnterEmail;
    Button btnChangePassword;

    //server variables
    HashMap<String, String> data = new HashMap<String,String>();
    String route = "/V1/postForgotPassword";
    RegisterUser registerUser = new RegisterUser("POST");

    //loading variable
    ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        registerUser.delegate = this;


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etEnterEmail = (EditText) findViewById(R.id.etEnterEmail);

        btnChangePassword = (Button) findViewById(R.id.btnChangePassword);

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmailValidator emailValidator = new EmailValidator();
                if(emailValidator.validate(etEnterEmail.getText().toString()))
                {
                    loading = ProgressDialog.show(ForgotPassword.this,null,"Please Wait", true, false);
                    data.put("forgot_pass_user_email",etEnterEmail.getText().toString());
                    registerUser.register(data,route);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Enter a valid email",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void processFinish(String output) {
        Log.i("kingsukmajumder",output);
        loading.dismiss();
        try
        {
            JSONObject jsonObject = new JSONObject(output);
            if(jsonObject.getBoolean("status"))
            {
                Toast.makeText(getApplicationContext(),jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this,LoginActivity.class);
                startActivity(intent);
            }
            else
            {
                Toast.makeText(getApplicationContext(),jsonObject.getString("message"),Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e)
        {
            Log.i("kingsukmajumder","error in password reset "+e.toString());
        }
    }
}
