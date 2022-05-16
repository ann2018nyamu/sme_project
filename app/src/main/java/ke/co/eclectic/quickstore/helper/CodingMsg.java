package ke.co.eclectic.quickstore.helper;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;


/**
 * The type Coding msg.
 */
public class CodingMsg {
    public static  void l( String msg){
        Log.v("DAV",msg);
    }

    /**
     * shows informative toast message
     *
     * @param con the context
     * @param msg the message to display
     */
    public static void tl(Context con, String msg){
        Toasty.info(con, msg, Toast.LENGTH_LONG, true).show();
    }

    /**
     * shows warning toast message
     *
     * @param con the context
     * @param msg the message to display
     */
    public static void tlw(Context con, String msg){
        Toasty.warning(con, msg, Toast.LENGTH_LONG, true).show();
    }

    /**
     * shows success toast message
     *
     * @param con the context
     * @param msg the message to display
     */
    public static void tls(Context con, String msg){
        Toasty.success(con, msg, Toast.LENGTH_LONG, true).show();
    }

    /**
     * shows error toast message
     *
     * @param con the context
     * @param msg the message to display
     */
    public static void tle(Context con, String msg){
        Toasty.error(con, msg, Toast.LENGTH_LONG, true).show();
    }


}
