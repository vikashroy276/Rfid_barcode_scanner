package com.mespl.emp_asset_mgmtapp.scanner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;



/**
 * Created by mars on 28/02/18.
 */

public class MesplBaseActivity extends AppCompatActivity {

    protected Toolbar toolbar;
    protected static Vibrator vibrator;
    protected static long pattern[] = {60, 120, 180, 240, 300, 360, 420, 480};
    protected ToneGenerator toneGenerator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Log.d("onCreate","onCreate");
            /*if(CacheUtils.getString("ORIENTATION") == null) {
                this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }else if( CacheUtils.getString("ORIENTATION").equalsIgnoreCase("PORTRAIT") ){
                this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }else{
                this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }*/
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if(getSupportActionBar()!=null)
                getSupportActionBar().hide();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }



    public static void startVibrate(){
        try {
            if(vibrator.hasVibrator()) vibrator.vibrate(pattern, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopVibrate(){
        try {
            if(vibrator.hasVibrator())vibrator.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void playAlert(){
        try {
            toneGenerator = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 500);
            toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void error(){
        try {
            toneGenerator = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 500);
            toneGenerator.startTone(ToneGenerator.TONE_SUP_ERROR/*TONE_CDMA_SOFT_ERROR_LITE*/);
           /* Thread.sleep(5000);
            toneGenerator.stopTone();*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
public void stoptone(){
        try {
            toneGenerator.stopTone();
        }
        catch (Exception e){
            e.printStackTrace();
        }
}
    public void hideKeyBoard(){
        try {
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showKeyBoard(final View view){
            try {
            view.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        } catch (Exception e) {
            e.printStackTrace(); } }
/*
    @Override
*/
   /* public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // Toast.makeText(getApplicationContext(),"Back",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            //Toast.makeText(getApplicationContext(),"Menu",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            // Toast.makeText(getApplicationContext(),"Home",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (keyCode == KeyEvent.KEYCODE_SEARCH) {
            //Toast.makeText(getApplicationContext(),"Search",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (keyCode == KeyEvent.KEYCODE_SETTINGS) {
            //Toast.makeText(getApplicationContext(),"settings",Toast.LENGTH_SHORT).show();
            return false;
        }

        return super.onKeyDown(keyCode, event);
    }*/


    @Override
    protected void onStart() {
        try {
            super.onStart();
            Log.d("onStart","onStart");
            IntentFilter filter = new IntentFilter();
            filter.addAction("com.example.paxtrack_bial.BASE");
            LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
        try {
            super.onRestart();
            Log.d("onRestart","onRestart");
            IntentFilter filter = new IntentFilter();
            filter.addAction("com.example.paxtrack_bial.BASE");
            LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        try {
            super.onStop();
            Log.d("onStop","onStop");
            LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Log.d("Broadcast","Triggered");
                if(intent.getAction()!=null){
                    if(intent.getAction().equals("com.example.paxtrack_bial.BASE")){
                        if(intent.getStringExtra("CloseApp")
                                .equalsIgnoreCase("CloseApp")){
                            finish();

                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.paxtrack_bial.BASE");
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,filter);

        Log.d("onResume","onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("onPause","onPause");
    }
}
