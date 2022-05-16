package ke.co.eclectic.quickstore.inc;


import android.app.Application;
import android.content.Context;
import android.support.multidex.BuildConfig;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import ke.co.eclectic.quickstore.helper.GlobalVariable;
import ke.co.eclectic.quickstore.rest.ApiClient;
import ke.co.eclectic.quickstore.rest.response.UserResponse;
import timber.log.Timber;

import static android.util.Log.ERROR;
import static org.apache.poi.ss.usermodel.DataValidation.ErrorStyle.WARNING;

/**
 * Created by David Manduku on 08/10/2018.
 */

public class AppConfig  extends MultiDexApplication {

//    public static  String SERVER_URL = "http://10.20.2.4:8686/sme/";
    public static  String SERVER_URL = "https://test-api.ekenya.co.ke/sme/";
//    public static  String SERVER_URL = "http://10.20.2.59:8686/sme/";
   // public static  String MPESA_URL = "https://testgateway.ekenya.co.ke:8443/ServiceLayer/onlinecheckout/request";

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

       @Override public void onCreate(){
              super.onCreate();
              if (BuildConfig.DEBUG) {
                     Timber.plant(
                             new Timber.DebugTree() {
                            @Override
                            protected String createStackElementTag(StackTraceElement element) {
                                   return String.format("C:%s:%s",
                                           super.createStackElementTag(element),
                                           element.getLineNumber());
                            }
                     });
              }else{
                  //SERVER_URL = "http://197.232.2.118:8686/sme/";
                  Timber.plant(new ReleaseTree());
              }
           Timber.plant(
                   new Timber.DebugTree() {
                       @Override
                       protected String createStackElementTag(StackTraceElement element) {
                           return String.format("C:%s:%s",
                                   super.createStackElementTag(element),
                                   element.getLineNumber());
                       }
                   });
          // AppCenter.start(this, "7554f63e-624c-4ad3-9254-a92de0531cef", Analytics.class, Crashes.class);

             // The begin method receives your personal app token as a parameter.
             // TestFairy.begin(this, "7290b975d865baab197d27faf2cb4f4e2a534420");
       }

    /**
     * The type Not logging tree.
     */
    private class NotLoggingTree extends Timber.Tree {
              @Override
              protected void log(final int priority, final String tag, final String message, final Throwable throwable) {
              }
       }

    /**
     * Send error message to serve when app is in production
     */
    public class ReleaseTree extends Timber.Tree {
              @Override
              protected void log(int priority, String tag, String message, Throwable t) {
              }
    }

}
