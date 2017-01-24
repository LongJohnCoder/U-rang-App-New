package HelperClasses;

import android.os.AsyncTask;

import java.util.HashMap;

/**
 * Created by root on 15/7/16.
 */
public class RegisterSms {
    String method;
    public AsyncResponseSms.ResponseSms delegate = null;

    public RegisterSms(String method)
    {
        this.method = method;
    }


    public void register(final HashMap<String, String> data, String route) {
        final String FEED_URL = route;
        class RegisterUserData extends AsyncTask<String, Void, String> {

            ConnectToServer ruc = new ConnectToServer(method);


            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                delegate.processFinishSms(s);
            }

            @Override
            protected String doInBackground(String... params) {

                String result = ruc.sendPostRequest(FEED_URL,data);

                return  result;
            }
        }


        RegisterUserData ru = new RegisterUserData();

        ru.execute();

    }
}
