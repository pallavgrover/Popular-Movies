package pallavgrover.popularmovies.Util;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by pallav.grover on 21/06/17.
 */

public class Util {

    private static ConnectivityManager mCM;
    private static ProgressDialog mProgressDialog = null;

    public static boolean hasInternetAccess(Context context) {
        if (context == null) {
            return false;
        }
        if (mCM == null) {
            mCM = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        NetworkInfo netInfo = mCM.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected())    // Modified in Rev. 211
        {
            return true;
        }
        return false;
    }

    public static void showProgressDialog(Context mContext, String message) {
        try {
            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(mContext, "", /*mContext.getString(R.string.updating_text)*/message + "\t\t\t\t\t", true, false);
                mProgressDialog.setCancelable(true);
                mProgressDialog.setIndeterminate(false);
            } else {
                if (!mProgressDialog.isShowing()) {
                    mProgressDialog.show();
                }
            }
        } catch (Exception e)    // Added in Rev. 255
        {
            // Never mind
            // Will throw if progress dialog is being shown on an activity which is already finished
        }
    }

    public static void hideProgressDialog() {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        } catch (IllegalArgumentException e) {
            //Never mind
            //Will throw if progress dialog has not attached to windows.If not attached then no need to hide
        }
    }

}
