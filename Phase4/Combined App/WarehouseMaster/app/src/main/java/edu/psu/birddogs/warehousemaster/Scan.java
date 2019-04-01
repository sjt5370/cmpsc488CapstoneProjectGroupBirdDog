package edu.psu.birddogs.warehousemaster;


import android.Manifest;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

public class Scan extends AppCompatActivity {
    BarcodeDetector bcD;
    CameraSource cS;
    private int productID;
    private final static int CAMERA_PERMISSION = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION);
        else startCamera();
    }

    private void startCamera(){
        bcD = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.PRODUCT).build();
        cS = new CameraSource.Builder(this, bcD).setRequestedPreviewSize(1920,1280).setAutoFocusEnabled(true).build();

        bcD.setProcessor(new Detector.Processor<Barcode>(){
            @Override
            public void release(){}

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections){
                final SparseArray list = detections.getDetectedItems();

                if(list.size() != 0) {
                    try {
                        String v = ((Barcode) list.valueAt(0)).displayValue;
                        productID = Integer.parseInt(v);
                    } catch (ClassCastException e) {
                        return;
                    }
                    System.out.println(productID);

                    Order.returnedId = productID;

                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            if (cS != null) cS.stop();
                            if (bcD != null) bcD.release();
                        }
                    });
                }
            }
        });
        ((SurfaceView) findViewById(R.id.camera)).getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    cS.start(((SurfaceView) findViewById(R.id.camera)).getHolder());
                } catch (IOException e) {
                    finish();
                } catch (SecurityException e) {
                    Toast.makeText(Scan.this, getResources().getString(R.string.camera_needed), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) { cS.stop(); }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cS != null) cS.release();
        if (bcD != null) bcD.release();
    }
    @Override
    public void onActivityResult(int reqCode, int resCode, Intent result) {
        if (cS != null) cS.release();
        startCamera();
    }
    @Override
    public void onRequestPermissionsResult(int reqCode, @NonNull String[] permissions, @NonNull int[] results) {
        if (reqCode == CAMERA_PERMISSION) {
            if (results.length != 0 && results[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else finish();
        } else super.onRequestPermissionsResult(reqCode, permissions, results);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(false);
    }

    public void back(View view){
        Order.returnedId = -1;
        Scan.this.finish();
    }

}
