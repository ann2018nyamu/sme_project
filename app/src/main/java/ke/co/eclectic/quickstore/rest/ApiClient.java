package ke.co.eclectic.quickstore.rest;


import android.content.Context;
import android.os.Build;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.Collections;
import java.util.Iterator;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import ke.co.eclectic.quickstore.helper.CodingMsg;
import ke.co.eclectic.quickstore.helper.GlobalVariable;
import ke.co.eclectic.quickstore.inc.AppConfig;
import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.TlsVersion;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

/**
 * Created by David Manduku on 08/10/2018.
 */
public class ApiClient {

    private static Retrofit retrofit = null;
    private static ApiInterface inventoryApi = null;
   // private static Context context;


    /**
     * Gets client.
     *
     * @param cont the cont
     * @return the client
     */
    public static Retrofit getClient(Context cont) {
        if (retrofit==null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            LoggingInterceptor loggingInterceptor =  new LoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder()
                    // .addInterceptor(interceptor)
                    .addInterceptor(loggingInterceptor)
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(AppConfig.SERVER_URL)
                    .client(client)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }


    public static ApiInterface getApi() {
        if (inventoryApi==null) {
            ConnectionSpec spec = new
                    ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                    .tlsVersions(TlsVersion.TLS_1_2)
                    .cipherSuites(
                            CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                            CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                            CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256)
                    .build();

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            LoggingInterceptor loggingInterceptor =  new LoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder()
                   // .addInterceptor(interceptor)
                    .connectionSpecs(Collections.singletonList(spec))
                    .addInterceptor(loggingInterceptor)
                    .build();


            inventoryApi = new Retrofit.Builder()
                    .baseUrl(AppConfig.SERVER_URL)
                    .client(client)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiInterface.class);
        }

        return inventoryApi;
    }
    public static ApiInterface getPaymentApi(String paymentEndpoint) {


            OkHttpClient client = new OkHttpClient.Builder()
                   // .addInterceptor(interceptor)
                   // .addInterceptor(loggingInterceptor)
                    .build();

            return  new Retrofit.Builder()
                    .baseUrl("https://testgateway.ekenya.co.ke:8443/")
                    .client(client)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiInterface.class);


    }


    /**
     * Gets api get.
     *
     * @param cont the cont
     * @return the api get
     */
    public static ApiInterface getApiGet(Context cont) {
        if (inventoryApi==null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            LoggingInterceptor loggingInterceptor =  new LoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder()
                    // .addInterceptor(interceptor)
                   // .addInterceptor(loggingInterceptor)
                    .build();


            inventoryApi = new Retrofit.Builder()

                    .baseUrl(AppConfig.SERVER_URL)
                    .client(client)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiInterface.class);
        }
        return inventoryApi;
    }


    /**
     * The type Logging interceptor.
     */
    static class LoggingInterceptor implements Interceptor {


        @Override public Response intercept(Chain chain) throws IOException {

                Request request = chain.request();
                long t1 = System.nanoTime();
                String requestLog = String.format("Sending request %s on %s%n%s",
                        request.url(), chain.connection(), request.headers());
            Timber.v(request.method());

            if(request.method().contentEquals("GET")){

            }else {
                JsonElement sessionData = new JsonParser().parse(bodyToString(request)).getAsJsonObject();

                    //intercepting and modifying request data
                    JsonObject clientTypeJson = new JsonObject();
                    clientTypeJson.addProperty("useragentversion", Build.MODEL);//"android kit kat");
                    clientTypeJson.addProperty("useragent", "android");
                    JSONObject resobj = null;

                    try {

                        resobj = new JSONObject(sessionData.toString());
                        Iterator<?> keys = resobj.keys();
                        JsonObject newSessionData = new JsonObject();
                        while (keys.hasNext()) {
                            String key = (String) keys.next();
                            Timber.v("key " + key);
                            if (resobj.get(key) instanceof JSONObject) {
                                Timber.v("res1 " + key + " " + resobj.get(key).toString());
                                newSessionData.addProperty(key, resobj.get(key).toString());
                                JSONObject xx = new JSONObject(resobj.get(key).toString());
                            }
                        }
                        Timber.v("res2 emnd " + newSessionData.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Timber.e("res2" + e.getMessage());
                    }


                    JsonObject json = new JsonObject();
                    json.add("request_type", ((JsonObject) sessionData).get("request_type"));
                    // json.add("session_data", new JsonParser().parse("{\"phonenumber\":\"25475702887\"}"));
                    json.add("session_data", sessionData);
                    json.add("client_type", clientTypeJson);
                    Request.Builder requestBuilder = request.newBuilder();

                    request = requestBuilder
                            .addHeader("Authorization", GlobalVariable.getCurrentUser().getAuth_token().replaceAll("\"", ""))
                            .post(RequestBody.create(MediaType.parse("application/json; charset=UTF-8"), json.toString()))
                            .build();
                    Timber.v("\nAuthorization:" + GlobalVariable.getCurrentUser().getAuth_token() + "\nrequest: " + request.url() + "\n" + bodyToString(request));
                   // CodingMsg.l("\nAuthorization:" + GlobalVariable.getCurrentUser().getAuth_token() + "\nrequest: " + request.url() + "\n" + bodyToString(request));


            }


            if (request.method().compareToIgnoreCase("post") == 0) {
                    requestLog = "\n" + requestLog + "\n" + bodyToString(request);
                }

                 Timber.v(requestLog);

            Response response  = chain.proceed(request);


                long t2 = System.nanoTime();

                String responseLog = String.format("Received response for %s in %.1fms%n%s",
                        response.request().url(), (t2 - t1) / 1e6d, response.headers());

                String bodyString = response.body().string();

                Timber.v("\nresponse: " + "\n" + bodyString);
                CodingMsg.l("\nresponse: " + "\n" + bodyString);


                return response
                        .newBuilder()
                        .body(ResponseBody.create(response.body().contentType(), bodyString))
                        .build();
                //return response;
            }

    }

    /**
     * Body to string string.
     *
     * @param request the request
     * @return the string
     */
    public static String bodyToString(final Request request) {
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            buffer.close();

            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }

    /**
     * Body to string string.
     *
     * @param request the request
     * @return the string
     */
    public static String bodyToString(final RequestBody request){
        try {
            final Buffer buffer = new Buffer();
            if(request != null)
                request.writeTo(buffer);
            else
                return "oops didnt work";
            buffer.close();

            return buffer.readUtf8();
        }
        catch (final IOException e) {
            return "did not work";
        }
    }


}
