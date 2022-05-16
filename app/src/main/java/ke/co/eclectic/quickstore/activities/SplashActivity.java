package ke.co.eclectic.quickstore.activities;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.activities.inventory.AddInventoryActivity;
import ke.co.eclectic.quickstore.activities.onboarding.SliderActivity;
import ke.co.eclectic.quickstore.helper.GlobalMethod;

/**
 * Created by David Manduku on 08/10/2018.
 */
public class SplashActivity extends AppCompatActivity {
    private CompositeDisposable disposable = new CompositeDisposable();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        init();


    }

    private void init() {

        disposable.add( Completable.timer(3, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .subscribe(()  -> {
                    GlobalMethod.goToActivity(SplashActivity.this,SliderActivity.class);
                    finish();}));

//todo remove code
//        final Handler handler = new Handler();
//        handler.postDelayed(() -> {
//            // Do something after 5s = 5000ms
//            GlobalMethod.goToActivity(SplashActivity.this,SliderActivity.class);
//            finish();
//        }, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }
}
