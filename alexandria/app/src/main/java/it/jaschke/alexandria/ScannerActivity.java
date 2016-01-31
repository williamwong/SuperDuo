package it.jaschke.alexandria;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zbar.BarcodeFormat;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

/**
 * Activity that scans for barcodes and returns results
 * Created by williamwong on 12/20/15.
 */
public class ScannerActivity extends AppCompatActivity implements ZBarScannerView.ResultHandler{
    private static final String TAG = "ScannerActivity";
    private ZBarScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZBarScannerView(this);    // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view

        List<BarcodeFormat> formats = new ArrayList<>();
        formats.add(BarcodeFormat.ISBN13);
        formats.add(BarcodeFormat.EAN13);
        mScannerView.setFormats(formats);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        if (rawResult.getBarcodeFormat().getId() == BarcodeFormat.ISBN13.getId()) {
            Intent scanResultIntent = new Intent();
            scanResultIntent.putExtra(AddBook.SCAN_CONTENTS, rawResult.getContents()); // Return scan results
            scanResultIntent.putExtra(AddBook.SCAN_FORMAT, rawResult.getBarcodeFormat().getName()); // Return the scan format (qrcode, pdf417 etc.)
            setResult(Activity.RESULT_OK, scanResultIntent);
            finish();
        } else {
            Toast.makeText(this, R.string.error_wrong_barcode_format, Toast.LENGTH_SHORT).show();
            mScannerView.startCamera();
        }
    }
}
