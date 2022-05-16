package ke.co.eclectic.quickstore.helper;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Base64;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.FrameLayout;
import android.widget.TextView;


import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;
import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.activities.auth.AuthActivity;
import ke.co.eclectic.quickstore.models.User;
import ke.co.eclectic.quickstore.rest.response.BaseResponse;
import ke.co.eclectic.quickstore.rest.response.UserResponse;
import ke.co.eclectic.quickstore.rest.ApiClient;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;


/**
 * Created by David Manduku on 08/10/2018.
 */
public class GlobalMethod {

    private static Dialog dialog;

    private static SweetAlertDialog pDialog;
    static SweetAlertDialog psDialog;


    public static String currencyFormat(String amount) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        return formatter.format(Double.parseDouble(amount));
    }

    /**
     * Send push notification request to api.
     *
     * @param context the context
     * @param data    the data to send
     */
    public static void sendNotification(Context context, HashMap<String,String> data) {

        //new ApiRequest(this,userMap,"getUsers", true);
        ApiClient.getApi().notify(data).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                int statusCode = response.code();
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Timber.v(t.getMessage()+"");
                //GlobalMethod.showCustomAlert(MainActivity.this,"Error","Contact support");
            }
        });
    }


    /**
     * Show custom alert.
     *
     * @param context the context
     * @param title   the title of the mesage
     * @param message the message to display
     */
    public static void showCustomAlert(Context context, String title, String message) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogue_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView txtTitle =  dialog.findViewById(R.id.txtTitle);
        TextView txtMessage =  dialog.findViewById(R.id.txtMessage);
        TextView txtOk =  dialog.findViewById(R.id.txtOk);
        txtTitle.setText(title);
        txtMessage.setText(Html.fromHtml(message));
        txtOk.setOnClickListener(v -> dialog.dismiss());
        dialog.setCancelable(true);
        dialog.show();
    }

    /**
     * changed request object to readable string
     *
     * @param request the request body
     * @return the string formatted string
     */
    private static String bodyToString(final ResponseBody request){
        try {
            final ResponseBody copy = request;
            request.source().request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = request.source().buffer();
            String responseBodyString = buffer.clone().readString(Charset.forName("UTF-8"));

            return responseBodyString;
        }
        catch (final IOException e) {
            return "did not work";
        }
    }

    /**
     * Stoploader .
     *
     * @param context the context
     */
    public static void stoploader(Context context){

        try {
            pDialog.dismiss();
            pDialog = null;
        } catch (Exception e) {
            e.printStackTrace();
            Timber.e("stoploader".concat(e.getMessage()));
        }

    }

    /**
     * Showloader.
     *
     * @param context    the context
     * @param message    the message to display
     * @param cancelable the cancelable flags loader to be cancelable or not
     */
    public static void showloader(Context context, String message, boolean cancelable) {
        if(pDialog != null){//don't show dialogue if it already showing
            pDialog.dismiss();
        }
        pDialog =  new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(message);
        pDialog.setCancelable(cancelable);
        pDialog.show();

//        dialog = new Dialog(context);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog.setContentView(R.layout.loading_layout);
//
//        TextView txtMessage = dialog.findViewById(R.id.txtMessage);
//        txtMessage.setText(message);
//        dialog.setCancelable(cancelable);
//        dialog.show();
    }

    /**
     * Show success message.
     *
     * @param context       the context
     * @param message       the message to display
     * @param autodismissal the autodismissal when true
     */
    public static void showSuccess(Context context, String message, boolean autodismissal) {

        psDialog =  new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE);
        psDialog.setTitleText("Success");
        psDialog.setContentText(message);
        //psDialog.setCancelable(true);
        psDialog.show();
       if(autodismissal){
           Completable.timer(4, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                   .subscribe(new DisposableCompletableObserver() {
                       @Override
                       public void onComplete() {
                           try {
                               psDialog.dismiss();
                           }catch (Exception e){

                           }
                       }

                       @Override
                       public void onError(Throwable e) {

                       }

                   });
       }
       
    }

    /**
     * opens up  activity.
     *
     * @param context   the context
     * @param className the class name to open
     */
    public static void goToActivity(Context context, Class className) {
        Intent intent = new Intent(context, className);
        context.startActivity(intent);
    }

    /**
     * open up activity.
     *
     * @param context    the context
     * @param className  the class name
     * @param intentData the intent data
     */
    public static void goToActivity(Context context, Class className, HashMap<String,String> intentData) {
        Intent intent = new Intent(context, className);
        for (Map.Entry<String, String> data : intentData.entrySet()) {
            intent.putExtra(data.getKey(),data.getValue());
        }

        context.startActivity(intent);
    }

    /**
     * open up  activity.
     *
     * @param context     the context
     * @param className   the class name
     * @param intentData  the intent data
     * @param requestcode the requestcode
     */
    public static void goToActivity(Context context, Class className, HashMap<String,String> intentData,int requestcode) {
        Intent intent = new Intent(context, className);
        if(intentData == null){
            intentData = new HashMap<>();
        }

        for (Map.Entry<String, String> data : intentData.entrySet()) {
            intent.putExtra(data.getKey(),data.getValue());
        }
        ((Activity)context).startActivityForResult(intent,requestcode);

    }

    /**
     * opens up activity.
     *
     * @param fragment    the fragment
     * @param className   the class name
     * @param intentData  the intent data
     * @param requestcode the requestcode
     */
    public static void goToActivity(Fragment fragment, Class className, HashMap<String,String> intentData,int requestcode) {
        Intent intent = new Intent(fragment.getActivity(), className);
        if(intentData == null){
            intentData = new HashMap<>();
        }

        for (Map.Entry<String, String> data : intentData.entrySet()) {
            intent.putExtra(data.getKey(),data.getValue());
        }
        fragment.startActivityForResult(intent,requestcode);

    }

    /**
     * Logs out a user.
     *
     * @param activity the activity calling this method
     */
    public static void logOut(final Activity activity){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogue_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView txtTitle =  dialog.findViewById(R.id.txtTitle);
        TextView txtMessage =  dialog.findViewById(R.id.txtMessage);
        TextView txtOk =  dialog.findViewById(R.id.txtOk);
        TextView txtCancel =  dialog.findViewById(R.id.txtCancel);
        txtCancel.setVisibility(View.VISIBLE);
        txtTitle.setText("Log Out");
        txtMessage.setText("Are you sure you want to logout?");
//        txtTitle.setBackgroundResource(R.color.colorPrimary);
        txtOk.setText("Yes");
        txtCancel.setText("No");
        //txtTitle.setVisibility(View.GONE);
        txtOk.setOnClickListener(v -> {
            GlobalVariable.setCurrentUser(new User());
            HashMap<String,String> data= new HashMap<>();
            data.put("action","deleteuser");
            GlobalMethod.goToActivity(activity,AuthActivity.class,data);
            activity.finish();
            dialog.dismiss();

        });

        txtCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.setCancelable(true);
        dialog.show();

    }

    /**
     * formats  date.
     *
     * @param  dateStr time
     * @return formatted date
     */
    public static String getFormatedDateStr(String  dateStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date = new Date();
        try {
             date = format.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            Timber.v("failed to parse date   ".concat(dateStr));
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
            TimeZone tz = cal.getTimeZone();
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
            sdf.setTimeZone(tz);
            dateStr =   sdf.format(cal.getTime());
            return dateStr;
    }

    /**
     * validated email.
     *
     * @param email the email
     * @return the boolean
     */
    public static boolean isValidEmail(String email) {
            Pattern pattern = Patterns.EMAIL_ADDRESS;
            return pattern.matcher(email).matches();
        }



    /**
     * Show success snack bar.
     *
     * @param context the context
     * @param message the message to display
     * @param view    the view
     */
    public static void showMessage(Context context,String message,View view,String status,boolean showOnTop) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar .setAction("", null);
        View sbView = snackbar.getView();

        sbView.setBackgroundColor(ContextCompat.getColor(context,R.color.colorWhite));
        TextView textView =  sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTypeface(GlobalVariable.getMontserratMedium(context));
        textView.setTextColor(ContextCompat.getColor(context,R.color.colorBlack));
        if(status.contentEquals("success")){
            textView.setTextColor(ContextCompat.getColor(context,R.color.successColor));
        }

        if(status.contentEquals("error")){
            textView.setTextColor(ContextCompat.getColor(context,R.color.errorColor));
            snackbar.setDuration(Snackbar.LENGTH_INDEFINITE);
        }

        if(showOnTop) {
            final ViewGroup.LayoutParams params = sbView.getLayoutParams();
            if (params instanceof CoordinatorLayout.LayoutParams) {
                ((CoordinatorLayout.LayoutParams) params).gravity = Gravity.TOP;
            } else {
                ((FrameLayout.LayoutParams) params).gravity = Gravity.TOP;
            }
            sbView.setLayoutParams(params);
        }

        snackbar.show();
    }


    /**
     * Parse comm error.
     *
     * @param activity the context
     * @param e       the error object
     * @param view    the view
     */
    public static void parseCommError(Activity activity,Throwable e,View view) {
        GlobalMethod.stoploader(activity);
        Timber.v(e);
        if(e instanceof com.jakewharton.retrofit2.adapter.rxjava2.HttpException){
            ResponseBody body = ((com.jakewharton.retrofit2.adapter.rxjava2.HttpException)e).response().errorBody();
            Timber.v(bodyToString(body));

            BaseResponse baseResponse = new Gson().fromJson(bodyToString(body),BaseResponse.class);
            Timber.v("userResponse "+baseResponse.getMessage());

            Snackbar snackbar = Snackbar.make(view, baseResponse.getMessage(), Snackbar.LENGTH_LONG);
            snackbar .setAction("", null);
            // Changing message text color
            // snackbar.setActionTextColor(Color.RED);

            // Changing action button text color
            View sbView = snackbar.getView();
            sbView.setBackgroundColor(ContextCompat.getColor(activity,R.color.colorPrimaryDark));
            TextView textView =  sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(ContextCompat.getColor(activity,R.color.errorColor));
            snackbar.show();

            try {

                if(baseResponse.getMessage().contains("Expired JWT token")) {
                    activity.finish();
                    HashMap<String, String> data = new HashMap<>();
                    data.put("action", "deleteuser");
                    GlobalMethod.goToActivity(activity, AuthActivity.class, data);
                    CodingMsg.tle(activity, "Session Expired.Please login again");
                }

            } catch (Exception ex) {
                CodingMsg.tle(activity,"An unlikely error occured");
            }

        }else{
            Timber.v(e.getMessage());
            CodingMsg.tle(activity,e.getMessage());
        }
    }

    /**
     * Parses raw error.
     *
     * @param e the error object
     */
    public static void parseCommRawError(Throwable e) {

        if(e instanceof com.jakewharton.retrofit2.adapter.rxjava2.HttpException){
            ResponseBody body = ((com.jakewharton.retrofit2.adapter.rxjava2.HttpException)e).response().errorBody();
            Timber.v(bodyToString(body));
            BaseResponse userResponse = new Gson().fromJson(bodyToString(body),BaseResponse.class);
            Timber.v("parseCommRawError "+userResponse.getMessage());
        }else{
            Timber.v(e);
        }
    }
    /**
     * Converts bitmap to base64 string
     * @param bitmap image bitmap to be converted to base 64
     * @return converting bitmap and return a string
     */
    public static String bitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp=Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    /**
     * convertes base64 string to bitmap
     *
     *
     * @param encodedString base64 image representation
     * @return bitmap (from given string)
     */
    public static Bitmap stringToBitMap(String encodedString){
        try{
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }

    /**
     * Save image to phone file.
     *
     * @param context  the context
     * @param bitmap   the bitmap to save
     * @param filename the filename to be saved as
     * @return the file
     */
    public static File saveImageToPhone(Context context,Bitmap bitmap,String filename){
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/Quickstore/Pictures");
        if(!myDir.exists()) {
            myDir.mkdirs();
        }


        File file = new File(myDir, filename);
        if (file.exists()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

            ContentValues image = new ContentValues();
            image.put(MediaStore.Images.Media.TITLE, "Quickstore");
            image.put(MediaStore.Images.Media.DISPLAY_NAME, filename);
            image.put(MediaStore.Images.Media.DESCRIPTION, "App Image");
            image.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
            image.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
            image.put(MediaStore.Images.Media.ORIENTATION, 0);
            File parent = file.getParentFile();
            image.put(MediaStore.Images.ImageColumns.BUCKET_ID, parent.toString()
                    .toLowerCase().hashCode());
            image.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, parent.getName()
                    .toLowerCase());
            image.put(MediaStore.Images.Media.SIZE, file.length());
            image.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
            Uri result = context.getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, image);
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }

       return null;

    }
}
