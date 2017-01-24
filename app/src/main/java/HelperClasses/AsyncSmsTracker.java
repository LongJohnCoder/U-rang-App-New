package HelperClasses;

/**
 * Created by root on 15/7/16.
 */
public class AsyncSmsTracker {
    public interface ResponseSms {
        void smsReceived(String output);
    }

}
