package com.example.ProductComparison;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


/**
 * @author Dillon Scott 1604465
 */

/**
 * Activty to scan barcode or qr codes
 */
public class ScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    int getCameraPermission= 0;
    ZXingScannerView scannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
    }

    /**
     * scans barcode and assigns string value
     * @param result
     */
    @Override
    public void handleResult(Result result) {
        AddEditNoteActivity.barcodeResult.setText(result.getText());
        onBackPressed();
    }

    /**
     * stop camera
     */
    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    /**
     * open camera
     */
    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                !=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},
                    getCameraPermission);
        }

        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }
}