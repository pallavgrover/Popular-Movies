package pallavgrover.popularmovies.Util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;


public class SnackBarManager {
    private static SnackBarManager snackBarManager;

    private SnackBarManager() {
    }

    public static SnackBarManager getSnackBarManagerInstance() {
        if (snackBarManager == null)
            snackBarManager = new SnackBarManager();
        return snackBarManager;
    }

    public void showSnackBar(Context context, String message) {
        showSnackBar(context, message, false);
    }

    public void showSnackBar(Context context, String message, boolean isLongDuration) {
        if (context != null && context instanceof Activity) {
            showMySnackbar(context, message, isLongDuration);
        } else if (SharedContext.getContext() != null) {
            showMySnackbar(SharedContext.getContext(), message, isLongDuration);
        }
    }

    private void showMySnackbar(Context context, String message, boolean isLongDuration) {
        Snackbar snackbar = null;
        if(!isLongDuration) {
            snackbar = Snackbar.make(((Activity) context).findViewById(android.R.id.content), "" + message, Snackbar.LENGTH_SHORT);
        } else {
            snackbar = Snackbar.make(((Activity) context).findViewById(android.R.id.content), "" + message, Snackbar.LENGTH_LONG);
        }
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(Color.BLACK);
        snackbar.show();
    }


    public interface SnackBarUnDoInterface {
        public void undoSnackBar();
    }
}
