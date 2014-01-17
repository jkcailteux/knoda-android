package core;

import android.app.AlertDialog;

import views.core.MainActivity;

/**
 * Created by nick on 1/17/14.
 */
public class ErrorReporter{

    private final MainActivity mActivity;

    public ErrorReporter(MainActivity activity) {
        this.mActivity = activity;
    }


    public void showError(String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setMessage(message).setPositiveButton("Ok", null);
        builder.create().show();
    }


}
