package com.edwardvanraak.materialbarcodescanner;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import java.io.IOException;

public class MWBarcodeScanner extends AppCompatActivity {

    private GraphicOverlay<BarcodeGraphic> mGraphicOverlay;
    private Detector<Barcode> barcodeDetector;
    private CameraSourcePreview mCameraSourcePreview;
    private CameraSource mCameraSource;
    ImageView barcodeSqaure;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barcode_capture);
        barcodeSqaure = (ImageView) findViewById(R.id.barcode_square);
        listnerView();
    }

    private void listnerView() {
        LinearLayout layout_cross = findViewById(R.id.layout_cross);
        layout_cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            startScan();
        }
    };

    private void startScan() throws SecurityException {
        barcodeSqaure.setImageResource(R.drawable.material_barcode_square_512);
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, code, 100);
            dialog.show();
        }
        mGraphicOverlay = (GraphicOverlay<BarcodeGraphic>) findViewById(R.id.graphicOverlay);
        BarcodeGraphicTracker.NewDetectionListener listener = new BarcodeGraphicTracker.NewDetectionListener() {
            @Override
            public void onNewDetection(final Barcode barcode) {
                Log.e("Barcode", barcode.rawValue);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //pass data to inteface
                        if (getIntent().hasExtra("AppAction"))
                            sendBroadcastData(getIntent().getExtras().getString("AppAction"), barcode.rawValue);
                        else
                            sendBroadcastData("NOACTION", barcode.rawValue);
                        barcodeSqaure.setImageResource(R.drawable.material_barcode_square_512_green);

                        //Toast.makeText(getApplicationContext(), "Barcode: " + barcode.rawValue, Toast.LENGTH_SHORT).show();
                        clean();
                        finish();
                      /*  if (getIntent().hasExtra("Type") && getIntent().getExtras().getString("Type").equalsIgnoreCase("C")) {
                            new Handler().postDelayed(runnable, 800);
                        } else {
                            finish();
                        }*/

                    }
                });
                //Release
            }
        };
        BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(mGraphicOverlay, listener, Color.RED);
        build();
        barcodeDetector.setProcessor(new MultiProcessor.Builder<>(barcodeFactory).build());

        if (mCameraSource != null) {
            try {
                mCameraSourcePreview = (CameraSourcePreview) findViewById(R.id.preview);
                mCameraSourcePreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e("TAG", "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }

    }

    public void build() {
        //  String focusMode = Camera.Parameters.FOCUS_MODE_FIXED;
        String focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE;
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        mCameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setFlashMode(null)
                .setFocusMode(focusMode)
                .build();
    }

    private void clean() {
        if (mCameraSourcePreview != null) {
            mCameraSourcePreview.release();
            mCameraSourcePreview = null;
        }
    }

    public void sendBroadcastData(String action, String data) {
        Intent intent = new Intent("BARCODE_ACTION");
        intent.putExtra("AppAction", action);
        intent.putExtra("Barcode", data);
        if (getIntent().hasExtra("ExtraData")) {
            intent.putExtra("ExtraData", getIntent().getExtras().getString("ExtraData"));
        }
        intent.setAction("BARCODE_ACTION");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        clean();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(runnable, 200);
    }
}