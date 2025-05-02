package com.mespl.emp_asset_mgmtapp.scanner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.edwardvanraak.materialbarcodescanner.MWBarcodeScanner;
import com.honeywell.aidc.AidcManager;
import com.honeywell.aidc.BarcodeFailureEvent;
import com.honeywell.aidc.BarcodeReader;
import com.honeywell.aidc.ScannerUnavailableException;
import com.honeywell.aidc.TriggerStateChangeEvent;
import com.honeywell.aidc.UnsupportedPropertyException;
import com.mespl.emp_asset_mgmtapp.activities.asset.AssetActivity;
import com.mespl.emp_asset_mgmtapp.activities.asset.NonTaggedAssetActivity;
import com.mespl.emp_asset_mgmtapp.activities.employee.AssetsItemActivity;
import com.mespl.emp_asset_mgmtapp.activities.employee.EmployeeActivity;
import com.mespl.emp_asset_mgmtapp.activities.employee.NonTaggedEmployeeActivity;
import com.mespl.emp_asset_mgmtapp.activities.mappingScreen.MappingActivity;
import com.mespl.emp_asset_mgmtapp.rfid.RFIDHandler;
import com.zebra.rfid.api3.TagData;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mars on 24/02/18.
 */

public abstract class ScannerBaseActivityforcam extends
        MesplBaseActivity implements IOnScannerEvent,
        BarcodeReader.BarcodeListener, BarcodeReader.TriggerListener,
        RFIDHandler.ResponseHandlerInterface {

    public static final String TAG = "ScannerBaseActivity";
    public static final int BLUETOOTH_PERMISSION_REQUEST_CODE = 1001;
    public static ScannerBaseActivityforcam currentActivity = null;
    protected RFIDHandler rfidHandler;
    boolean isEnable = false;
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("BARCODE_ACTION")) {
                String action = intent.getStringExtra("AppAction");
                String barcode = intent.getStringExtra("Barcode");
                onScannedData(barcode);
            }
        }
    };
    private AidcManager manager;
    private BarcodeReader barcodeReader;
    private TextView statusTextViewRFID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentFilter filter = new IntentFilter("BARCODE_ACTION");
        filter.addAction("BARCODE_ACTION");
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
        registerReceiver(receiver, filter);

        if (isFeatureAvailable_honeywell("datacollection")) {
            // get bar code instance from MainActivity
            AidcManager.create(this, new AidcManager.CreatedCallback() {

                @Override
                public void onCreated(AidcManager aidcManager) {
                    manager = aidcManager;
                    barcodeReader = manager.createBarcodeReader();
                    setupScanner();
                }
            });
        }
    }


    protected void initializeRFIDHandler() {

    }

    protected void setupRFIDStatusTextView(int statusTextViewId) {
        statusTextViewRFID = findViewById(statusTextViewId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentActivity = this;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (currentActivity == this) {
            currentActivity = null;
        }
    }

    public void callSubclassMethod() {
        // Check if the current activity is of a specific type

    }

    @Override
    public void onDataScanned(String scanData) {
        System.out.println("scanData " + scanData);
        if (currentActivity != null && currentActivity instanceof NonTaggedAssetActivity) {
            ((NonTaggedAssetActivity) currentActivity).onScannedData(scanData);
        } else if (currentActivity != null && currentActivity instanceof EmployeeActivity) {
            ((EmployeeActivity) currentActivity).onScannedData(scanData);
        } else if (currentActivity != null && currentActivity instanceof NonTaggedEmployeeActivity) {
            ((NonTaggedEmployeeActivity) currentActivity).onScannedData(scanData);
        } else if (currentActivity != null && currentActivity instanceof AssetActivity) {
            ((AssetActivity) currentActivity).onScannedData(scanData);
        } else if (currentActivity != null && currentActivity instanceof MappingActivity) {
            ((MappingActivity) currentActivity).onScannedData(scanData);
        }

    }

    @Override
    public void onStatusUpdate(String scanStatus) {
        scannerStatus(scanStatus);
    }


    @Override
    public void onError() {
        System.out.println(TAG + "onError() ");
    }


    public abstract void onScannedData(String data);

    public void scannerStatus(String statusData) {
        System.out.println("statusData " + statusData);
    }

    public void enableScan() {
        try {
            isEnable = true;
            if (isFeatureAvailable("com.symbol.emdk")) {
                BarcodeScanner.getInstance(this);
                BarcodeScanner.registerUIobject(this);
            } else if (isFeatureAvailable_honeywell("datacollection")) {
                enableScan_honey();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void enableCamera() {
        try {
            Intent intent = new Intent(getApplicationContext(), MWBarcodeScanner.class);
            intent.putExtra("AppAction", "BARCODE_ACTION");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disableScan() {
        isEnable = false;
        BarcodeScanner.unregisterUIobject();
        if (isFeatureAvailable("com.symbol.emdk")) {
            BarcodeScanner.deInitScanner();
            //resuming from a screen suspend event the onDestroy method doesn't trigger, therefore releasing EMDK here
            BarcodeScanner.releaseEmdk();
        }
    }

    public void scan() {
        try {
            if (isFeatureAvailable("com.symbol.emdk")) {
                BarcodeScanner.softScan();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showToast(String msg, boolean isError) {
        //`disableScan();
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

    }

    public boolean isFeatureAvailable(String feature) {
        if (android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.contains("Motorola Solutions"))
            return true;
        return false;
    }

    public boolean isFeatureAvailable_honeywell(String feature) {
        if (android.os.Build.MANUFACTURER.contains("Honeywell")) return true;
        return false;
    }

    public void setupScanner() {

        try {
            if (barcodeReader != null) {

                // register bar code event listener
                barcodeReader.addBarcodeListener(this);

                try {
                    barcodeReader.setProperty(BarcodeReader.PROPERTY_TRIGGER_CONTROL_MODE, BarcodeReader.TRIGGER_CONTROL_MODE_CLIENT_CONTROL);
                } catch (UnsupportedPropertyException e) {
                    Toast.makeText(this, "Failed to apply properties", Toast.LENGTH_SHORT).show();
                }
                // register trigger state change listener
                barcodeReader.addTriggerListener(this);

                Map<String, Object> properties = new HashMap<String, Object>();
                // Set Symbologies On/Off
                properties.put(BarcodeReader.PROPERTY_CODE_128_ENABLED, true);
                properties.put(BarcodeReader.PROPERTY_GS1_128_ENABLED, true);
                properties.put(BarcodeReader.PROPERTY_QR_CODE_ENABLED, true);
                properties.put(BarcodeReader.PROPERTY_CODE_39_ENABLED, true);
                properties.put(BarcodeReader.PROPERTY_DATAMATRIX_ENABLED, true);
                properties.put(BarcodeReader.PROPERTY_UPC_A_ENABLE, true);
                properties.put(BarcodeReader.PROPERTY_EAN_13_ENABLED, false);
                properties.put(BarcodeReader.PROPERTY_AZTEC_ENABLED, false);
                properties.put(BarcodeReader.PROPERTY_CODABAR_ENABLED, false);
                properties.put(BarcodeReader.PROPERTY_INTERLEAVED_25_ENABLED, false);
                properties.put(BarcodeReader.PROPERTY_PDF_417_ENABLED, true);
                // Set Max Code 39 barcode length
                properties.put(BarcodeReader.PROPERTY_CODE_39_MAXIMUM_LENGTH, 10);
                // Turn on center decoding
                properties.put(BarcodeReader.PROPERTY_CENTER_DECODE, true);
                // Disable bad read response, handle in onFailureEvent
                properties.put(BarcodeReader.PROPERTY_NOTIFICATION_BAD_READ_ENABLED, false);
                // Apply the settings
                barcodeReader.setProperties(properties);

                // set the trigger mode to client control
                onResume();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // When using Automatic Trigger control do not need to implement the
    // onTriggerEvent function
    @Override
    public void onTriggerEvent(TriggerStateChangeEvent event) {
//        try {
//            barcodeReader.aim(event.getState());
//            barcodeReader.light(event.getState());
//            barcodeReader.decode(event.getState());
//
//        } catch (ScannerNotClaimedException e) {
//            e.printStackTrace();
//            Toast.makeText(this, "Scanner is not claimed", Toast.LENGTH_SHORT).show();
//        } catch (ScannerUnavailableException e) {
//            e.printStackTrace();
//            Toast.makeText(this, "Scanner unavailable", Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    public void onFailureEvent(BarcodeFailureEvent arg0) {
    }

    public void enableScan_honey() {

        try {
            if (barcodeReader != null) {
                try {
                    barcodeReader.claim();
                } catch (ScannerUnavailableException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Scanner unavailable", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disableScan_honey() {
        try {
            if (barcodeReader != null) {
                barcodeReader.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleTriggerPress(boolean pressed) {
        if (pressed) {
            rfidHandler.performInventory();
        } else {
            rfidHandler.stopInventory();
        }
    }

    @Override
    public void handleTagdata(TagData[] tagData) {
        updateTagData(tagData);
    }

    private void updateTagData(TagData[] tagData) {
        TagData closestTag = null;
        int highestRSSI = Integer.MIN_VALUE;
        for (TagData tag : tagData) {
            int currentRSSI = tag.getPeakRSSI();
            if (currentRSSI > highestRSSI) {
                highestRSSI = currentRSSI;
                closestTag = tag;
            }
            Log.i(TAG, "Tag ID: " + tag.getTagID() + " RSSI: " + currentRSSI);
        }

        if (closestTag != null) {
            Log.d(TAG, "Closest Tag ID: " + closestTag.getTagID() + " RSSI: " + highestRSSI);
            passTheTag(closestTag.getTagID());
        }
    }

    private void passTheTag(String tagData) {
        if (currentActivity != null && currentActivity instanceof NonTaggedAssetActivity) {
            ((NonTaggedAssetActivity) currentActivity).handleTagdata(tagData);
        } else if (currentActivity != null && currentActivity instanceof EmployeeActivity) {
            ((EmployeeActivity) currentActivity).handleTagdata(tagData);
        } else if (currentActivity != null && currentActivity instanceof NonTaggedEmployeeActivity) {
            ((NonTaggedEmployeeActivity) currentActivity).handleTagdata(tagData);
        } else if (currentActivity != null && currentActivity instanceof AssetActivity) {
            ((AssetActivity) currentActivity).handleTagdata(tagData);
        } else if (currentActivity != null && currentActivity instanceof MappingActivity) {
            ((MappingActivity) currentActivity).handleTagdata(tagData);
        } else if (currentActivity != null && currentActivity instanceof AssetsItemActivity) {
            ((AssetsItemActivity) currentActivity).handleTagdata(tagData);
        }
    }

    @Override
    public boolean showStatus() {
        return rfidHandler.isReaderConnected();
    }

    public void connectRfid() {
        rfidHandler = RFIDHandler.getInstance(30, this);
        rfidHandler.setHandler(this);
        if (rfidHandler != null && !rfidHandler.isReaderConnected()) {
            rfidHandler.connect();
        }
    }

    public void disconnectRfid(){
        if (rfidHandler != null && rfidHandler.isReaderConnected()) {
            rfidHandler.disconnect();
        }
    }

}