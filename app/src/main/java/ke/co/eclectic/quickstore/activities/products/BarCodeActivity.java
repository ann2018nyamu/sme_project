package ke.co.eclectic.quickstore.activities.products;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class BarCodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    public static final String EXTRA_REPLY = "BARCODE_REPLY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mScannerView = new ZXingScannerView(this);
        mScannerView.setResultHandler(this);// Register ourselves as a handler for scan results.
        mScannerView.startCamera();

        setContentView(mScannerView);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkIfAlreadyhavePermission()) {
                requestForSpecificPermission();
            }
        }

    }

    /**
     * request camera permission
     */
    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.CAMERA
        }, 101);
    }

    /**
     * chekcs if permission is granted
     * @return
     */
    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * listen to granted permission
     * @param requestCode request code
     * @param permissions permission requested
     * @param grantResults results
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                } else {
                    //not granted
                    finish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * register and start camera
     */
    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    /**
     * stops camera
     */
    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();// Stop camera on pause
    }

    /**
     * listens to responce given from the camera after scanning barcode
     * @param rawResult result from camera
     */
    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
       // CodingMsg.l( rawResult.getText()); // Prints scan results
       // Prints the scan format (qrcode, pdf417 etc.)
       // Timber.v( rawResult.getText()); // Prints scan results
      //  Timber.v( rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)


        Intent replyIntent = new Intent();
        replyIntent.putExtra(EXTRA_REPLY, rawResult.getText());
        setResult(RESULT_OK, replyIntent);
        finish();


    }

}
