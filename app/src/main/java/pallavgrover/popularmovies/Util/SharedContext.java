package pallavgrover.popularmovies.Util;

import android.content.Context;

public class SharedContext {
    private static Context sContext;

    public static void setContext(Context context) {
        sContext = context;
    }

    public static Context getContext(){
        return sContext;
    }
}
