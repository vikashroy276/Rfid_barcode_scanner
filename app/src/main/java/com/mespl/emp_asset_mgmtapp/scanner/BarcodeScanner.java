package com.mespl.emp_asset_mgmtapp.scanner;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.symbol.emdk.EMDKManager;
import com.symbol.emdk.EMDKResults;
import com.symbol.emdk.VersionManager;
import com.symbol.emdk.barcode.BarcodeManager;
import com.symbol.emdk.barcode.ScanDataCollection;
import com.symbol.emdk.barcode.Scanner;
import com.symbol.emdk.barcode.ScannerConfig;
import com.symbol.emdk.barcode.ScannerException;
import com.symbol.emdk.barcode.ScannerResults;
import com.symbol.emdk.barcode.StatusData;

import java.util.ArrayList;

// Class registers with EMDK listeners for decoding barcode data and Status of the scanner
public class BarcodeScanner implements EMDKManager.EMDKListener, Scanner.StatusListener, Scanner.DataListener {
    // Declare variables to call interface class
    private static final int I_ON_DATA = 0;
    private static final int I_ON_STATUS = 1;
    private static final int I_ON_ERROR = 2;


    private static EMDKManager emdkManager = null;  // Declare variable to store EMDKManager object
    private static BarcodeManager barcodeManager; // Declare variable to store BarcodeManager object
    private static Scanner scanner = null; //Declare variable to hold scanner device to scann

    private static IOnScannerEvent mUIactivity = null; // Declare scanner event listener for UI activity
    private static Handler mScanHandler; // Handler to call listener
    private static IOnScannerEventRunnable mEventRunnable; // Runnable will be called on mScanHandler
    private static BarcodeScanner mBarcodeScanner; // Declare variable to store BarcodeScanner object

    private final static String TAG = "[Z] BarcodeScanner - "; //TAG for logging
    private Context context;

    // Entry point - passing context of UI activity
    // Constructor context of BarcodeScanner
    public static BarcodeScanner getInstance(Context context) {
        if (mBarcodeScanner == null) {
            mBarcodeScanner = new BarcodeScanner(context); // call BarcodeScanner() and returns BarcodeScanner object()

            //i.e.: com.BarcodeClassSample.BarcodeScanner@7594390
        }
        return mBarcodeScanner;
    }

    //get EMDKManager with context of UI activity
    //i.e.: com.BarcodeClassSample.Main1Activity@c7b924
    private BarcodeScanner(Context context) {
        // The EMDKManager object will be created and returned in the callback.
        EMDKResults results = EMDKManager.getEMDKManager(context, this);
        // Check the return status of getEMDKManager
        if (results.statusCode != EMDKResults.STATUS_CODE.SUCCESS) {
            System.out.println (TAG + "EMDKManager Request Failed");
        }
        // create Handler to move data from Scanner object to an object on the UI thread
        // i.e.: Handler (android.os.Handler) {beeae42}
        mScanHandler = new Handler(Looper.getMainLooper());
        //create runnable object
        //i.e.: com.BarcodeClassSample.BarcodeScanner$IOnScannerEventRunnable@6c47e53
        mEventRunnable = new IOnScannerEventRunnable();
    }

    //Method to release the EMDK manager
    public static void releaseEmdk() {
        if (emdkManager != null) {
            emdkManager.release();
            emdkManager = null;
        }
        mBarcodeScanner = null;
    }

    public static void softScan(){
        try {
            if (scanner.isEnabled()) {
                if(scanner.isReadPending())
                    scanner.cancelRead();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                scanner.triggerType = Scanner.TriggerType.SOFT_ALWAYS;
                scanner.read();

            }
        } catch (ScannerException e) {
            System.out.println (TAG + "onStatus() - ScannerException " + e.getMessage());
            e.printStackTrace();

        }
        catch (Exception e) {
            System.out.println (TAG + "onStatus() - Exception " + e.getMessage());
            e.printStackTrace();

        }
    }

    //Function to register BarcodeScanner listener by UI Activity
    //i.e.: com.BarcodeClassSample.Main1Activity@c7b924
    public static void registerUIobject(IOnScannerEvent UIactivity) {
        mUIactivity = UIactivity;
    }

    public static void unregisterUIobject() {
        mUIactivity = null;
    }

    //This method is called automatically whenever the EMDKmanager is initialised
    @Override
    public void onOpened(EMDKManager emdkManager) {
        this.emdkManager = emdkManager;
       // getEMDKVersion(emdkManager);

        //Method calls to initialize and set decoder parameters of scanner
        initializeScanner();
        setScannerParameters();
    }

    //Called when the barcode is scanned
    @Override
    public void onData(ScanDataCollection scanDataCollection) {
        if (scanDataCollection != null
                && scanDataCollection.getResult() == ScannerResults.SUCCESS) {
            ArrayList<ScanDataCollection.ScanData> scanData = scanDataCollection.getScanData();
            if (scanData != null && scanData.size() > 0) {
                final ScanDataCollection.ScanData data = scanData.get(0);
                callIOnScannerEvent(I_ON_DATA, data.getData(), null);
            }
        }
    }

    //Called when the status of the scanner is changed
    //i.e.: com.symbol.emdk.barcode.StatusData@6803045
    @Override
    public void onStatus(StatusData statusData) {
        String statusStr = "";
        StatusData.ScannerStates state = statusData.getState();
        switch (state) {
            case IDLE: //Scanner is IDLE - this is when to request a read
                statusStr = "Scanner enabled and idle";
                try {
                    if (scanner.isEnabled() && !scanner.isReadPending()) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        scanner.read();
                    }
                } catch (ScannerException e) {
                    System.out.println (TAG + "onStatus() - ScannerException " + e.getMessage());
                    e.printStackTrace();
                    statusStr = e.getMessage();
                }
                break;
            case SCANNING: //Scanner is SCANNING
                statusStr = "Scanner beam is on, aim at the barcode";
                break;
            case WAITING: //Scanner is waiting for trigger press
                statusStr = "Waiting for trigger, press to scan barcode";
                break;
            case DISABLED: //Scanner is disabled
                statusStr = "Scanner is not enabled";
                break;
            case ERROR: //Error occurred
                statusStr = "Error occurred during scanning";
                break;
        }
        //Return result to populate UI thread
        callIOnScannerEvent(I_ON_STATUS, null, statusStr);
    }

    //Clean up objects created by EMDK manager, if EMDK was closed abruptly.
    @Override
    public void onClosed() {
        if (emdkManager != null) {
            emdkManager.release();
            emdkManager = null;
        }
        mBarcodeScanner = null;
    }

    //Method to initialize, add listeners and enable scanner
    private void initializeScanner() {
        try {
            //Get instance of the Barcode Manager object
            barcodeManager = (BarcodeManager) emdkManager.getInstance(EMDKManager.FEATURE_TYPE.BARCODE);
            //Using the default scanner device to scan barcodes
            scanner = barcodeManager.getDevice(BarcodeManager.DeviceIdentifier.DEFAULT);
            //Add data and status listeners
            scanner.addDataListener(this);
            scanner.addStatusListener(this);
//            if (Boolean.FALSE.equals(CacheUtils.INSTANCE.getLight())) {
                scanner.triggerType = Scanner.TriggerType.HARD; // Set to HARD if "isLight" is false
//            } else {
//                scanner.triggerType = Scanner.TriggerType.SOFT_ALWAYS; // Default to SOFT_ALWAYS otherwise
//            }

            scanner.enable(); //Enable the scanner

        } catch (ScannerException e) {
            System.out.println (TAG + "initializeScanner() - ScannerException " + e.getMessage());
            e.printStackTrace();
        }
        catch (Exception ex) {
            System.out.println (TAG + "initializeScanner() - Exception " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    //Method to set decoder parameters in the ScannerConfig object
    public void setScannerParameters() {
        try {
            ScannerConfig config = scanner.getConfig();
            config.decoderParams.qrCode.enabled = true; // enable barcode symbology code128
            config.decoderParams.code128.enabled = true; // enable barcode symbology code128
            config.decoderParams.code39.enabled = true; // enable barcode symbology code39
            config.decoderParams.upca.enabled = true; // enable barcode symbology UPCA
            config.decoderParams.i2of5.enabled = true; // enable barcode symbology i2Of5
            scanner.setConfig(config);
        } catch (ScannerException e) {
            System.out.println (TAG + "setScannerParameters() - ScannerException " + e.getMessage());
            e.printStackTrace();
            callIOnScannerEvent(I_ON_ERROR, null, null);
        } catch (RuntimeException e) {
            e.printStackTrace();
            System.out.println (TAG + "setScannerParameters() - RuntimeException " + e.getMessage());
            callIOnScannerEvent(I_ON_ERROR, null, null);
        }
    }

    //Method to de-initialize scanner
    public static void deInitScanner() {
        if (scanner != null) {
            try {
                if(scanner.isReadPending()) {
                    scanner.cancelRead();
                }
                scanner.disable();
                scanner.removeDataListener(mBarcodeScanner);
                scanner.removeStatusListener(mBarcodeScanner);
                scanner.release();
            } catch (ScannerException e) {
                System.out.println (TAG + "deInitScanner() - ScannerException " + e.getMessage());
                e.printStackTrace();
            }
            scanner = null;
        }

        //Release instance of barcodeManager object
        if (barcodeManager != null) {
            barcodeManager = null;
        }
    }

    private void callIOnScannerEvent(int interfaceId, String data, String status) {
        if (mUIactivity != null) {
            mEventRunnable.setDetails(interfaceId, data, status);
            mScanHandler.post(mEventRunnable);
        }
    }

    private static class IOnScannerEventRunnable implements Runnable {
        private int mInterfaceId = 0;
        private String mBarcodeData = "";
        private String mBarcodeStatus = "";

        public void setDetails(int id, String data, String statusStr) {
            mInterfaceId = id;
            mBarcodeData = data;
            mBarcodeStatus = statusStr;
        }

        @Override
        public void run() {
            if(mUIactivity!=null) {
                switch (mInterfaceId) {
                    case I_ON_DATA:
                        mUIactivity.onDataScanned(mBarcodeData);
                        break;
                    case I_ON_STATUS:
                        mUIactivity.onStatusUpdate(mBarcodeStatus);
                        break;
                    case I_ON_ERROR:
                        mUIactivity.onError();
                        break;
                }
            }
        }
    }

    public static String getEMDKVersion(EMDKManager emdkManager)
    {
        VersionManager versionManager = (VersionManager) emdkManager.getInstance(EMDKManager.FEATURE_TYPE.VERSION);
        String emdkVersion = versionManager.getVersion(VersionManager.VERSION_TYPE.EMDK);
        return emdkVersion;
    }
}
